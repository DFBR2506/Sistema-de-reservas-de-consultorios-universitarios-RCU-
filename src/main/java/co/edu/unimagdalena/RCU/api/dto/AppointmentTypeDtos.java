package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

import jakarta.validation.constraints.Min;

public class AppointmentTypeDtos {
    public record CreateAppointmentTypeRequest(
        @NotBlank String name, 
        @Min(1) String description,
        @Min(1) Integer durationMinutes
    ) implements Serializable{ }

    public record AppointmentTypeResponse(
        UUID id,
        String name, 
        String description,
        Integer durationMinutes,
        Boolean active
    ) implements Serializable{ }
}
