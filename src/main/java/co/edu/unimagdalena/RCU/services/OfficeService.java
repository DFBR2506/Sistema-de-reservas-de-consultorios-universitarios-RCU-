package co.edu.unimagdalena.RCU.services;

import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.CreateOfficeRequest;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.OfficeResponse;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.UpdateOfficeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface OfficeService {
    OfficeResponse create(CreateOfficeRequest request);
    Page<OfficeResponse> getAll(Pageable pageable);
    OfficeResponse update(UUID id, UpdateOfficeRequest request);
}
