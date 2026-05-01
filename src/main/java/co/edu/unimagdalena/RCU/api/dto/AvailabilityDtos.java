package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.time.Instant;

import jakarta. validation.constraints.*;

public class AvailabilityDtos {
    public record AvailabilitySlotResponse(
        @NotNull(message = "Start time is required") Instant startAt,
        @NotNull(message = "End time is required") @Future Instant endAt
    ) implements Serializable {}
}