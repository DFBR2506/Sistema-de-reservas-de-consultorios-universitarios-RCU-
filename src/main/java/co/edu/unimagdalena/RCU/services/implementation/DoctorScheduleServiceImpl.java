package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.domine.repositories.DoctorRepository;
import co.edu.unimagdalena.RCU.domine.repositories.DoctorScheduleRepository;
import co.edu.unimagdalena.RCU.exceptions.BusinessException;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.DoctorScheduleService;
import co.edu.unimagdalena.RCU.services.mapper.DoctorScheduleMapper;
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
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Override
    public DoctorScheduleResponse create(UUID doctorId, CreateDoctorScheduleRequest request) {
        requireNonNull(doctorId, "The doctor id cannot be null");
        requireNonNull(request, "The request cannot be null");
        requireNonNull(request.dayOfWeek(), "The day of week cannot be null");
        requireNonNull(request.startTime(), "The start time cannot be null");
        requireNonNull(request.endTime(), "The end time cannot be null");

        if (request.startTime().isAfter(request.endTime())) {
            throw new BusinessException("The start time must be before the end time");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id '" + doctorId + "' not found"));
        if (!doctor.getActive()) {
            throw new BusinessException("The doctor is not active");
        }

        if (doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctorId, request.dayOfWeek())) {
            throw new ConflictException("The doctor already has a schedule for " + request.dayOfWeek());
        }

        DoctorSchedule schedule = doctorScheduleMapper.toEntity(request);
        schedule.setDoctor(doctor);
        schedule.setActive(true);
        schedule.setCreatedAt(Instant.now());
        return doctorScheduleMapper.toResponse(doctorScheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DoctorScheduleResponse> getAllSchedules(UUID doctorId, Pageable pageable) {
        requireNonNull(doctorId, "The doctor id cannot be null");
        requireNonNull(pageable, "The pageable cannot be null");
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor with id '" + doctorId + "' not found");
        }
        return doctorScheduleRepository.findByDoctorId(doctorId, pageable)
                .map(doctorScheduleMapper::toResponse);
    }

    private static void requireNonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(message);
        }
    }
}