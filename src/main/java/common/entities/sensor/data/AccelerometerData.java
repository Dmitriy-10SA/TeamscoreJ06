package common.entities.sensor.data;

import common.entities.Sensor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "accelerometer_data", schema = "sensors_data")
@Getter
@NoArgsConstructor
public class AccelerometerData extends SensorData {
    @Column(name = "x", nullable = false)
    private double x;

    @Column(name = "y", nullable = false)
    private double y;

    @Column(name = "z", nullable = false)
    private double z;

    public AccelerometerData(Sensor sensor, LocalDateTime measureAt, double x, double y, double z) {
        super(sensor, measureAt);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
