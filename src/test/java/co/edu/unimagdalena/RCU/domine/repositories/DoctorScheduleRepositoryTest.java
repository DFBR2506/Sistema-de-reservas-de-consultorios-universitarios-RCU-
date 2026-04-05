package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;

class DoctorScheduleRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("DoctorSchedule: consulta por doctor y dia")
    void shouldQueryByDoctorAndDayOfWeek() {
	String suffix = UUID.randomUUID().toString().substring(0, 8);

	Specialty specialty = specialtyRepository.save(Specialty.builder()
		.name("Medicina Interna-" + suffix)
		.description("Especialidad")
		.active(true)
		.createdAt(Instant.now())
		.build());

	Doctor doctor = doctorRepository.save(Doctor.builder()
		.firstName("Jose")
		.lastName("Rojas")
		.email("jose." + suffix + "@demo.com")
		.phone("3222222222")
		.documentType(DocumentType.CC)
		.documentNumber("DOCSCH-" + suffix)
		.gender(Gender.MALE)
		.active(true)
		.createdAt(Instant.now())
		.licenseNumber("LICSCH-" + suffix)
		.specialty(specialty)
		.build());

	DoctorSchedule schedule = DoctorSchedule.builder()
		.doctor(doctor)
		.dayOfWeek(DayOfWeek.MONDAY)
		.startTime(LocalTime.of(8, 0))
		.endTime(LocalTime.of(12, 0))
		.active(true)
		.createdAt(Instant.now())
		.build();
	doctorScheduleRepository.save(schedule);

	assertThat(doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY)).isTrue();
	assertThat(doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.TUESDAY)).isFalse();

	assertThat(doctorScheduleRepository.findByDoctorId(doctor.getId())).hasSize(1);
	assertThat(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY)).isPresent();
	assertThat(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.TUESDAY)).isNotPresent();
    }
}
