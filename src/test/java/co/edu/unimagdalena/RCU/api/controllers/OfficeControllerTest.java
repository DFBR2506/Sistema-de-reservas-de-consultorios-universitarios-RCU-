package co.edu.unimagdalena.RCU.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.CreateOfficeRequest;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.OfficeResponse;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.UpdateOfficeRequest;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.OfficeService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(OfficeController.class)
class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfficeService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID officeId;
    private CreateOfficeRequest createRequest;
    private OfficeResponse officeResponse;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();
        createRequest = new CreateOfficeRequest("C-101", 1);
        officeResponse = new OfficeResponse(officeId, "C-101", 1, true);
    }

    @Test
    void testCreateOfficeSuccess() throws Exception {
        when(service.create(any(CreateOfficeRequest.class))).thenReturn(officeResponse);

        mockMvc.perform(post("/api/offices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(officeId.toString()))
            .andExpect(jsonPath("$.code").value("C-101"));
    }

    @Test
    void testCreateOfficeWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateOfficeRequest(null, 1);

        mockMvc.perform(post("/api/offices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testListOfficesSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(officeResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/offices")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(officeId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdateOfficeSuccess() throws Exception {
        var updateRequest = new UpdateOfficeRequest("C-102", 2, true);
        var updatedResponse = new OfficeResponse(officeId, "C-102", 2, true);
        when(service.update(eq(officeId), any(UpdateOfficeRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/offices/{id}", officeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("C-102"));
    }

    @Test
    void testUpdateOfficeNotFound() throws Exception {
        var updateRequest = new UpdateOfficeRequest("C-102", 2, true);
        when(service.update(eq(officeId), any(UpdateOfficeRequest.class)))
            .thenThrow(new ResourceNotFoundException("Office not found"));

        mockMvc.perform(patch("/api/offices/{id}", officeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }
}