package com.gjq.train.business.trainseat.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.trainseat.entity.TrainSeat;
import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
import com.gjq.train.business.trainseat.mapper.TrainSeatMapper;
import com.gjq.train.business.trainseat.req.TrainSeatQueryReq;
import com.gjq.train.business.trainseat.req.TrainSeatSaveReq;
import com.gjq.train.business.trainseat.req.TrainSeatUpdateReq;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class TrainSeatServiceImplTests {

    @Mock
    private TrainSeatMapper trainSeatMapper;

    @Mock
    private TrainCarriageService trainCarriageService;

    @InjectMocks
    private TrainSeatServiceImpl trainSeatService;

    @Test
    void shouldInsertNewTrainSeat() {
        when(trainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        trainSeatService.save(saveRequest());

        verify(trainSeatMapper).insert(argThat(
                (TrainSeat seat) -> "G1".equals(seat.getTrainCode())
                        && Integer.valueOf(1).equals(
                        seat.getCarriageIndex()
                )
                        && "01".equals(seat.getRow())
                        && "B".equals(seat.getCol())
                        && seat.getCreateTime() != null
                        && seat.getCreateTime().equals(seat.getUpdateTime())
        ));
    }

    @Test
    void shouldRejectColumnOutsideSeatType() {
        TrainSeatSaveReq request = saveRequest();
        request.setSeatType("1");

        assertThrows(
                BusinessException.class,
                () -> trainSeatService.save(request)
        );

        verify(trainSeatMapper, never()).insert(any(TrainSeat.class));
    }

    @Test
    void shouldRejectDuplicateSeatLocation() {
        when(trainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(1L);

        assertThrows(
                BusinessException.class,
                () -> trainSeatService.save(saveRequest())
        );

        verify(trainSeatMapper, never()).insert(any(TrainSeat.class));
    }

    @Test
    void shouldRejectDuplicateCarriageSeatIndex() {
        when(trainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 1L);

        assertThrows(
                BusinessException.class,
                () -> trainSeatService.save(saveRequest())
        );

        verify(trainSeatMapper, never()).insert(any(TrainSeat.class));
    }

    @Test
    void shouldDeleteExistingTrainSeat() {
        when(trainSeatMapper.deleteById(100L)).thenReturn(1);

        trainSeatService.delete(100L);

        verify(trainSeatMapper).deleteById(100L);
    }

    @Test
    void shouldUpdateExistingTrainSeat() {
        TrainSeat trainSeat = new TrainSeat();
        trainSeat.setId(100L);
        when(trainSeatMapper.selectById(100L)).thenReturn(trainSeat);
        when(trainSeatMapper.selectCount(any(Wrapper.class)))
                .thenReturn(0L, 0L);

        trainSeatService.update(updateRequest());

        verify(trainSeatMapper).updateById(argThat(
                (TrainSeat updated) -> Long.valueOf(100L).equals(
                        updated.getId()
                )
                        && "C".equals(updated.getCol())
                        && updated.getUpdateTime() != null
                        && updated.getCreateTime() == null
        ));
    }

    @Test
    void shouldQueryTrainSeatPageByTrainCode() {
        TrainSeatQueryReq request = new TrainSeatQueryReq();
        request.setPage(1);
        request.setSize(10);
        request.setTrainCode("G1");

        TrainSeat trainSeat = new TrainSeat();
        trainSeat.setId(100L);
        trainSeat.setTrainCode("G1");
        when(trainSeatMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<TrainSeat> page = invocation.getArgument(0);
            page.setRecords(List.of(trainSeat));
            page.setTotal(1);
            return page;
        });

        PageResp<?> response = trainSeatService.queryList(request);

        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    void shouldRegenerateSeatsForEveryCarriage() {
        TrainCarriage firstClass = carriage(1, "1", 1);
        TrainCarriage secondClass = carriage(2, "2", 1);
        when(trainCarriageService.listByTrainCode("G1"))
                .thenReturn(List.of(firstClass, secondClass));

        trainSeatService.generateByTrainCode("G1");

        verify(trainSeatMapper).delete(any(Wrapper.class));
        verify(trainCarriageService).listByTrainCode("G1");
        ArgumentCaptor<TrainSeat> captor =
                ArgumentCaptor.forClass(TrainSeat.class);
        verify(trainSeatMapper, times(9)).insert(captor.capture());
        List<TrainSeat> seats = captor.getAllValues();
        assertEquals(List.of("A", "C", "D", "F"), seats.subList(0, 4)
                .stream().map(TrainSeat::getCol).toList());
        assertEquals(List.of("A", "B", "C", "D", "F"), seats.subList(4, 9)
                .stream().map(TrainSeat::getCol).toList());
        assertEquals(1, seats.get(0).getCarriageSeatIndex());
        assertEquals(1, seats.get(4).getCarriageSeatIndex());
        assertEquals("01", seats.get(0).getRow());
    }

    @Test
    void shouldRejectGeneratingSeatsWithoutCarriage() {
        when(trainCarriageService.listByTrainCode("G1"))
                .thenReturn(List.of());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainSeatService.generateByTrainCode("G1")
        );

        assertEquals(
                BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_EMPTY,
                exception.getExceptionEnum()
        );
        verify(trainSeatMapper, never()).delete(any(Wrapper.class));
        verify(trainSeatMapper, never()).insert(any(TrainSeat.class));
    }

    @Test
    void shouldListAllSeatsByTrainCode() {
        TrainSeat trainSeat = new TrainSeat();
        trainSeat.setTrainCode("G1");
        when(trainSeatMapper.selectList(any(Wrapper.class)))
                .thenReturn(List.of(trainSeat));

        List<TrainSeat> result = trainSeatService.listByTrainCode("G1");

        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getTrainCode());
    }

    private TrainSeatSaveReq saveRequest() {
        TrainSeatSaveReq request = new TrainSeatSaveReq();
        request.setTrainCode("G1");
        request.setCarriageIndex(1);
        request.setRow("01");
        request.setCol("B");
        request.setSeatType("2");
        request.setCarriageSeatIndex(2);
        return request;
    }

    private TrainSeatUpdateReq updateRequest() {
        TrainSeatUpdateReq request = new TrainSeatUpdateReq();
        request.setId(100L);
        request.setTrainCode("G1");
        request.setCarriageIndex(1);
        request.setRow("01");
        request.setCol("C");
        request.setSeatType("2");
        request.setCarriageSeatIndex(3);
        return request;
    }

    private TrainCarriage carriage(
            int index,
            String seatType,
            int rowCount
    ) {
        TrainCarriage carriage = new TrainCarriage();
        carriage.setTrainCode("G1");
        carriage.setIndex(index);
        carriage.setSeatType(seatType);
        carriage.setRowCount(rowCount);
        return carriage;
    }
}
