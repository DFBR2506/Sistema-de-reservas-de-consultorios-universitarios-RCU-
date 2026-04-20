package co.edu.unimagdalena.RCU.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.AppointmentTypeResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.CreateAppointmentTypeRequest;
import co.edu.unimagdalena.RCU.services.AppointmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
@Validated
public class AppointmentTypeController {
    private final AppointmentTypeService service;   

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> createAppointmentType(@Valid @RequestBody CreateAppointmentTypeRequest request,
                                                UriComponentsBuilder uriBuilder) {
    var appointmentTypeCreated=service.create(request);
    var location=uriBuilder.path("/api/appointment-types/{id}").buildAndExpand(appointmentTypeCreated.id()).toUri();
    return ResponseEntity.created(location).body(appointmentTypeCreated);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentTypeResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        var result = service.getAll(pageable);
        return ResponseEntity.ok(result);
    }
    
}


