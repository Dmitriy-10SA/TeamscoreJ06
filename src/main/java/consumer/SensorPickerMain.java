package consumer;

import common.EntityManagerFactoryProvider;
import jakarta.persistence.EntityManagerFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SensorPickerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        EntityManagerFactory factory = EntityManagerFactoryProvider.getFactory();
        try (scanner; factory) {
            SensorPicker sensorPicker = new SensorPicker(factory);
            executorService.execute(sensorPicker::start);
            //перед логами hibernate выводиться
            //можно было бы написать костыль типа: Thread.sleep(1500)
            //но решил не делать так
            System.out.println("Для завершения работы наберите в консоли STOP.");
            while (true) {
                if ("STOP".equals(scanner.nextLine())) {
                    sensorPicker.stop();
                    executorService.shutdown();
                    break;
                }
            }
        }
    }
}