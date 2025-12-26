package common.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Датчик
 *
 * @see Device
 */
@Entity
@Table(name = "sensor", schema = "sensors")
@Getter
@NoArgsConstructor
public class Sensor {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    @Setter
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "sensor_type")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnTransformer(write = "?::sensor_type")
    private SensorType type;

    public Sensor(String id, SensorType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Тип датчика
     */
    public enum SensorType {
        LIGHT, BAROMETER, LOCATION, ACCELEROMETER
    }
}
