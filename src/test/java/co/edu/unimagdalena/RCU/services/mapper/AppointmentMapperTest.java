package co.edu.unimagdalena.RCU.services.mapper;

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.*;
import co.edu.unimagdalena.RCU.domine.entities.enums.Status;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class AppointmentMapperTest {
    private final AppointmentMapper appointmentMapper = Mappers.getMapper(AppointmentMapper.class);

    @Test
    public void toEntity_ShouldMapCreateAppointmentRequestToAppointment() {
        // Given
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = Instant.now();

        var request = new CreateAppointmentRequest(
            patientId,
            doctorId,
            officeId,
            appointmentTypeId,
            startAt
        );

        // When
        var appointment = appointmentMapper.toEntity(request);

        // Then
        assertThat(appointment).isNotNull();
        // Note: appointment mapper ignores all fields when mapping to entity
        assertThat(appointment.getId()).isNull();
        assertThat(appointment.getStatus()).isNull();
    }

    @Test
    public void toResponse_ShouldMapAppointmentToAppointmentResponse() {
        // Given
        var appoId = UUID.randomUUID();
        var patId = UUID.randomUUID();
        var docId = UUID.randomUUID();
        var offId = UUID.randomUUID();
        var appTypeId = UUID.randomUUID();
        var startAt = Instant.now();
        var endAt = Instant.now().plusSeconds(1800);

        var doctor = Doctor.builder().id(docId).build();
        var patient = Patient.builder().id(patId).build();
        var appointmentType = AppointmentType.builder().id(appTypeId).build();
        var office = Office.builder().id(offId).build();

        var appointment = Appointment.builder()
            .id(appoId)
            .patient(patient)
            .doctor(doctor)
            .office(office)
            .appointmentType(appointmentType)
            .startAt(startAt)
            .endAt(endAt)
            .status(Status.SCHEDULED)
            .cancellationReason(null)
            .note("General checkup")
            .build();

        // When
        var response = appointmentMapper.toResponse(appointment);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(appoId);
        assertThat(response.patientId()).isEqualTo(patId);
        assertThat(response.doctorId()).isEqualTo(docId);
        assertThat(response.officeId()).isEqualTo(offId);
        assertThat(response.appointmentTypeId()).isEqualTo(appTypeId);
        assertThat(response.startAt()).isEqualTo(startAt);
        assertThat(response.endAt()).isEqualTo(endAt);
        assertThat(response.status()).isEqualTo(Status.SCHEDULED);
        assertThat(response.cancellationReason()).isNull();
        assertThat(response.notes()).isEqualTo("General checkup");
    }

    @Test
    public void toResponse_ShouldMapNoteToNotesField() {
        // Given
        var appointment = Appointment.builder()
            .id(UUID.randomUUID())
            .patient(Patient.builder().id(UUID.randomUUID()).build())
            .doctor(Doctor.builder().id(UUID.randomUUID()).build())
            .office(Office.builder().id(UUID.randomUUID()).build())
            .appointmentType(AppointmentType.builder().id(UUID.randomUUID()).build())
            .startAt(Instant.now())
            .endAt(Instant.now().plusSeconds(1800))
            .status(Status.SCHEDULED)
            .note("Follow-up visit")
            .build();

        // When
        var response = appointmentMapper.toResponse(appointment);

        // Then
        assertThat(response.notes()).isEqualTo("Follow-up visit");
    }

    @Test
    public void toResponse_ShouldMapAppointmentWithCancellation() {
        // Given
        var appointment = Appointment.builder()
            .id(UUID.randomUUID())
            .patient(Patient.builder().id(UUID.randomUUID()).build())
            .doctor(Doctor.builder().id(UUID.randomUUID()).build())
            .office(Office.builder().id(UUID.randomUUID()).build())
            .appointmentType(AppointmentType.builder().id(UUID.randomUUID()).build())
            .startAt(Instant.now())
            .endAt(Instant.now().plusSeconds(1800))
            .status(Status.CANCELLED)
            .cancellationReason("Patient requested cancellation")
            .build();

        // When
        var response = appointmentMapper.toResponse(appointment);

        // Then
        assertThat(response.status()).isEqualTo(Status.CANCELLED);
        assertThat(response.cancellationReason()).isEqualTo("Patient requested cancellation");
    }
}
