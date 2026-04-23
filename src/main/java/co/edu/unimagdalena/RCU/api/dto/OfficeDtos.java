package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class OfficeDtos {
    public record CreateOfficeRequest(
        @NotBlank String code,
        Integer floor
    ) implements Serializable {}

    public record UpdateOfficeRequest(
        String code,
        Integer floor,
        Boolean active
    ) implements Serializable {}

    public record OfficeResponse(
        UUID id,
        String code,
        Integer floor,
        Boolean active
    ) implements Serializable {}
}
