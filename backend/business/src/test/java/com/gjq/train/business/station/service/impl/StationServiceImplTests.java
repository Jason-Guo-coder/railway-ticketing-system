package com.gjq.train.business.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.station.entity.Station;
import com.gjq.train.business.station.mapper.StationMapper;
import com.gjq.train.business.station.req.StationQueryReq;
import com.gjq.train.business.station.req.StationSaveReq;
import com.gjq.train.business.station.req.StationUpdateReq;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceImplTests {

    @Mock
    private StationMapper stationMapper;

    @InjectMocks
    private StationServiceImpl stationService;

    @Test
    void shouldInsertNewStation() {
        StationSaveReq request = saveRequest();
        when(stationMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        stationService.save(request);

        verify(stationMapper).insert(argThat(
                (Station station) ->
                        "南京南".equals(station.getName())
                                && "nanjingnan".equals(
                                station.getNamePinyin()
                        )
                                && station.getCreateTime() != null
                                && station.getCreateTime().equals(
                                station.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectDuplicateStationName() {
        when(stationMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> stationService.save(saveRequest())
        );

        verify(stationMapper, never()).insert(any(Station.class));
    }

    @Test
    void shouldDeleteExistingStation() {
        when(stationMapper.deleteById(100L)).thenReturn(1);

        stationService.delete(100L);

        verify(stationMapper).deleteById(100L);
    }

    @Test
    void shouldUpdateExistingStation() {
        Station station = new Station();
        station.setId(100L);
        when(stationMapper.selectById(100L)).thenReturn(station);
        when(stationMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        stationService.update(updateRequest());

        verify(stationMapper).updateById(argThat(
                (Station updated) ->
                        Long.valueOf(100L).equals(updated.getId())
                                && "南京南".equals(updated.getName())
                                && updated.getUpdateTime() != null
                                && updated.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryStationPage() {
        StationQueryReq request = new StationQueryReq();
        request.setPage(2);
        request.setSize(10);

        Station station = new Station();
        station.setId(100L);
        station.setName("南京南");
        when(stationMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<Station> page = invocation.getArgument(0);
            page.setRecords(List.of(station));
            page.setTotal(11);
            return page;
        });

        PageResp<?> response = stationService.queryList(request);

        assertEquals(11L, response.getTotal());
        assertEquals(1, response.getList().size());
        assertNotNull(response.getList().get(0));
    }

    @Test
    void shouldQueryAllStations() {
        Station station = new Station();
        station.setId(100L);
        station.setName("南京南");
        when(stationMapper.selectList(any(Wrapper.class)))
                .thenReturn(List.of(station));

        List<?> response = stationService.queryAll();

        assertEquals(1, response.size());
        assertNotNull(response.get(0));
    }

    @Test
    void shouldCheckStationExistsByName() {
        when(stationMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        boolean exists = stationService.existsByName("南京南");

        assertTrue(exists);
    }

    private StationSaveReq saveRequest() {
        StationSaveReq request = new StationSaveReq();
        request.setName("南京南");
        request.setNamePinyin("nanjingnan");
        request.setNamePy("njn");
        return request;
    }

    private StationUpdateReq updateRequest() {
        StationUpdateReq request = new StationUpdateReq();
        request.setId(100L);
        request.setName("南京南");
        request.setNamePinyin("nanjingnan");
        request.setNamePy("njn");
        return request;
    }
}
