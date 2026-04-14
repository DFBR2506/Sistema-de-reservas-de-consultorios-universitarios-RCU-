package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.ReportDtos.*;
import co.edu.unimagdalena.RCU.domine.repositories.AppointmentRepository;
import co.edu.unimagdalena.RCU.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;

    @Override
        public Page<OfficeOccupancyResponse> getOfficeOccupancy(Instant startDate, Instant endDate, Pageable pageable) {
        requireNonNull(startDate, "The start date cannot be null");
        requireNonNull(endDate, "The end date cannot be null");
        requireNonNull(pageable, "The pageable cannot be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return appointmentRepository.findOfficeOccupancy(startDate, endDate, pageable)
            .map(row -> new OfficeOccupancyResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (Long) row[2]
            ));
    }

    @Override
        public Page<DoctorProductivityResponse> getDoctorProductivity(Pageable pageable) {
        requireNonNull(pageable, "The pageable cannot be null");
        return appointmentRepository.findDoctorProductivity(pageable)
            .map(row -> new DoctorProductivityResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Long) row[3]
            ));
    }

    @Override
        public Page<NoShowPatientResponse> getNoShowPatients(Instant startDate, Instant endDate, Pageable pageable) {
        requireNonNull(startDate, "The start date cannot be null");
        requireNonNull(endDate, "The end date cannot be null");
        requireNonNull(pageable, "The pageable cannot be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return appointmentRepository.findNoShowPatients(startDate, endDate, pageable)
            .map(row -> new NoShowPatientResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Long) row[3]
            ));
    }

    private static void requireNonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(message);
        }
    }
}