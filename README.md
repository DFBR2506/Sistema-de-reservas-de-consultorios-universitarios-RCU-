# Documentacion Tecnica - Capa de Repositorio

## Descripcion general

La capa de repositorio del proyecto RCU encapsula el acceso a datos para las entidades clinicas principales (citas, doctores, pacientes, consultorios, especialidades, tipos de cita y horarios).

Esta capa usa Spring Data JPA con dos enfoques:

1. Query methods derivados por convencion de nombre.
2. Consultas JPQL para agregacion, ranking y reportes.

La suite de tests de repositorio valida estos metodos sobre PostgreSQL real mediante Testcontainers, reduciendo riesgo de diferencias entre H2/in-memory y el comportamiento real en SQL.

## Responsabilidad principal del modulo

- Exponer operaciones de lectura y validacion de negocio orientadas a persistencia.
- Resolver consultas de disponibilidad, traslapes y reportes agregados.
- Mantener contratos simples para que la capa de servicio no dependa de SQL embebido.
- Proveer pruebas de integracion que validen consultas reales contra esquema JPA.

## Alcance de esta documentacion

Se documentan:

- Repositories en src/main/java/co/edu/unimagdalena/RCU/domine/repositories.
- Tests en src/test/java/co/edu/unimagdalena/RCU/domine/repositories.

No se documentan aqui los metodos heredados de JpaRepository (findAll, save, deleteById, etc.), por ser API estandar del framework.

## Documentacion por clase

### AppointmentRepository

**Responsabilidad**

Centraliza consultas de citas para:

- Deteccion de traslapes (doctor, consultorio, paciente).
- Filtros por estado y rango temporal.
- Construccion de reportes (ocupacion, productividad, no-show y agregados por especialidad).

**Metodos**

1. existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan
- Que hace: valida si existe al menos una cita del doctor que se cruza con una ventana temporal.
- Parametros: doctorId, endAt (fin de ventana evaluada), startAt (inicio de ventana evaluada).
- Retorno: boolean.
- Posibles excepciones: IllegalArgumentException por parametros invalidos en capa superior; DataAccessException en fallos de persistencia.

2. existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan
- Que hace: valida traslape de citas por consultorio.
- Parametros: officeId, endAt, startAt.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

3. existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan
- Que hace: valida traslape de citas por paciente.
- Parametros: patientId, endAt, startAt.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

4. findByPatientIdAndStatus
- Que hace: obtiene citas de un paciente filtradas por estado.
- Parametros: patientId, status.
- Retorno: List<Appointment>.
- Posibles excepciones: DataAccessException.

5. findByDoctorIdAndStartAtBetween
- Que hace: lista citas de un doctor en rango [startAt, endAt].
- Parametros: doctorId, startAt, endAt.
- Retorno: List<Appointment>.
- Posibles excepciones: DataAccessException.

6. findByOfficeIdAndStartAtBetween
- Que hace: lista citas de un consultorio en rango [startAt, endAt].
- Parametros: officeId, startAt, endAt.
- Retorno: List<Appointment>.
- Posibles excepciones: DataAccessException.

7. findByStartAtBetween
- Que hace: lista citas globalmente por rango de fecha/hora.
- Parametros: startAt, endAt.
- Retorno: List<Appointment>.
- Posibles excepciones: DataAccessException.

8. findBookedSlotsByDoctorAndDate
- Que hace: retorna bloques ocupados de un doctor en fecha/rango, excluyendo CANCELLED y ordenados por startAt asc.
- Parametros: doctorId, dayStart, dayEnd.
- Retorno: List<Appointment>.
- Posibles excepciones: DataAccessException.

9. findOfficeOccupancy
- Que hace: calcula ocupacion por consultorio en rango temporal.
- Parametros: startAt, endAt.
- Retorno: List<Object[]> con [officeId, code, totalAppointments].
- Posibles excepciones: DataAccessException.

10. findOfficeDailyOccupancy
- Que hace: calcula ocupacion diaria por consultorio.
- Parametros: dayStart, dayEnd.
- Retorno: List<Object[]> con [officeId, code, totalAppointments].
- Posibles excepciones: DataAccessException.

11. countCancelledAndNoShowBySpecialty
- Que hace: agrega cantidades de CANCELLED y NO_SHOW por especialidad en un periodo.
- Parametros: startAt, endAt.
- Retorno: List<Object[]> con [specialtyId, specialtyName, cancelledCount, noShowCount].
- Posibles excepciones: DataAccessException.

12. findDoctorProductivity
- Que hace: ranking de doctores por cantidad de citas COMPLETED.
- Parametros: ninguno.
- Retorno: List<Object[]> con [doctorId, firstName, lastName, completedCount].
- Posibles excepciones: DataAccessException.

13. findNoShowPatients
- Que hace: ranking de pacientes con mayor NO_SHOW en periodo.
- Parametros: startAt, endAt.
- Retorno: List<Object[]> con [patientId, firstName, lastName, noShowCount].
- Posibles excepciones: DataAccessException.

**Logica importante (paso a paso)**

- La deteccion de traslape usa la condicion estandar de intervalos: existing.start < requestedEnd AND existing.end > requestedStart.
- Los reportes usan GROUP BY y ORDER BY COUNT para ranking.
- Los bloques ocupados excluyen CANCELLED para no bloquear disponibilidad.

---

### DoctorRepository

**Responsabilidad**

Consulta de doctores para validaciones de unicidad y filtro por especialidad activa.

**Metodos**

