package aggregator;

import aggregator.data.printer.AccelerometerSensorDataPrinter;
import aggregator.data.printer.BarometerSensorDataPrinter;
import aggregator.data.printer.LightSensorDataPrinter;
import aggregator.data.printer.LocationSensorDataPrinter;
import aggregator.data.provider.AccelerometerSensorDataProvider;
import aggregator.data.provider.BarometerSensorDataProvider;
import aggregator.data.provider.LightSensorDataProvider;
import aggregator.data.provider.LocationSensorDataProvider;
import common.entity.Sensor;
import common.entity.sensor.data.AccelerometerData;
import common.entity.sensor.data.BarometerData;
import common.entity.sensor.data.LightData;
import common.entity.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

/**
 * Класс для вывода данных нужного датчика в виде таблицы
 */
public class SensorPrinter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Scanner scanner;
    private final AccelerometerSensorDataPrinter accelerometerSensorDataPrinter;
    private final AccelerometerSensorDataProvider accelerometerSensorDataProvider;
    private final BarometerSensorDataPrinter barometerSensorDataPrinter;
    private final BarometerSensorDataProvider barometerSensorDataProvider;
    private final LightSensorDataPrinter lightSensorDataPrinter;
    private final LightSensorDataProvider lightSensorDataProvider;
    private final LocationSensorDataPrinter locationSensorDataPrinter;
    private final LocationSensorDataProvider locationSensorDataProvider;

    @Getter
    private boolean isRunning;

    public SensorPrinter(Scanner scanner, EntityManagerFactory factory) {
        this.scanner = scanner;
        this.accelerometerSensorDataPrinter = new AccelerometerSensorDataPrinter(scanner);
        this.accelerometerSensorDataProvider = new AccelerometerSensorDataProvider(factory);
        this.barometerSensorDataPrinter = new BarometerSensorDataPrinter(scanner);
        this.barometerSensorDataProvider = new BarometerSensorDataProvider(factory);
        this.lightSensorDataPrinter = new LightSensorDataPrinter(scanner);
        this.lightSensorDataProvider = new LightSensorDataProvider(factory);
        this.locationSensorDataPrinter = new LocationSensorDataPrinter(scanner);
        this.locationSensorDataProvider = new LocationSensorDataProvider(factory);
        this.isRunning = false;
    }

    /**
     * Печать данных в виде таблицы
     */
    private void printData(
            Sensor.SensorType sensorType,
            LocalDateTime start,
            LocalDateTime end,
            SensorPrinterInterval interval,
            String deviceName
    ) {
        switch (sensorType) {
            case LOCATION -> {
                List<LocationData> data = locationSensorDataProvider.getData(start, end, deviceName);
                locationSensorDataPrinter.printData(data, interval, start, end);
            }
            case BAROMETER -> {
                List<BarometerData> data = barometerSensorDataProvider.getData(start, end, deviceName);
                barometerSensorDataPrinter.printData(data, interval, start, end);
            }
            case LIGHT -> {
                List<LightData> data = lightSensorDataProvider.getData(start, end, deviceName);
                lightSensorDataPrinter.printData(data, interval, start, end);
            }
            case ACCELEROMETER -> {
                List<AccelerometerData> data = accelerometerSensorDataProvider.getData(start, end, deviceName);
                accelerometerSensorDataPrinter.printData(data, interval, start, end);
            }
        }
    }

    /**
     * Получение интервала по коду (ввод пользователя см. функцию start)
     */
    private SensorPrinterInterval getSensorPrinterIntervalByCode(int code) {
        return switch (code) {
            case 0 -> SensorPrinterInterval.MINUTE;
            case 1 -> SensorPrinterInterval.HOUR;
            case 2 -> SensorPrinterInterval.DAY;
            case 3 -> SensorPrinterInterval.WEEK;
            default -> throw new IllegalArgumentException("Неизвестный интервал");
        };
    }

    /**
     * Получение типа датчика по коду (ввод пользователя см. функцию start)
     */
    private Sensor.SensorType getSensorTypeByCode(int code) {
        return switch (code) {
            case 1 -> Sensor.SensorType.ACCELEROMETER;
            case 2 -> Sensor.SensorType.BAROMETER;
            case 3 -> Sensor.SensorType.LIGHT;
            case 4 -> Sensor.SensorType.LOCATION;
            default -> throw new IllegalArgumentException("Неизвестный тип датчика");
        };
    }

    /**
     * Запуск класса для вывода данных
     */
    public void start() {
        if (isRunning) {
            throw new IllegalArgumentException("SensorPrinter уже запущен!");
        }
        isRunning = true;
        while (isRunning) {
            System.out.print("Выберите действие (0 - выход, 1 - печать ACCELEROMETER," +
                    " 2 - печать BAROMETER, 3 - печать LIGHT, 4 - печать LOCATION): ");
            int sensorTypeCode = scanner.nextInt();
            if (sensorTypeCode == 0) {
                stop();
                break;
            } else if (sensorTypeCode < 0 || sensorTypeCode > 4) {
                System.out.println("Неизвестное действие. Попробуйте снова.");
                continue;
            }
            scanner.nextLine();
            System.out.print("Введите начальную дату и время (в формате yyyy-MM-dd HH:mm:ss): ");
            LocalDateTime start = LocalDateTime.parse(scanner.nextLine(), DATE_TIME_FORMATTER);
            System.out.print("Введите конечную дату и время (в формате yyyy-MM-dd HH:mm:ss): ");
            LocalDateTime end = LocalDateTime.parse(scanner.nextLine(), DATE_TIME_FORMATTER);
            System.out.print("Введите интервал (0 - минута, 1 - час, 2 - день, 3 - неделя): ");
            int intervalCode = scanner.nextInt();
            if (intervalCode < 0 || intervalCode > 3) {
                System.out.println("Неизвестное действие. Попробуйте снова.");
                continue;
            }
            scanner.nextLine();
            SensorPrinterInterval interval = getSensorPrinterIntervalByCode(intervalCode);
            System.out.print("Введите имя устройства (нажмите Enter, чтобы не указывать): ");
            String deviceName = scanner.nextLine();
            System.out.println("\n------------------------------------------------------------------------------");
            System.out.println("Идет загрузка данных, пожалуйста, подождите...");
            System.out.println("------------------------------------------------------------------------------");
            Sensor.SensorType type = getSensorTypeByCode(sensorTypeCode);
            printData(type, start, end, interval, deviceName);
            System.out.println("------------------------------------------------------------------------------\n");
        }
    }

    /**
     * Остановка класса для вывода данных
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Интервал времени, который будем использовать для усреднения значений
     */
    @Getter
    public enum SensorPrinterInterval {
        MINUTE(ChronoUnit.MINUTES),
        HOUR(ChronoUnit.HOURS),
        DAY(ChronoUnit.DAYS),
        WEEK(ChronoUnit.WEEKS);

        private final ChronoUnit chronoUnit;

        SensorPrinterInterval(ChronoUnit chronoUnit) {
            this.chronoUnit = chronoUnit;
        }
    }
}