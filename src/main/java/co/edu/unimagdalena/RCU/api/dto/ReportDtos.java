package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.util.UUID;

public class ReportDtos {
    public record OfficeOccupancyResponse(
        UUID officeId,
        String code,
        Long totalAppointments
    ) implements Serializable {}

    public record DoctorProductivityResponse(
        UUID doctorId,
        String firstName,
        String lastName,
        Long completedAppointments
    ) implements Serializable {}

    public record NoShowPatientResponse(
        UUID patientId,
        String firstName,
        String lastName,
        Long noShowCount
    ) implements Serializable {}
}