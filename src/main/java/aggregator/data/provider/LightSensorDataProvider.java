package aggregator.data.provider;

import common.entity.sensor.data.LightData;
import jakarta.persistence.EntityManagerFactory;

/**
 * Класс для получения данных с датчика LIGHT
 *
 * @see SensorDataProvider
 */
public class LightSensorDataProvider extends SensorDataProvider<LightData> {
    public LightSensorDataProvider(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    protected String getEntityName() {
        return "LightData";
    }

    @Override
    protected Class<LightData> getEntityClass() {
        return LightData.class;
    }
}
