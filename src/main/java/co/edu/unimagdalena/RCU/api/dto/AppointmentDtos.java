package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.Status;

import jakarta.validation.constraints.*;

public class AppointmentDtos {
    public record CreateAppointmentRequest(
        @NotNull(message = "Patient ID is required") UUID patientId,
        @NotNull(message = "Doctor ID is required") UUID doctorId,
        @NotNull(message = "Office ID is required") UUID officeId,
        @NotNull(message = "Appointment Type ID is required") UUID appointmentTypeId,
        @NotNull(message = "Start time is required") Instant startAt // no pongo endAt porque eso lo calcula el sistema dependiento el tipo de cita
    ) implements Serializable{} 

    public record CancelAppointmentRequest(
        @NotBlank(message = "Cancellation reason is required") String cancellationReason
    ) implements Serializable{}

    public record AppointmentResponse(
        @NotNull(message = "ID is required") UUID id,
        @NotNull(message = "Patient ID is required") UUID patientId,
        @NotNull(message = "Doctor ID is required") UUID doctorId,
        @NotNull(message = "Office ID is required") UUID officeId,
        @NotNull(message = "Appointment Type ID is required") UUID appointmentTypeId,
        @NotNull(message = "Start time is required") Instant startAt,
        @NotNull(message = "End time is required") Instant endAt,
        @NotNull(message = "Status is required") Status status,
        String cancellationReason,
        String notes
    ) implements Serializable{}


}
