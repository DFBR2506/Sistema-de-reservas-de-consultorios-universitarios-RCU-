package co.edu.unimagdalena.RCU.services.implementations;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.repositories.DoctorRepository;
import co.edu.unimagdalena.RCU.domine.repositories.SpecialtyRepository;
import co.edu.unimagdalena.RCU.exceptions.BusinessException;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.DoctorService;
import co.edu.unimagdalena.RCU.services.mappers.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public DoctorResponse create(CreateDoctorRequest request) {
        requireNonNull(request, "The request cannot be null");
        requireNonBlank(request.firstName(), "The first name cannot be blank");
        requireNonBlank(request.lastName(), "The last name cannot be blank");
        requireNonBlank(request.email(), "The email cannot be blank");
        requireNonBlank(request.licenseNumber(), "The license number cannot be blank");
        requireNonNull(request.specialtyId(), "The specialty id cannot be null");

        if (doctorRepository.existsByEmail(request.email())) {
            throw new ConflictException("A doctor with email '" + request.email() + "' already exists");
        }
        if (doctorRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new ConflictException("A doctor with document number '" + request.documentNumber() + "' already exists");
        }
        if (doctorRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new ConflictException("A doctor with license number '" + request.licenseNumber() + "' already exists");
        }

        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty with id '" + request.specialtyId() + "' not found"));
        if (!specialty.getActive()) {
            throw new BusinessException("The specialty is not active");
        }
        
        Doctor doctor = doctorMapper.toEntity(request);
        doctor.setSpecialty(specialty);
        doctor.setActive(true);
        doctor.setCreatedAt(Instant.now());
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    @Override
    public DoctorResponse getDoctorById(UUID id) {
        requireNonNull(id, "The id cannot be null");
        return doctorMapper.toResponse(
            doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id '" + id + "' not found"))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Override
    public DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest request) {
        requireNonNull(id, "The id cannot be null");
        requireNonNull(request, "The request cannot be null");

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id '" + id + "' not found"));

        if (request.email() != null && !request.email().equals(doctor.getEmail())) {
            if (doctorRepository.existsByEmail(request.email())) {
                throw new ConflictException("A doctor with email '" + request.email() + "' already exists");
            }
        }
        if (request.documentNumber() != null && !request.documentNumber().equals(doctor.getDocumentNumber())) {
            if (doctorRepository.existsByDocumentNumber(request.documentNumber())) {
                throw new ConflictException("A doctor with document number '" + request.documentNumber() + "' already exists");
            }
        }
        if (request.licenseNumber() != null && !request.licenseNumber().equals(doctor.getLicenseNumber())) {
            if (doctorRepository.existsByLicenseNumber(request.licenseNumber())) {
                throw new ConflictException("A doctor with license number '" + request.licenseNumber() + "' already exists");
            }
        }
        if (request.specialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.specialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty with id '" + request.specialtyId() + "' not found"));
            if (!specialty.getActive()) {
                throw new BusinessException("The specialty is not active");
            }
            doctor.setSpecialty(specialty);
        }

        doctorMapper.updateEntity(request, doctor);
        doctor.setUpdatedAt(Instant.now());
        return doctorMapper.toResponse(doctorRepository.save(doctor));
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