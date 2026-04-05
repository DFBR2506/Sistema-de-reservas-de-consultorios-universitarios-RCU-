package co.edu.unimagdalena.RCU.service.implementation;

import co.edu.unimagdalena.RCU.service.SpecialtyService;
import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import co.edu.unimagdalena.RCU.entities.Specialty;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.mapper.SpecialtyMapper;
import co.edu.unimagdalena.RCU.repository.SpecialtyRepository;
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
    public List<SpecialtyResponse> getAll() {
        return specialtyRepository.findAll()
            .stream()
            .map(specialtyMapper::toResponse)
            .toList();
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
