package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.ReportDtos.*;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    Page<OfficeOccupancyResponse> getOfficeOccupancy(Instant startDate, Instant endDate, Pageable pageable);
    Page<DoctorProductivityResponse> getDoctorProductivity(Pageable pageable);
    Page<NoShowPatientResponse> getNoShowPatients(Instant startDate, Instant endDate, Pageable pageable);
}