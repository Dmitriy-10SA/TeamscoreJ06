package aggregator.data.printer;

import aggregator.SensorPrinter;
import common.entities.sensor.data.AccelerometerData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Класс для печати данных AccelerometerData в виде таблицы
 */
public class AccelerometerSensorDataPrinter extends SensorDataPrinter<AccelerometerData> {
    private final Scanner scanner;

    public AccelerometerSensorDataPrinter(Scanner scanner) {
        this.scanner = scanner;
    }

    private List<AccelerometerRow> collectDataInIntervalToAccelerometerRows(
            List<AccelerometerData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<AccelerometerRow> rows = new ArrayList<>();
        data.stream()
                .filter(accelerometerData -> {
                    LocalDateTime measureAt = accelerometerData.getMeasureAt();
                    return !measureAt.isBefore(startInterval) && measureAt.isBefore(endInterval);
                })
                .collect(Collectors.groupingBy(
                        accelerometerData -> accelerometerData.getSensor().getDevice().getName(),
                        TreeMap::new,
                        Collectors.toList()
                ))
                .forEach((deviceName, values) -> {
                    double x = values.stream().mapToDouble(AccelerometerData::getX).average().orElse(0);
                    double y = values.stream().mapToDouble(AccelerometerData::getY).average().orElse(0);
                    double z = values.stream().mapToDouble(AccelerometerData::getZ).average().orElse(0);
                    rows.add(new AccelerometerRow(deviceName, startInterval, x, y, z));
                });
        return rows;
    }

    @Override
    public void printData(
            List<AccelerometerData> data,
            SensorPrinter.SensorPrinterInterval interval,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (data.isEmpty()) {
            System.out.println("Нет данных для выбранного периода.");
            return;
        }
        List<AccelerometerRow> rows = new ArrayList<>();
        LocalDateTime startInterval = getStartInterval(start, interval);
        while (startInterval.isBefore(end)) {
            LocalDateTime endInterval = startInterval.plus(1, interval.getChronoUnit());
            rows.addAll(collectDataInIntervalToAccelerometerRows(data, startInterval, endInterval));
            startInterval = endInterval;
        }
        System.out.printf("%-32s %-19s %10s %10s %10s%n", "DEVICE", "DATE", "X", "Y", "Z");
        int printed = 0;
        int total = rows.size();
        for (AccelerometerRow row : rows) {
            System.out.printf(
                    "%-32s %-19s %10.2f %10.2f %10.2f%n",
                    row.deviceName,
                    row.dateTime.format(DATE_TIME_FORMATTER),
                    row.x,
                    row.y,
                    row.z
            );
            printed++;
            if (printed % PAGE_SIZE == 0 && printed < total) {
                System.out.printf("Выведено %d строк из %d. Нажмите Enter для продолжения...%n", printed, total);
                scanner.nextLine();
            }
        }
        System.out.printf("Выведено %d строк из %d. Конец таблицы%n", printed, total);
    }

    private record AccelerometerRow(String deviceName, LocalDateTime dateTime, double x, double y, double z) {
    }
}
