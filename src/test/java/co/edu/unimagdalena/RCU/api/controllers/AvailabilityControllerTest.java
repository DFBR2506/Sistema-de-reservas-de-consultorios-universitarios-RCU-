package co.edu.unimagdalena.RCU.api.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import co.edu.unimagdalena.RCU.api.dto.AvailabilityDtos.AvailabilitySlotResponse;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.AvailabilityService;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService service;


    private UUID doctorId;
    private UUID officeId;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        officeId = UUID.randomUUID();
        date = LocalDate.now().plusDays(1);
    }

    @Test
    void testGetAvailabilitySuccess() throws Exception {
        var slots = List.of(
            new AvailabilitySlotResponse(Instant.now().plusSeconds(3600), Instant.now().plusSeconds(5400)),
            new AvailabilitySlotResponse(Instant.now().plusSeconds(5400), Instant.now().plusSeconds(7200))
        );
        when(service.getAvailableSlots(eq(doctorId), eq(officeId), eq(date))).thenReturn(slots);

        mockMvc.perform(get("/api/doctors/{doctorId}/availability", doctorId)
                .param("officeId", officeId.toString())
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAvailabilityDoctorNotFound() throws Exception {
        when(service.getAvailableSlots(eq(doctorId), eq(officeId), eq(date)))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/doctors/{doctorId}/availability", doctorId)
                .param("officeId", officeId.toString())
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAvailabilityNoScheduleForDay() throws Exception {
        when(service.getAvailableSlots(eq(doctorId), eq(officeId), eq(date)))
            .thenThrow(new ResourceNotFoundException("Doctor has no schedule for this day"));

        mockMvc.perform(get("/api/doctors/{doctorId}/availability", doctorId)
                .param("officeId", officeId.toString())
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAvailabilityEmptySlots() throws Exception {
        when(service.getAvailableSlots(eq(doctorId), eq(officeId), eq(date))).thenReturn(List.of());

        mockMvc.perform(get("/api/doctors/{doctorId}/availability", doctorId)
                .param("officeId", officeId.toString())
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}