package common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Устройство
 *
 * @see Sensor
 */
@Entity
@Table(name = "device", schema = "devices")
@Getter
@NoArgsConstructor
public class Device {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 32, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sensor> sensors = new ArrayList<>();

    public Device(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
    }

    /**
     * Добавление датчика у устройства
     */
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        sensor.setDevice(this);
    }
}
