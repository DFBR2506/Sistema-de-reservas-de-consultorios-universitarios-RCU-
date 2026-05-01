package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import jakarta.validation.constraints.*;

public class AppointmentTypeDtos {
    public record CreateAppointmentTypeRequest(
        @NotBlank(message = "Name is required") String name, 
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Duration in minutes is required") @Positive Integer durationMinutes
    ) implements Serializable{ }

    public record AppointmentTypeResponse(
        @NotNull(message = "ID is required") UUID id,
        @NotBlank(message = "Name is required") String name, 
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Duration in minutes is required") @Positive Integer durationMinutes,
        @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable{ }
}
