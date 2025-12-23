package aggregator.data.printer;

import aggregator.SensorPrinter;
import common.entities.sensor.data.LocationData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class LocationSensorDataPrinter extends SensorDataPrinter<LocationData> {
    private final Scanner scanner;

    public LocationSensorDataPrinter(Scanner scanner) {
        this.scanner = scanner;
    }

    private List<LocationRow> collectDataInIntervalToLocationRows(
            List<LocationData> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        List<LocationRow> rows = new ArrayList<>();
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
                    double lat = values.stream().mapToDouble(LocationData::getLatitude).average().orElse(0);
                    double lon = values.stream().mapToDouble(LocationData::getLongitude).average().orElse(0);
                    rows.add(new LocationRow(device, startInterval, lat, lon));
                });
        return rows;
    }

    @Override
    public void printData(
            List<LocationData> data,
            SensorPrinter.SensorPrinterInterval interval,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (data.isEmpty()) {
            System.out.println("Нет данных для выбранного периода.");
            return;
        }
        List<LocationRow> rows = new ArrayList<>();
        LocalDateTime startInterval = getStartInterval(start, interval);
        while (startInterval.isBefore(end)) {
            LocalDateTime endInterval = startInterval.plus(1, interval.getChronoUnit());
            rows.addAll(collectDataInIntervalToLocationRows(data, startInterval, endInterval));
            startInterval = endInterval;
        }
        System.out.printf("%-32s %-19s %12s %12s%n", "DEVICE", "DATE", "LAT", "LON");
        int printed = 0;
        int total = rows.size();
        for (LocationRow row : rows) {
            System.out.printf(
                    "%-32s %-19s %12.6f %12.6f%n",
                    row.deviceName,
                    row.dateTime.format(DATE_TIME_FORMATTER),
                    row.latitude,
                    row.longitude
            );
            printed++;
            if (printed % PAGE_SIZE == 0 && printed < total) {
                System.out.printf("Выведено %d строк из %d. Нажмите Enter для продолжения...%n", printed, total);
                scanner.nextLine();
            }
        }
        System.out.printf("Выведено %d строк из %d. Конец таблицы%n", printed, total);
    }

    private record LocationRow(String deviceName, LocalDateTime dateTime, double latitude, double longitude) {
    }
}
