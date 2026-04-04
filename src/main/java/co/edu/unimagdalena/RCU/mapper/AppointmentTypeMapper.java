package co.edu.unimagdalena.RCU.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.*;

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