1. findBySpecialtyIdAndActiveTrue
- Que hace: obtiene doctores activos de una especialidad.
- Parametros: specialtyId.
- Retorno: List<Doctor>.
- Posibles excepciones: DataAccessException.

2. existsByEmail
- Que hace: verifica unicidad de email.
- Parametros: email.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

3. existsByDocumentNumber
- Que hace: verifica unicidad de numero de documento.
- Parametros: documentNumber.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

4. existsByLicenseNumber
- Que hace: verifica unicidad de licencia medica.
- Parametros: licenseNumber.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

---

### DoctorScheduleRepository

**Responsabilidad**

Gestion de consultas de horarios de doctores por dia y por doctor.

**Metodos**

1. existsByDoctorIdAndDayOfWeek
- Que hace: confirma existencia de horario para doctor+diasemana.
- Parametros: doctorId, dayOfWeek.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

2. findByDoctorId
- Que hace: lista todos los horarios de un doctor.
- Parametros: doctorId.
- Retorno: List<DoctorSchedule>.
- Posibles excepciones: DataAccessException.

3. findByDoctorIdAndDayOfWeek
- Que hace: obtiene horario puntual por doctor+diasemana.
- Parametros: doctorId, dayOfWeek.
- Retorno: Optional<DoctorSchedule>.
- Posibles excepciones: DataAccessException.

---

### OfficeRepository

**Responsabilidad**

Consulta de existencia de consultorio por codigo.

**Metodo**

1. existsByCode
- Que hace: valida si ya existe un consultorio con el codigo.
- Parametros: code.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

---

### PatientRepository

**Responsabilidad**

Consulta de unicidad de paciente por email y documento.

**Metodos**

1. existsByEmail
- Que hace: valida existencia por email.
- Parametros: email.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

2. existsByDocumentNumber
- Que hace: valida existencia por numero de documento.
- Parametros: documentNumber.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

---

### SpecialtyRepository

**Responsabilidad**

Consulta de existencia de especialidad por nombre.

**Metodo**

1. existsByName
- Que hace: valida si existe una especialidad con ese nombre.
- Parametros: name.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

---

### AppointmentTypeRepository

**Responsabilidad**

Consulta de existencia de tipo de cita por nombre.

**Metodo**

1. existsByName
- Que hace: valida si existe un tipo de cita con ese nombre.
- Parametros: name.
- Retorno: boolean.
- Posibles excepciones: DataAccessException.

## Documentacion de tests por clase

### AbstractRepositoryIT

**Responsabilidad**

Clase base para pruebas de integracion JPA de repositorios.

**Que configura**

- Contexto @DataJpaTest.
- Perfil test.
- Base de datos real PostgreSQL via Testcontainers.
- Reemplazo de base embebida deshabilitado.

**Notas**

- Esta clase evita duplicar bootstrapping en cada test de repositorio.
- Cambios en su configuracion impactan toda la suite de repositorio.

---

### AppointmentRepositoryTest

**Objetivo**

Validar el comportamiento de todos los metodos custom de AppointmentRepository.

**Cobertura principal**

- Traslape positivo y negativo para doctor/office/patient.
- Filtros por estado y rango.
- Respuestas vacias cuando no hay coincidencias.
- Slots ocupados excluyendo CANCELLED y orden cronologico.
- Reportes de ocupacion (rango y diario), productividad, no-show.
- Escenario sin COMPLETED para ranking vacio.
- Agregacion CANCELLED/NO_SHOW por especialidad y fuera de rango.

**Posibles excepciones en test**

- AssertionError cuando una condicion no se cumple.
- DataAccessException si hay fallos de conexion o SQL.

---

### DoctorRepositoryTest

**Objetivo**

Validar unicidad y filtro de activos por especialidad.

**Cobertura principal**

- existsByEmail/document/license en true y false.
- findBySpecialtyIdAndActiveTrue con mezcla de activos/inactivos y especialidades diferentes.
- caso vacio sin activos.

---

### DoctorScheduleRepositoryTest

**Objetivo**

Validar consultas por doctor, por dia y presencia/ausencia.

**Cobertura principal**

- exists y find por dia configurado.
- caso negativo en dia no configurado.
- multiples horarios por doctor.
- caso vacio cuando no hay horarios.

---

### PatientRepositoryTest

**Objetivo**

Validar metodos de existencia por email y documento.

**Cobertura principal**

- caso positivo y negativo por cada metodo.

---

### OfficeRepositoryTest

**Objetivo**

Validar existsByCode en caso positivo y negativo.

---

### SpecialtyRepositoryTest

**Objetivo**

Validar existsByName en caso positivo y negativo.

---

### AppointmentTypeRepositoryTest

**Objetivo**

Validar existsByName en caso positivo y negativo.

## Notas tecnicas

1. Sobre retornos List<Object[]>
- En reportes agregados funciona, pero para mantenibilidad se recomienda migrar a proyecciones tipadas (interfaces o DTO constructor expressions).

2. Sobre excepciones
- Repositories no declaran checked exceptions; los errores de persistencia emergen como DataAccessException/RuntimeException.
- La validacion de argumentos nulos o de reglas de negocio debe permanecer en services.

3. Sobre estrategia de tests
- La suite actual prioriza pruebas por metodo con escenarios positivos, negativos y de ocurrencia.
- Este enfoque facilita diagnostico rapido cuando falla una consulta especifica.

4. Buenas practicas aplicadas
- Nombres de test orientados a comportamiento.
- Estructura Given/When/Then para legibilidad.
- Fixtures reutilizables para reducir ruido y duplicacion.


