package common.entities.sensor.data;

import common.entities.Sensor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "location_data", schema = "sensors_data")
@Getter
@NoArgsConstructor
public class LocationData extends SensorData {
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    public LocationData(Sensor sensor, LocalDateTime measureAt, double longitude, double latitude) {
        super(sensor, measureAt);
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
