-- Drop tables if they exist
DROP TABLE IF EXISTS "HorariosDisponibilidad";
DROP TABLE IF EXISTS "Prestamos";
DROP TABLE IF EXISTS "Reservas";
DROP TABLE IF EXISTS "Recursos";
DROP TABLE IF EXISTS "Tipo_Recurso";
DROP TABLE IF EXISTS "Empleados";
DROP TABLE IF EXISTS "Unidad_Servicio";
DROP TABLE IF EXISTS "Usuarios";
DROP TABLE IF EXISTS "Convenios";

-- Drop types if they exist
DROP TYPE IF EXISTS "dia_semana";
DROP TYPE IF EXISTS "estado_transaccion";

--types
CREATE TYPE "dia_semana" AS ENUM (
    'lunes',
    'martes',
    'miercoles',
    'jueves',
    'viernes',
	'sabado',
	'domingo'
);
CREATE CAST (varchar AS public."dia_semana") WITH INOUT AS IMPLICIT;
CREATE TYPE "estado_transaccion" AS ENUM ('activa', 'cancelada', 'completada');
CREATE CAST (varchar AS public."estado_transaccion") WITH INOUT AS IMPLICIT;

--CONVENIOS
CREATE TABLE "Convenios" (
    "convenio_id" SERIAL PRIMARY KEY,
    "nombre" varchar(100) NOT NULL,
    "descripcion" varchar(255) NOT NULL
);

-- USUARIOS
CREATE TABLE "Usuarios" (
    "usuario_id" SERIAL PRIMARY KEY,
    "nombre" varchar(100) NOT NULL,
    "rol" VARCHAR(50) NOT NULL,
    "convenio_id" int,
    "id_externa" varchar(100),
    FOREIGN KEY ("convenio_id") REFERENCES "Convenios" ("convenio_id"),
    -- Check constraint to ensure that if convenio_id is not null, id_externa is not null
    CONSTRAINT chk_convenio_id_id_externa CHECK (
        ("convenio_id" IS NOT NULL AND "id_externa" IS NOT NULL)
        OR ("convenio_id" IS NULL AND "id_externa" IS NULL)
    )
);

-- UNIDADES DE SERVICIO
CREATE TABLE "Unidad_Servicio" (
    "unidad_id" SERIAL PRIMARY KEY,
    "nombre" varchar(100) UNIQUE NOT NULL,
    "horario_laboral_inicio" time NOT NULL,
    "horario_laboral_fin" time NOT NULL,
    -- Check constraint to ensure that the start time is before the end time
    CONSTRAINT chk_horario_laborar CHECK (horario_laboral_inicio < horario_laboral_fin)
);

-- EMPLEADOS
CREATE TABLE "Empleados" (
    "empleado_id" SERIAL PRIMARY KEY,
    "usuario_id" int NOT NULL,
    "unidad_id" int NOT NULL,
    "cargo" VARCHAR(50) NOT NULL,
    "status" boolean NOT NULL DEFAULT TRUE,
    FOREIGN KEY ("usuario_id") REFERENCES "Usuarios" ("usuario_id"),
    FOREIGN KEY ("unidad_id") REFERENCES "Unidad_Servicio" ("unidad_id")
);

-- Validacion de empleados
CREATE OR REPLACE FUNCTION validate_empleado()
RETURNS TRIGGER AS $$
    BEGIN
        -- Verifica que el empleado no sea externo
        IF EXISTS (
            SELECT 1 FROM "Usuarios"
            WHERE "usuario_id" = NEW.usuario_id
            AND ("rol" = 'externo'
            OR "rol" = 'invitado'
            OR "convenio_id" IS NOT NULL
            OR "id_externa" IS NOT NULL)
        )
        THEN RAISE EXCEPTION 'El empleado no puede ser externo';
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER before_insert_update_empleado
    BEFORE INSERT OR UPDATE ON "Empleados"
    FOR EACH ROW EXECUTE FUNCTION validate_empleado();

-- TIPOS DE RECURSO
CREATE TABLE "Tipo_Recurso" (
    "tipo_recurso_id" SERIAL PRIMARY KEY,
    "unidad_id" int,
    "nombre" varchar(100) NOT NULL,
    "descripcion" varchar(255) NOT NULL,
    "tiempo_minimo_prestamo" time NOT NULL,
    FOREIGN KEY ("unidad_id") REFERENCES "Unidad_Servicio" ("unidad_id")
);

