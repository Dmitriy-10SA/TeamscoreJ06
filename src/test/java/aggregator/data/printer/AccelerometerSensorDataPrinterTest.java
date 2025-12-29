package aggregator.data.printer;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.sensor.data.AccelerometerData;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccelerometerSensorDataPrinterTest {
    private static final Device DEVICE = new Device("Device1");
    private static final Sensor SENSOR = new Sensor("sensor1", Sensor.SensorType.ACCELEROMETER);
    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2025, 12, 29, 10, 0);
    private static final List<AccelerometerData> DATA = List.of(
            new AccelerometerData(SENSOR, FIXED_DATE_TIME.minusHours(1), 1, 2, 3),
            new AccelerometerData(SENSOR, FIXED_DATE_TIME, 4, 5, 6),
            new AccelerometerData(SENSOR, FIXED_DATE_TIME.plusHours(1), 7, 8, 9)
    );

    static {
        DEVICE.addSensor(SENSOR);
    }

    @Test
    void printHeaderTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(System.in);
        AccelerometerSensorDataPrinter printer = new AccelerometerSensorDataPrinter(scanner);
        printer.printHeader();
        String expectedHeader = String.format("%-32s %-19s %10s %10s %10s%n", "DEVICE", "DATE", "X", "Y", "Z");
        assertEquals(expectedHeader, outContent.toString());
    }

    @Test
    void getSensorDataRowsTest() {
        Scanner scanner = new Scanner(System.in);
        AccelerometerSensorDataPrinter printer = new AccelerometerSensorDataPrinter(scanner);
        LocalDateTime start = FIXED_DATE_TIME.minusHours(2);
        LocalDateTime end = FIXED_DATE_TIME.plusHours(2);
        List<AccelerometerSensorDataPrinter.AccelerometerDataRow> rows = printer.getSensorDataRows(DATA, start, end);
        assertEquals(1, rows.size());
        AccelerometerSensorDataPrinter.AccelerometerDataRow row = rows.get(0);
        assertEquals("Device1", row.getDeviceName());
        assertEquals(start, row.getDateTime());
        assertEquals(4.0, row.getX());
        assertEquals(5.0, row.getY());
        assertEquals(6.0, row.getZ());
    }

    @Test
    void printSensorDataRowTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Scanner scanner = new Scanner(System.in);
            AccelerometerSensorDataPrinter printer = new AccelerometerSensorDataPrinter(scanner);
            AccelerometerSensorDataPrinter.AccelerometerDataRow row = new AccelerometerSensorDataPrinter.AccelerometerDataRow(
                    "Device1",
                    FIXED_DATE_TIME,
                    1.1,
                    2.2,
                    3.3
            );
            printer.printSensorDataRow(row);
            String expected = String.format(
                    "%-32s %-19s %10.2f %10.2f %10.2f%n",
                    "Device1",
                    FIXED_DATE_TIME.format(SensorDataPrinter.DATE_TIME_FORMATTER),
                    1.1,
                    2.2,
                    3.3
            );
            assertEquals(expected, outContent.toString());
        } finally {
            System.setOut(originalOut);
        }
    }
}