package com.gjq.train.business.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.station.service.StationService;
import com.gjq.train.business.train.service.TrainService;
import com.gjq.train.business.trainstation.entity.TrainStation;
import com.gjq.train.business.trainstation.mapper.TrainStationMapper;
import com.gjq.train.business.trainstation.req.TrainStationQueryReq;
import com.gjq.train.business.trainstation.req.TrainStationSaveReq;
import com.gjq.train.business.trainstation.req.TrainStationUpdateReq;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class TrainStationServiceImplTests {

    @Mock
    private TrainStationMapper trainStationMapper;

    @Mock
    private TrainService trainService;

    @Mock
    private StationService stationService;

    @InjectMocks
    private TrainStationServiceImpl trainStationService;

    @Test
    void shouldInsertNewTrainStation() {
        mockReferencesExist();
        when(trainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        trainStationService.save(saveRequest());

        verify(trainStationMapper).insert(argThat(
                (TrainStation trainStation) ->
                        "G1".equals(trainStation.getTrainCode())
                                && Integer.valueOf(2).equals(
                                trainStation.getIndex()
                        )
                                && new BigDecimal("300.50").equals(
                                trainStation.getKm()
                        )
                                && trainStation.getCreateTime() != null
                                && trainStation.getCreateTime().equals(
                                trainStation.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectDuplicateIndex() {
        mockReferencesExist();
        when(trainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> trainStationService.save(saveRequest())
        );

        verify(trainStationMapper, never()).insert(any(TrainStation.class));
    }

    @Test
    void shouldRejectDuplicateName() {
        mockReferencesExist();
        when(trainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 1L);

        assertThrows(
                BusinessException.class,
                () -> trainStationService.save(saveRequest())
        );

        verify(trainStationMapper, never()).insert(any(TrainStation.class));
    }

    @Test
    void shouldRejectTrainStationWithMissingTrain() {
        when(trainService.existsByCode("G1")).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> trainStationService.save(saveRequest())
        );

        verify(trainStationMapper, never()).insert(any(TrainStation.class));
    }

    @Test
    void shouldRejectTrainStationWithMissingStation() {
        when(trainService.existsByCode("G1")).thenReturn(true);
        when(stationService.existsByName("南京南")).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> trainStationService.save(saveRequest())
        );

        verify(trainStationMapper, never()).insert(any(TrainStation.class));
    }

    @Test
    void shouldDeleteExistingTrainStation() {
        when(trainStationMapper.deleteById(100L)).thenReturn(1);

        trainStationService.delete(100L);

        verify(trainStationMapper).deleteById(100L);
    }

    @Test
    void shouldUpdateExistingTrainStation() {
        TrainStation trainStation = new TrainStation();
        trainStation.setId(100L);
        when(trainStationMapper.selectById(100L)).thenReturn(trainStation);
        mockReferencesExist();
        when(trainStationMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        trainStationService.update(updateRequest());

        verify(trainStationMapper).updateById(argThat(
                (TrainStation updated) ->
                        Long.valueOf(100L).equals(updated.getId())
                                && "G1".equals(updated.getTrainCode())
                                && updated.getUpdateTime() != null
                                && updated.getCreateTime() == null
        ));
    }

    @Test
    void shouldRejectUpdatingTrainStationWithMissingTrain() {
        TrainStation trainStation = new TrainStation();
        trainStation.setId(100L);
        when(trainStationMapper.selectById(100L)).thenReturn(trainStation);
        when(trainService.existsByCode("G1")).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> trainStationService.update(updateRequest())
        );

        verify(trainStationMapper, never())
                .updateById(any(TrainStation.class));
    }

    @Test
    void shouldQueryTrainStationPageByTrainCode() {
        TrainStationQueryReq request = new TrainStationQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setTrainCode("G1");

        TrainStation trainStation = new TrainStation();
        trainStation.setId(100L);
        trainStation.setTrainCode("G1");
        when(trainStationMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<TrainStation> page = invocation.getArgument(0);
            page.setRecords(List.of(trainStation));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = trainStationService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    private void mockReferencesExist() {
        when(trainService.existsByCode("G1")).thenReturn(true);
        when(stationService.existsByName("南京南")).thenReturn(true);
    }

    private TrainStationSaveReq saveRequest() {
        TrainStationSaveReq request = new TrainStationSaveReq();
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

    private TrainStationUpdateReq updateRequest() {
        TrainStationUpdateReq request = new TrainStationUpdateReq();
        request.setId(100L);
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
