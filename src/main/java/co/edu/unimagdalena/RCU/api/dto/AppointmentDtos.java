package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import co.edu.unimagdalena.RCU.entities.enums.Status;

public class AppointmentDtos {
    public record CreateAppointmentRequest(
        UUID patientId,
        UUID doctorId,
        UUID officeId,
        UUID appointmentTypeId,
        Instant startAt // no pongo endAt porque eso lo calcula el sistema dependiento el tipo de cita
    ) implements Serializable{} 

    public record CancelAppointmentRequest(
        String cancellationReason
    ) implements Serializable{}

    public record AppointmentResponse(
        UUID id,
        UUID patientId,
        UUID doctorId,
        UUID officeId,
        UUID appointmentTypeId,
        Instant startAt,
        Instant endAt,
        Status status,
        String cancellationReason,
        String notes
    ) implements Serializable{}


}
