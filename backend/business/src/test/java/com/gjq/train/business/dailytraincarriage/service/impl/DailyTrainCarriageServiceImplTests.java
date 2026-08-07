package com.gjq.train.business.dailytraincarriage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.dailytraincarriage.entity.DailyTrainCarriage;
import com.gjq.train.business.dailytraincarriage.mapper.DailyTrainCarriageMapper;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageQueryReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageSaveReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageUpdateReq;
import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
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
class DailyTrainCarriageServiceImplTests {

    @Mock
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Mock
    private TrainCarriageService trainCarriageService;

    @InjectMocks
    private DailyTrainCarriageServiceImpl dailyTrainCarriageService;

    @Test
    void shouldInsertAndCalculateSeatLayout() {
        when(dailyTrainCarriageMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        dailyTrainCarriageService.save(saveRequest());

        verify(dailyTrainCarriageMapper).insert(argThat(
                (DailyTrainCarriage carriage) ->
                        LocalDate.of(2026, 8, 7).equals(carriage.getDate())
                                && "G1".equals(carriage.getTrainCode())
                                && Integer.valueOf(5).equals(
                                carriage.getColCount()
                        )
                                && Integer.valueOf(100).equals(
                                carriage.getSeatCount()
                        )
                                && carriage.getCreateTime() != null
                                && carriage.getCreateTime().equals(
                                carriage.getUpdateTime()
                        )
        ));
    }

    @Test
    void shouldRejectInvalidSeatType() {
        DailyTrainCarriageSaveReq request = saveRequest();
        request.setSeatType("9");

        assertThrows(
                BusinessException.class,
                () -> dailyTrainCarriageService.save(request)
        );

        verify(dailyTrainCarriageMapper, never())
                .insert(any(DailyTrainCarriage.class));
    }

    @Test
    void shouldRejectDuplicateIndex() {
        when(dailyTrainCarriageMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainCarriageService.save(saveRequest())
        );
    }

    @Test
    void shouldDeleteExistingCarriage() {
        when(dailyTrainCarriageMapper.deleteById(100L)).thenReturn(1);

        dailyTrainCarriageService.delete(100L);

        verify(dailyTrainCarriageMapper).deleteById(100L);
    }

    @Test
    void shouldRejectDeletingMissingCarriage() {
        when(dailyTrainCarriageMapper.deleteById(100L)).thenReturn(0);

        assertThrows(
                BusinessException.class,
                () -> dailyTrainCarriageService.delete(100L)
        );
    }

    @Test
    void shouldUpdateAndRecalculateSeatLayout() {
        DailyTrainCarriage existing = new DailyTrainCarriage();
        existing.setId(100L);
        when(dailyTrainCarriageMapper.selectById(100L))
                .thenReturn(existing);
        when(dailyTrainCarriageMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        dailyTrainCarriageService.update(updateRequest());

        verify(dailyTrainCarriageMapper).updateById(argThat(
                (DailyTrainCarriage carriage) ->
                        Long.valueOf(100L).equals(carriage.getId())
                                && Integer.valueOf(5).equals(
                                carriage.getColCount()
                        )
                                && Integer.valueOf(100).equals(
                                carriage.getSeatCount()
                        )
                                && carriage.getUpdateTime() != null
                                && carriage.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryByDateAndTrainCode() {
        DailyTrainCarriageQueryReq request =
                new DailyTrainCarriageQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");

        DailyTrainCarriage carriage = new DailyTrainCarriage();
        carriage.setId(100L);
        when(dailyTrainCarriageMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<DailyTrainCarriage> page = invocation.getArgument(0);
            page.setRecords(List.of(carriage));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = dailyTrainCarriageService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldGenerateDailyCarriagesFromBaseCarriages() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        TrainCarriage trainCarriage = new TrainCarriage();
        trainCarriage.setId(200L);
        trainCarriage.setTrainCode("G1");
        trainCarriage.setIndex(1);
        trainCarriage.setSeatType("2");
        trainCarriage.setRowCount(18);
        trainCarriage.setColumnCount(5);
        trainCarriage.setSeatCount(90);
        when(trainCarriageService.listByTrainCode("G1"))
                .thenReturn(List.of(trainCarriage));

        dailyTrainCarriageService.generateByTrainCode(date, "G1");

        verify(dailyTrainCarriageMapper).delete(any(Wrapper.class));
        verify(dailyTrainCarriageMapper).insert(argThat(
                (DailyTrainCarriage carriage) -> carriage.getId() == null
                        && date.equals(carriage.getDate())
                        && Integer.valueOf(5).equals(carriage.getColCount())
                        && Integer.valueOf(90).equals(carriage.getSeatCount())
        ));
    }

    private DailyTrainCarriageSaveReq saveRequest() {
        DailyTrainCarriageSaveReq request =
                new DailyTrainCarriageSaveReq();
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");
        request.setIndex(1);
        request.setSeatType("2");
        request.setRowCount(20);
        return request;
    }

    private DailyTrainCarriageUpdateReq updateRequest() {
        DailyTrainCarriageUpdateReq request =
                new DailyTrainCarriageUpdateReq();
        request.setId(100L);
        request.setDate(LocalDate.of(2026, 8, 7));
        request.setTrainCode("G1");
        request.setIndex(1);
        request.setSeatType("2");
        request.setRowCount(20);
        return request;
    }
}
