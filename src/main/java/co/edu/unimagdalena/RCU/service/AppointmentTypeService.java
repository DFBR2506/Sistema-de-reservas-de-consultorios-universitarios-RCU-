package co.edu.unimagdalena.RCU.service;

import java.util.List;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.AppointmentTypeResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.CreateAppointmentTypeRequest;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(CreateAppointmentTypeRequest request);
    List<AppointmentTypeResponse> getAll();
}