-- RECURSOS
CREATE TABLE "Recursos" (
    "recurso_id" SERIAL PRIMARY KEY,
    "unidad_id" int NOT NULL,
    "tipo_recurso_id" int NOT NULL,
    "nombre" varchar(100) NOT NULL,
    "descripcion" varchar(255) NOT NULL,
    FOREIGN KEY ("unidad_id") REFERENCES "Unidad_Servicio" ("unidad_id"),
    FOREIGN KEY ("tipo_recurso_id") REFERENCES "Tipo_Recurso" ("tipo_recurso_id")
);

-- RESERVAS
CREATE TABLE "Reservas" (
    "reserva_id" SERIAL PRIMARY KEY,
    "usuario_id" int NOT NULL,
    "recurso_id" int NOT NULL,
    "fecha_reserva" date NOT NULL,
    "hora_inicio_reserva" time NOT NULL,
    "hora_fin_reserva" time NOT NULL,
    "estado" estado_transaccion NOT NULL DEFAULT 'activa',
    FOREIGN KEY ("usuario_id") REFERENCES "Usuarios" ("usuario_id"),
    FOREIGN KEY ("recurso_id") REFERENCES "Recursos" ("recurso_id"),
    -- Check constraint to ensure that the start time is before the end time
    CONSTRAINT chk_hora_reserva CHECK (hora_inicio_reserva < hora_fin_reserva),
    -- Check constraint to ensure that the reservation date is not in the past
    CONSTRAINT chk_fecha_reserva_today CHECK (fecha_reserva >= CURRENT_DATE)
);

-- Validación de las reservas
CREATE OR REPLACE FUNCTION validate_reserva()
RETURNS TRIGGER AS $$
    DECLARE
        tipo INT;
        horario INT;
        dia dia_semana;
    BEGIN
        SELECT tipo_recurso_id INTO tipo FROM "Recursos"
            WHERE "recurso_id" = NEW.recurso_id;
        SELECT CASE EXTRACT(ISODOW FROM NEW.fecha_reserva)
            WHEN 1 THEN 'lunes'
            WHEN 2 THEN 'martes'
            WHEN 3 THEN 'miercoles'
            WHEN 4 THEN 'jueves'
            WHEN 5 THEN 'viernes'
            ELSE NULL
        END INTO dia;
        SELECT horario_disponibilidad_id INTO horario FROM "HorariosDisponibilidad"
            WHERE "tipo_recurso_id" = tipo
            AND "dia_semana" = dia;
		-- Si no existe un horario de disponibilidad para ese recurso y día, se rechaza la reserva
        IF horario IS NULL
        THEN RAISE EXCEPTION 'No hay horario de disponibilidad para ese recurso y día';
        END IF;
        -- Verifica que la hora de inicio de la reserva no sea menor al horario de disponibilidad
        IF NEW.hora_inicio_reserva < (
            SELECT hora_inicio
            FROM "HorariosDisponibilidad"
            WHERE "horario_disponibilidad_id" = horario
        )
        THEN RAISE EXCEPTION 'La hora de inicio de la reserva no puede salir del horario de disponibilidad';
        END IF;
        -- Verifica que la hora de finalización de la reserva no sea mayor al horario de disponibilidad
        IF NEW.hora_fin_reserva > (
            SELECT hora_fin
            FROM "HorariosDisponibilidad"
            WHERE "horario_disponibilidad_id" = horario
        )
        THEN RAISE EXCEPTION 'La hora de finalización de la reserva no puede salir del horario de disponibilidad';
        END IF;
        -- Verifica que la duración de la reserva no sea menor al tiempo mínimo de prestamo
        IF NEW.hora_fin_reserva - NEW.hora_inicio_reserva < (
            SELECT tiempo_minimo_prestamo
            FROM "Tipo_Recurso"
            WHERE "tipo_recurso_id" = tipo
        )
        THEN RAISE EXCEPTION 'La duración de la reserva no puede ser menor al tiempo minimo de prestamo';
        END IF;
        -- Verifica que el usuario no sea empleado de la unidad de servicio del recurso
        IF EXISTS (
            SELECT 1 FROM "Empleados"
            JOIN "Recursos" ON "Empleados".unidad_id = "Recursos".unidad_id
            WHERE "Empleados".usuario_id = NEW.usuario_id
            AND "Empleados".unidad_id = "Recursos".unidad_id
            AND "Empleados".status = TRUE
            AND "Recursos".recurso_id = NEW.recurso_id
        )
        THEN RAISE EXCEPTION 'El usuario no puede ser empleado de la unidad de servicio';
        END IF;
        -- Verifica que el recurso no se encuentre reservado en ese horario
        IF EXISTS (
            SELECT 1 FROM "Reservas"
            WHERE (
                NEW.recurso_id = "Reservas".recurso_id
                AND NEW.fecha_reserva = "Reservas".fecha_reserva
            )
            AND (
                (NEW.hora_inicio_reserva BETWEEN "Reservas".hora_inicio_reserva AND "Reservas".hora_fin_reserva)
                OR (NEW.hora_fin_reserva BETWEEN "Reservas".hora_inicio_reserva AND "Reservas".hora_fin_reserva)
                OR ("Reservas".hora_fin_reserva BETWEEN NEW.hora_inicio_reserva AND NEW.hora_fin_reserva)
            )
        )
        THEN RAISE EXCEPTION 'El recurso se encuentra esta reservado en ese horario';
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER before_insert_update_reserva
    BEFORE INSERT OR UPDATE ON "Reservas"
    FOR EACH ROW EXECUTE FUNCTION validate_reserva();

