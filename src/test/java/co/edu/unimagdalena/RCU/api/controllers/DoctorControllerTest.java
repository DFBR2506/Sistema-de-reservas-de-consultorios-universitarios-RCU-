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

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.CreateDoctorRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.DoctorResponse;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.UpdateDoctorRequest;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.DoctorService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID doctorId;
    private UUID specialtyId;
    private CreateDoctorRequest createRequest;
    private DoctorResponse doctorResponse;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();
        createRequest = new CreateDoctorRequest(
            "John", "Doe", "3001234567", "john@email.com",
            DocumentType.CC, "123456789", Gender.MALE,
            "LIC-001", specialtyId
        );
        doctorResponse = new DoctorResponse(
            doctorId, "John", "Doe", "3001234567", "john@email.com",
            DocumentType.CC, "123456789", Gender.MALE,
            "LIC-001", specialtyId, true
        );
    }

    @Test
    void testCreateDoctorSuccess() throws Exception {
        when(service.create(any(CreateDoctorRequest.class))).thenReturn(doctorResponse);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(doctorId.toString()))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testCreateDoctorWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateDoctorRequest(
            null, "Doe", "3001234567", "john@email.com",
            DocumentType.CC, "123456789", Gender.MALE,
            "LIC-001", specialtyId
        );

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetDoctorByIdSuccess() throws Exception {
        when(service.getDoctorById(doctorId)).thenReturn(doctorResponse);

        mockMvc.perform(get("/api/doctors/{id}", doctorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(doctorId.toString()))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testGetDoctorByIdNotFound() throws Exception {
        when(service.getDoctorById(doctorId))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/doctors/{id}", doctorId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testListDoctorsSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(doctorResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAllDoctors(any())).thenReturn(page);

        mockMvc.perform(get("/api/doctors")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(doctorId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdateDoctorSuccess() throws Exception {
        var updateRequest = new UpdateDoctorRequest(
            "Jane", "Doe", "3001234567", "jane@email.com",
            DocumentType.CC, "123456789", Gender.FEMALE,
            "LIC-001", specialtyId, true
        );
        var updatedResponse = new DoctorResponse(
            doctorId, "Jane", "Doe", "3001234567", "jane@email.com",
            DocumentType.CC, "123456789", Gender.FEMALE,
            "LIC-001", specialtyId, true
        );
        when(service.updateDoctor(eq(doctorId), any(UpdateDoctorRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void testUpdateDoctorNotFound() throws Exception {
        var updateRequest = new UpdateDoctorRequest(
            "Jane", "Doe", "3001234567", "jane@email.com",
            DocumentType.CC, "123456789", Gender.FEMALE,
            "LIC-001", specialtyId, true
        );
        when(service.updateDoctor(eq(doctorId), any(UpdateDoctorRequest.class)))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(patch("/api/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }
}