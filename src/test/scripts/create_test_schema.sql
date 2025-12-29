DO
$$
    BEGIN
        IF current_database() = 'test_sensors_db' THEN
            CREATE SCHEMA IF NOT EXISTS devices;
            CREATE SCHEMA IF NOT EXISTS sensors;
            CREATE SCHEMA IF NOT EXISTS sensors_readings;
            CREATE SCHEMA IF NOT EXISTS sensors_data;

            CREATE TABLE IF NOT EXISTS devices.device
            (
                id   BIGSERIAL PRIMARY KEY,
                name VARCHAR(32) NOT NULL UNIQUE
            );

            CREATE TYPE SENSOR_TYPE AS ENUM ('LIGHT', 'BAROMETER', 'LOCATION', 'ACCELEROMETER');

            CREATE TABLE IF NOT EXISTS sensors.sensor
            (
                id        VARCHAR(36) PRIMARY KEY,
                device_id BIGINT      NOT NULL,
                type      SENSOR_TYPE NOT NULL,

                FOREIGN KEY (device_id) REFERENCES devices.device (id)
            );

            CREATE TABLE IF NOT EXISTS sensors_readings.sensor_reading
            (
                id          BIGSERIAL PRIMARY KEY,
                sensor_id   VARCHAR(36)  NOT NULL,
                measured_at TIMESTAMP(3) NOT NULL,
                saved_at    TIMESTAMP(3) DEFAULT NULL,
                value       JSONB        NOT NULL,

                FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (id)
            );

            CREATE TABLE IF NOT EXISTS sensors_data.light_data
            (
                id         BIGSERIAL PRIMARY KEY,
                sensor_id  VARCHAR(36)  NOT NULL,
                measure_at TIMESTAMP(3) NOT NULL,
                light      INT          NOT NULL,

                FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (id)
            );

            CREATE TABLE IF NOT EXISTS sensors_data.barometer_data
            (
                id           BIGSERIAL PRIMARY KEY,
                sensor_id    VARCHAR(36)  NOT NULL,
                measure_at   TIMESTAMP(3) NOT NULL,
                air_pressure FLOAT        NOT NULL,

                FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (id)
            );

            CREATE TABLE IF NOT EXISTS sensors_data.location_data
            (
                id         BIGSERIAL PRIMARY KEY,
                sensor_id  VARCHAR(36)  NOT NULL,
                measure_at TIMESTAMP(3) NOT NULL,
                longitude  FLOAT        NOT NULL,
                latitude   FLOAT        NOT NULL,

                FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (id)
            );

            CREATE TABLE IF NOT EXISTS sensors_data.accelerometer_data
            (
                id         BIGSERIAL PRIMARY KEY,
                sensor_id  VARCHAR(36)  NOT NULL,
                measure_at TIMESTAMP(3) NOT NULL,
                x          FLOAT        NOT NULL,
                y          FLOAT        NOT NULL,
                z          FLOAT        NOT NULL,

                FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (id)
            );

        ELSE
            RAISE NOTICE 'Schema creation skipped: current database is %, expected test_sensors_db', current_database();
        END IF;
    END
$$;