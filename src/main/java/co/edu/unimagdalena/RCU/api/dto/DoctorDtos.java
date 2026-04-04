package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import co.edu.unimagdalena.RCU.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.entities.enums.Gender;

public class DoctorDtos {
    public record CreateDoctorRequest(
        String firstName,
        String lastName,
        String phone,
        String email,
        DocumentType documentType,
        String documentNumber,
        Gender gender, 
        String licenseNumber,
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
