package co.edu.unimagdalena.RCU.domine.entities;
import java.util.*;

import co.edu.unimagdalena.RCU.domine.entities.enums.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Patient extends Person{
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Appointment> appointments = new HashSet<>();

    private void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setPatient(this);
    }
    
}