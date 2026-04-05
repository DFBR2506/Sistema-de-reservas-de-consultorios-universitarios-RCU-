package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.ReportDtos.*;
import java.time.Instant;
import java.util.List;

public interface ReportService {
    List<OfficeOccupancyResponse> getOfficeOccupancy(Instant startDate, Instant endDate);
    List<DoctorProductivityResponse> getDoctorProductivity();
    List<NoShowPatientResponse> getNoShowPatients(Instant startDate, Instant endDate);
}