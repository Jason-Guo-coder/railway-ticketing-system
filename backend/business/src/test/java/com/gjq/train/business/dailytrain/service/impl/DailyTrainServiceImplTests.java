package com.gjq.train.business.dailytrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.dailytrain.entity.DailyTrain;
import com.gjq.train.business.dailytrain.mapper.DailyTrainMapper;
import com.gjq.train.business.dailytrain.req.DailyTrainQueryReq;
import com.gjq.train.business.dailytrain.req.DailyTrainSaveReq;
import com.gjq.train.business.dailytrain.req.DailyTrainUpdateReq;
import com.gjq.train.business.dailytraincarriage.service.DailyTrainCarriageService;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
import com.gjq.train.business.dailytrainstation.service.DailyTrainStationService;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.business.train.entity.Train;
import com.gjq.train.business.train.service.TrainService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class DailyTrainServiceImplTests {

    @Mock
    private DailyTrainMapper dailyTrainMapper;

    @Mock
    private TrainService trainService;

    @Mock
    private DailyTrainStationService dailyTrainStationService;

    @Mock
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Mock
    private DailyTrainSeatService dailyTrainSeatService;

    @Mock
    private DailyTrainTicketService dailyTrainTicketService;

    @InjectMocks
    private DailyTrainServiceImpl dailyTrainService;

    @Test
    void shouldInsertNewDailyTrain() {
        when(dailyTrainMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        dailyTrainService.save(saveRequest());

        verify(dailyTrainMapper).insert(argThat(
                (DailyTrain dailyTrain) ->
                        LocalDate.of(2026, 8, 7).equals(
                                dailyTrain.getDate()
                        )
                                && "G100".equals(dailyTrain.getCode())
                                && dailyTrain.getCreateTime() != null
                                && dailyTrain.getCreateTime().equals(
                                dailyTrain.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectUnsupportedTrainType() {
        DailyTrainSaveReq request = saveRequest();
        request.setType("Z");

        assertThrows(
                BusinessException.class,
                () -> dailyTrainService.save(request)
        );

        verify(dailyTrainMapper, never()).insert(any(DailyTrain.class));
    }

    @Test
    void shouldRejectDuplicateDateAndCode() {
        when(dailyTrainMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainService.save(saveRequest())
        );

        verify(dailyTrainMapper, never()).insert(any(DailyTrain.class));
    }

    @Test
    void shouldDeleteExistingDailyTrain() {
        when(dailyTrainMapper.deleteById(100L)).thenReturn(1);

        dailyTrainService.delete(100L);

        verify(dailyTrainMapper).deleteById(100L);
    }

    @Test
    void shouldRejectDeletingMissingDailyTrain() {
        when(dailyTrainMapper.deleteById(100L)).thenReturn(0);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainService.delete(100L)
        );
    }

    @Test
    void shouldUpdateExistingDailyTrain() {
        DailyTrain existing = new DailyTrain();
        existing.setId(100L);
        when(dailyTrainMapper.selectById(100L)).thenReturn(existing);
        when(dailyTrainMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        dailyTrainService.update(updateRequest());

        verify(dailyTrainMapper).updateById(argThat(
                (DailyTrain dailyTrain) ->
                        Long.valueOf(100L).equals(dailyTrain.getId())
                                && "G100".equals(dailyTrain.getCode())
                                && dailyTrain.getUpdateTime() != null
                                && dailyTrain.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryDailyTrainPage() {
        DailyTrainQueryReq request = new DailyTrainQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setCode("G100");

        DailyTrain dailyTrain = new DailyTrain();
        dailyTrain.setId(100L);
        dailyTrain.setCode("G100");
        when(dailyTrainMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<DailyTrain> page = invocation.getArgument(0);
            page.setRecords(List.of(dailyTrain));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = dailyTrainService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldGenerateDailyDataForEveryTrain() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        Train train = new Train();
        train.setId(200L);
        train.setCode("G1");
        train.setType("G");
        train.setStart("北京南");
        train.setStartPinyin("beijingnan");
        train.setStartTime(LocalTime.of(8, 0));
        train.setEnd("上海虹桥");
        train.setEndPinyin("shanghaihongqiao");
        train.setEndTime(LocalTime.of(12, 30));
        when(trainService.list(any(Wrapper.class)))
                .thenReturn(List.of(train));

        dailyTrainService.generate(date);

        verify(dailyTrainMapper).delete(any(Wrapper.class));
        verify(dailyTrainMapper).insert(argThat(
                (DailyTrain dailyTrain) -> dailyTrain.getId() == null
                        && date.equals(dailyTrain.getDate())
                        && "G1".equals(dailyTrain.getCode())
                        && dailyTrain.getCreateTime() != null
        ));
        verify(dailyTrainStationService)
                .generateByTrainCode(date, "G1");
        verify(dailyTrainCarriageService)
                .generateByTrainCode(date, "G1");
        verify(dailyTrainSeatService)
                .generateByTrainCode(date, "G1");
        verify(dailyTrainTicketService)
                .generateByTrainCode(date, "G1", "G");
    }

    @Test
    void shouldSkipScheduledGenerationWhenDateExists() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        when(dailyTrainMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        dailyTrainService.generateIfAbsent(date);

        verify(trainService, never()).list(any(Wrapper.class));
        verify(dailyTrainMapper, never()).delete(any(Wrapper.class));
    }

    @Test
    void shouldGenerateScheduledDataWhenDateIsAbsent() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        when(dailyTrainMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);
        when(trainService.list(any(Wrapper.class)))
                .thenReturn(List.of());

        dailyTrainService.generateIfAbsent(date);

        verify(trainService).list(any(Wrapper.class));
    }

    private DailyTrainSaveReq saveRequest() {
        DailyTrainSaveReq request = new DailyTrainSaveReq();
        request.setDate(LocalDate.of(2026, 8, 7));
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

    private DailyTrainUpdateReq updateRequest() {
        DailyTrainUpdateReq request = new DailyTrainUpdateReq();
        request.setId(100L);
        request.setDate(LocalDate.of(2026, 8, 7));
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
