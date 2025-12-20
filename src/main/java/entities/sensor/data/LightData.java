package entities.sensor.data;

import entities.Sensor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "light_data", schema = "sensors_data")
@Getter
@NoArgsConstructor
public class LightData extends SensorData {
    @Column(name = "light", nullable = false)
    private int light;

    public LightData(Sensor sensor, LocalDateTime measureAt, int light) {
        super(sensor, measureAt);
        this.light = light;
    }
}
