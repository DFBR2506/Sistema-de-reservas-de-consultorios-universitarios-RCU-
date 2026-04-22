package co.edu.unimagdalena.RCU.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
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

import co.edu.unimagdalena.RCU.api.dto.ReportDtos.DoctorProductivityResponse;
import co.edu.unimagdalena.RCU.api.dto.ReportDtos.NoShowPatientResponse;
import co.edu.unimagdalena.RCU.api.dto.ReportDtos.OfficeOccupancyResponse;
import co.edu.unimagdalena.RCU.services.ReportService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService service;


    private Instant startDate;
    private Instant endDate;

    @BeforeEach
    void setUp() {
        startDate = Instant.now().minusSeconds(86400);
        endDate = Instant.now();
    }

    @Test
    void testGetOfficeOccupancySuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(new OfficeOccupancyResponse(UUID.randomUUID(), "C-101", 5L)),
            PageRequest.of(0, 10), 1
        );
        when(service.getOfficeOccupancy(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/reports/office-occupancy")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].code").value("C-101"))
            .andExpect(jsonPath("$.content[0].totalAppointments").value(5));
    }

    @Test
    void testGetDoctorProductivitySuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(new DoctorProductivityResponse(UUID.randomUUID(), "John", "Doe", 10L)),
            PageRequest.of(0, 10), 1
        );
        when(service.getDoctorProductivity(any())).thenReturn(page);

        mockMvc.perform(get("/api/reports/doctor-productivity")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].firstName").value("John"))
            .andExpect(jsonPath("$.content[0].completedAppointments").value(10));
    }

    @Test
    void testGetNoShowPatientsSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(new NoShowPatientResponse(UUID.randomUUID(), "Jane", "Doe", 3L)),
            PageRequest.of(0, 10), 1
        );
        when(service.getNoShowPatients(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/reports/no-show-patients")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].firstName").value("Jane"))
            .andExpect(jsonPath("$.content[0].noShowCount").value(3));
    }

    @Test
    void testGetOfficeOccupancyEmptyResult() throws Exception {
        var page = new PageImpl<OfficeOccupancyResponse>(
            java.util.List.of(),
            PageRequest.of(0, 10), 0
        );
        when(service.getOfficeOccupancy(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/reports/office-occupancy")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0));
    }
}