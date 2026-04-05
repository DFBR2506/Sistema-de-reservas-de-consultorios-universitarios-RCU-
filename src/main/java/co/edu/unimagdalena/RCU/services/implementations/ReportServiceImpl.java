package co.edu.unimagdalena.RCU.services.implementations;

import co.edu.unimagdalena.RCU.api.dto.ReportDtos.*;
import co.edu.unimagdalena.RCU.domine.repositories.AppointmentRepository;
import co.edu.unimagdalena.RCU.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public List<OfficeOccupancyResponse> getOfficeOccupancy(Instant startDate, Instant endDate) {
        requireNonNull(startDate, "The start date cannot be null");
        requireNonNull(endDate, "The end date cannot be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return appointmentRepository.findOfficeOccupancy(startDate, endDate)
                .stream()
                .map(row -> new OfficeOccupancyResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

    @Override
    public List<DoctorProductivityResponse> getDoctorProductivity() {
        return appointmentRepository.findDoctorProductivity()
                .stream()
                .map(row -> new DoctorProductivityResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Long) row[3]
                ))
                .toList();
    }

    @Override
    public List<NoShowPatientResponse> getNoShowPatients(Instant startDate, Instant endDate) {
        requireNonNull(startDate, "The start date cannot be null");
        requireNonNull(endDate, "The end date cannot be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return appointmentRepository.findNoShowPatients(startDate, endDate)
                .stream()
                .map(row -> new NoShowPatientResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Long) row[3]
                ))
                .toList();
    }

    private static void requireNonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(message);
        }
    }
}