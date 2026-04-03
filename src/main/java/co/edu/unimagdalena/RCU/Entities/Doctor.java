package co.edu.unimagdalena.RCU.Entities;

import co.edu.unimagdalena.RCU.Entities.Enums.DocumentType;
import co.edu.unimagdalena.RCU.Entities.Enums.*;
import java.util.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Doctor extends Person {

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @ManyToOne
    @JoinColumn(name = "Specialty_id", nullable = false)
    private Specialty specialty;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private Set<DoctorSchedule> schedules = new HashSet<>();

    private void addSchedule(DoctorSchedule schedule) {
        schedules.add(schedule);
        schedule.setDoctor(this);
    }

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private Set<Appointment> appointments = new HashSet<>();

    private void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setDoctor(this);
    }

}
