package com.gjq.train.batch.job.controller;

import com.gjq.train.batch.job.service.CronJobService;
import com.gjq.train.common.controller.ControllerExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Quartz任务管理Controller测试，验证请求参数校验能够生效。
 */
class CronJobAdminControllerTests {

    @Test
    void shouldRejectInvalidAddRequest() throws Exception {
        CronJobService service = mock(CronJobService.class);
        CronJobAdminController controller =
                new CronJobAdminController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();

        mockMvc.perform(post("/admin/job/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
