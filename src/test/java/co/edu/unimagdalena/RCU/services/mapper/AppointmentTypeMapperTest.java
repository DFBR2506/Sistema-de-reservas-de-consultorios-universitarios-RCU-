package co.edu.unimagdalena.RCU.service.mappers;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.*;
import co.edu.unimagdalena.RCU.entities.AppointmentType;
import co.edu.unimagdalena.RCU.mapper.AppointmentTypeMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class AppointmentTypeMapperTest {
    private final AppointmentTypeMapper appointmentTypeMapper = Mappers.getMapper(AppointmentTypeMapper.class);

    @Test
    public void toEntity_ShouldMapCreateAppointmentTypeRequestToAppointmentType() {
        // Given
        var request = new CreateAppointmentTypeRequest("Consultation", "Regular medical consultation", 30);

        // When
        var appointmentType = appointmentTypeMapper.toEntity(request);

        // Then
        assertThat(appointmentType).isNotNull();
        assertThat(appointmentType.getName()).isEqualTo(request.name());
        assertThat(appointmentType.getDescription()).isEqualTo(request.description());
        assertThat(appointmentType.getDurationMinutes()).isEqualTo(request.durationMinutes());
    }

    @Test
    public void toResponse_ShouldMapAppointmentTypeToAppointmentTypeResponse() {
        // Given
        var appointmentType = AppointmentType.builder()
            .id(UUID.randomUUID())
            .name("Consultation")
            .description("Regular medical consultation")
            .durationMinutes(30)
            .active(true)
            .build();

        // When
        var response = appointmentTypeMapper.toResponse(appointmentType);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(appointmentType.getId());
        assertThat(response.name()).isEqualTo(appointmentType.getName());
        assertThat(response.description()).isEqualTo(appointmentType.getDescription());
        assertThat(response.durationMinutes()).isEqualTo(appointmentType.getDurationMinutes());
        assertThat(response.active()).isEqualTo(appointmentType.getActive());
    }
}
