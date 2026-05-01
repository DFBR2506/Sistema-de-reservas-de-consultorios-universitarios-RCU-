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

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.SpecialtyResponse;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.services.SpecialtyService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService service;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID specialtyId;
    private CreateSpecialtyRequest createRequest;
    private SpecialtyResponse specialtyResponse;

    @BeforeEach
    void setUp() {
        specialtyId = UUID.randomUUID();
        createRequest = new CreateSpecialtyRequest("Cardiology", "Heart specialist");
        specialtyResponse = new SpecialtyResponse(specialtyId, "Cardiology", "Heart specialist", true);
    }

    @Test
    void testCreateSpecialtySuccess() throws Exception {
        when(service.create(any(CreateSpecialtyRequest.class))).thenReturn(specialtyResponse);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(specialtyId.toString()))
            .andExpect(jsonPath("$.name").value("Cardiology"));
    }

    @Test
    void testCreateSpecialtyWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateSpecialtyRequest(null, "Heart specialist");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSpecialtyConflict() throws Exception {
        when(service.create(any(CreateSpecialtyRequest.class)))
            .thenThrow(new ConflictException("Specialty already exists"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    void testListSpecialtiesSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(specialtyResponse),
            PageRequest.of(0, 10), 1
        );
        when(service.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/specialties")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(specialtyId.toString()))
            .andExpect(jsonPath("$.totalElements").value(1));
    }
}