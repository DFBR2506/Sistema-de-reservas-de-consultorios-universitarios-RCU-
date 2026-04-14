package co.edu.unimagdalena.RCU.services.implementation;

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.*;
import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.domine.entities.enums.Status;
import co.edu.unimagdalena.RCU.domine.repositories.*;
import co.edu.unimagdalena.RCU.exceptions.*;
import co.edu.unimagdalena.RCU.services.AppointmentService;
import co.edu.unimagdalena.RCU.services.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AppointmentResponse create(CreateAppointmentRequest request) {
        requireNonNull(request, "The request cannot be null");

        // Validar que no sea en el pasado
        if (request.startAt().isBefore(Instant.now())) {
            throw new BusinessException("Cannot create an appointment in the past");
        }

        // Validar paciente
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id '" + request.patientId() + "' not found"));
        if (!patient.getActive()) {
            throw new BusinessException("The patient is not active");
        }

        // Validar doctor
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id '" + request.doctorId() + "' not found"));
        if (!doctor.getActive()) {
            throw new BusinessException("The doctor is not active");
        }

        // Validar consultorio
        Office office = officeRepository.findById(request.officeId())
                .orElseThrow(() -> new ResourceNotFoundException("Office with id '" + request.officeId() + "' not found"));
        if (!office.getActive()) {
            throw new BusinessException("The office is not active");
        }

        // Validar tipo de cita y calcular endAt
        AppointmentType appointmentType = appointmentTypeRepository.findById(request.appointmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment type with id '" + request.appointmentTypeId() + "' not found"));
        if (!appointmentType.getActive()) {
            throw new BusinessException("The appointment type is not active");
        }
        Instant endAt = request.startAt().plusSeconds(appointmentType.getDurationMinutes() * 60L);

        // Validar que la cita esté dentro del horario del doctor
        validateDoctorSchedule(request.doctorId(), request.startAt(), endAt);

        // Validar traslape doctor
        if (appointmentRepository.existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(
                request.doctorId(), endAt, request.startAt())) {
            throw new ConflictException("The doctor already has an appointment in this time range");
        }

        // Validar traslape consultorio
        if (appointmentRepository.existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(
                request.officeId(), endAt, request.startAt())) {
            throw new ConflictException("The office already has an appointment in this time range");
        }

        // Validar traslape paciente
        if (appointmentRepository.existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(
                request.patientId(), endAt, request.startAt())) {
            throw new ConflictException("The patient already has an appointment in this time range");
        }

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setOffice(office);
        appointment.setAppointmentType(appointmentType);
        appointment.setEndAt(endAt);
        appointment.setStatus(Status.SCHEDULED);
        appointment.setCreatedAt(Instant.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    @Override
    public AppointmentResponse getById(UUID id) {
        requireNonNull(id, "The id cannot be null");
        return appointmentMapper.toResponse(
            appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id '" + id + "' not found"))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AppointmentResponse> getAll(Pageable pageable) {
        requireNonNull(pageable, "The pageable cannot be null");
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toResponse);
    }

    @Override
    public AppointmentResponse confirm(UUID id) {
        requireNonNull(id, "The id cannot be null");
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id '" + id + "' not found"));
        if (appointment.getStatus() != Status.SCHEDULED) {
            throw new BusinessException("Only SCHEDULED appointments can be confirmed");
        }
        appointment.setStatus(Status.CONFIRMED);
        appointment.setUpdatedAt(Instant.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse cancel(UUID id, CancelAppointmentRequest request) {
        requireNonNull(id, "The id cannot be null");
        requireNonNull(request, "The request cannot be null");
        requireNonBlank(request.cancellationReason(), "The cancellation reason cannot be blank");
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id '" + id + "' not found"));
        if (appointment.getStatus() != Status.SCHEDULED && appointment.getStatus() != Status.CONFIRMED) {
            throw new BusinessException("Only SCHEDULED or CONFIRMED appointments can be cancelled");
        }
        appointment.setStatus(Status.CANCELLED);
        appointment.setCancellationReason(request.cancellationReason());
        appointment.setUpdatedAt(Instant.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse complete(UUID id) {
        requireNonNull(id, "The id cannot be null");
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id '" + id + "' not found"));
        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED appointments can be completed");
        }
        if (Instant.now().isBefore(appointment.getStartAt())) {
            throw new BusinessException("Cannot complete an appointment before its start time");
        }
        appointment.setStatus(Status.COMPLETED);
        appointment.setUpdatedAt(Instant.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse markAsNoShow(UUID id) {
        requireNonNull(id, "The id cannot be null");
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id '" + id + "' not found"));
        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED appointments can be marked as NO_SHOW");
        }
        if (Instant.now().isBefore(appointment.getStartAt())) {
            throw new BusinessException("Cannot mark an appointment as NO_SHOW before its start time");
        }
        appointment.setStatus(Status.NO_SHOW);
        appointment.setUpdatedAt(Instant.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    private void validateDoctorSchedule(UUID doctorId, Instant startAt, Instant endAt) {
        LocalTime start = LocalTime.ofInstant(startAt, ZoneOffset.UTC);
        LocalTime end = LocalTime.ofInstant(endAt, ZoneOffset.UTC);
        
        DayOfWeek day = DayOfWeek.valueOf(
            startAt.atZone(ZoneOffset.UTC).getDayOfWeek().name()
        );

        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, day)
                .orElseThrow(() -> new BusinessException("The doctor has no schedule for this day"));

        if (start.isBefore(schedule.getStartTime()) || end.isAfter(schedule.getEndTime())) {
            throw new BusinessException("The appointment is outside the doctor's working hours");
        }
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