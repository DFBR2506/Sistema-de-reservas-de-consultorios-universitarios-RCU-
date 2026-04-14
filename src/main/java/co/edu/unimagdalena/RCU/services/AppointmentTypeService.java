package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.AppointmentTypeResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.CreateAppointmentTypeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(CreateAppointmentTypeRequest request);
    Page<AppointmentTypeResponse> getAll(Pageable pageable);
}
