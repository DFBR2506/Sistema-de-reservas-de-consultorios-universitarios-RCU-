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

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.AppointmentResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CancelAppointmentRequest;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CreateAppointmentRequest;
import co.edu.unimagdalena.RCU.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

	private final AppointmentService service;

	@PostMapping
	public ResponseEntity<AppointmentResponse> createAppointment(
			@Valid @RequestBody CreateAppointmentRequest request,
			UriComponentsBuilder uriBuilder) {
		var created = service.create(request);
		var location = uriBuilder.path("/api/appointments/{id}")
				.buildAndExpand(created.id())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AppointmentResponse> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(service.getById(id));
	}

	@GetMapping
	public ResponseEntity<Page<AppointmentResponse>> list(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		var pageable = PageRequest.of(page, size, Sort.by("startAt").descending());
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@PatchMapping("/{id}/confirm")
	public ResponseEntity<AppointmentResponse> confirm(@PathVariable UUID id) {
		return ResponseEntity.ok(service.confirm(id));
	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<AppointmentResponse> cancel(
			@PathVariable UUID id,
			@Valid @RequestBody CancelAppointmentRequest request) {
		return ResponseEntity.ok(service.cancel(id, request));
	}

	@PatchMapping("/{id}/complete")
	public ResponseEntity<AppointmentResponse> complete(@PathVariable UUID id) {
		return ResponseEntity.ok(service.complete(id));
	}

	@PatchMapping("/{id}/no-show")
	public ResponseEntity<AppointmentResponse> markAsNoShow(@PathVariable UUID id) {
		return ResponseEntity.ok(service.markAsNoShow(id));
	}
}
