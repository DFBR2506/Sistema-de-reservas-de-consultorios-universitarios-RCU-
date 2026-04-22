package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;

import jakarta.validation.constraints.*;

public class PatientDtos {
    public record CreatePatientRequest(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") String email,
            @NotBlank(message = "Phone is required") String phone,
            @NotNull(message = "Document type is required") DocumentType documentType,
            @NotBlank(message = "Document number is required") String documentNumber,
            @NotNull(message = "Gender is required") Gender gender
    ) implements Serializable {
    }

    public record UpdatePatientRequest(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") String email,
            @NotBlank(message = "Phone is required") String phone,
            @NotNull(message = "Document type is required") DocumentType documentType,
            @Size(max = 20, message = "Document number must be at most 20 characters") String documentNumber,
            @NotNull(message = "Gender is required") Gender gender,
            @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable { }

    public record PatientResponse(
            @NotNull(message = "ID is required") UUID id,
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") String email,
            @NotBlank(message = "Phone is required") String phone,
            @NotNull(message = "Document type is required") DocumentType documentType,
            @Size(max = 20, message = "Document number must be at most 20 characters") String documentNumber,
            @NotNull(message = "Gender is required") Gender gender,
            @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable { }


}
