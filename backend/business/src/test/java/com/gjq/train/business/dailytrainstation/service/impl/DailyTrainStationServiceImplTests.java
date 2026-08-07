package com.gjq.train.business.dailytrainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.dailytrainstation.entity.DailyTrainStation;
import com.gjq.train.business.dailytrainstation.mapper.DailyTrainStationMapper;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationQueryReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationSaveReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationUpdateReq;
import com.gjq.train.business.trainstation.entity.TrainStation;
import com.gjq.train.business.trainstation.service.TrainStationService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyTrainStationServiceImplTests {

    @Mock
    private DailyTrainStationMapper dailyTrainStationMapper;

    @Mock
    private TrainStationService trainStationService;

    @InjectMocks
    private DailyTrainStationServiceImpl dailyTrainStationService;

    @Test
    void shouldInsertNewDailyTrainStation() {
        when(dailyTrainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        dailyTrainStationService.save(saveRequest());

        verify(dailyTrainStationMapper).insert(argThat(
                (DailyTrainStation station) ->
                        LocalDate.of(2026, 8, 7).equals(station.getDate())
                                && "G1".equals(station.getTrainCode())
                                && Integer.valueOf(2).equals(station.getIndex())
                                && station.getCreateTime() != null
                                && station.getCreateTime().equals(
                                station.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectDuplicateIndex() {
        when(dailyTrainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainStationService.save(saveRequest())
        );

        verify(dailyTrainStationMapper, never())
                .insert(any(DailyTrainStation.class));
    }

    @Test
    void shouldRejectDuplicateName() {
        when(dailyTrainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainStationService.save(saveRequest())
        );

        verify(dailyTrainStationMapper, never())
                .insert(any(DailyTrainStation.class));
    }

    @Test
    void shouldDeleteExistingDailyTrainStation() {
        when(dailyTrainStationMapper.deleteById(100L)).thenReturn(1);

        dailyTrainStationService.delete(100L);

        verify(dailyTrainStationMapper).deleteById(100L);
    }

    @Test
    void shouldRejectDeletingMissingDailyTrainStation() {
        when(dailyTrainStationMapper.deleteById(100L)).thenReturn(0);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainStationService.delete(100L)
        );
    }

    @Test
    void shouldUpdateExistingDailyTrainStation() {
        DailyTrainStation existing = new DailyTrainStation();
        existing.setId(100L);
        when(dailyTrainStationMapper.selectById(100L)).thenReturn(existing);
        when(dailyTrainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        dailyTrainStationService.update(updateRequest());

        verify(dailyTrainStationMapper).updateById(argThat(
                (DailyTrainStation station) ->
                        Long.valueOf(100L).equals(station.getId())
                                && "G1".equals(station.getTrainCode())
                                && station.getUpdateTime() != null
                                && station.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryDailyTrainStationPage() {
        DailyTrainStationQueryReq request = new DailyTrainStationQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");

        DailyTrainStation station = new DailyTrainStation();
        station.setId(100L);
        station.setTrainCode("G1");
        when(dailyTrainStationMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<DailyTrainStation> page = invocation.getArgument(0);
            page.setRecords(List.of(station));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = dailyTrainStationService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldGenerateDailyStationsFromBaseStations() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        TrainStation trainStation = new TrainStation();
        trainStation.setId(200L);
        trainStation.setTrainCode("G1");
        trainStation.setIndex(1);
        trainStation.setName("北京南");
        trainStation.setNamePinyin("beijingnan");
        trainStation.setKm(BigDecimal.ZERO);
        when(trainStationService.listByTrainCode("G1"))
                .thenReturn(List.of(trainStation));

        dailyTrainStationService.generateByTrainCode(date, "G1");

        verify(dailyTrainStationMapper).delete(any(Wrapper.class));
        verify(dailyTrainStationMapper).insert(argThat(
                (DailyTrainStation station) -> station.getId() == null
                        && date.equals(station.getDate())
                        && "G1".equals(station.getTrainCode())
                        && "北京南".equals(station.getName())
        ));
    }

    private DailyTrainStationSaveReq saveRequest() {
        DailyTrainStationSaveReq request =
                new DailyTrainStationSaveReq();
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");
        request.setIndex(2);
        request.setName("南京南");
        request.setNamePinyin("nanjingnan");
        request.setInTime(LocalTime.of(10, 0));
        request.setOutTime(LocalTime.of(10, 5));
        request.setStopTime(LocalTime.of(0, 5));
        request.setKm(new BigDecimal("300.50"));
        return request;
    }

    private DailyTrainStationUpdateReq updateRequest() {
        DailyTrainStationUpdateReq request =
                new DailyTrainStationUpdateReq();
        request.setId(100L);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");
        request.setIndex(2);
        request.setName("南京南");
        request.setNamePinyin("nanjingnan");
        request.setInTime(LocalTime.of(10, 0));
        request.setOutTime(LocalTime.of(10, 5));
        request.setStopTime(LocalTime.of(0, 5));
        request.setKm(new BigDecimal("300.50"));
        return request;
    }
}
