package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.domine.repositories.AppointmentRepository;
import co.edu.unimagdalena.RCU.services.implementation.ReportServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void shouldGetOfficeOccupancy() {
        // Given
        var startDate = Instant.now().minusSeconds(86400);
        var endDate = Instant.now();
        var pageable = Pageable.ofSize(10);
        var officeId = UUID.randomUUID();
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{officeId, "C-101", 5L});
        when(appointmentRepository.findOfficeOccupancy(startDate, endDate, pageable))
            .thenReturn(new PageImpl<>(rows, pageable, 1));

        // When
        var result = reportService.getOfficeOccupancy(startDate, endDate, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).officeId()).isEqualTo(officeId);
        assertThat(result.getContent().get(0).code()).isEqualTo("C-101");
        assertThat(result.getContent().get(0).totalAppointments()).isEqualTo(5L);
    }

    @Test
    void shouldThrowWhenOfficeOccupancyStartDateAfterEndDate() {
        // Given
        var startDate = Instant.now();
        var endDate = Instant.now().minusSeconds(86400);
        var pageable = Pageable.ofSize(10);

        // When / Then
        assertThrows(IllegalArgumentException.class,
            () -> reportService.getOfficeOccupancy(startDate, endDate, pageable));
        verify(appointmentRepository, never()).findOfficeOccupancy(any(), any(), any());
    }

    @Test
    void shouldThrowWhenOfficeOccupancyStartDateIsNull() {
        var pageable = Pageable.ofSize(10);
        // When / Then
        assertThrows(IllegalArgumentException.class,
            () -> reportService.getOfficeOccupancy(null, Instant.now(), pageable));
        verify(appointmentRepository, never()).findOfficeOccupancy(any(), any(), any());
    }

    @Test
    void shouldGetDoctorProductivity() {
        // Given
        var doctorId = UUID.randomUUID();
        var pageable = Pageable.ofSize(10);
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{doctorId, "John", "Doe", 10L});
        when(appointmentRepository.findDoctorProductivity(pageable)).thenReturn(new PageImpl<>(rows, pageable, 1));

        // When
        var result = reportService.getDoctorProductivity(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).doctorId()).isEqualTo(doctorId);
        assertThat(result.getContent().get(0).firstName()).isEqualTo("John");
        assertThat(result.getContent().get(0).lastName()).isEqualTo("Doe");
        assertThat(result.getContent().get(0).completedAppointments()).isEqualTo(10L);
    }

    @Test
    void shouldGetNoShowPatients() {
        // Given
        var startDate = Instant.now().minusSeconds(86400);
        var endDate = Instant.now();
        var pageable = Pageable.ofSize(10);
        var patientId = UUID.randomUUID();
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{patientId, "Jane", "Doe", 3L});
        when(appointmentRepository.findNoShowPatients(startDate, endDate, pageable))
            .thenReturn(new PageImpl<>(rows, pageable, 1));

        // When
        var result = reportService.getNoShowPatients(startDate, endDate, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).patientId()).isEqualTo(patientId);
        assertThat(result.getContent().get(0).firstName()).isEqualTo("Jane");
        assertThat(result.getContent().get(0).lastName()).isEqualTo("Doe");
        assertThat(result.getContent().get(0).noShowCount()).isEqualTo(3L);
    }

    @Test
    void shouldThrowWhenNoShowPatientsStartDateAfterEndDate() {
        // Given
        var startDate = Instant.now();
        var endDate = Instant.now().minusSeconds(86400);
        var pageable = Pageable.ofSize(10);

        // When / Then
        assertThrows(IllegalArgumentException.class,
            () -> reportService.getNoShowPatients(startDate, endDate, pageable));
        verify(appointmentRepository, never()).findNoShowPatients(any(), any(), any());
    }
}