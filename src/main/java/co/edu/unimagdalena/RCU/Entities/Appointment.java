package co.edu.unimagdalena.RCU.entities;
import java.util.*;

import co.edu.unimagdalena.RCU.entities.enums.Status;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne
    @JoinColumn(name = "appointment_type_id", nullable = false)
    private AppointmentType appointmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "start_at", nullable = false)
    private LocalTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalTime endAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
