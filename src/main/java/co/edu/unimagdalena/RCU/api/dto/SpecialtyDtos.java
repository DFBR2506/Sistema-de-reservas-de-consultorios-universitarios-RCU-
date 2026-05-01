package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;
import jakarta.validation.constraints.*;

public class SpecialtyDtos {
    public record CreateSpecialtyRequest(
       @NotBlank(message = "Name is required") 
       String name,
       @Size(max = 200, message = "Description must be at most 200 characters")
        String description
    ) implements Serializable {}

    public record SpecialtyResponse(
       @NotNull(message = "ID is required") UUID id,
       @NotBlank(message = "Name is required") String name,
       @Size(max = 200, message = "Description must be at most 200 characters") String description,
       @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable {}
}
