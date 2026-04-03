package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

import co.edu.unimagdalena.RCU.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.entities.enums.Gender;

public class PatientDtos {
    public record CreatePatientRequest(
            String firstName,
            String lastName,
            String email,
            String phone,
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
