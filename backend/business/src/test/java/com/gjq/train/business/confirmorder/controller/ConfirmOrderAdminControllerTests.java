package com.gjq.train.business.confirmorder.controller;

import com.gjq.train.business.confirmorder.req.ConfirmOrderQueryReq;
import com.gjq.train.business.confirmorder.resp.ConfirmOrderQueryResp;
import com.gjq.train.business.confirmorder.service.ConfirmOrderService;
import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfirmOrderAdminControllerTests {

    private MockMvc mockMvc;

    private ConfirmOrderService confirmOrderService;

    @BeforeEach
    void setUp() {
        confirmOrderService = mock(ConfirmOrderService.class);
        ConfirmOrderAdminController controller =
                new ConfirmOrderAdminController();
        ReflectionTestUtils.setField(
                controller,
                "confirmOrderService",
                confirmOrderService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldQueryConfirmOrderPage() throws Exception {
        ConfirmOrderQueryResp order = new ConfirmOrderQueryResp();
        order.setId(100L);
        order.setStatus("I");
        PageResp<ConfirmOrderQueryResp> response = new PageResp<>();
        response.setTotal(1L);
        response.setList(List.of(order));
        when(confirmOrderService.queryList(
                any(ConfirmOrderQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/confirm-order/query-list")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.total").value(1))
                .andExpect(jsonPath("$.content.list[0].id").value("100"))
                .andExpect(jsonPath("$.content.list[0].status").value("I"));

        verify(confirmOrderService).queryList(argThat(
                (ConfirmOrderQueryReq request) ->
                        Integer.valueOf(1).equals(request.getPage())
                                && Integer.valueOf(10).equals(
                                request.getSize()
                        )
        ));
    }
}
