package co.edu.unimagdalena.RCU.service.mappers;

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import co.edu.unimagdalena.RCU.entities.Specialty;
import co.edu.unimagdalena.RCU.mapper.SpecialtyMapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecialtyMapperTest {
    private final SpecialtyMapper specialtyMapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test 
    public void toEntity_ShouldMapCreateSpecialtyRequestToSpecialty() {
        // Given
        var request = new CreateSpecialtyRequest("Cardiology", "Heart specialty");

        // When
        var specialty = specialtyMapper.toEntity(request);

        // Then
        assertThat(specialty).isNotNull();
        assertThat(specialty.getName()).isEqualTo(request.name());
        assertThat(specialty.getDescription()).isEqualTo(request.description());
    }

    @Test
    public void toResponse_ShouldMapSpecialtyToSpecialtyResponse() {
        // Given
        var specialty = Specialty.builder()
            .id(UUID.randomUUID())
            .name("Cardiology")
            .description("Heart specialty")
            .active(true)
            .build();
        // When
        var response = specialtyMapper.toResponse(specialty);
        // Then 
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(specialty.getId());
        assertThat(response.name()).isEqualTo(specialty.getName());
        assertThat(response.description()).isEqualTo(specialty.getDescription());
        assertThat(response.active()).isEqualTo(specialty.getActive());
    }
}
