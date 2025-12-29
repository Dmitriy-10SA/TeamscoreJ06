package aggregator.data.printer;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.sensor.data.BarometerData;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BarometerSensorDataPrinterTest {
    private static final Device DEVICE = new Device("Device1");
    private static final Sensor SENSOR = new Sensor("sensor1", Sensor.SensorType.BAROMETER);
    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2025, 12, 29, 10, 0);
    private static final List<BarometerData> DATA = List.of(
            new BarometerData(SENSOR, FIXED_DATE_TIME.minusHours(1), 1010),
            new BarometerData(SENSOR, FIXED_DATE_TIME, 1020),
            new BarometerData(SENSOR, FIXED_DATE_TIME.plusHours(1), 1030)
    );

    static {
        DEVICE.addSensor(SENSOR);
    }

    @Test
    void printHeaderTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(System.in);
        BarometerSensorDataPrinter printer = new BarometerSensorDataPrinter(scanner);
        printer.printHeader();
        String expectedHeader = String.format("%-32s %-19s %12s%n", "DEVICE", "DATE", "AIR PRESSURE");
        assertEquals(expectedHeader, outContent.toString());
    }

    @Test
    void getSensorDataRowsTest() {
        Scanner scanner = new Scanner(System.in);
        BarometerSensorDataPrinter printer = new BarometerSensorDataPrinter(scanner);
        LocalDateTime start = FIXED_DATE_TIME.minusHours(2);
        LocalDateTime end = FIXED_DATE_TIME.plusHours(2);
        List<BarometerSensorDataPrinter.BarometerDataRow> rows = printer.getSensorDataRows(DATA, start, end);
        assertEquals(1, rows.size());
        BarometerSensorDataPrinter.BarometerDataRow row = rows.get(0);
        assertEquals("Device1", row.getDeviceName());
        assertEquals(start, row.getDateTime());
        assertEquals(1020.0, row.getAirPressure());
    }

    @Test
    void printSensorDataRowTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Scanner scanner = new Scanner(System.in);
            BarometerSensorDataPrinter printer = new BarometerSensorDataPrinter(scanner);
            BarometerSensorDataPrinter.BarometerDataRow row = new BarometerSensorDataPrinter.BarometerDataRow(
                    "Device1",
                    FIXED_DATE_TIME,
                    1013.25
            );
            printer.printSensorDataRow(row);
            String expected = String.format(
                    "%-32s %-19s %12.2f%n",
                    "Device1",
                    FIXED_DATE_TIME.format(SensorDataPrinter.DATE_TIME_FORMATTER),
                    1013.25
            );
            assertEquals(expected, outContent.toString());
        } finally {
            System.setOut(originalOut);
        }
    }
}