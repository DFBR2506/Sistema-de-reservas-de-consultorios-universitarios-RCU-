package co.edu.unimagdalena.RCU.api.dtos;

import java.io.Serializable;
import java.time.Instant;

public class AvailabilityDtos {
    public record AvailabilitySlotResponse(
        Instant startAt,
        Instant endAt
    ) implements Serializable {}
}