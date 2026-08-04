package com.gjq.train.member.controller;

import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.member.req.PassengerSaveReq;
import com.gjq.train.member.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PassengerControllerTests {

    private MockMvc mockMvc;
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        passengerService = mock(PassengerService.class);
        PassengerController controller = new PassengerController();
        ReflectionTestUtils.setField(
                controller,
                "passengerService",
                passengerService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSavePassengerWithJsonBody() throws Exception {
        mockMvc.perform(
                        post("/passenger/save")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "张三",
                                          "idCard": "110101199001011234",
                                          "type": "1"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(passengerService).save(argThat(
                (PassengerSaveReq request) ->
                        request.getMemberId() == null
                                && "张三".equals(request.getName())
                                && "110101199001011234".equals(request.getIdCard())
                                && "1".equals(request.getType())
        ));
    }

}
