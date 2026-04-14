package co.edu.unimagdalena.RCU.domine.entities;

import java.util.*;
import lombok.*;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Doctor> doctors = new HashSet<>();

    private void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        doctor.setSpecialty(this);
    }

    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
