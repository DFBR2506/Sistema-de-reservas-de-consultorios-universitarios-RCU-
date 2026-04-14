package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.repositories.SpecialtyRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.SpecialtyService;
import co.edu.unimagdalena.RCU.services.mapper.SpecialtyMapper;
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
public class SpecialtyServiceImpl implements SpecialtyService {
    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public SpecialtyResponse create(CreateSpecialtyRequest request) {
        requireNonNull(request, "The request cannot be null");
        requireNonBlank(request.name(), "The specialty name cannot be blank");
        if (specialtyRepository.existsByName(request.name())) {
            throw new ConflictException("A specialty with the name '" + request.name() + "' already exists");
        }
        Specialty specialty = specialtyMapper.toEntity(request);
        specialty.setActive(true);
        specialty.setCreatedAt(Instant.now());
        return specialtyMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Transactional(readOnly = true)
    @Override
    public SpecialtyResponse getById(UUID id) {
        requireNonNull(id, "The ID cannot be null");
        return specialtyMapper.toResponse(
            specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty with id '" + id + "' not found"))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SpecialtyResponse> getAll(Pageable pageable) {
        requireNonNull(pageable, "The pageable cannot be null");
        return specialtyRepository.findAll(pageable)
            .map(specialtyMapper::toResponse);
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
