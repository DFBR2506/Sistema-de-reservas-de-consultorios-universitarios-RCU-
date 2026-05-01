package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import jakarta.validation.constraints.*;

public class OfficeDtos {
    public record CreateOfficeRequest(
        @NotBlank(message = "Code is required") String code,
        @NotNull(message = "Floor is required") @Positive Integer floor
    ) implements Serializable {}

    public record UpdateOfficeRequest(
        @NotBlank(message = "Code is required") String code,
        @NotNull(message = "Floor is required") @Positive Integer floor,
        @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable {}

    public record OfficeResponse(
        @NotNull(message = "ID is required") UUID id,
        @NotBlank(message = "Code is required") String code,
        @NotNull(message = "Floor is required") @Positive Integer floor,
        @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable {}
}
