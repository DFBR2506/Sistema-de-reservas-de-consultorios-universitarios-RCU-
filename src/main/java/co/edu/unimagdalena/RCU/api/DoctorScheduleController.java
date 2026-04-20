package co.edu.unimagdalena.RCU.api;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import co.edu.unimagdalena.RCU.services.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
@Validated
public class DoctorScheduleController {
    private final DoctorScheduleService service;    

    @PostMapping
    public ResponseEntity<DoctorScheduleResponse> createDoctorSchedule(@PathVariable UUID doctorId,
                                                                        @Valid @RequestBody CreateDoctorScheduleRequest request,
                                                                        UriComponentsBuilder uriBuilder) {
    var created = service.create(doctorId, request);
    var location = uriBuilder
            .path("/api/doctors/{doctorId}/schedules/{id}")
            .buildAndExpand(doctorId, created.id())
            .toUri();

    return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<DoctorScheduleResponse>> list(@PathVariable UUID doctorId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
 var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    return ResponseEntity.ok(service.getAllSchedules(doctorId, pageable));     }
}
