package com.gjq.train.business.dailytrainseat.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import com.gjq.train.business.dailytrainseat.mapper.DailyTrainSeatMapper;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatQueryReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatSaveReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatUpdateReq;
import com.gjq.train.business.trainseat.entity.TrainSeat;
import com.gjq.train.business.trainseat.service.TrainSeatService;
import com.gjq.train.business.trainstation.entity.TrainStation;
import com.gjq.train.business.trainstation.service.TrainStationService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyTrainSeatServiceImplTests {

    @Mock
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Mock
    private TrainSeatService trainSeatService;

    @Mock
    private TrainStationService trainStationService;

    @InjectMocks
    private DailyTrainSeatServiceImpl dailyTrainSeatService;

    @Test
    void shouldInsertDailyTrainSeat() {
        when(dailyTrainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        dailyTrainSeatService.save(saveRequest());

        verify(dailyTrainSeatMapper).insert(argThat(
                (DailyTrainSeat seat) ->
                        LocalDate.of(2026, 8, 7).equals(seat.getDate())
                                && "G1".equals(seat.getTrainCode())
                                && "01".equals(seat.getRow())
                                && "000".equals(seat.getSell())
                                && seat.getCreateTime() != null
                                && seat.getCreateTime().equals(
                                seat.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectInvalidSeatType() {
        DailyTrainSeatSaveReq request = saveRequest();
        request.setSeatType("9");

        assertThrows(
                BusinessException.class,
                () -> dailyTrainSeatService.save(request)
        );
    }

    @Test
    void shouldRejectColumnNotSupportedBySeatType() {
        DailyTrainSeatSaveReq request = saveRequest();
        request.setCol("B");

        assertThrows(
                BusinessException.class,
                () -> dailyTrainSeatService.save(request)
        );
    }

    @Test
    void shouldRejectDuplicateLocation() {
        when(dailyTrainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainSeatService.save(saveRequest())
        );

        verify(dailyTrainSeatMapper, never())
                .insert(any(DailyTrainSeat.class));
    }

    @Test
    void shouldRejectDuplicateSeatIndex() {
        when(dailyTrainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainSeatService.save(saveRequest())
        );
    }

    @Test
    void shouldDeleteExistingSeat() {
        when(dailyTrainSeatMapper.deleteById(100L)).thenReturn(1);

        dailyTrainSeatService.delete(100L);

        verify(dailyTrainSeatMapper).deleteById(100L);
    }

    @Test
    void shouldRejectDeletingMissingSeat() {
        when(dailyTrainSeatMapper.deleteById(100L)).thenReturn(0);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainSeatService.delete(100L)
        );
    }

    @Test
    void shouldUpdateExistingSeat() {
        DailyTrainSeat existing = new DailyTrainSeat();
        existing.setId(100L);
        when(dailyTrainSeatMapper.selectById(100L)).thenReturn(existing);
        when(dailyTrainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        dailyTrainSeatService.update(updateRequest());

        verify(dailyTrainSeatMapper).updateById(argThat(
                (DailyTrainSeat seat) ->
                        Long.valueOf(100L).equals(seat.getId())
                                && "A".equals(seat.getCol())
                                && seat.getUpdateTime() != null
                                && seat.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryByDateAndTrainCode() {
        DailyTrainSeatQueryReq request = new DailyTrainSeatQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");

        DailyTrainSeat seat = new DailyTrainSeat();
        seat.setId(100L);
        when(dailyTrainSeatMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<DailyTrainSeat> page = invocation.getArgument(0);
            page.setRecords(List.of(seat));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = dailyTrainSeatService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldGenerateDailySeatsWithSellSegments() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        TrainSeat trainSeat = new TrainSeat();
        trainSeat.setId(200L);
        trainSeat.setTrainCode("G1");
        trainSeat.setCarriageIndex(1);
        trainSeat.setRow("01");
        trainSeat.setCol("A");
        trainSeat.setSeatType("1");
        trainSeat.setCarriageSeatIndex(1);
        when(trainStationService.listByTrainCode("G1")).thenReturn(List.of(
                new TrainStation(),
                new TrainStation(),
                new TrainStation(),
                new TrainStation()
        ));
        when(trainSeatService.listByTrainCode("G1"))
                .thenReturn(List.of(trainSeat));

        dailyTrainSeatService.generateByTrainCode(date, "G1");

        verify(dailyTrainSeatMapper).delete(any(Wrapper.class));
        verify(dailyTrainSeatMapper).insert(argThat(
                (DailyTrainSeat seat) -> seat.getId() == null
                        && date.equals(seat.getDate())
                        && "000".equals(seat.getSell())
                        && "01".equals(seat.getRow())
        ));
    }

    @Test
    void shouldCountSeatsByDateTrainAndType() {
        when(dailyTrainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(12L, 0L);

        assertEquals(
                12,
                dailyTrainSeatService.countSeat(
                        LocalDate.of(2026, 8, 8),
                        "G1",
                        "1"
                )
        );
        assertEquals(
                -1,
                dailyTrainSeatService.countSeat(
                        LocalDate.of(2026, 8, 8),
                        "G1",
                        "2"
                )
        );
    }

    private DailyTrainSeatSaveReq saveRequest() {
        DailyTrainSeatSaveReq request = new DailyTrainSeatSaveReq();
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");
        request.setCarriageIndex(1);
        request.setRow("01");
        request.setCol("A");
        request.setSeatType("1");
        request.setCarriageSeatIndex(1);
        request.setSell("000");
        return request;
    }

    private DailyTrainSeatUpdateReq updateRequest() {
        DailyTrainSeatUpdateReq request = new DailyTrainSeatUpdateReq();
        request.setId(100L);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");
        request.setCarriageIndex(1);
        request.setRow("01");
        request.setCol("A");
        request.setSeatType("1");
        request.setCarriageSeatIndex(1);
        request.setSell("000");
        return request;
    }
}
