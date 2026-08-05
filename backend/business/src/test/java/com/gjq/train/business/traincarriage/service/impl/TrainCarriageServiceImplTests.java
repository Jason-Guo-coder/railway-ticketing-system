package com.gjq.train.business.traincarriage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.gjq.train.business.traincarriage.mapper.TrainCarriageMapper;
import com.gjq.train.business.traincarriage.req.TrainCarriageQueryReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageSaveReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageUpdateReq;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainCarriageServiceImplTests {

    @Mock
    private TrainCarriageMapper trainCarriageMapper;

    @InjectMocks
    private TrainCarriageServiceImpl trainCarriageService;

    @Test
    void shouldInsertCarriageAndCalculateSeatLayout() {
        when(trainCarriageMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        trainCarriageService.save(saveRequest());

        verify(trainCarriageMapper).insert(argThat(
                (TrainCarriage carriage) ->
                        "G1".equals(carriage.getTrainCode())
                                && Integer.valueOf(1).equals(
                                carriage.getIndex()
                        )
                                && Integer.valueOf(5).equals(
                                carriage.getColumnCount()
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
        TrainCarriageSaveReq request = saveRequest();
        request.setSeatType("9");

        assertThrows(
                BusinessException.class,
                () -> trainCarriageService.save(request)
        );

        verify(trainCarriageMapper, never()).insert(any(TrainCarriage.class));
    }

    @Test
    void shouldRejectDuplicateCarriageIndex() {
        when(trainCarriageMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> trainCarriageService.save(saveRequest())
        );

        verify(trainCarriageMapper, never()).insert(any(TrainCarriage.class));
    }

    @Test
    void shouldDeleteExistingCarriage() {
        when(trainCarriageMapper.deleteById(100L)).thenReturn(1);

        trainCarriageService.delete(100L);

        verify(trainCarriageMapper).deleteById(100L);
    }

    @Test
    void shouldRejectDeletingMissingCarriage() {
        when(trainCarriageMapper.deleteById(100L)).thenReturn(0);

        assertThrows(
                BusinessException.class,
                () -> trainCarriageService.delete(100L)
        );
    }

    @Test
    void shouldUpdateExistingCarriageAndRecalculateSeatLayout() {
        TrainCarriage carriage = new TrainCarriage();
        carriage.setId(100L);
        when(trainCarriageMapper.selectById(100L)).thenReturn(carriage);
        when(trainCarriageMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L);

        trainCarriageService.update(updateRequest());

        verify(trainCarriageMapper).updateById(argThat(
                (TrainCarriage updated) ->
                        Long.valueOf(100L).equals(updated.getId())
                                && Integer.valueOf(4).equals(
                                updated.getColumnCount()
                        )
                                && Integer.valueOf(72).equals(
                                updated.getSeatCount()
                        )
                                && updated.getCreateTime() == null
                                && updated.getUpdateTime() != null
        ));
    }

    @Test
    void shouldQueryCarriagePageByTrainCode() {
        TrainCarriageQueryReq request = new TrainCarriageQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setTrainCode("G1");

        TrainCarriage carriage = new TrainCarriage();
        carriage.setId(100L);
        carriage.setTrainCode("G1");
        when(trainCarriageMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<TrainCarriage> page = invocation.getArgument(0);
            page.setRecords(List.of(carriage));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = trainCarriageService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldListCarriagesByTrainCode() {
        TrainCarriage carriage = new TrainCarriage();
        carriage.setTrainCode("G1");
        when(trainCarriageMapper.selectList(any(Wrapper.class)))
                .thenReturn(List.of(carriage));

        List<TrainCarriage> result =
                trainCarriageService.listByTrainCode("G1");

        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getTrainCode());
    }

    private TrainCarriageSaveReq saveRequest() {
        TrainCarriageSaveReq request = new TrainCarriageSaveReq();
        request.setTrainCode("G1");
        request.setIndex(1);
        request.setSeatType("2");
        request.setRowCount(20);
        return request;
    }

    private TrainCarriageUpdateReq updateRequest() {
        TrainCarriageUpdateReq request = new TrainCarriageUpdateReq();
        request.setId(100L);
        request.setTrainCode("G1");
        request.setIndex(1);
        request.setSeatType("1");
        request.setRowCount(18);
        return request;
    }
}
