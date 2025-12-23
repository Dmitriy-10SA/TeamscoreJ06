package aggregator.data.printer;

import aggregator.SensorPrinter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public abstract class SensorDataPrinter<T> {
    public final int PAGE_SIZE = 16;
    public final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime getStartInterval(
            LocalDateTime start,
            SensorPrinter.SensorPrinterInterval interval
    ) {
        return switch (interval) {
            case MINUTE, HOUR, DAY -> start.truncatedTo(interval.getChronoUnit());
            case WEEK -> start.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        };
    }

    public abstract void printData(
            List<T> data,
            SensorPrinter.SensorPrinterInterval interval,
            LocalDateTime start,
            LocalDateTime end
    );
}
