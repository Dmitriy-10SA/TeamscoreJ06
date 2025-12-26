package aggregator.data.printer;

import common.entity.sensor.data.LightData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Класс для печати данных LightData в виде таблицы
 *
 * @see SensorDataPrinter
 */
public class LightSensorDataPrinter extends SensorDataPrinter<LightData, LightSensorDataPrinter.LightDataRow> {
    public LightSensorDataPrinter(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected List<LightDataRow> getSensorDataRows(List<LightData> data, LocalDateTime startInterval, LocalDateTime endInterval) {
        List<LightDataRow> rows = new ArrayList<>();
        TreeMap<String, List<LightData>> intervalData = getCollectDataInInterval(data, startInterval, endInterval);
        intervalData.forEach((device, values) -> {
            double light = values.stream().mapToInt(LightData::getLight).average().orElse(0);
            rows.add(new LightDataRow(device, startInterval, light));
        });
        return rows;
    }

    @Override
    protected void printHeader() {
        System.out.printf("%-32s %-19s %10s%n", "DEVICE", "DATE", "LIGHT");
    }

    @Override
    protected void printSensorDataRow(LightDataRow lightDataRow) {
        System.out.printf(
                "%-32s %-19s %10.2f%n",
                lightDataRow.getDeviceName(),
                lightDataRow.getDateTime().format(DATE_TIME_FORMATTER),
                lightDataRow.getLight()
        );
    }

    /**
     * Строка данных датчика LIGHT
     */
    @Getter
    protected static class LightDataRow extends SensorDataRow {
        private final double light;

        public LightDataRow(String deviceName, LocalDateTime dateTime, double light) {
            super(deviceName, dateTime);
            this.light = light;
        }
    }
}