-- PRESTAMOS
CREATE TABLE "Prestamos" (
    "prestamo_id" SERIAL PRIMARY KEY,
    "reserva_id" int UNIQUE NOT NULL,
    "empleado_id" int NOT NULL,
    "hora_entrega" timestamp NOT NULL,
    "hora_devolucion" timestamp,
    "estado" estado_transaccion NOT NULL,
    FOREIGN KEY ("reserva_id") REFERENCES "Reservas" ("reserva_id"),
    FOREIGN KEY ("empleado_id") REFERENCES "Empleados" ("empleado_id"),
    -- Check constraint to ensure that the return time is after the delivery time
    CONSTRAINT chck_hora_devolucion CHECK (hora_devolucion > hora_entrega)
);

-- Validación de los prestamos
CREATE OR REPLACE FUNCTION validate_prestamo() 
RETURNS TRIGGER AS $$ 
    DECLARE
    unidad INT;
    BEGIN
        -- Verifica que la hora de entrega sea mayor a la hora de inicio de la reserva con un margen de 10 minutos
        IF NEW.hora_entrega < (
            SELECT fecha_reserva + hora_inicio_reserva - INTERVAL '10 minutes'
            FROM "Reservas"
            WHERE "reserva_id" = NEW.reserva_id
        ) THEN RAISE EXCEPTION 'La hora de entrega no puede ser anterior al inicio de la reserva';
        END IF;
        -- Verifica que la hora de entrega sea menor a la hora de finalización de la reserva con un margen de mas o menos 5 minutos
        IF NEW.hora_entrega > (
            SELECT fecha_reserva + hora_fin_reserva + INTERVAL '5 minutes'
            FROM "Reservas"
            WHERE "reserva_id" = NEW.reserva_id
        ) THEN RAISE EXCEPTION 'La hora de entrega no puede ser despues del fin de la reserva';
        END IF;
        --Verifica que el empleado este activo
        IF NOT EXISTS (
            SELECT 1
            FROM "Empleados"
            WHERE "empleado_id" = NEW.empleado_id
            AND "status" = TRUE)
        THEN RAISE EXCEPTION 'El empleado no se encuentra activo';
        END IF;
        -- Verifica que el empleado corresponda a la unidad de servicio del recurso
        SELECT unidad_id INTO unidad FROM "Empleados"
            WHERE empleado_id = NEW.empleado_id;
        IF unidad != (
            SELECT "Recursos".unidad_id FROM "Reservas"
            JOIN "Recursos" ON "Reservas".recurso_id = "Recursos".recurso_id
            WHERE "Reservas".reserva_id = NEW.reserva_id
        ) THEN RAISE EXCEPTION 'El empleado no corresponde a la unidad de servicio correspondiente';
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;
-- Asigna la comprobación a la tabla
CREATE TRIGGER before_insert_update_prestamo
    BEFORE INSERT OR UPDATE ON "Prestamos"
    FOR EACH ROW EXECUTE FUNCTION validate_prestamo();


