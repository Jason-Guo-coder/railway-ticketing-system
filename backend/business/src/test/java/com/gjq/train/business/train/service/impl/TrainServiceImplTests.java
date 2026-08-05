package com.gjq.train.business.train.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.train.entity.Train;
import com.gjq.train.business.train.mapper.TrainMapper;
import com.gjq.train.business.train.req.TrainQueryReq;
import com.gjq.train.business.train.req.TrainSaveReq;
import com.gjq.train.business.train.req.TrainUpdateReq;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class TrainServiceImplTests {

    @Mock
    private TrainMapper trainMapper;

    @InjectMocks
    private TrainServiceImpl trainService;

    @Test
    void shouldInsertNewTrain() {
        when(trainMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        trainService.save(saveRequest());

        verify(trainMapper).insert(argThat(
                (Train train) ->
                        "G100".equals(train.getCode())
                                && "G".equals(train.getType())
                                && LocalTime.of(8, 0).equals(
                                train.getStartTime()
                        )
                                && train.getCreateTime() != null
                                && train.getCreateTime().equals(
                                train.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectUnsupportedTrainType() {
        TrainSaveReq request = saveRequest();
        request.setType("Z");

        assertThrows(
                BusinessException.class,
                () -> trainService.save(request)
        );

        verify(trainMapper, never()).insert(any(Train.class));
    }

    @Test
    void shouldRejectDuplicateTrainCode() {
        when(trainMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> trainService.save(saveRequest())
        );

        verify(trainMapper, never()).insert(any(Train.class));
    }

    @Test
    void shouldDeleteExistingTrain() {
        when(trainMapper.deleteById(100L)).thenReturn(1);

        trainService.delete(100L);

        verify(trainMapper).deleteById(100L);
    }

    @Test
    void shouldUpdateExistingTrain() {
        Train train = new Train();
        train.setId(100L);
        when(trainMapper.selectById(100L)).thenReturn(train);
        when(trainMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        trainService.update(updateRequest());

        verify(trainMapper).updateById(argThat(
                (Train updated) ->
                        Long.valueOf(100L).equals(updated.getId())
                                && "G100".equals(updated.getCode())
                                && updated.getUpdateTime() != null
                                && updated.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryTrainPage() {
        TrainQueryReq request = new TrainQueryReq();
        request.setPage(2);
        request.setSize(10);

        Train train = new Train();
        train.setId(100L);
        train.setCode("G100");
        when(trainMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<Train> page = invocation.getArgument(0);
            page.setRecords(List.of(train));
            page.setTotal(11);
            return page;
        });

        PageResp<?> response = trainService.queryList(request);

        assertEquals(11L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldQueryAllTrains() {
        Train train = new Train();
        train.setId(100L);
        train.setCode("G100");
        when(trainMapper.selectList(any(Wrapper.class)))
                .thenReturn(List.of(train));

        List<?> response = trainService.queryAll();

        assertEquals(1, response.size());
    }

    private TrainSaveReq saveRequest() {
        TrainSaveReq request = new TrainSaveReq();
        request.setCode("G100");
        request.setType("G");
        request.setStart("北京南");
        request.setStartPinyin("beijingnan");
        request.setStartTime(LocalTime.of(8, 0));
        request.setEnd("上海虹桥");
        request.setEndPinyin("shanghaihongqiao");
        request.setEndTime(LocalTime.of(12, 30));
        return request;
    }

    private TrainUpdateReq updateRequest() {
        TrainUpdateReq request = new TrainUpdateReq();
        request.setId(100L);
        request.setCode("G100");
        request.setType("G");
        request.setStart("北京南");
        request.setStartPinyin("beijingnan");
        request.setStartTime(LocalTime.of(8, 0));
        request.setEnd("上海虹桥");
        request.setEndPinyin("shanghaihongqiao");
        request.setEndTime(LocalTime.of(12, 30));
        return request;
    }
}
