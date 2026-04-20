package co.edu.unimagdalena.RCU.api.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unimagdalena.RCU.api.dto.AvailabilityDtos.AvailabilitySlotResponse;
import co.edu.unimagdalena.RCU.services.AvailabilityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors/{doctorId}/availability")
@RequiredArgsConstructor
@Validated
public class AvailabilityController {
    private final AvailabilityService service;

    @GetMapping
    public ResponseEntity<List<AvailabilitySlotResponse>> getAvailability(
            @PathVariable UUID doctorId,
            @RequestParam UUID officeId,
            @RequestParam LocalDate date) {
        var availability = service.getAvailableSlots(doctorId, officeId, date);
        return ResponseEntity.ok(availability);
    }
}
