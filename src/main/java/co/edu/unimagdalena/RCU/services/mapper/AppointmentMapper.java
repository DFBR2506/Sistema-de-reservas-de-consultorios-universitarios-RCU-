package co.edu.unimagdalena.RCU.services.mapper;

import org.mapstruct.*;

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "appointmentType.id", target = "appointmentTypeId")
    @Mapping(source = "office.id", target = "officeId")
    AppointmentResponse toResponse(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointmentType", ignore = true)
    @Mapping(target = "office", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "endAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(CreateAppointmentRequest request);

}
