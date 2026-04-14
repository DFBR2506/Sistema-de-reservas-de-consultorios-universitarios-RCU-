package co.edu.unimagdalena.RCU.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.*;

@Mapper(componentModel = "spring")
public interface AppointmentTypeMapper {
    AppointmentTypeResponse toResponse(AppointmentType appointmentType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AppointmentType toEntity(CreateAppointmentTypeRequest request);
}
