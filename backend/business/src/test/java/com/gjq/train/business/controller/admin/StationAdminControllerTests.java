package com.gjq.train.business.controller.admin;

import com.gjq.train.business.req.StationQueryReq;
import com.gjq.train.business.req.StationSaveReq;
import com.gjq.train.business.req.StationUpdateReq;
import com.gjq.train.business.resp.StationQueryResp;
import com.gjq.train.business.service.StationService;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StationAdminControllerTests {

    private MockMvc mockMvc;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = mock(StationService.class);
        StationAdminController controller = new StationAdminController();
        ReflectionTestUtils.setField(
                controller,
                "stationService",
                stationService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveStation() throws Exception {
        mockMvc.perform(
                        post("/admin/station/save")
                                .contentType(APPLICATION_JSON)
                                .content(stationJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(stationService).save(argThat(
                (StationSaveReq request) ->
                        "南京南".equals(request.getName())
                                && "nanjingnan".equals(
                                request.getNamePinyin()
                        )
        ));
    }

    @Test
    void shouldDeleteStation() throws Exception {
        mockMvc.perform(delete("/admin/station/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(stationService).delete(100L);
    }

    @Test
    void shouldUpdateStation() throws Exception {
        mockMvc.perform(
                        post("/admin/station/update")
                                .contentType(APPLICATION_JSON)
                                .content(stationJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(stationService).update(argThat(
                (StationUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "南京南".equals(request.getName())
        ));
    }

    @Test
    void shouldQueryStationPage() throws Exception {
        PageResp<StationQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(stationService.queryList(any(StationQueryReq.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/admin/station/query-list")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(stationService).queryList(argThat(
                (StationQueryReq request) -> request.getPage() == 1
                        && request.getSize() == 10
        ));
    }

    private String stationJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "name": "南京南",
                  "namePinyin": "nanjingnan",
                  "namePy": "njn"
                }
                """.formatted(id);
    }
}
