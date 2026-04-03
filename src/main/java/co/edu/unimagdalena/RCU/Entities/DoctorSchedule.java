package co.edu.unimagdalena.RCU.Entities;
import java.util.*;
import lombok.*;

import co.edu.unimagdalena.RCU.Entities.Enums.DayOfWeek;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "doctor_schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "active", nullable = false, updatable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
