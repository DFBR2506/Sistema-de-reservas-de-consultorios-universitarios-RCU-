package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Patient;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import co.edu.unimagdalena.RCU.domine.repositories.PatientRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.implementation.PatientServiceImpl;
import co.edu.unimagdalena.RCU.services.mapper.PatientMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Spy
    private PatientMapper patientMapper = Mappers.getMapper(PatientMapper.class);

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    void shouldCreatePatientSuccessfully() {
        // Given
        var request = new CreatePatientRequest(
            "Jairo", "Blanco", "jairo@email.com", "3001235421",
            DocumentType.CC, "1082898231", Gender.MALE
        );
        when(patientRepository.existsByEmail(request.email())).thenReturn(false);
        when(patientRepository.existsByDocumentNumber(request.documentNumber())).thenReturn(false);
        when(patientRepository.save(any())).thenAnswer(inv -> {
            Patient p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        // When
        var result = patientService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jairo");
        assertThat(result.lastName()).isEqualTo("Blanco");
        assertThat(result.email()).isEqualTo("jairo@email.com");
        verify(patientRepository).save(any());
    }

    @Test
    void shouldThrowWhenPatientEmailAlreadyExists() {
        // Given
        var request = new CreatePatientRequest(
            "Jairo", "Blanco", "jairo@email.com", "3001235421",
            DocumentType.CC, "1082898231", Gender.MALE
        );
        when(patientRepository.existsByEmail(request.email())).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> patientService.create(request));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPatientDocumentNumberAlreadyExists() {
        // Given
        var request = new CreatePatientRequest(
            "Jairo", "Blanco", "jairo@email.com", "3001235421",
            DocumentType.CC, "1082898231", Gender.MALE
        );
        when(patientRepository.existsByEmail(request.email())).thenReturn(false);
        when(patientRepository.existsByDocumentNumber(request.documentNumber())).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> patientService.create(request));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateRequestIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> patientService.create(null));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldGetPatientById() {
        // Given
        var id = UUID.randomUUID();
        var patient = Patient.builder()
            .id(id)
            .firstName("Jairo")
            .lastName("Blanco")
            .email("jairo@email.com")
            .documentNumber("1082898231")
            .active(true)
            .build();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        // When
        var result = patientService.getById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.firstName()).isEqualTo("Jairo");
    }

    @Test
    void shouldThrowWhenPatientNotFound() {
        // Given
        var id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> patientService.getById(id));
    }

    @Test
    void shouldGetAllPatients() {
        // Given
        var patient = Patient.builder()
            .id(UUID.randomUUID())
            .firstName("Jairo")
            .lastName("Blanco")
            .email("jairo@email.com")
            .documentNumber("1082898231")
            .active(true)
            .build();
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        // When
        var result = patientService.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("Jairo");
    }

    @Test
    void shouldUpdatePatientSuccessfully() {
        // Given
        var id = UUID.randomUUID();
        var patient = Patient.builder()
            .id(id)
            .firstName("Jairo")
            .lastName("Blanco")
            .email("jairo@email.com")
            .documentNumber("1082898231")
            .active(true)
            .build();
        var request = new UpdatePatientRequest(
            "Julio", null, "julio@email.com", null, DocumentType.PASSPORT, null, null, null
        );
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmail("julio@email.com")).thenReturn(false);
        when(patientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        var result = patientService.update(id, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Julio");
        verify(patientRepository).save(any());
    }
}
