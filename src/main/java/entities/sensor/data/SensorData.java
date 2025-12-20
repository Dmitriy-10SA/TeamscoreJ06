package entities.sensor.data;

import entities.Sensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(name = "measure_at", nullable = false)
    private LocalDateTime measureAt;

    public SensorData(Sensor sensor, LocalDateTime measureAt) {
        this.sensor = sensor;
        this.measureAt = measureAt;
    }
}