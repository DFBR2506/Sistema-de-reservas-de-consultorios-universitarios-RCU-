package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatientDtos {
    public record CreatePatientRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email,
            @Size(min = 10, max = 15) String phone,
            DocumentType documentType,
            String documentNumber,
            Gender gender
    ) implements Serializable {
    }

    public record UpdatePatientRequest(
            String firstName,
            String lastName,
            String email,
            String phone,
            DocumentType documentType,
            String documentNumber,
            Gender gender,
            Boolean active
    ) implements Serializable { }

    public record PatientResponse(
            UUID id,
            String firstName,
            String lastName,
            String email,
            String phone,
            DocumentType documentType,
            String documentNumber,
            Gender gender,
            Boolean active
    ) implements Serializable { }


}
