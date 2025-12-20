package common.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_reading", schema = "sensors_readings")
@Getter
@NoArgsConstructor
public class SensorReading {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "saved_at")
    @Setter
    private LocalDateTime savedAt;

    @Column(name = "value", nullable = false, columnDefinition = "JSONB")
    private String valueJson;

    public SensorReading(Sensor sensor, LocalDateTime measuredAt, String valueJson) {
        this.sensor = sensor;
        this.measuredAt = measuredAt;
        this.savedAt = null;
        this.valueJson = valueJson;
    }
}
