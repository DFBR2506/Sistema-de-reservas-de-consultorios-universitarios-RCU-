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

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.CreateOfficeRequest;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.OfficeResponse;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.UpdateOfficeRequest;
import co.edu.unimagdalena.RCU.services.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Validated
public class OfficeController {
    private final OfficeService service;    
    
    @PostMapping
    public ResponseEntity<OfficeResponse> createOffice(@Valid @RequestBody CreateOfficeRequest request,
                                                UriComponentsBuilder uriBuilder) {
    var officeCreated=service.create(request);
    var location=uriBuilder.path("/api/offices/{id}").buildAndExpand(officeCreated.id()).toUri();
    return ResponseEntity.created(location).body(officeCreated);
}

    @GetMapping
    public ResponseEntity<Page<OfficeResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        var result = service.getAll(pageable);
        return ResponseEntity.ok(result);
     }  

    @PatchMapping("/{id}")
    public ResponseEntity<OfficeResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateOfficeRequest request){
        var result=service.update(id, request);
        return ResponseEntity.ok(result);
    }
}
