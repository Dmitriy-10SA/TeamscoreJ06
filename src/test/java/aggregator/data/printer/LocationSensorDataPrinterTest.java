package aggregator.data.printer;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.sensor.data.LocationData;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationSensorDataPrinterTest {
    private static final Device DEVICE = new Device("Device1");
    private static final Sensor SENSOR = new Sensor("sensor1", Sensor.SensorType.LOCATION);
    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime
            .of(2025, 12, 29, 10, 0);
    private static final List<LocationData> DATA = List.of(
            new LocationData(SENSOR, FIXED_DATE_TIME.minusHours(1), 50.0, 100.0),
            new LocationData(SENSOR, FIXED_DATE_TIME, 50.0, 100.0),
            new LocationData(SENSOR, FIXED_DATE_TIME.plusHours(1), 100.0, 200.0),
            new LocationData(SENSOR, FIXED_DATE_TIME.plusHours(1), 100.0, 200.0)
    );

    static {
        DEVICE.addSensor(SENSOR);
    }

    @Test
    void printHeaderTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n\n".getBytes()));
        LocationSensorDataPrinter printer = new LocationSensorDataPrinter(scanner);
        printer.printHeader();
        String expectedHeader = String.format("%-32s %-19s %12s %12s%n", "DEVICE", "DATE", "LAT", "LON");
        assertTrue(outContent.toString().startsWith(expectedHeader));
    }

    @Test
    void getSensorDataRowsTest() {
        Scanner scanner = new Scanner(System.in);
        LocationSensorDataPrinter printer = new LocationSensorDataPrinter(scanner);
        LocalDateTime startInterval = FIXED_DATE_TIME.minusHours(2);
        LocalDateTime endInterval = FIXED_DATE_TIME.plusHours(2);
        List<LocationSensorDataPrinter.LocationDataRow> rows = printer.getSensorDataRows(
                DATA,
                startInterval,
                endInterval
        );
        assertEquals(1, rows.size());
        LocationSensorDataPrinter.LocationDataRow row = rows.get(0);
        assertEquals("Device1", row.getDeviceName());
        assertEquals(startInterval, row.getDateTime());
        assertEquals(75, row.getLongitude());
        assertEquals(150, row.getLatitude());
    }

    @Test
    void printSensorDataRowTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Scanner scanner = new Scanner(System.in);
            LocationSensorDataPrinter printer = new LocationSensorDataPrinter(scanner);
            LocationSensorDataPrinter.LocationDataRow row = new LocationSensorDataPrinter.LocationDataRow(
                    "Device1",
                    FIXED_DATE_TIME,
                    50.5,
                    60.5
            );
            printer.printSensorDataRow(row);
            String expected = String.format(
                    "%-32s %-19s %12.6f %12.6f%n",
                    "Device1",
                    FIXED_DATE_TIME.format(SensorDataPrinter.DATE_TIME_FORMATTER),
                    50.5,
                    60.5
            );
            String output = outContent.toString();
            assertEquals(expected, output);
        } finally {
            System.setOut(originalOut);
        }
    }
}