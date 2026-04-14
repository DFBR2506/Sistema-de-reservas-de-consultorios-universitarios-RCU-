package co.edu.unimagdalena.RCU.service.mappers;

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.*;
import co.edu.unimagdalena.RCU.entities.Patient;
import co.edu.unimagdalena.RCU.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.entities.enums.Gender;
import co.edu.unimagdalena.RCU.mapper.PatientMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class PatientMapperTest {
    private final PatientMapper patientMapper = Mappers.getMapper(PatientMapper.class);

    @Test
    public void toEntity_ShouldMapCreatePatientRequestToPatient() {
        // Given
        var request = new CreatePatientRequest(
            "Jairo",
            "Blanco",
            "jairo@email.com",
            "3001235421",
            DocumentType.CC,
            "1082898231",
            Gender.MALE
        );

        // When
        var patient = patientMapper.toEntity(request);

        // Then
        assertThat(patient).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo(request.firstName());
        assertThat(patient.getLastName()).isEqualTo(request.lastName());
        assertThat(patient.getEmail()).isEqualTo(request.email());
        assertThat(patient.getPhone()).isEqualTo(request.phone());
        assertThat(patient.getDocumentType()).isEqualTo(request.documentType());
        assertThat(patient.getDocumentNumber()).isEqualTo(request.documentNumber());
        assertThat(patient.getGender()).isEqualTo(request.gender());
    }

    @Test
    public void toResponse_ShouldMapPatientToPatientResponse() {
        // Given
        var patient = Patient.builder()
            .id(UUID.randomUUID())
            .firstName("Jairo")
            .lastName("Blanco")
            .email("jairo@email.com")
            .phone("3001235421")
            .documentType(DocumentType.CC)
            .documentNumber("1082898231")
            .gender(Gender.MALE)
            .build();

        // When
        var response = patientMapper.toResponse(patient);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo(patient.getFirstName());
        assertThat(response.lastName()).isEqualTo(patient.getLastName());
        assertThat(response.email()).isEqualTo(patient.getEmail());
        assertThat(response.phone()).isEqualTo(patient.getPhone());
        assertThat(response.documentType()).isEqualTo(patient.getDocumentType());
        assertThat(response.documentNumber()).isEqualTo(patient.getDocumentNumber());
        assertThat(response.gender()).isEqualTo(patient.getGender());
    }

    @Test
    public void updateEntity_ShouldUpdatePatientWithUpdatePatientRequest() {
        // Given
        var patient = Patient.builder()
            .id(UUID.randomUUID())
            .firstName("Jairo")
            .lastName("Blanco")
            .email("jairo@email.com")
            .phone("3001235421")
            .documentType(DocumentType.CC)
            .documentNumber("1082898231")
            .gender(Gender.MALE)
            .build();
        var request = new UpdatePatientRequest(
            "Julio", null, "julio@email.com", null, DocumentType.PASSPORT, null, null, null
        );

        // When
        patientMapper.updateEntity(request, patient);

        // Then
        assertThat(patient.getFirstName()).isEqualTo(request.firstName());
        assertThat(patient.getLastName()).isEqualTo("Blanco");
        assertThat(patient.getEmail()).isEqualTo(request.email());
        assertThat(patient.getPhone()).isEqualTo("3001235421");
        assertThat(patient.getDocumentType()).isEqualTo(request.documentType());
        assertThat(patient.getDocumentNumber()).isEqualTo("1082898231");
        assertThat(patient.getGender()).isEqualTo(Gender.MALE);
    }
}
