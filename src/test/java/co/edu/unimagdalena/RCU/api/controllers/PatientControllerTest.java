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

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.CreatePatientRequest;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.PatientResponse;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.UpdatePatientRequest;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.PatientService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID patientId;
    private CreatePatientRequest createRequest;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        createRequest = new CreatePatientRequest(
            "John", "Doe", "john@email.com", "3001234567",
            DocumentType.CC, "123456789", Gender.MALE
        );
        patientResponse = new PatientResponse(
            patientId, "John", "Doe", "john@email.com", "3001234567",
            DocumentType.CC, "123456789", Gender.MALE, true
        );
    }

    @Test
    void testCreatePatientSuccess() throws Exception {
        when(service.create(any(CreatePatientRequest.class))).thenReturn(patientResponse);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(patientId.toString()))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testCreatePatientWithInvalidRequest() throws Exception {
        var invalidRequest = new CreatePatientRequest(
            null, "Doe", "john@email.com", "3001234567",
            DocumentType.CC, "123456789", Gender.MALE
        );

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientByIdSuccess() throws Exception {
        when(service.getById(patientId)).thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/{id}", patientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(patientId.toString()))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testGetPatientByIdNotFound() throws Exception {
        when(service.getById(patientId))
            .thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(get("/api/patients/{id}", patientId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testListPatientsSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(patientResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/patients")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(patientId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdatePatientSuccess() throws Exception {
        var updateRequest = new UpdatePatientRequest(
            "Jane", "Doe", "jane@email.com", "3001234567",
            DocumentType.CC, "123456789", Gender.FEMALE, true
        );
        var updatedResponse = new PatientResponse(
            patientId, "Jane", "Doe", "jane@email.com", "3001234567",
            DocumentType.CC, "123456789", Gender.FEMALE, true
        );
        when(service.update(eq(patientId), any(UpdatePatientRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void testUpdatePatientNotFound() throws Exception {
        var updateRequest = new UpdatePatientRequest(
            "Jane", "Doe", "jane@email.com", "3001234567",
            DocumentType.CC, "123456789", Gender.FEMALE, true
        );
        when(service.update(eq(patientId), any(UpdatePatientRequest.class)))
            .thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(patch("/api/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }
}