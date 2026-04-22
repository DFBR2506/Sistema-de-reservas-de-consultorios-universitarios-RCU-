package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;

import jakarta.validation.constraints.*;

public class DoctorDtos {
    public record CreateDoctorRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Phone is required") String phone,
        @NotBlank(message = "Email is required") @Email String email,
        @NotNull(message = "Document type is required") DocumentType documentType,
        @NotBlank(message = "Document number is required") String documentNumber,
        @NotNull(message = "Gender is required") Gender gender, 
        @NotBlank(message = "License number is required") String licenseNumber,
        @NotNull(message = "Specialty ID is required") UUID specialtyId
    ) implements Serializable{}

    public record UpdateDoctorRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Phone is required") String phone,
        @NotBlank(message = "Email is required") @Email String email,
        @NotNull(message = "Document type is required") DocumentType documentType,
        @NotBlank(message = "Document number is required") String documentNumber,
        @NotNull(message = "Gender is required") Gender gender, 
        @NotBlank(message = "License number is required") String licenseNumber,
        @NotNull(message = "Specialty ID is required") UUID specialtyId,
        @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable{}

    public record DoctorResponse(
        @NotNull(message = "ID is required") UUID id,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Phone is required") String phone,
        @NotBlank(message = "Email is required") @Email String email,
        @NotNull(message = "Document type is required") DocumentType documentType,
        @NotBlank(message = "Document number is required") String documentNumber,
        @NotNull(message = "Gender is required") Gender gender,
        @NotBlank(message = "License number is required") String licenseNumber,
        @NotNull(message = "Specialty ID is required") UUID specialtyId,
        @NotNull(message = "Active status is required") Boolean active
    ) implements Serializable{}


}
