package co.edu.unimagdalena.RCU.services;

import java.util.List;
import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.CreateOfficeRequest;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.OfficeResponse;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.UpdateOfficeRequest;



public interface OfficeService {
    OfficeResponse create(CreateOfficeRequest request);
    List<OfficeResponse> getAll();
    OfficeResponse update(UUID id, UpdateOfficeRequest request);
}
