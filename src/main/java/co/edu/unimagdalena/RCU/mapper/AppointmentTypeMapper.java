package co.edu.unimagdalena.RCU.mapper;

import org.mapstruct.Mapper;
import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.*;

@Mapper(componentModel = "spring")
public interface AppointmentTypeMapper {
    AppointmentTypeResponse toResponse(AppointmentType appointmentType);

    AppointmentType toEntity(CreateAppointmentTypeRequest request);
}
