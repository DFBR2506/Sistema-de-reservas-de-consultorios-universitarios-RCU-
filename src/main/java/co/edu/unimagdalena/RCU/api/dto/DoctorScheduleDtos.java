package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;

import jakarta.validation.constraints.*;

public class DoctorScheduleDtos {
    public record CreateDoctorScheduleRequest(
        @NotNull(message = "Day of week is required") DayOfWeek dayOfWeek,
        @NotNull(message = "Start time is required") LocalTime startTime,
        @NotNull(message = "End time is required") @Future LocalTime endTime
    ) implements Serializable{}

    public record DoctorScheduleResponse(
        @NotNull(message = "ID is required") UUID id,
        @NotNull(message = "Day of week is required") DayOfWeek dayOfWeek,
        @NotNull(message = "Start time is required") LocalTime startTime,
        @NotNull(message = "End time is required") @Future LocalTime endTime,
        @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable{}
}
