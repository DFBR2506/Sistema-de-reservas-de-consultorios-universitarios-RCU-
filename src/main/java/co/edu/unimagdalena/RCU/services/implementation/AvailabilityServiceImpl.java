package co.edu.unimagdalena.RCU.service.implementation;

import co.edu.unimagdalena.RCU.api.dto.AvailabilityDtos.*;
import co.edu.unimagdalena.RCU.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.repository.AppointmentRepository;
import co.edu.unimagdalena.RCU.repository.DoctorRepository;
import co.edu.unimagdalena.RCU.repository.DoctorScheduleRepository;
import co.edu.unimagdalena.RCU.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AvailabilitySlotResponse> getAvailableSlots(UUID doctorId, UUID officeId, LocalDate date) {
        requireNonNull(doctorId, "The doctor id cannot be null");
        requireNonNull(officeId, "The office id cannot be null");
        requireNonNull(date, "The date cannot be null");

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor with id '" + doctorId + "' not found");
        }

        // Obtener el día de la semana
        DayOfWeek day = DayOfWeek.valueOf(date.getDayOfWeek().name());

        // Buscar horario del doctor para ese día
        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, day)
                .orElseThrow(() -> new ResourceNotFoundException("The doctor has no schedule for " + day));

        // Convertir el horario a Instant
        Instant dayStart = date.atTime(schedule.getStartTime()).toInstant(ZoneOffset.UTC);
        Instant dayEnd = date.atTime(schedule.getEndTime()).toInstant(ZoneOffset.UTC);

        // Obtener citas existentes del doctor en ese día
        List<Instant[]> occupiedSlots = appointmentRepository
                .findByDoctorIdAndStartAtBetween(doctorId, dayStart, dayEnd)
                .stream()
                .map(a -> new Instant[]{a.getStartAt(), a.getEndAt()})
                .toList();

        // También verificar ocupación del consultorio
        List<Instant[]> officeOccupied = appointmentRepository
                .findByOfficeIdAndStartAtBetween(officeId, dayStart, dayEnd)
                .stream()
                .map(a -> new Instant[]{a.getStartAt(), a.getEndAt()})
                .toList();

        // Calcular slots disponibles de 30 minutos
        List<AvailabilitySlotResponse> slots = new ArrayList<>();
        Instant slotStart = dayStart;
        long slotDuration = 30 * 60L;

        while (slotStart.plusSeconds(slotDuration).compareTo(dayEnd) <= 0) {
            Instant slotEnd = slotStart.plusSeconds(slotDuration);
            if (isSlotFree(slotStart, slotEnd, occupiedSlots) && isSlotFree(slotStart, slotEnd, officeOccupied)) {
                slots.add(new AvailabilitySlotResponse(slotStart, slotEnd));
            }
            slotStart = slotStart.plusSeconds(slotDuration);
        }

        return slots;
    }

    private boolean isSlotFree(Instant slotStart, Instant slotEnd, List<Instant[]> occupied) {
        return occupied.stream().noneMatch(o ->
            slotStart.isBefore(o[1]) && slotEnd.isAfter(o[0])
        );
    }

    private static void requireNonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(message);
        }
    }
}