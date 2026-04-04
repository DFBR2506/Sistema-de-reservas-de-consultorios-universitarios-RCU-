package co.edu.unimagdalena.RCU.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.AvailabilityDtos.AvailabilitySlotResponse;

public interface AvailabilityService {
List<AvailabilitySlotResponse> getAvailableSlots(UUID doctorId, UUID officeId, LocalDate date);
}
