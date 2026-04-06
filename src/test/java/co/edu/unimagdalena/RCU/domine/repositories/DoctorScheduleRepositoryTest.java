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
    @DisplayName("DoctorSchedule: consulta por doctor y día existente")
    void shouldFindScheduleByDoctorAndDay() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	Doctor doctor = createDoctor("jose." + suffix + "@demo.com", "DOCSCH-" + suffix, "LICSCH-" + suffix);
	doctorScheduleRepository.save(createSchedule(doctor, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));

	// when
	boolean exists = doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY);

	// then
	assertThat(exists).isTrue();
	assertThat(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY)).isPresent();
    }

    @Test
    @DisplayName("DoctorSchedule: no encuentra horario para día no configurado")
    void shouldNotFindScheduleForUnconfiguredDay() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	Doctor doctor = createDoctor("maria." + suffix + "@demo.com", "DOCSCH2-" + suffix, "LICSCH2-" + suffix);
	doctorScheduleRepository.save(createSchedule(doctor, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));

	// when / then
	assertThat(doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.TUESDAY)).isFalse();
	assertThat(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.TUESDAY)).isNotPresent();
    }

    @Test
    @DisplayName("DoctorSchedule: devuelve múltiples horarios de un mismo doctor")
    void shouldReturnMultipleSchedulesForDoctor() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	Doctor doctor = createDoctor("pedro." + suffix + "@demo.com", "DOCSCH3-" + suffix, "LICSCH3-" + suffix);
	doctorScheduleRepository.save(createSchedule(doctor, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));
	doctorScheduleRepository.save(createSchedule(doctor, DayOfWeek.TUESDAY, LocalTime.of(13, 0), LocalTime.of(17, 0)));

	// when
	var schedules = doctorScheduleRepository.findByDoctorId(doctor.getId());

	// then
	assertThat(schedules).hasSize(2);
	assertThat(schedules).extracting(DoctorSchedule::getDayOfWeek)
		.containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);
    }

    @Test
    @DisplayName("DoctorSchedule: retorna vacío cuando el doctor no tiene horarios")
    void shouldReturnEmptyWhenDoctorHasNoSchedules() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	Doctor doctor = createDoctor("sofia." + suffix + "@demo.com", "DOCSCH4-" + suffix, "LICSCH4-" + suffix);

	// when
	var schedules = doctorScheduleRepository.findByDoctorId(doctor.getId());

	// then
	assertThat(schedules).isEmpty();
    }

    private Doctor createDoctor(String email, String documentNumber, String licenseNumber) {
	Specialty specialty = specialtyRepository.save(Specialty.builder()
		.name("Medicina Interna-" + UUID.randomUUID())
		.description("Especialidad")
		.active(true)
		.createdAt(Instant.now())
		.build());

	return doctorRepository.save(Doctor.builder()
		.firstName("Jose")
		.lastName("Rojas")
		.email(email)
		.phone("3222222222")
		.documentType(DocumentType.CC)
		.documentNumber(documentNumber)
		.gender(Gender.MALE)
		.active(true)
		.createdAt(Instant.now())
		.licenseNumber(licenseNumber)
		.specialty(specialty)
		.build());
    }

    private DoctorSchedule createSchedule(Doctor doctor, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
	return DoctorSchedule.builder()
		.doctor(doctor)
		.dayOfWeek(dayOfWeek)
		.startTime(startTime)
		.endTime(endTime)
		.active(true)
		.createdAt(Instant.now())
		.build();
    }
}
