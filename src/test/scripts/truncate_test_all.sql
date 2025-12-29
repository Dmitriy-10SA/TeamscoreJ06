DO
$$
    BEGIN
        IF current_database() = 'test_sensors_db' THEN
            TRUNCATE TABLE
                sensors_readings.sensor_reading,
                sensors_data.accelerometer_data,
                sensors_data.barometer_data,
                sensors_data.light_data,
                sensors_data.location_data,
                sensors.sensor,
                devices.device
                CASCADE;
        ELSE
            RAISE NOTICE 'TRUNCATE skipped: current database is %, expected test_sensors_db', current_database();
        END IF;
    END
$$;