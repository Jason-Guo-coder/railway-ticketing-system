package com.gjq.train.business.admin.controller;

import com.gjq.train.common.controller.ControllerExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AdminController controller = new AdminController(
                "admin",
                "admin123",
                "admin-controller-test-secret"
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldLoginWithConfiguredAccount() throws Exception {
        mockMvc.perform(
                        post("/admin/login")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "admin",
                                          "password": "admin123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.username").value("admin"))
                .andExpect(jsonPath("$.content.token").isNotEmpty());
    }

    @Test
    void shouldRejectWrongPassword() throws Exception {
        mockMvc.perform(
                        post("/admin/login")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "admin",
                                          "password": "wrong"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("管理员账号或密码错误"));
    }
}
