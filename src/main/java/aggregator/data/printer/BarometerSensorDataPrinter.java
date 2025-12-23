package aggregator.data.printer;

import aggregator.SensorPrinter;
import common.entities.sensor.data.BarometerData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Класс для печати данных BarometerData в виде таблицы
 */
public class BarometerSensorDataPrinter extends SensorDataPrinter<BarometerData> {
    private final Scanner scanner;

    public BarometerSensorDataPrinter(Scanner scanner) {
        this.scanner = scanner;
    }

    private List<BarometerRow> collectDataInIntervalToBarometerRows(
            List<BarometerData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<BarometerRow> rows = new ArrayList<>();
        data.stream()
                .filter(barometerData -> {
                    LocalDateTime dateTime = barometerData.getMeasureAt();
                    return !dateTime.isBefore(startInterval) && dateTime.isBefore(endInterval);
                })
                .collect(Collectors.groupingBy(
                        barometerData -> barometerData.getSensor().getDevice().getName(),
                        TreeMap::new,
                        Collectors.toList()
                ))
                .forEach((device, values) -> {
                    double airPressure = values.stream()
                            .mapToDouble(BarometerData::getAirPressure)
                            .average()
                            .orElse(0);
                    rows.add(new BarometerRow(device, startInterval, airPressure));
                });
        return rows;
    }

    @Override
    public void printData(
            List<BarometerData> data,
            SensorPrinter.SensorPrinterInterval interval,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (data.isEmpty()) {
            System.out.println("Нет данных для выбранного периода.");
            return;
        }
        List<BarometerRow> rows = new ArrayList<>();
        LocalDateTime startInterval = getStartInterval(start, interval);
        while (startInterval.isBefore(end)) {
            LocalDateTime endInterval = startInterval.plus(1, interval.getChronoUnit());
            rows.addAll(collectDataInIntervalToBarometerRows(data, startInterval, endInterval));
            startInterval = endInterval;
        }
        System.out.printf("%-32s %-19s %12s%n", "DEVICE", "DATE", "AIR PRESSURE");
        int printed = 0;
        int total = rows.size();
        for (BarometerRow row : rows) {
            System.out.printf(
                    "%-32s %-19s %12.2f%n",
                    row.deviceName,
                    row.dateTime.format(DATE_TIME_FORMATTER),
                    row.airPressure
            );
            printed++;
            if (printed % PAGE_SIZE == 0 && printed < total) {
                System.out.printf("Выведено %d строк из %d. Нажмите Enter для продолжения...%n", printed, total);
                scanner.nextLine();
            }
        }
        System.out.printf("Выведено %d строк из %d. Конец таблицы%n", printed, total);
    }

    private record BarometerRow(String deviceName, LocalDateTime dateTime, double airPressure) {
    }
}
