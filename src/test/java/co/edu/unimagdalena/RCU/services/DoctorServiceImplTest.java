package co.edu.unimagdalena.RCU.service;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.*;
import co.edu.unimagdalena.RCU.entities.Doctor;
import co.edu.unimagdalena.RCU.entities.Specialty;
import co.edu.unimagdalena.RCU.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.entities.enums.Gender;
import co.edu.unimagdalena.RCU.exceptions.BusinessException;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.mapper.DoctorMapper;
import co.edu.unimagdalena.RCU.repository.DoctorRepository;
import co.edu.unimagdalena.RCU.repository.SpecialtyRepository;
import co.edu.unimagdalena.RCU.service.implementation.DoctorServiceImpl;

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
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Spy
    private DoctorMapper doctorMapper = Mappers.getMapper(DoctorMapper.class);

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void shouldCreateDoctorSuccessfully() {
        // Given
        var specialtyId = UUID.randomUUID();
        var request = new CreateDoctorRequest(
            "Carlos", "López", "3009876543", "carlos.lopez@email.com",
            DocumentType.CC, "9876543210", Gender.MALE, "LIC-123456", specialtyId
        );
        var specialty = Specialty.builder().id(specialtyId).name("Cardiology").active(true).build();
        
        when(doctorRepository.existsByEmail(request.email())).thenReturn(false);
        when(doctorRepository.existsByDocumentNumber(request.documentNumber())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(request.licenseNumber())).thenReturn(false);
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        when(doctorRepository.save(any())).thenAnswer(inv -> {
            Doctor d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        // When
        var result = doctorService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Carlos");
        assertThat(result.licenseNumber()).isEqualTo("LIC-123456");
        verify(doctorRepository).save(any());
    }

    @Test
    void shouldThrowWhenSpecialtyNotFound() {
        // Given
        var specialtyId = UUID.randomUUID();
        var request = new CreateDoctorRequest(
            "Carlos", "López", "3009876543", "carlos.lopez@email.com",
            DocumentType.CC, "9876543210", Gender.MALE, "LIC-123456", specialtyId
        );
        when(doctorRepository.existsByEmail(request.email())).thenReturn(false);
        when(doctorRepository.existsByDocumentNumber(request.documentNumber())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(request.licenseNumber())).thenReturn(false);
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> doctorService.create(request));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenSpecialtyNotActive() {
        // Given
        var specialtyId = UUID.randomUUID();
        var request = new CreateDoctorRequest(
            "Carlos", "López", "3009876543", "carlos.lopez@email.com",
            DocumentType.CC, "9876543210", Gender.MALE, "LIC-123456", specialtyId
        );
        var specialty = Specialty.builder().id(specialtyId).name("Cardiology").active(false).build();
        
        when(doctorRepository.existsByEmail(request.email())).thenReturn(false);
        when(doctorRepository.existsByDocumentNumber(request.documentNumber())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(request.licenseNumber())).thenReturn(false);
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));

        // When / Then
        assertThrows(BusinessException.class, () -> doctorService.create(request));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void shouldGetDoctorById() {
        // Given
        var id = UUID.randomUUID();
        var specialty = Specialty.builder().id(UUID.randomUUID()).name("Cardiology").build();
        var doctor = Doctor.builder()
            .id(id)
            .firstName("Carlos")
            .lastName("López")
            .email("carlos.lopez@email.com")
            .licenseNumber("LIC-123456")
            .specialty(specialty)
            .active(true)
            .build();
        when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));

        // When
        var result = doctorService.getDoctorById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.firstName()).isEqualTo("Carlos");
    }

    @Test
    void shouldThrowWhenDoctorNotFound() {
        // Given
        var id = UUID.randomUUID();
        when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(id));
    }

    @Test
    void shouldGetAllDoctors() {
        // Given
        var specialty = Specialty.builder().id(UUID.randomUUID()).name("Cardiology").build();
        var doctor = Doctor.builder()
            .id(UUID.randomUUID())
            .firstName("Carlos")
            .lastName("López")
            .email("carlos.lopez@email.com")
            .specialty(specialty)
            .active(true)
            .build();
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        // When
        var result = doctorService.getAllDoctors();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("Carlos");
    }
}
