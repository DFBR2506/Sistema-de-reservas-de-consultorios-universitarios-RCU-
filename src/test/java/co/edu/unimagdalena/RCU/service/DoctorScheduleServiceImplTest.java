package co.edu.unimagdalena.RCU.service;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.*;
import co.edu.unimagdalena.RCU.entities.Doctor;
import co.edu.unimagdalena.RCU.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.exceptions.BusinessException;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.mapper.DoctorScheduleMapper;
import co.edu.unimagdalena.RCU.repository.DoctorRepository;
import co.edu.unimagdalena.RCU.repository.DoctorScheduleRepository;
import co.edu.unimagdalena.RCU.service.implementation.DoctorScheduleServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Spy
    private DoctorScheduleMapper doctorScheduleMapper = Mappers.getMapper(DoctorScheduleMapper.class);

    @InjectMocks
    private DoctorScheduleServiceImpl doctorScheduleService;

    @Test
    void shouldCreateDoctorScheduleSuccessfully() {
        // Given
        var doctorId = UUID.randomUUID();
        var request = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
        var doctor = Doctor.builder().id(doctorId).active(true).build();
        
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(false);
        when(doctorScheduleRepository.save(any())).thenAnswer(inv -> {
            DoctorSchedule ds = inv.getArgument(0);
            ds.setId(UUID.randomUUID());
            return ds;
        });

        // When
        var result = doctorScheduleService.create(doctorId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(result.startTime()).isEqualTo(LocalTime.of(8, 0));
        verify(doctorScheduleRepository).save(any());
    }

    @Test
    void shouldThrowWhenDoctorNotFound() {
        // Given
        var doctorId = UUID.randomUUID();
        var request = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> doctorScheduleService.create(doctorId, request));
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDoctorNotActive() {
        // Given
        var doctorId = UUID.randomUUID();
        var request = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
        var doctor = Doctor.builder().id(doctorId).active(false).build();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // When / Then
        assertThrows(BusinessException.class, () -> doctorScheduleService.create(doctorId, request));
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenStartTimeAfterEndTime() {
        // Given
        var doctorId = UUID.randomUUID();
        var request = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(12, 0));
       

        // When / Then
        assertThrows(BusinessException.class, () -> doctorScheduleService.create(doctorId, request));
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenScheduleAlreadyExistsForDay() {
        // Given
        var doctorId = UUID.randomUUID();
        var request = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
        var doctor = Doctor.builder().id(doctorId).active(true).build();
        
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> doctorScheduleService.create(doctorId, request));
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldGetAllSchedulesForDoctor() {
        // Given
        var doctorId = UUID.randomUUID();
        var schedule = DoctorSchedule.builder()
            .id(UUID.randomUUID())
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(8, 0))
            .endTime(LocalTime.of(12, 0))
            .active(true)
            .build();
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorId(doctorId)).thenReturn(List.of(schedule));

        // When
        var result = doctorScheduleService.getAllSchedules(doctorId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    void shouldThrowWhenDoctorNotFoundForGetSchedules() {
        // Given
        var doctorId = UUID.randomUUID();
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> doctorScheduleService.getAllSchedules(doctorId));
    }
}
