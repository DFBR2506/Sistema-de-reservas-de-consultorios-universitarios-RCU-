package co.edu.unimagdalena.RCU.api.controllers;

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

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.SpecialtyResponse;
import co.edu.unimagdalena.RCU.services.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Validated
public class SpecialtyController {
    private final SpecialtyService service;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(@Valid @RequestBody CreateSpecialtyRequest request,
                                            UriComponentsBuilder uriBuilder) {
    var specialtyCreated=service.create(request);
    var location=uriBuilder.path("/api/specialties/{id}").buildAndExpand(specialtyCreated.id()).toUri();
    return ResponseEntity.created(location).body(specialtyCreated);
    }

    @GetMapping
    public ResponseEntity<Page<SpecialtyResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        var result = service.getAll(pageable);
        return ResponseEntity.ok(result);
     }
    
    

}
