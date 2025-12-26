package aggregator.printer;

import aggregator.SensorPrinter;
import common.entities.sensor.data.SensorData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;


/**
 * Абстрактный класс для печати данных датчиков
 */
public abstract class SensorDataPrinter<T extends SensorData, V extends SensorDataPrinter.SensorDataRow> {
    protected static final int PAGE_SIZE = 16;
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Scanner scanner;

    public SensorDataPrinter(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Возвращает начало интервала для усреднения данных по заданному типу интервала
     */
    protected final LocalDateTime getStartInterval(
            LocalDateTime start,
            SensorPrinter.SensorPrinterInterval interval
    ) {
        return switch (interval) {
            case MINUTE, HOUR, DAY -> start.truncatedTo(interval.getChronoUnit());
            case WEEK -> start.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        };
    }

    /**
     * Возвращает TreeMap по указанному интервалу, где ключ - это название устройства, а значение - это список данных
     */
    protected final TreeMap<String, List<T>> getCollectDataInInterval(
            List<T> data,
            LocalDateTime startInterval,
            LocalDateTime endInterval
    ) {
        return data.stream()
                .filter(it -> !it.getMeasureAt().isBefore(startInterval) && it.getMeasureAt().isBefore(endInterval))
                .collect(Collectors.groupingBy(
                        it -> it.getSensor().getDevice().getName(),
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    /**
     * Получение массива строк с данными датчика по интервалу
     */
    protected abstract List<V> getSensorDataRows(List<T> data, LocalDateTime startInterval, LocalDateTime endInterval);

    /**
     * Печать заголовка
     */
    protected abstract void printHeader();

    /**
     * Печать строки с данными датчика
     */
    protected abstract void printSensorDataRow(V row);

    /**
     * Печать данных датчика в виде таблицы
     */
    public final void printData(
            List<T> data,
            SensorPrinter.SensorPrinterInterval interval,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (data.isEmpty()) {
            System.out.println("Нет данных для выбранного периода.");
            return;
        }
        List<V> rows = new ArrayList<>();
        LocalDateTime startInterval = getStartInterval(start, interval);
        while (startInterval.isBefore(end)) {
            LocalDateTime endInterval = startInterval.plus(1, interval.getChronoUnit());
            rows.addAll(getSensorDataRows(data, startInterval, endInterval));
            startInterval = endInterval;
        }
        printHeader();
        int printed = 0;
        int total = rows.size();
        for (V row : rows) {
            printSensorDataRow(row);
            printed++;
            if (printed % PAGE_SIZE == 0 && printed < total) {
                System.out.printf("Выведено %d строк из %d. Нажмите Enter для продолжения...%n", printed, total);
                scanner.nextLine();
            }
        }
        System.out.printf("Выведено %d строк из %d. Конец таблицы%n", printed, total);
    }

    /**
     * Абстрактный класс строки данных датчика
     */
    @Getter
    @AllArgsConstructor
    protected abstract static class SensorDataRow {
        private final String deviceName;
        private final LocalDateTime dateTime;
    }
}