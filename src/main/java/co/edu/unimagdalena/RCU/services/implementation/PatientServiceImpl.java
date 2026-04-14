package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Patient;
import co.edu.unimagdalena.RCU.domine.repositories.PatientRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.PatientService;
import co.edu.unimagdalena.RCU.services.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public PatientResponse create(CreatePatientRequest request) {
        requireNonNull(request, "The request cannot be null");
        requireNonBlank(request.firstName(), "The first name cannot be blank");
        requireNonBlank(request.lastName(), "The last name cannot be blank");
        requireNonBlank(request.email(), "The email cannot be blank");
        requireNonBlank(request.documentNumber(), "The document number cannot be blank");
        if (patientRepository.existsByEmail(request.email())) {
            throw new ConflictException("A patient with email '" + request.email() + "' already exists");
        }
        if (patientRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new ConflictException("A patient with document number '" + request.documentNumber() + "' already exists");
        }
        Patient patient = patientMapper.toEntity(request);
        patient.setActive(true);
        patient.setCreatedAt(Instant.now());
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    @Override
    public PatientResponse getById(UUID id) {
        requireNonNull(id, "The id cannot be null");
        return patientMapper.toResponse(
            patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id '" + id + "' not found"))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PatientResponse> getAll(Pageable pageable) {
        requireNonNull(pageable, "The pageable cannot be null");
        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    public PatientResponse update(UUID id, UpdatePatientRequest request) {
        requireNonNull(id, "The id cannot be null");
        requireNonNull(request, "The request cannot be null");
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id '" + id + "' not found"));
        if (request.email() != null && !request.email().equals(patient.getEmail())) {
            if (patientRepository.existsByEmail(request.email())) {
                throw new ConflictException("A patient with email '" + request.email() + "' already exists");
            }
        }
        if (request.documentNumber() != null && !request.documentNumber().equals(patient.getDocumentNumber())) {
            if (patientRepository.existsByDocumentNumber(request.documentNumber())) {
                throw new ConflictException("A patient with document number '" + request.documentNumber() + "' already exists");
            }
        }
        patientMapper.updateEntity(request, patient);
        patient.setUpdatedAt(Instant.now());
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    private static void requireNonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void requireNonBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}