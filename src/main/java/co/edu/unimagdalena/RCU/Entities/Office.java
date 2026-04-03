package co.edu.unimagdalena.RCU.entities;
import java.util.*;

import co.edu.unimagdalena.RCU.entities.enums.DayOfWeek;
import lombok.*;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "offices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private Integer code ;

    @Column(name = "floor", nullable = false)
    private String floor;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Appointment> appointments = new HashSet<>();

    private void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setOffice(this);
    }

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
