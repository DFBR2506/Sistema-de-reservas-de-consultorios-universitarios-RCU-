package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.domine.entities.Appointment;
import co.edu.unimagdalena.RCU.domine.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.domine.repositories.AppointmentRepository;
import co.edu.unimagdalena.RCU.domine.repositories.DoctorRepository;
import co.edu.unimagdalena.RCU.domine.repositories.DoctorScheduleRepository;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.implementation.AvailabilityServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @Test
    void shouldReturnAvailableSlotsWhenNoAppointments() {
        // Given
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);
        var day = DayOfWeek.valueOf(date.getDayOfWeek().name());

        var schedule = DoctorSchedule.builder()
            .id(UUID.randomUUID())
            .dayOfWeek(day)
            .startTime(LocalTime.of(8, 0))
            .endTime(LocalTime.of(10, 0))
            .active(true)
            .build();

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, day)).thenReturn(Optional.of(schedule));
        when(appointmentRepository.findByDoctorIdAndStartAtBetween(any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.findByOfficeIdAndStartAtBetween(any(), any(), any())).thenReturn(List.of());

        // When
        var result = availabilityService.getAvailableSlots(doctorId, officeId, date);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(4); // 2 horas / 30 min = 4 slots
        assertThat(result.get(0).startAt()).isNotNull();
        assertThat(result.get(0).endAt()).isNotNull();
    }

    @Test
    void shouldReturnEmptySlotsWhenAllOccupied() {
        // Given
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);
        var day = DayOfWeek.valueOf(date.getDayOfWeek().name());

        var schedule = DoctorSchedule.builder()
            .id(UUID.randomUUID())
            .dayOfWeek(day)
            .startTime(LocalTime.of(8, 0))
            .endTime(LocalTime.of(9, 0))
            .active(true)
            .build();

        var dayStart = date.atTime(LocalTime.of(8, 0)).toInstant(java.time.ZoneOffset.UTC);
        var dayEnd = date.atTime(LocalTime.of(9, 0)).toInstant(java.time.ZoneOffset.UTC);

        var appointment = Appointment.builder()
            .id(UUID.randomUUID())
            .startAt(dayStart)
            .endAt(dayEnd)
            .build();

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, day)).thenReturn(Optional.of(schedule));
        when(appointmentRepository.findByDoctorIdAndStartAtBetween(any(), any(), any())).thenReturn(List.of(appointment));
        when(appointmentRepository.findByOfficeIdAndStartAtBetween(any(), any(), any())).thenReturn(List.of());

        // When
        var result = availabilityService.getAvailableSlots(doctorId, officeId, date);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowWhenDoctorNotFound() {
        // Given
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
            () -> availabilityService.getAvailableSlots(doctorId, officeId, date));
        verify(doctorScheduleRepository, never()).findByDoctorIdAndDayOfWeek(any(), any());
    }

    @Test
    void shouldThrowWhenDoctorHasNoScheduleForDay() {
        // Given
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);
        var day = DayOfWeek.valueOf(date.getDayOfWeek().name());

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, day)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
            () -> availabilityService.getAvailableSlots(doctorId, officeId, date));
    }

    @Test
    void shouldThrowWhenDoctorIdIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
            () -> availabilityService.getAvailableSlots(null, UUID.randomUUID(), LocalDate.now()));
    }
}