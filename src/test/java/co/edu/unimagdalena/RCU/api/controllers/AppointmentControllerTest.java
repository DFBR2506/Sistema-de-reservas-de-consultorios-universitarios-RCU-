package co.edu.unimagdalena.RCU.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
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

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.AppointmentResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CancelAppointmentRequest;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CreateAppointmentRequest;
import co.edu.unimagdalena.RCU.domine.entities.enums.*;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.AppointmentService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID appointmentId;
    private CreateAppointmentRequest createRequest;
    private AppointmentResponse appointmentResponse;

    private Instant futureStartAt() {
        return LocalDate.now().plusDays(1)
                .atTime(LocalTime.of(10, 0))
                .toInstant(ZoneOffset.UTC);
    }

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        var startAt = futureStartAt();
        createRequest = new CreateAppointmentRequest(
            UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), startAt
        );
        appointmentResponse = new AppointmentResponse(
            appointmentId,
            createRequest.patientId(),
            createRequest.doctorId(),
            createRequest.officeId(),
            createRequest.appointmentTypeId(),
            startAt,
            startAt.plusSeconds(1800),
            Status.SCHEDULED,
            null, null
        );
    }

    @Test
    void testCreateAppointmentSuccess() throws Exception {
        when(service.create(any(CreateAppointmentRequest.class))).thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(appointmentId.toString()))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void testCreateAppointmentWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateAppointmentRequest(
            null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), futureStartAt()
        );

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAppointmentByIdSuccess() throws Exception {
        when(service.getById(appointmentId)).thenReturn(appointmentResponse);

        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(appointmentId.toString()))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void testGetAppointmentByIdNotFound() throws Exception {
        when(service.getById(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testListAppointmentsSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(appointmentResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/appointments")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(appointmentId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testConfirmAppointmentSuccess() throws Exception {
        var confirmedResponse = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.startAt(), createRequest.startAt().plusSeconds(1800),
            Status.CONFIRMED, null, null
        );
        when(service.confirm(appointmentId)).thenReturn(confirmedResponse);

        mockMvc.perform(patch("/api/appointments/{id}/confirm", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testConfirmAppointmentNotFound() throws Exception {
        when(service.confirm(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/confirm", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCancelAppointmentSuccess() throws Exception {
        var cancelRequest = new CancelAppointmentRequest("Patient requested cancellation");
        var cancelledResponse = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.startAt(), createRequest.startAt().plusSeconds(1800),
            Status.CANCELLED, "Patient requested cancellation", null
        );
        when(service.cancel(eq(appointmentId), any(CancelAppointmentRequest.class))).thenReturn(cancelledResponse);

        mockMvc.perform(patch("/api/appointments/{id}/cancel", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testCancelAppointmentWithInvalidRequest() throws Exception {
        mockMvc.perform(patch("/api/appointments/{id}/cancel", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCompleteAppointmentSuccess() throws Exception {
        var completedResponse = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.startAt(), createRequest.startAt().plusSeconds(1800),
            Status.COMPLETED, null, null
        );
        when(service.complete(appointmentId)).thenReturn(completedResponse);

        mockMvc.perform(patch("/api/appointments/{id}/complete", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testCompleteAppointmentNotFound() throws Exception {
        when(service.complete(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/complete", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testMarkAsNoShowSuccess() throws Exception {
        var noShowResponse = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.startAt(), createRequest.startAt().plusSeconds(1800),
            Status.NO_SHOW, null, null
        );
        when(service.markAsNoShow(appointmentId)).thenReturn(noShowResponse);

        mockMvc.perform(patch("/api/appointments/{id}/no-show", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }

    @Test
    void testMarkAsNoShowNotFound() throws Exception {
        when(service.markAsNoShow(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/no-show", appointmentId))
            .andExpect(status().isNotFound());
    }
}