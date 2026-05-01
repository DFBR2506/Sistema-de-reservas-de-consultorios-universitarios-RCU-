package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;
import jakarta.validation.constraints.*;

public class ReportDtos {
    public record OfficeOccupancyResponse(
        @NotNull(message = "Office ID is required") UUID officeId,
        @NotBlank(message = "Code is required") String code,
        @NotNull(message = "Total appointments is required") Long totalAppointments
    ) implements Serializable {}

    public record DoctorProductivityResponse(
        @NotNull(message = "Doctor ID is required") UUID doctorId,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotNull(message = "Completed appointments is required") Long completedAppointments
    ) implements Serializable {}

    public record NoShowPatientResponse(
        @NotNull(message = "Patient ID is required") UUID patientId,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotNull(message = "No-show count is required") Long noShowCount
    ) implements Serializable {}
}