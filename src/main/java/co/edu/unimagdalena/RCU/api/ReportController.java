package co.edu.unimagdalena.RCU.api;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unimagdalena.RCU.api.dto.ReportDtos.DoctorProductivityResponse;
import co.edu.unimagdalena.RCU.api.dto.ReportDtos.NoShowPatientResponse;
import co.edu.unimagdalena.RCU.api.dto.ReportDtos.OfficeOccupancyResponse;
import co.edu.unimagdalena.RCU.services.ReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {
    private final ReportService service;  

    @GetMapping("/office-occupancy")
    public ResponseEntity<Page<OfficeOccupancyResponse>> getOfficeOccupancy(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("totalAppointments").descending());
        return ResponseEntity.ok(service.getOfficeOccupancy(startDate, endDate, pageable));
    }

    @GetMapping("/doctor-productivity")
    public ResponseEntity<Page<DoctorProductivityResponse>> getDoctorProductivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("completedAppointments").descending());
        return ResponseEntity.ok(service.getDoctorProductivity(pageable));
    }

    @GetMapping("/no-show-patients")
    public ResponseEntity<Page<NoShowPatientResponse>> getNoShowPatients(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("noShowCount").descending());
        return ResponseEntity.ok(service.getNoShowPatients(startDate, endDate, pageable));
    }
}
