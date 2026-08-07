package com.gjq.train.business.dailytrainticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.mapper.DailyTrainTicketMapper;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketSaveReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketUpdateReq;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
import com.gjq.train.business.trainstation.entity.TrainStation;
import com.gjq.train.business.trainstation.service.TrainStationService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyTrainTicketServiceImplTests {

    @Mock
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Mock
    private TrainStationService trainStationService;

    @Mock
    private DailyTrainSeatService dailyTrainSeatService;

    @InjectMocks
    private DailyTrainTicketServiceImpl dailyTrainTicketService;

    private LocalDate date;

    @BeforeEach
    void setUp() {
        date = LocalDate.of(2026, 8, 8);
    }

    @Test
    void shouldInsertDailyTrainTicket() {
        when(dailyTrainTicketMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        dailyTrainTicketService.save(saveRequest());

        verify(dailyTrainTicketMapper).insert(argThat(
                (DailyTrainTicket ticket) ->
                        LocalDate.of(2026, 8, 8).equals(ticket.getDate())
                                && "G1".equals(ticket.getTrainCode())
                                && "北京南".equals(ticket.getStart())
                                && "上海虹桥".equals(ticket.getEnd())
                                && ticket.getCreateTime() != null
                                && ticket.getCreateTime().equals(
                                ticket.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectDuplicateRoute() {
        when(dailyTrainTicketMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainTicketService.save(saveRequest())
        );

        verify(dailyTrainTicketMapper, never())
                .insert(any(DailyTrainTicket.class));
    }

    @Test
    void shouldDeleteExistingTicket() {
        when(dailyTrainTicketMapper.deleteById(100L)).thenReturn(1);

        dailyTrainTicketService.delete(100L);

        verify(dailyTrainTicketMapper).deleteById(100L);
    }

    @Test
    void shouldRejectDeletingMissingTicket() {
        when(dailyTrainTicketMapper.deleteById(100L)).thenReturn(0);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainTicketService.delete(100L)
        );
    }

    @Test
    void shouldUpdateExistingTicket() {
        DailyTrainTicket existing = new DailyTrainTicket();
        existing.setId(100L);
        when(dailyTrainTicketMapper.selectById(100L))
                .thenReturn(existing);
        when(dailyTrainTicketMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        dailyTrainTicketService.update(updateRequest());

        verify(dailyTrainTicketMapper).updateById(argThat(
                (DailyTrainTicket ticket) ->
                        Long.valueOf(100L).equals(ticket.getId())
                                && Integer.valueOf(120).equals(ticket.getYdz())
                                && ticket.getCreateTime() == null
                                && ticket.getUpdateTime() != null
        ));
    }

    @Test
    void shouldRejectUpdatingMissingTicket() {
        when(dailyTrainTicketMapper.selectById(100L))
                .thenReturn(null);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainTicketService.update(updateRequest())
        );
    }

    @Test
    void shouldQueryByAllConditions() {
        DailyTrainTicketQueryReq request =
                new DailyTrainTicketQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setDate(LocalDate.of(2026, 8, 8));
        request.setTrainCode("G1");
        request.setStart("北京南");
        request.setEnd("上海虹桥");

        DailyTrainTicket ticket = new DailyTrainTicket();
        ticket.setId(100L);
        when(dailyTrainTicketMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<DailyTrainTicket> page = invocation.getArgument(0);
            page.setRecords(List.of(ticket));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = dailyTrainTicketService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldGenerateAllTicketRoutesWithCalculatedPrices() {
        when(trainStationService.listByTrainCode("G1"))
                .thenReturn(stations());
        when(dailyTrainSeatService.countSeat(
                any(LocalDate.class),
                any(String.class),
                any(String.class)
        )).thenAnswer(invocation -> switch (
                invocation.<String>getArgument(2)
        ) {
            case "1" -> 80;
            case "2" -> 100;
            default -> -1;
        });

        dailyTrainTicketService.generateByTrainCode(date, "G1", "G");

        verify(dailyTrainTicketMapper).delete(any(Wrapper.class));
        ArgumentCaptor<DailyTrainTicket> captor =
                ArgumentCaptor.forClass(DailyTrainTicket.class);
        verify(dailyTrainTicketMapper, times(3)).insert(captor.capture());
        List<DailyTrainTicket> tickets = captor.getAllValues();
        DailyTrainTicket fullRoute = tickets.get(1);
        assertEquals("北京南", fullRoute.getStart());
        assertEquals("南京南", fullRoute.getEnd());
        assertEquals(new BigDecimal("72.12"), fullRoute.getYdzPrice());
        assertEquals(new BigDecimal("54.09"), fullRoute.getEdzPrice());
        assertEquals(new BigDecimal("108.18"), fullRoute.getRwPrice());
        assertEquals(new BigDecimal("90.15"), fullRoute.getYwPrice());
        assertEquals(80, fullRoute.getYdz());
        assertEquals(100, fullRoute.getEdz());
        assertEquals(-1, fullRoute.getRw());
        assertEquals(-1, fullRoute.getYw());
        verify(dailyTrainSeatService, times(4)).countSeat(
                any(LocalDate.class),
                any(String.class),
                any(String.class)
        );
    }

    @Test
    void shouldOnlyClearTicketWhenTrainHasInsufficientStations() {
        when(trainStationService.listByTrainCode("G1"))
                .thenReturn(List.of());

        dailyTrainTicketService.generateByTrainCode(date, "G1", "G");

        verify(dailyTrainTicketMapper).delete(any(Wrapper.class));
        verify(dailyTrainTicketMapper, never())
                .insert(any(DailyTrainTicket.class));
        verify(dailyTrainSeatService, never()).countSeat(
                any(LocalDate.class),
                any(String.class),
                any(String.class)
        );
    }

    private DailyTrainTicketSaveReq saveRequest() {
        DailyTrainTicketSaveReq request =
                new DailyTrainTicketSaveReq();
        request.setDate(LocalDate.of(2026, 8, 8));
        request.setTrainCode("G1");
        request.setStart("北京南");
        request.setStartPinyin("beijingnan");
        request.setStartTime(LocalTime.of(7, 0));
        request.setStartIndex(1);
        request.setEnd("上海虹桥");
        request.setEndPinyin("shanghaihongqiao");
        request.setEndTime(LocalTime.of(11, 30));
        request.setEndIndex(5);
        request.setYdz(120);
        request.setYdzPrice(new BigDecimal("933.00"));
        request.setEdz(500);
        request.setEdzPrice(new BigDecimal("553.00"));
        request.setRw(-1);
        request.setRwPrice(BigDecimal.ZERO);
        request.setYw(-1);
        request.setYwPrice(BigDecimal.ZERO);
        return request;
    }

    private DailyTrainTicketUpdateReq updateRequest() {
        DailyTrainTicketSaveReq saveRequest = saveRequest();
        DailyTrainTicketUpdateReq request =
                new DailyTrainTicketUpdateReq();
        request.setId(100L);
        request.setDate(saveRequest.getDate());
        request.setTrainCode(saveRequest.getTrainCode());
        request.setStart(saveRequest.getStart());
        request.setStartPinyin(saveRequest.getStartPinyin());
        request.setStartTime(saveRequest.getStartTime());
        request.setStartIndex(saveRequest.getStartIndex());
        request.setEnd(saveRequest.getEnd());
        request.setEndPinyin(saveRequest.getEndPinyin());
        request.setEndTime(saveRequest.getEndTime());
        request.setEndIndex(saveRequest.getEndIndex());
        request.setYdz(saveRequest.getYdz());
        request.setYdzPrice(saveRequest.getYdzPrice());
        request.setEdz(saveRequest.getEdz());
        request.setEdzPrice(saveRequest.getEdzPrice());
        request.setRw(saveRequest.getRw());
        request.setRwPrice(saveRequest.getRwPrice());
        request.setYw(saveRequest.getYw());
        request.setYwPrice(saveRequest.getYwPrice());
        return request;
    }

    private List<TrainStation> stations() {
        TrainStation beijing = station(
                1,
                "北京南",
                "beijingnan",
                LocalTime.of(7, 0),
                null,
                BigDecimal.ZERO
        );
        TrainStation jinan = station(
                2,
                "济南西",
                "jinanxi",
                LocalTime.of(8, 40),
                LocalTime.of(8, 35),
                new BigDecimal("100.10")
        );
        TrainStation nanjing = station(
                3,
                "南京南",
                "nanjingnan",
                null,
                LocalTime.of(10, 30),
                new BigDecimal("50.15")
        );
        return List.of(beijing, jinan, nanjing);
    }

    private TrainStation station(
            int index,
            String name,
            String namePinyin,
            LocalTime outTime,
            LocalTime inTime,
            BigDecimal km
    ) {
        TrainStation station = new TrainStation();
        station.setIndex(index);
        station.setName(name);
        station.setNamePinyin(namePinyin);
        station.setOutTime(outTime);
        station.setInTime(inTime);
        station.setKm(km);
        return station;
    }
}
