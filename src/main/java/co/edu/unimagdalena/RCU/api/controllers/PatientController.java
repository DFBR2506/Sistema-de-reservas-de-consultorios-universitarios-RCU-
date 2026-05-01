package co.edu.unimagdalena.RCU.api.controllers;

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

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.CreatePatientRequest;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.PatientResponse;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.UpdatePatientRequest;
import co.edu.unimagdalena.RCU.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {
    private final PatientService service;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request,
                                                UriComponentsBuilder uriBuilder) {
    var patientCreated=service.create(request);
    var location=uriBuilder.path("/api/patients/{id}").buildAndExpand(patientCreated.id()).toUri();
    return ResponseEntity.created(location).body(patientCreated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(service.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        var result = service.getAll(pageable);
        return ResponseEntity.ok(result);

     }  
    
    @PatchMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdatePatientRequest request){
        var result=service.update(id, request);
        return ResponseEntity.ok(result);
    }
}
