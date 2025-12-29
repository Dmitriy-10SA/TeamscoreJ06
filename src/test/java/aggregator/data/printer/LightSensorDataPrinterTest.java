package aggregator.data.printer;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.sensor.data.LightData;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LightSensorDataPrinterTest {
    private static final Device DEVICE = new Device("Device1");
    private static final Sensor SENSOR = new Sensor("sensor1", Sensor.SensorType.LIGHT);
    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2025, 12, 29, 10, 0);
    private static final List<LightData> DATA = List.of(
            new LightData(SENSOR, FIXED_DATE_TIME.minusHours(1), 100),
            new LightData(SENSOR, FIXED_DATE_TIME, 200),
            new LightData(SENSOR, FIXED_DATE_TIME.plusHours(1), 300)
    );

    static {
        DEVICE.addSensor(SENSOR);
    }

    @Test
    void printHeaderTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(System.in);
        LightSensorDataPrinter printer = new LightSensorDataPrinter(scanner);
        printer.printHeader();
        String expectedHeader = String.format("%-32s %-19s %10s%n", "DEVICE", "DATE", "LIGHT");
        assertEquals(expectedHeader, outContent.toString());
    }

    @Test
    void getSensorDataRowsTest() {
        Scanner scanner = new Scanner(System.in);
        LightSensorDataPrinter printer = new LightSensorDataPrinter(scanner);
        LocalDateTime start = FIXED_DATE_TIME.minusHours(2);
        LocalDateTime end = FIXED_DATE_TIME.plusHours(2);
        List<LightSensorDataPrinter.LightDataRow> rows = printer.getSensorDataRows(DATA, start, end);
        assertEquals(1, rows.size());
        LightSensorDataPrinter.LightDataRow row = rows.get(0);
        assertEquals("Device1", row.getDeviceName());
        assertEquals(start, row.getDateTime());
        assertEquals(200.0, row.getLight());
    }

    @Test
    void printSensorDataRowTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Scanner scanner = new Scanner(System.in);
            LightSensorDataPrinter printer = new LightSensorDataPrinter(scanner);
            LightSensorDataPrinter.LightDataRow row = new LightSensorDataPrinter.LightDataRow(
                    "Device1",
                    FIXED_DATE_TIME,
                    123.45
            );
            printer.printSensorDataRow(row);
            String expected = String.format(
                    "%-32s %-19s %10.2f%n",
                    "Device1",
                    FIXED_DATE_TIME.format(SensorDataPrinter.DATE_TIME_FORMATTER),
                    123.45
            );
            assertEquals(expected, outContent.toString());
        } finally {
            System.setOut(originalOut);
        }
    }
}