package co.edu.unimagdalena.RCU.services;

import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.AppointmentResponse;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CancelAppointmentRequest;
import co.edu.unimagdalena.RCU.api.dto.AppointmentDtos.CreateAppointmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest request);
    AppointmentResponse getById(UUID id);
    Page<AppointmentResponse> getAll(Pageable pageable);

    AppointmentResponse confirm(UUID id);
    AppointmentResponse cancel(UUID id, CancelAppointmentRequest request);
    AppointmentResponse complete(UUID id);
    AppointmentResponse markAsNoShow(UUID id);
}
