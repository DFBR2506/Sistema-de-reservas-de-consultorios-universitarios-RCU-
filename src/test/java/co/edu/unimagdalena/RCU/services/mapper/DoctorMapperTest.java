package co.edu.unimagdalena.RCU.services.mapper;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import co.edu.unimagdalena.RCU.services.mapper.DoctorMapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class DoctorMapperTest {
    private final DoctorMapper doctorMapper = Mappers.getMapper(DoctorMapper.class);

    @Test
    public void toEntity_ShouldMapCreateDoctorRequestToDoctor() {
        // Given
        var request = new CreateDoctorRequest(
            "Carlos",
            "López",
            "3009876543",
            "carlos.lopez@email.com",
            DocumentType.CC,
            "9876543210",
            Gender.MALE,
            "LIC-123456",
            UUID.randomUUID()
        );

        // When
        var doctor = doctorMapper.toEntity(request);

        // Then
        assertThat(doctor).isNotNull();
        assertThat(doctor.getFirstName()).isEqualTo(request.firstName());
        assertThat(doctor.getLastName()).isEqualTo(request.lastName());
        assertThat(doctor.getPhone()).isEqualTo(request.phone());
        assertThat(doctor.getEmail()).isEqualTo(request.email());
        assertThat(doctor.getDocumentType()).isEqualTo(request.documentType());
        assertThat(doctor.getDocumentNumber()).isEqualTo(request.documentNumber());
        assertThat(doctor.getGender()).isEqualTo(request.gender());
        assertThat(doctor.getLicenseNumber()).isEqualTo(request.licenseNumber());
    }

    @Test
    public void toResponse_ShouldMapDoctorToDoctorResponse() {
        // Given
        var specialtyId = UUID.randomUUID();
        var specialty = Specialty.builder()
            .id(specialtyId)
            .name("Cardiology")
            .build();
        
        var doctor = Doctor.builder()
            .id(UUID.randomUUID())
            .firstName("Carlos")
            .lastName("López")
            .phone("3009876543")
            .email("carlos.lopez@email.com")
            .documentType(DocumentType.CC)
            .documentNumber("9876543210")
            .gender(Gender.MALE)
            .licenseNumber("LIC-123456")
            .specialty(specialty)
            .active(true)
            .build();

        // When
        var response = doctorMapper.toResponse(doctor);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(doctor.getId());
        assertThat(response.firstName()).isEqualTo(doctor.getFirstName());
        assertThat(response.lastName()).isEqualTo(doctor.getLastName());
        assertThat(response.phone()).isEqualTo(doctor.getPhone());
        assertThat(response.email()).isEqualTo(doctor.getEmail());
        assertThat(response.documentType()).isEqualTo(doctor.getDocumentType());
        assertThat(response.documentNumber()).isEqualTo(doctor.getDocumentNumber());
        assertThat(response.gender()).isEqualTo(doctor.getGender());
        assertThat(response.licenseNumber()).isEqualTo(doctor.getLicenseNumber());
        assertThat(response.specialtyId()).isEqualTo(specialty.getId());
        assertThat(response.active()).isEqualTo(doctor.getActive());
    }

    @Test
    public void updateEntity_ShouldUpdateDoctorWithUpdateDoctorRequest() {
        // Given
        var doctor = Doctor.builder()
            .id(UUID.randomUUID())
            .firstName("Carlos")
            .lastName("López")
            .phone("3009876543")
            .email("carlos.lopez@email.com")
            .documentType(DocumentType.CC)
            .documentNumber("9876543210")
            .gender(Gender.MALE)
            .licenseNumber("LIC-123456")
            .active(false)
            .build();
        
        var specialtyId = UUID.randomUUID();
        var request = new UpdateDoctorRequest(
            "Juan",
            "Martínez",
            "3001234567",
            "juan.martinez@email.com",
            DocumentType.CE,
            "1234567890",
            Gender.MALE,
            "LIC-654321",
            specialtyId,
            true
        );

        // When
        doctorMapper.updateEntity(request, doctor);

        // Then
        assertThat(doctor.getFirstName()).isEqualTo(request.firstName());
        assertThat(doctor.getLastName()).isEqualTo(request.lastName());
        assertThat(doctor.getPhone()).isEqualTo(request.phone());
        assertThat(doctor.getEmail()).isEqualTo(request.email());
        assertThat(doctor.getDocumentType()).isEqualTo(request.documentType());
        assertThat(doctor.getDocumentNumber()).isEqualTo(request.documentNumber());
        assertThat(doctor.getGender()).isEqualTo(request.gender());
        assertThat(doctor.getLicenseNumber()).isEqualTo(request.licenseNumber());
        assertThat(doctor.getActive()).isEqualTo(true);
    }

    @Test
    public void updateEntity_ShouldIgnoreNullValuesInUpdateDoctorRequest() {
        // Given
        var doctor = Doctor.builder()
            .id(UUID.randomUUID())
            .firstName("Carlos")
            .lastName("López")
            .phone("3009876543")
            .email("carlos.lopez@email.com")
            .documentType(DocumentType.CC)
            .documentNumber("9876543210")
            .gender(Gender.MALE)
            .licenseNumber("LIC-123456")
            .active(true)
            .build();
        
        var request = new UpdateDoctorRequest(
            "Juan",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        // When
        doctorMapper.updateEntity(request, doctor);

        // Then
        assertThat(doctor.getFirstName()).isEqualTo(request.firstName());
        assertThat(doctor.getLastName()).isEqualTo("López");
        assertThat(doctor.getPhone()).isEqualTo("3009876543");
        assertThat(doctor.getEmail()).isEqualTo("carlos.lopez@email.com");
        assertThat(doctor.getDocumentType()).isEqualTo(DocumentType.CC);
        assertThat(doctor.getDocumentNumber()).isEqualTo("9876543210");
        assertThat(doctor.getGender()).isEqualTo(Gender.MALE);
        assertThat(doctor.getLicenseNumber()).isEqualTo("LIC-123456");
        assertThat(doctor.getActive()).isEqualTo(true);
    }
}
