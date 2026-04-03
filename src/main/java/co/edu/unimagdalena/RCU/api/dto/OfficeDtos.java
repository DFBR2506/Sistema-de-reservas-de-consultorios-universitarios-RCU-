package co.edu.unimagdalena.RCU.dto;

import java.io.Serializable;
import java.util.UUID;

public class OfficeDtos {
    public record CreateOfficeRequest(
        String code,
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
