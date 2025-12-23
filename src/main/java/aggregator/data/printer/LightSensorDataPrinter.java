package aggregator.data.printer;

import aggregator.SensorPrinter;
import common.entities.sensor.data.LightData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Класс для печати данных LightData в виде таблицы
 */
public class LightSensorDataPrinter extends SensorDataPrinter<LightData> {
    private final Scanner scanner;

    public LightSensorDataPrinter(Scanner scanner) {
        this.scanner = scanner;
    }

    private List<LightRow> collectDataInIntervalToLightRows(
            List<LightData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<LightRow> rows = new ArrayList<>();
        data.stream()
                .filter(it -> {
                    LocalDateTime t = it.getMeasureAt();
                    return !t.isBefore(startInterval) && t.isBefore(endInterval);
                })
                .collect(Collectors.groupingBy(
                        it -> it.getSensor().getDevice().getName(),
                        TreeMap::new,
                        Collectors.toList()
                ))
                .forEach((device, values) -> {
                    double light = values.stream().mapToInt(LightData::getLight).average().orElse(0);
                    rows.add(new LightRow(device, startInterval, light));
                });
        return rows;
    }

    @Override
    public void printData(
            List<LightData> data,
            SensorPrinter.SensorPrinterInterval interval,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (data.isEmpty()) {
            System.out.println("Нет данных для выбранного периода.");
            return;
        }
        List<LightRow> rows = new ArrayList<>();
        LocalDateTime startInterval = getStartInterval(start, interval);
        while (startInterval.isBefore(end)) {
            LocalDateTime endInterval = startInterval.plus(1, interval.getChronoUnit());
            rows.addAll(collectDataInIntervalToLightRows(data, startInterval, endInterval));
            startInterval = endInterval;
        }
        System.out.printf("%-32s %-19s %10s%n", "DEVICE", "DATE", "LIGHT");
        int printed = 0;
        int total = rows.size();
        for (LightRow row : rows) {
            System.out.printf(
                    "%-32s %-19s %10.2f%n",
                    row.deviceName,
                    row.dateTime.format(DATE_TIME_FORMATTER),
                    row.light
            );
            printed++;
            if (printed % PAGE_SIZE == 0 && printed < total) {
                System.out.printf("Выведено %d строк из %d. Нажмите Enter для продолжения...%n", printed, total);
                scanner.nextLine();
            }
        }
        System.out.printf("Выведено %d строк из %d. Конец таблицы%n", printed, total);
    }

    private record LightRow(String deviceName, LocalDateTime dateTime, double light) {
    }
}
