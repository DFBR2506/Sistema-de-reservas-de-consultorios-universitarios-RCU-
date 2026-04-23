package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DoctorDtos {
    public record CreateDoctorRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        @Email @NotBlank String email,
        Gender gender,
        @NotBlank @Size(min = 10, max = 13) String licenseNumber,
        UUID specialtyId
    ) implements Serializable{}

    public record UpdateDoctorRequest(
        String firstName,
        String lastName,
        String phone,
        String email,
        DocumentType documentType,
        String documentNumber,
        Gender gender, 
        String licenseNumber,
        UUID specialtyId,
        Boolean active
    ) implements Serializable{}

    public record DoctorResponse(
        UUID id,
        String firstName,
        String lastName,
        String phone,
        String email,
        DocumentType documentType,
        String documentNumber,
        Gender gender,
        String licenseNumber,
        UUID specialtyId,
        Boolean active
    ) implements Serializable{}


}
