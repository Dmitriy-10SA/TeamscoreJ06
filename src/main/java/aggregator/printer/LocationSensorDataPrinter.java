package aggregator.printer;

import common.entities.sensor.data.LocationData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Класс для печати данных LocationData в виде таблицы
 *
 * @see SensorDataPrinter
 */
public class LocationSensorDataPrinter
        extends SensorDataPrinter<LocationData, LocationSensorDataPrinter.LocationDataRow> {
    public LocationSensorDataPrinter(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void printHeader() {
        System.out.printf("%-32s %-19s %12s %12s%n", "DEVICE", "DATE", "LAT", "LON");
    }

    @Override
    protected List<LocationDataRow> getSensorDataRows(
            List<LocationData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<LocationDataRow> rows = new ArrayList<>();
        TreeMap<String, List<LocationData>> intervalData = getCollectDataInInterval(data, startInterval, endInterval);
        intervalData.forEach((device, values) -> {
            double lat = values.stream().mapToDouble(LocationData::getLatitude).average().orElse(0);
            double lon = values.stream().mapToDouble(LocationData::getLongitude).average().orElse(0);
            rows.add(new LocationDataRow(device, startInterval, lat, lon));
        });
        return rows;
    }

    @Override
    protected void printSensorDataRow(LocationDataRow locationDataRow) {
        System.out.printf(
                "%-32s %-19s %12.6f %12.6f%n",
                locationDataRow.getDeviceName(),
                locationDataRow.getDateTime().format(DATE_TIME_FORMATTER),
                locationDataRow.getLatitude(),
                locationDataRow.getLongitude()
        );
    }

    /**
     * Строка данных датчика LOCATION
     */
    @Getter
    protected static class LocationDataRow extends SensorDataRow {
        private final double latitude;
        private final double longitude;

        public LocationDataRow(String deviceName, LocalDateTime dateTime, double latitude, double longitude) {
            super(deviceName, dateTime);
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}