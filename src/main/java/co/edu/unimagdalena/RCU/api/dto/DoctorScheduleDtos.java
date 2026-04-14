package co.edu.unimagdalena.RCU.api.dto;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;

public class DoctorScheduleDtos {
    public record CreateDoctorScheduleRequest(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
    ) implements Serializable{}

    public record DoctorScheduleResponse(
        UUID id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Boolean active
    ) implements Serializable{}
}
