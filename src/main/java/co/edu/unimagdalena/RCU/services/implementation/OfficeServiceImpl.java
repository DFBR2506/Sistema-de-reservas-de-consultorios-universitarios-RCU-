package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Office;
import co.edu.unimagdalena.RCU.domine.repositories.OfficeRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.OfficeService;
import co.edu.unimagdalena.RCU.services.mapper.OfficeMapper;
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
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    @Override
    public OfficeResponse create(CreateOfficeRequest request) {
        requireNonNull(request, "The request cannot be null");
        requireNonBlank(request.code(), "The office code cannot be blank");
        requireNonNull(request.floor(), "The floor cannot be null");
        if (officeRepository.existsByCode(request.code())) {
            throw new ConflictException("An office with code '" + request.code() + "' already exists");
        }
        Office office = officeMapper.toEntity(request);
        office.setActive(true);
        office.setCreatedAt(Instant.now());
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OfficeResponse> getAll(Pageable pageable) {
        requireNonNull(pageable, "The pageable cannot be null");
        return officeRepository.findAll(pageable)
                .map(officeMapper::toResponse);
    }

    @Override
    public OfficeResponse update(UUID id, UpdateOfficeRequest request) {
        requireNonNull(id, "The id cannot be null");
        requireNonNull(request, "The request cannot be null");
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office with id '" + id + "' not found"));
        if (request.code() != null && !request.code().equals(office.getCode())) {
            if (officeRepository.existsByCode(request.code())) {
                throw new ConflictException("An office with code '" + request.code() + "' already exists");
            }
        }
        officeMapper.updateEntity(request, office);
        office.setUpdatedAt(Instant.now());
        return officeMapper.toResponse(officeRepository.save(office));
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