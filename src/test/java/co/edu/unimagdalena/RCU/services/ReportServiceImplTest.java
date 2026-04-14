package co.edu.unimagdalena.RCU.service;

import co.edu.unimagdalena.RCU.repository.AppointmentRepository;
import co.edu.unimagdalena.RCU.service.implementation.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        var officeId = UUID.randomUUID();
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{officeId, "C-101", 5L});
        when(appointmentRepository.findOfficeOccupancy(startDate, endDate)).thenReturn(rows);

        // When
        var result = reportService.getOfficeOccupancy(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).officeId()).isEqualTo(officeId);
        assertThat(result.get(0).code()).isEqualTo("C-101");
        assertThat(result.get(0).totalAppointments()).isEqualTo(5L);
    }

    @Test
    void shouldThrowWhenOfficeOccupancyStartDateAfterEndDate() {
        // Given
        var startDate = Instant.now();
        var endDate = Instant.now().minusSeconds(86400);

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> reportService.getOfficeOccupancy(startDate, endDate));
        verify(appointmentRepository, never()).findOfficeOccupancy(any(), any());
    }

    @Test
    void shouldThrowWhenOfficeOccupancyStartDateIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> reportService.getOfficeOccupancy(null, Instant.now()));
        verify(appointmentRepository, never()).findOfficeOccupancy(any(), any());
    }

    @Test
    void shouldGetDoctorProductivity() {
        // Given
        var doctorId = UUID.randomUUID();
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{doctorId, "John", "Doe", 10L});
        when(appointmentRepository.findDoctorProductivity()).thenReturn(rows);

        // When
        var result = reportService.getDoctorProductivity();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).doctorId()).isEqualTo(doctorId);
        assertThat(result.get(0).firstName()).isEqualTo("John");
        assertThat(result.get(0).lastName()).isEqualTo("Doe");
        assertThat(result.get(0).completedAppointments()).isEqualTo(10L);
    }

    @Test
    void shouldGetNoShowPatients() {
        // Given
        var startDate = Instant.now().minusSeconds(86400);
        var endDate = Instant.now();
        var patientId = UUID.randomUUID();
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{patientId, "Jane", "Doe", 3L});
        when(appointmentRepository.findNoShowPatients(startDate, endDate)).thenReturn(rows);

        // When
        var result = reportService.getNoShowPatients(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).patientId()).isEqualTo(patientId);
        assertThat(result.get(0).firstName()).isEqualTo("Jane");
        assertThat(result.get(0).lastName()).isEqualTo("Doe");
        assertThat(result.get(0).noShowCount()).isEqualTo(3L);
    }

    @Test
    void shouldThrowWhenNoShowPatientsStartDateAfterEndDate() {
        // Given
        var startDate = Instant.now();
        var endDate = Instant.now().minusSeconds(86400);

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> reportService.getNoShowPatients(startDate, endDate));
        verify(appointmentRepository, never()).findNoShowPatients(any(), any());
    }
}
