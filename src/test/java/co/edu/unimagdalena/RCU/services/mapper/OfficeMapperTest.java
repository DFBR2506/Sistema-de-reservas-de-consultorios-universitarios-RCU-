package co.edu.unimagdalena.RCU.services.mapper;

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Office;
import co.edu.unimagdalena.RCU.services.mapper.OfficeMapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class OfficeMapperTest {
    private final OfficeMapper officeMapper = Mappers.getMapper(OfficeMapper.class);

    @Test
    public void toEntity_ShouldMapCreateOfficeRequestToOffice() {
        // Given
        var request = new CreateOfficeRequest("CON-101", 3);

        // When
        var office = officeMapper.toEntity(request);

        // Then
        assertThat(office).isNotNull();
        assertThat(office.getCode()).isEqualTo(request.code());
        assertThat(office.getFloor()).isEqualTo(request.floor());
    }

    @Test
    public void toResponse_ShouldMapOfficeToOfficeResponse() {
        // Given
        var office = Office.builder()
            .id(UUID.randomUUID())
            .code("CON-101")
            .floor(3)
            .active(true)
            .build();

        // When
        var response = officeMapper.toResponse(office);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(office.getId());
        assertThat(response.code()).isEqualTo(office.getCode());
        assertThat(response.floor()).isEqualTo(office.getFloor());
        assertThat(response.active()).isEqualTo(office.getActive());
    }

    @Test
    public void updateEntity_ShouldUpdateOfficeWithUpdateOfficeRequest() {
        // Given
        var office = Office.builder()
            .id(UUID.randomUUID())
            .code("CON-101")
            .floor(3)
            .active(true)
            .build();
        var request = new UpdateOfficeRequest("CON-102", 4, false);

        // When
        officeMapper.updateEntity(request, office);

        // Then
        assertThat(office.getCode()).isEqualTo(request.code());
        assertThat(office.getFloor()).isEqualTo(request.floor());
        assertThat(office.getActive()).isEqualTo(request.active());
    }

    @Test
    public void updateEntity_ShouldIgnoreNullValuesInUpdateOfficeRequest() {
        // Given
        var office = Office.builder()
            .id(UUID.randomUUID())
            .code("CON-101")
            .floor(3)
            .active(true)
            .build();
        var request = new UpdateOfficeRequest(null, 5, null);

        // When
        officeMapper.updateEntity(request, office);

        // Then
        assertThat(office.getCode()).isEqualTo("CON-101");
        assertThat(office.getFloor()).isEqualTo(5);
        assertThat(office.getActive()).isEqualTo(true);
    }
}
