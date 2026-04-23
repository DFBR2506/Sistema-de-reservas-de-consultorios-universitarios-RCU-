package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class SpecialtyDtos {
    public record CreateSpecialtyRequest(
        @NotBlank String name,
        String description
    ) implements Serializable {}

    public record SpecialtyResponse(
        UUID id,
        String name,
        String description,
        Boolean active
    ) implements Serializable {}
}
