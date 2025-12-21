package common.entities.sensor.data;

import common.entities.Sensor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "barometer_data", schema = "sensors_data")
@Getter
@NoArgsConstructor
public class BarometerData extends SensorData {
    @Column(name = "air_pressure", nullable = false)
    private double airPressure;

    public BarometerData(Sensor sensor, LocalDateTime measureAt, double airPressure) {
        super(sensor, measureAt);
        this.airPressure = airPressure;
    }
}
