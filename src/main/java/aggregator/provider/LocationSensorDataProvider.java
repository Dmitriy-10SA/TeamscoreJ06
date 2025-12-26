package aggregator.provider;

import common.entities.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;

/**
 * Класс для получения данных с датчика LOCATION
 *
 * @see SensorDataProvider
 */
public class LocationSensorDataProvider extends SensorDataProvider<LocationData> {
    public LocationSensorDataProvider(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    protected String getEntityName() {
        return "LocationData";
    }

    @Override
    protected Class<LocationData> getEntityClass() {
        return LocationData.class;
    }
}
