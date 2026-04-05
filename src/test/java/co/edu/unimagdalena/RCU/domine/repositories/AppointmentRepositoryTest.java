package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.Appointment;
import co.edu.unimagdalena.RCU.domine.entities.AppointmentType;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.Office;
import co.edu.unimagdalena.RCU.domine.entities.Patient;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import co.edu.unimagdalena.RCU.domine.entities.enums.Status;

class AppointmentRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    OfficeRepository officeRepository;

    @Autowired
    AppointmentTypeRepository appointmentTypeRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("Appointment: solapes y consultas por rango/estado")
    void shouldValidateOverlapAndBasicFilters() {
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	Instant start = Instant.parse("2026-04-01T10:00:00Z");
	Instant end = Instant.parse("2026-04-01T11:00:00Z");
	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED, start, end);

	boolean doctorOverlap = appointmentRepository
		.existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(data.doctor.getId(),
			Instant.parse("2026-04-01T11:30:00Z"),
			Instant.parse("2026-04-01T10:30:00Z"));
	boolean officeOverlap = appointmentRepository
		.existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(data.office.getId(),
			Instant.parse("2026-04-01T11:30:00Z"),
			Instant.parse("2026-04-01T10:30:00Z"));
	boolean patientOverlap = appointmentRepository
		.existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(data.patient.getId(),
			Instant.parse("2026-04-01T11:30:00Z"),
			Instant.parse("2026-04-01T10:30:00Z"));

	assertThat(doctorOverlap).isTrue();
	assertThat(officeOverlap).isTrue();
	assertThat(patientOverlap).isTrue();

	List<Appointment> byPatientAndStatus = appointmentRepository.findByPatientIdAndStatus(data.patient.getId(),
		Status.SCHEDULED);
	assertThat(byPatientAndStatus).hasSize(1);

	List<Appointment> byDoctorRange = appointmentRepository.findByDoctorIdAndStartAtBetween(data.doctor.getId(),
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-01T23:59:59Z"));
	assertThat(byDoctorRange).hasSize(1);

	List<Appointment> byOfficeRange = appointmentRepository.findByOfficeIdAndStartAtBetween(data.office.getId(),
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-01T23:59:59Z"));
	assertThat(byOfficeRange).hasSize(1);
    }

    @Test
    @DisplayName("Appointment: reportes de ocupacion, productividad y no-show")
    void shouldCalculateReports() {
	TestData alpha = createBaseData("alpha");
	TestData beta = createBaseData("beta");

	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.SCHEDULED,
		Instant.parse("2026-04-02T10:00:00Z"), Instant.parse("2026-04-02T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.SCHEDULED,
		Instant.parse("2026-04-03T10:00:00Z"), Instant.parse("2026-04-03T11:00:00Z"));

	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.COMPLETED,
		Instant.parse("2026-04-04T10:00:00Z"), Instant.parse("2026-04-04T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.COMPLETED,
		Instant.parse("2026-04-05T10:00:00Z"), Instant.parse("2026-04-05T11:00:00Z"));
	saveAppointment(beta.doctor, beta.patient, beta.office, beta.type, Status.COMPLETED,
		Instant.parse("2026-04-06T10:00:00Z"), Instant.parse("2026-04-06T11:00:00Z"));

	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.NO_SHOW,
		Instant.parse("2026-04-07T10:00:00Z"), Instant.parse("2026-04-07T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.NO_SHOW,
		Instant.parse("2026-04-08T10:00:00Z"), Instant.parse("2026-04-08T11:00:00Z"));
	saveAppointment(beta.doctor, beta.patient, beta.office, beta.type, Status.NO_SHOW,
		Instant.parse("2026-04-09T10:00:00Z"), Instant.parse("2026-04-09T11:00:00Z"));

	List<Object[]> officeOccupancy = appointmentRepository.findOfficeOccupancy(
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-30T23:59:59Z"));
	assertThat(officeOccupancy).isNotEmpty();
	assertThat(officeOccupancy.get(0)[1]).isEqualTo(alpha.office.getCode());
	assertThat(((Long) officeOccupancy.get(0)[2])).isEqualTo(7L);

	List<Object[]> doctorProductivity = appointmentRepository.findDoctorProductivity();
	assertThat(doctorProductivity).isNotEmpty();
	assertThat((String) doctorProductivity.get(0)[1]).isEqualTo(alpha.doctor.getFirstName());
	assertThat(((Long) doctorProductivity.get(0)[3])).isEqualTo(2L);

	List<Object[]> noShowPatients = appointmentRepository.findNoShowPatients(
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-30T23:59:59Z"));
	assertThat(noShowPatients).isNotEmpty();
	assertThat((String) noShowPatients.get(0)[1]).isEqualTo(alpha.patient.getFirstName());
	assertThat(((Long) noShowPatients.get(0)[3])).isEqualTo(2L);
    }

    private Appointment saveAppointment(Doctor doctor, Patient patient, Office office, AppointmentType type, Status status,
	    Instant start, Instant end) {
	return appointmentRepository.save(Appointment.builder()
		.doctor(doctor)
		.patient(patient)
		.office(office)
		.appointmentType(type)
		.status(status)
		.note("nota")
		.startAt(start)
		.endAt(end)
		.createdAt(Instant.now())
		.build());
    }

    private TestData createBaseData(String suffix) {
	Specialty specialty = specialtyRepository.save(Specialty.builder()
		.name("Especialidad-" + suffix)
		.description("desc")
		.active(true)
		.createdAt(Instant.now())
		.build());

	Doctor doctor = doctorRepository.save(Doctor.builder()
		.firstName("Doctor" + suffix)
		.lastName("Apellido")
		.email("doctor." + suffix + "@demo.com")
		.phone("3333333333")
		.documentType(DocumentType.CC)
		.documentNumber("DOCA-" + suffix)
		.gender(Gender.MALE)
		.active(true)
		.createdAt(Instant.now())
		.licenseNumber("LICA-" + suffix)
		.specialty(specialty)
		.build());

	Patient patient = patientRepository.save(Patient.builder()
		.firstName("Paciente" + suffix)
		.lastName("Apellido")
		.email("patient." + suffix + "@demo.com")
		.phone("3444444444")
		.documentType(DocumentType.CC)
		.documentNumber("PACA-" + suffix)
		.gender(Gender.OTHER)
		.active(true)
		.createdAt(Instant.now())
		.build());

	Office office = officeRepository.save(Office.builder()
		.code("OF-" + suffix)
		.floor(2)
		.active(true)
		.createdAt(Instant.now())
		.build());

	AppointmentType type = appointmentTypeRepository.save(AppointmentType.builder()
		.name("Tipo-" + suffix)
		.description("desc")
		.active(true)
		.durationMinutes(30)
		.createdAt(Instant.now())
		.build());

	return new TestData(doctor, patient, office, type);
    }

    private record TestData(Doctor doctor, Patient patient, Office office, AppointmentType type) {
    }
}
