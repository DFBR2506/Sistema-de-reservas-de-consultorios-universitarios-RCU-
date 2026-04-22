package co.edu.unimagdalena.RCU.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
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

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.DoctorScheduleService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DoctorScheduleController.class)
class DoctorScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorScheduleService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID doctorId;
    private UUID scheduleId;
    private CreateDoctorScheduleRequest createRequest;
    private DoctorScheduleResponse scheduleResponse;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        scheduleId = UUID.randomUUID();
        createRequest = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(17, 0));
        scheduleResponse = new DoctorScheduleResponse(scheduleId, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(17, 0), true);
    }

    @Test
    void testCreateDoctorScheduleSuccess() throws Exception {
        when(service.create(eq(doctorId), any(CreateDoctorScheduleRequest.class))).thenReturn(scheduleResponse);

        mockMvc.perform(post("/api/doctors/{doctorId}/schedules", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(scheduleId.toString()))
            .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));
    }

    @Test
    void testCreateDoctorScheduleWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateDoctorScheduleRequest(null, LocalTime.of(8, 0), LocalTime.of(17, 0));

        mockMvc.perform(post("/api/doctors/{doctorId}/schedules", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDoctorScheduleDoctorNotFound() throws Exception {
        when(service.create(eq(doctorId), any(CreateDoctorScheduleRequest.class)))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(post("/api/doctors/{doctorId}/schedules", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testListDoctorSchedulesSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(scheduleResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAllSchedules(eq(doctorId), any())).thenReturn(page);

        mockMvc.perform(get("/api/doctors/{doctorId}/schedules", doctorId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(scheduleId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testListDoctorSchedulesDoctorNotFound() throws Exception {
        when(service.getAllSchedules(eq(doctorId), any()))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/doctors/{doctorId}/schedules", doctorId))
            .andExpect(status().isNotFound());
    }
}