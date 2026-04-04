package co.edu.unimagdalena.RCU.service.mappers;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.*;
import co.edu.unimagdalena.RCU.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.mapper.DoctorScheduleMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class DoctorScheduleMapperTest {
    private final DoctorScheduleMapper doctorScheduleMapper = Mappers.getMapper(DoctorScheduleMapper.class);

    @Test
    public void toEntity_ShouldMapCreateDoctorScheduleRequestToDoctorSchedule() {
        // Given
        var request = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));

        // When
        var doctorSchedule = doctorScheduleMapper.toEntity(request);

        // Then
        assertThat(doctorSchedule).isNotNull();
        assertThat(doctorSchedule.getDayOfWeek()).isEqualTo(request.dayOfWeek());
        assertThat(doctorSchedule.getStartTime()).isEqualTo(request.startTime());
        assertThat(doctorSchedule.getEndTime()).isEqualTo(request.endTime());
    }

    @Test
    public void toResponse_ShouldMapDoctorScheduleToDoctorScheduleResponse() {
        // Given
        var doctorSchedule = DoctorSchedule.builder()
            .id(UUID.randomUUID())
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(8, 0))
            .endTime(LocalTime.of(12, 0))
            .active(true)
            .build();

        // When
        var response = doctorScheduleMapper.toResponse(doctorSchedule);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(doctorSchedule.getId());
        assertThat(response.dayOfWeek()).isEqualTo(doctorSchedule.getDayOfWeek());
        assertThat(response.startTime()).isEqualTo(doctorSchedule.getStartTime());
        assertThat(response.endTime()).isEqualTo(doctorSchedule.getEndTime());
        assertThat(response.active()).isEqualTo(doctorSchedule.getActive());
    }
}
