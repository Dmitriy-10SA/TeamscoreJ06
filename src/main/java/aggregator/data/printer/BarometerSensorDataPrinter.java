package aggregator.data.printer;

import common.entities.sensor.data.BarometerData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Класс для печати данных BarometerData в виде таблицы
 *
 * @see SensorDataPrinter
 */
public class BarometerSensorDataPrinter
        extends SensorDataPrinter<BarometerData, BarometerSensorDataPrinter.BarometerDataRow> {
    public BarometerSensorDataPrinter(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected List<BarometerDataRow> getSensorDataRows(
            List<BarometerData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<BarometerDataRow> rows = new ArrayList<>();
        TreeMap<String, List<BarometerData>> intervalData = getCollectDataInInterval(data, startInterval, endInterval);
        intervalData.forEach((device, values) -> {
            double airPressure = values.stream().mapToDouble(BarometerData::getAirPressure).average().orElse(0);
            rows.add(new BarometerDataRow(device, startInterval, airPressure));
        });
        return rows;
    }

    @Override
    protected void printHeader() {
        System.out.printf("%-32s %-19s %12s%n", "DEVICE", "DATE", "AIR PRESSURE");
    }

    @Override
    protected void printSensorDataRow(BarometerDataRow barometerDataRow) {
        System.out.printf(
                "%-32s %-19s %12.2f%n",
                barometerDataRow.getDeviceName(),
                barometerDataRow.getDateTime().format(DATE_TIME_FORMATTER),
                barometerDataRow.getAirPressure()
        );
    }

    /**
     * Строка данных датчика BAROMETER
     */
    @Getter
    protected static class BarometerDataRow extends SensorDataRow {
        private final double airPressure;

        public BarometerDataRow(String deviceName, LocalDateTime dateTime, double airPressure) {
            super(deviceName, dateTime);
            this.airPressure = airPressure;
        }
    }
}
