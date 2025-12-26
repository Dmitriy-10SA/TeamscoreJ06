package aggregator.printer;

import common.entities.sensor.data.AccelerometerData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Класс для печати данных AccelerometerData в виде таблицы
 *
 * @see SensorDataPrinter
 */
public class AccelerometerSensorDataPrinter
        extends SensorDataPrinter<AccelerometerData, AccelerometerSensorDataPrinter.AccelerometerDataRow> {
    public AccelerometerSensorDataPrinter(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected List<AccelerometerDataRow> getSensorDataRows(
            List<AccelerometerData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<AccelerometerDataRow> rows = new ArrayList<>();
        TreeMap<String, List<AccelerometerData>> intervalData = getCollectDataInInterval(
                data,
                startInterval,
                endInterval
        );
        intervalData.forEach((deviceName, values) -> {
            double x = values.stream().mapToDouble(AccelerometerData::getX).average().orElse(0);
            double y = values.stream().mapToDouble(AccelerometerData::getY).average().orElse(0);
            double z = values.stream().mapToDouble(AccelerometerData::getZ).average().orElse(0);
            rows.add(new AccelerometerDataRow(deviceName, startInterval, x, y, z));
        });
        return rows;
    }

    @Override
    protected void printHeader() {
        System.out.printf("%-32s %-19s %10s %10s %10s%n", "DEVICE", "DATE", "X", "Y", "Z");
    }

    @Override
    protected void printSensorDataRow(AccelerometerDataRow accelerometerDataRow) {
        System.out.printf(
                "%-32s %-19s %10.2f %10.2f %10.2f%n",
                accelerometerDataRow.getDeviceName(),
                accelerometerDataRow.getDateTime().format(DATE_TIME_FORMATTER),
                accelerometerDataRow.getX(),
                accelerometerDataRow.getY(),
                accelerometerDataRow.getZ()
        );
    }

    /**
     * Строка данных датчика ACCELEROMETER
     */
    @Getter
    protected static class AccelerometerDataRow extends SensorDataRow {
        private final double x;
        private final double y;
        private final double z;

        public AccelerometerDataRow(String deviceName, LocalDateTime dateTime, double x, double y, double z) {
            super(deviceName, dateTime);
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}