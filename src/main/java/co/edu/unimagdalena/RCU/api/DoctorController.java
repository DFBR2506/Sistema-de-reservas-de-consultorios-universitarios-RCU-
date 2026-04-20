package co.edu.unimagdalena.RCU.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.CreateDoctorRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.DoctorResponse;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.UpdateDoctorRequest;
import co.edu.unimagdalena.RCU.services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Validated

public class DoctorController {
    private final DoctorService service;

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest request,
                                                UriComponentsBuilder uriBuilder) {
    var doctorCreated=service.create(request);
    var location=uriBuilder.path("/api/doctors/{id}").buildAndExpand(doctorCreated.id()).toUri();
    return ResponseEntity.created(location).body(doctorCreated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(service.getDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        var result = service.getAllDoctors(pageable);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateDoctorRequest request){
        var result=service.updateDoctor(id, request);
        return ResponseEntity.ok(result);
    }
    
    
}