CREATE TABLE "HorariosDisponibilidad" (
    "horario_disponibilidad_id" SERIAL PRIMARY KEY,
    "tipo_recurso_id" int NOT NULL,
    "dia_semana" dia_semana NOT NULL,
    "hora_inicio" time NOT NULL,
    "hora_fin" time NOT NULL,
    FOREIGN KEY ("tipo_recurso_id") REFERENCES "Tipo_Recurso" ("tipo_recurso_id"),
    CONSTRAINT chk_hora_inicio_fin CHECK (hora_inicio < hora_fin)
);
-- Validación de los horarios de disponibilidad
CREATE OR REPLACE FUNCTION validate_horario()
RETURNS TRIGGER AS $$
    DECLARE
        unidad INT;
    BEGIN
        -- Verifica que la hora de inicio no sea previa al inicio del horario laboral
        SELECT unidad_id INTO unidad FROM "Tipo_Recurso"
            WHERE "tipo_recurso_id" = NEW.tipo_recurso_id;
        IF NEW.hora_inicio < (
            SELECT horario_laboral_inicio
            FROM "Unidad_Servicio"
            WHERE "unidad_id" = unidad
        )
        THEN RAISE EXCEPTION 'La hora de inicio no puede ser previa al inicio del horario laboral';
        END IF;
        -- Verifica que la hora de finalización no sea posterior al final del horario laboral
        IF NEW.hora_fin > (
            SELECT horario_laboral_fin
            FROM "Unidad_Servicio"
            WHERE "unidad_id" = unidad
        )
        THEN RAISE EXCEPTION 'La hora de finalización no puede ser posterior al final del horario laboral';
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER before_insert_update_horario
    BEFORE INSERT OR UPDATE ON "HorariosDisponibilidad"
    FOR EACH ROW EXECUTE FUNCTION validate_horario();

-- convenios de ejemplo
INSERT INTO "Convenios" ("nombre", "descripcion")
VALUES ('Convenio 1', 'Convenio de empresa 1'),
    ('Convenio 2', 'Convenio de empresa 2');

-- usuarios de ejemplo
INSERT INTO "Usuarios" ("nombre", "rol", "convenio_id", "id_externa")
VALUES ('John Doe', 'usuario', null, null),
    ('Jane Smith', 'usuario', null, null),
    ('Mike Johnson', 'usuario', null, null),
    ('Alice Brown', 'externo', 1, '1234567890'),
    ('Bob White', 'externo', 2, '0987654321');

-- unidades de servicio de ejemplo
INSERT INTO "Unidad_Servicio" (
    "nombre",
    "horario_laboral_inicio",
    "horario_laboral_fin"
)
VALUES ('Unidad 1', '08:00:00', '17:00:00'),
    ('Unidad 2', '09:00:00', '18:00:00');

-- empleados de ejemplo
INSERT INTO "Empleados" ("usuario_id", "unidad_id", "cargo")
VALUES (1, 1, 'gerente'),
    (2, 2, 'recepcionista'),
    (3, 1, 'recepcionista');

-- tipos de recurso de ejemplo
INSERT INTO "Tipo_Recurso" (
    "unidad_id",
    "nombre",
    "descripcion",
    "tiempo_minimo_prestamo"
)
VALUES (1, 'Proyector', 'Proyector de alta definición', '02:00:00'),
    (1, 'Aula', 'Aula de conferencias', '04:00:00'),
    (2, 'Laboratorio', 'Laboratorio de computación', '03:00:00');

--horarios de ejemplo
INSERT INTO "HorariosDisponibilidad" (
    "tipo_recurso_id",
    "dia_semana",
    "hora_inicio",
    "hora_fin"
)
VALUES (1, 'lunes', '08:00:00', '17:00:00'),
	(1, 'martes', '08:00:00', '12:00:00'),
    (2, 'martes', '09:00:00', '17:00:00'),
    (3, 'miercoles', '10:00:00', '18:00:00');

-- recursos de ejemplo
INSERT INTO "Recursos" (
    "unidad_id",
    "tipo_recurso_id",
    "nombre",
    "descripcion"
)
VALUES (1, 1, 'Proyector Sala 1','Proyector para sala de conferencias'),
    (1, 2, 'Aula Grande', 'Aula para eventos'),
    (2, 3, 'Lab 1', 'Laboratorio de programación');

-- reservas de ejemplo
INSERT INTO "Reservas" (
    "usuario_id",
    "recurso_id",
    "fecha_reserva",
    "hora_inicio_reserva",
    "hora_fin_reserva"
)
VALUES (2, 1, '2024-05-27', '10:00:00', '12:00:00'),
    (2, 2, '2024-05-28', '13:00:00', '17:00:00'),
    (1, 3, '2024-05-29', '10:00:00', '13:00:00'),
	(2, 1, '2024-05-21', '08:00:00', '12:00:00');

-- prestamos de ejemplo
INSERT INTO "Prestamos" (
    "reserva_id",
    "empleado_id",
    "hora_entrega",
    "estado"
)
VALUES (1, 1, '2024-05-27 09:55:00', 'activa'),
    (2, 3, '2024-05-28 13:55:00', 'activa'),
    (3, 2, '2024-05-29 09:55:00', 'activa');