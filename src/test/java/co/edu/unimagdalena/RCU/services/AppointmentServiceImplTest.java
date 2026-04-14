package co.edu.unimagdalena.RCU.service;

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.*;
import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.entities.enums.Status;
import co.edu.unimagdalena.RCU.exceptions.BusinessException;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.mapper.AppointmentMapper;
import co.edu.unimagdalena.RCU.repository.*;
import co.edu.unimagdalena.RCU.service.implementation.AppointmentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private OfficeRepository officeRepository;
    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;
    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;
    @Spy
    private AppointmentMapper appointmentMapper = Mappers.getMapper(AppointmentMapper.class);
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Instant futureStartAt() {
        return LocalDate.now().plusDays(1)
                .atTime(LocalTime.of(10, 0))
                .toInstant(ZoneOffset.UTC);
    }

    private DoctorSchedule scheduleForDate(Instant startAt) {
        var dayOfWeek = co.edu.unimagdalena.RCU.entities.enums.DayOfWeek.valueOf(
                startAt.atZone(ZoneOffset.UTC).getDayOfWeek().name()
        );
        return DoctorSchedule.builder()
                .id(UUID.randomUUID())
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(18, 0))
                .active(true)
                .build();
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
        // Given
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = futureStartAt();

        var request = new CreateAppointmentRequest(patientId, doctorId, officeId, appointmentTypeId, startAt);
        var patient = Patient.builder().id(patientId).active(true).build();
        var doctor = Doctor.builder().id(doctorId).active(true).build();
        var office = Office.builder().id(officeId).active(true).build();
        var appointmentType = AppointmentType.builder().id(appointmentTypeId).durationMinutes(30).active(true).build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(appointmentTypeRepository.findById(appointmentTypeId)).thenReturn(Optional.of(appointmentType));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(any(), any())).thenReturn(Optional.of(scheduleForDate(startAt)));
        when(appointmentRepository.existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any())).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        // When
        var result = appointmentService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.patientId()).isEqualTo(patientId);
        assertThat(result.doctorId()).isEqualTo(doctorId);
        assertThat(result.status()).isEqualTo(Status.SCHEDULED);
        verify(appointmentRepository).save(any());
    }

    @Test
    void shouldThrowWhenAppointmentInThePast() {
        // Given
        var request = new CreateAppointmentRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Instant.now().minusSeconds(3600)
        );

        // When / Then
        assertThrows(BusinessException.class, () -> appointmentService.create(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPatientNotFound() {
        // Given
        var patientId = UUID.randomUUID();
        var request = new CreateAppointmentRequest(patientId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), futureStartAt());
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDoctorHasConflict() {
        // Given
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = futureStartAt();

        var request = new CreateAppointmentRequest(patientId, doctorId, officeId, appointmentTypeId, startAt);
        var patient = Patient.builder().id(patientId).active(true).build();
        var doctor = Doctor.builder().id(doctorId).active(true).build();
        var office = Office.builder().id(officeId).active(true).build();
        var appointmentType = AppointmentType.builder().id(appointmentTypeId).durationMinutes(30).active(true).build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(appointmentTypeRepository.findById(appointmentTypeId)).thenReturn(Optional.of(appointmentType));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(any(), any())).thenReturn(Optional.of(scheduleForDate(startAt)));
        when(appointmentRepository.existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(any(), any(), any())).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> appointmentService.create(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldGetAppointmentById() {
        // Given
        var id = UUID.randomUUID();
        var appointment = Appointment.builder()
                .id(id)
                .status(Status.SCHEDULED)
                .startAt(futureStartAt())
                .endAt(futureStartAt().plusSeconds(1800))
                .build();
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        // When
        var result = appointmentService.getById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.status()).isEqualTo(Status.SCHEDULED);
    }

    @Test
    void shouldThrowWhenAppointmentNotFound() {
        // Given
        var id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.getById(id));
    }

    @Test
    void shouldGetAllAppointments() {
        // Given
        var appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .status(Status.SCHEDULED)
                .startAt(futureStartAt())
                .endAt(futureStartAt().plusSeconds(1800))
                .build();
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        // When
        var result = appointmentService.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(Status.SCHEDULED);
    }

    @Test
    void shouldConfirmAppointmentSuccessfully() {
        // Given
        var id = UUID.randomUUID();
        var appointment = Appointment.builder()
                .id(id)
                .status(Status.SCHEDULED)
                .startAt(futureStartAt())
                .endAt(futureStartAt().plusSeconds(1800))
                .build();
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        var result = appointmentService.confirm(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(Status.CONFIRMED);
        verify(appointmentRepository).save(any());
    }

    @Test
    void shouldThrowWhenConfirmingNonScheduledAppointment() {
        // Given
        var id = UUID.randomUUID();
        var appointment = Appointment.builder()
                .id(id)
                .status(Status.CONFIRMED)
                .startAt(futureStartAt())
                .endAt(futureStartAt().plusSeconds(1800))
                .build();
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        // When / Then
        assertThrows(BusinessException.class, () -> appointmentService.confirm(id));
    }

    @Test
    void shouldCancelAppointmentSuccessfully() {
        // Given
        var id = UUID.randomUUID();
        var appointment = Appointment.builder()
                .id(id)
                .status(Status.SCHEDULED)
                .startAt(futureStartAt())
                .endAt(futureStartAt().plusSeconds(1800))
                .build();
        var request = new CancelAppointmentRequest("Patient requested cancellation");
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        var result = appointmentService.cancel(id, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(Status.CANCELLED);
        assertThat(result.cancellationReason()).isEqualTo("Patient requested cancellation");
        verify(appointmentRepository).save(any());
    }

    @Test
    void shouldThrowWhenCancelingAlreadyCancelledAppointment() {
        // Given
        var id = UUID.randomUUID();
        var appointment = Appointment.builder()
                .id(id)
                .status(Status.CANCELLED)
                .startAt(futureStartAt())
                .endAt(futureStartAt().plusSeconds(1800))
                .build();
        var request = new CancelAppointmentRequest("Patient requested cancellation");
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        // When / Then
        assertThrows(BusinessException.class, () -> appointmentService.cancel(id, request));
    }
}
