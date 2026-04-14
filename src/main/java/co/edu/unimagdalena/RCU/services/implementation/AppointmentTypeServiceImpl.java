package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.AppointmentType;
import co.edu.unimagdalena.RCU.domine.repositories.AppointmentTypeRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.services.AppointmentTypeService;
import co.edu.unimagdalena.RCU.services.mapper.AppointmentTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentTypeServiceImpl implements AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentTypeMapper appointmentTypeMapper;

    @Override
    public AppointmentTypeResponse create(CreateAppointmentTypeRequest request) {
        requireNonNull(request, "The request cannot be null");
        requireNonBlank(request.name(), "The appointment type name cannot be blank");
        requireNonNull(request.durationMinutes(), "The duration cannot be null");
        if (request.durationMinutes() <= 0) {
            throw new IllegalArgumentException("The duration must be greater than 0");
        }
        if (appointmentTypeRepository.existsByName(request.name())) {
            throw new ConflictException("An appointment type with name '" + request.name() + "' already exists");
        }
        AppointmentType appointmentType = appointmentTypeMapper.toEntity(request);
        appointmentType.setActive(true);
        appointmentType.setCreatedAt(Instant.now());
        return appointmentTypeMapper.toResponse(appointmentTypeRepository.save(appointmentType));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AppointmentTypeResponse> getAll(Pageable pageable) {
        requireNonNull(pageable, "The pageable cannot be null");
        return appointmentTypeRepository.findAll(pageable)
                .map(appointmentTypeMapper::toResponse);
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