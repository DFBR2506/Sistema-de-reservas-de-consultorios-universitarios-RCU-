package co.edu.unimagdalena.RCU.api.controllers;

import static org.mockito.ArgumentMatchers.any;
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

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.AppointmentTypeResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.CreateAppointmentTypeRequest;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.services.AppointmentTypeService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AppointmentTypeController.class)
class AppointmentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentTypeService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID appointmentTypeId;
    private CreateAppointmentTypeRequest createRequest;
    private AppointmentTypeResponse appointmentTypeResponse;

    @BeforeEach
    void setUp() {
        appointmentTypeId = UUID.randomUUID();
        createRequest = new CreateAppointmentTypeRequest("Consultation", "Regular consultation", 30);
        appointmentTypeResponse = new AppointmentTypeResponse(appointmentTypeId, "Consultation", "Regular consultation", 30, true);
    }

    @Test
    void testCreateAppointmentTypeSuccess() throws Exception {
        when(service.create(any(CreateAppointmentTypeRequest.class))).thenReturn(appointmentTypeResponse);

        mockMvc.perform(post("/api/appointment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(appointmentTypeId.toString()))
            .andExpect(jsonPath("$.name").value("Consultation"));
    }

    @Test
    void testCreateAppointmentTypeWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateAppointmentTypeRequest(null, "Regular consultation", 30);

        mockMvc.perform(post("/api/appointment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAppointmentTypeConflict() throws Exception {
        when(service.create(any(CreateAppointmentTypeRequest.class)))
            .thenThrow(new ConflictException("AppointmentType already exists"));

        mockMvc.perform(post("/api/appointment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    void testListAppointmentTypesSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(appointmentTypeResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/appointment-types")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(appointmentTypeId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }
}