package aggregator.data.printer;

import aggregator.SensorPrinter;
import common.entity.Device;
import common.entity.Sensor;
import common.entity.sensor.data.SensorData;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensorDataPrinterTest {
    private static class TestSensorData extends SensorData {
        private final int value;

        public TestSensorData(Sensor sensor, LocalDateTime measureAt, int value) {
            super(sensor, measureAt);
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static class TestRow extends SensorDataPrinter.SensorDataRow {
        public TestRow(String deviceName, LocalDateTime dateTime) {
            super(deviceName, dateTime);
        }
    }

    private SensorDataPrinter<TestSensorData, TestRow> getTestPrinter(Scanner scanner) {
        return new SensorDataPrinter<>(scanner) {
            @Override
            protected List<TestRow> getSensorDataRows(
                    List<TestSensorData> data,
                    LocalDateTime startInterval,
                    LocalDateTime endInterval
            ) {
                return List.of(new TestRow("1", LocalDateTime.now()));
            }

            @Override
            protected void printHeader() {
                System.out.println("Header");
            }

            @Override
            protected void printSensorDataRow(TestRow row) {
                System.out.println("SensorDataRow");
            }
        };
    }

    @Test
    void testGetSensorDataRows() {
        Scanner scanner = new Scanner("");
        SensorDataPrinter<?, TestRow> printer = getTestPrinter(scanner);
        List<TestRow> rows = printer.getSensorDataRows(List.of(), LocalDateTime.now(), LocalDateTime.now());
        assertEquals(1, rows.size());
        assertEquals("1", rows.get(0).getDeviceName());
    }

    @Test
    void testGetStartInterval() {
        SensorDataPrinter<?, ?> printer = getTestPrinter(new Scanner(""));
        LocalDateTime dt = LocalDateTime.of(2025, 12, 29, 10, 23, 45);
        assertEquals(
                LocalDateTime.of(2025, 12, 29, 10, 23, 0),
                printer.getStartInterval(dt, SensorPrinter.SensorPrinterInterval.MINUTE)
        );
        assertEquals(
                LocalDateTime.of(2025, 12, 29, 10, 0, 0),
                printer.getStartInterval(dt, SensorPrinter.SensorPrinterInterval.HOUR)
        );
        assertEquals(
                LocalDateTime.of(2025, 12, 29, 0, 0, 0),
                printer.getStartInterval(dt, SensorPrinter.SensorPrinterInterval.DAY)
        );
        assertEquals(
                LocalDateTime.of(2025, 12, 29, 0, 0, 0),
                printer.getStartInterval(dt, SensorPrinter.SensorPrinterInterval.WEEK)
        );
    }

    @Test
    void testGetCollectDataInInterval() {
        List<TestSensorData> data = getTestSensorData();
        SensorDataPrinter<TestSensorData, TestRow> printer = getTestPrinter(new Scanner(""));
        LocalDateTime startInterval = LocalDateTime.of(2025, 12, 29, 10, 0);
        LocalDateTime endInterval = LocalDateTime.of(2025, 12, 29, 12, 0);
        TreeMap<String, List<TestSensorData>> result = printer.getCollectDataInInterval(data, startInterval, endInterval);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("Device1"));
        assertEquals(2, result.get("Device1").size());
        assertEquals(1, result.get("Device2").size());
    }

    private static List<TestSensorData> getTestSensorData() {
        Device device1 = new Device("Device1");
        Device device2 = new Device("Device2");
        Sensor sensor1 = new Sensor("1", Sensor.SensorType.LOCATION);
        Sensor sensor2 = new Sensor("2", Sensor.SensorType.LOCATION);
        device1.addSensor(sensor1);
        device2.addSensor(sensor2);
        return List.of(
                new TestSensorData(sensor1, LocalDateTime.of(2025, 12, 29, 8, 0), 1_000),
                new TestSensorData(sensor1, LocalDateTime.of(2025, 12, 29, 10, 0), 10),
                new TestSensorData(sensor1, LocalDateTime.of(2025, 12, 29, 11, 0), 20),
                new TestSensorData(sensor2, LocalDateTime.of(2025, 12, 29, 11, 0), 20),
                new TestSensorData(sensor1, LocalDateTime.of(2025, 12, 29, 12, 0), 30),
                new TestSensorData(sensor1, LocalDateTime.of(2025, 12, 29, 12, 0), 5_000),
                new TestSensorData(sensor2, LocalDateTime.of(2025, 12, 29, 12, 0), 5_000)
        );
    }

    @Test
    void testPrintSensorDataRow() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(System.in);
        SensorDataPrinter<?, TestRow> printer = getTestPrinter(scanner);
        printer.printSensorDataRow(new TestRow(null, null));
        String output = outContent.toString();
        assertEquals("SensorDataRow\n", output);
    }

    @Test
    void testPrintHeader() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(System.in);
        SensorDataPrinter<?, ?> printer = getTestPrinter(scanner);
        printer.printHeader();
        String output = outContent.toString();
        assertEquals("Header\n", output);
    }

    @Test
    void testPrintData() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        SensorDataPrinter<TestSensorData, TestRow> printer = getTestSensorDataTestRowSensorDataPrinter();
        List<TestSensorData> data = new ArrayList<>();
        common.entity.Sensor sensor = new common.entity.Sensor("s1", common.entity.Sensor.SensorType.LOCATION);
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 3; i++) {
            data.add(new TestSensorData(sensor, now.plusHours(i), 100));
        }
        printer.printData(data, SensorPrinter.SensorPrinterInterval.HOUR, now, now.plusHours(3));
        String output = outContent.toString();
        assertTrue(output.contains("HEADER"));
        assertTrue(output.contains("ROW Device0"));
        assertTrue(output.contains("ROW Device1"));
        assertTrue(output.contains("ROW Device2"));
        assertTrue(output.contains("Конец таблицы"));
    }

    private static SensorDataPrinter<TestSensorData, TestRow> getTestSensorDataTestRowSensorDataPrinter() {
        Scanner scanner = new Scanner("\n");
        return new SensorDataPrinter<>(scanner) {
            @Override
            protected List<TestRow> getSensorDataRows(List<TestSensorData> data, LocalDateTime startInterval, LocalDateTime endInterval) {
                List<TestRow> rows = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    rows.add(new TestRow("Device" + i, data.get(i).getMeasureAt()));
                }
                return rows;
            }

            @Override
            protected void printHeader() {
                System.out.println("HEADER");
            }

            @Override
            protected void printSensorDataRow(TestRow row) {
                System.out.println("ROW " + row.getDeviceName());
            }
        };
    }
}