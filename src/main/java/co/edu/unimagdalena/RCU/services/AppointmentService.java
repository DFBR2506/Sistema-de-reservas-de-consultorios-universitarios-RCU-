package co.edu.unimagdalena.RCU.services;

import java.util.List;
import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.AppointmentResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CancelAppointmentRequest;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CreateAppointmentRequest;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest request);
    AppointmentResponse getById(UUID id);
    List<AppointmentResponse> getAll();

    AppointmentResponse confirm(UUID id);
    AppointmentResponse cancel(UUID id, CancelAppointmentRequest request);
    AppointmentResponse complete(UUID id);
    AppointmentResponse markAsNoShow(UUID id);
}
