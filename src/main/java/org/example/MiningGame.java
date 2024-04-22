package org.example;

import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MiningGame {

    private static final int MAX_MINE_SIZE = 10;
    public static int totalResourcesInMine = 0;
    private static final List<Worker> workers = new ArrayList<>();
    private static long startTime;
    private static long endTime;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("КОМАНДИ:\n" +
                "За добавяне на нов работник  [+] \n" +
                "За премахване на работник [-2] (минус/цифра. Цифрата е Идентификатора(ID) на съответния работник)\n" +
                "За изход [q]");
        System.out.println();

        System.out.print("Изберете капацитета на мината (брой ресурси): ");
        totalResourcesInMine = scanner.nextInt();

        System.out.print("Изберете брой работници (минимум един), с които да започнете: ");
        int mineSize = scanner.nextInt();

        ExecutorService executor = Executors.newFixedThreadPool(MAX_MINE_SIZE);
        startTime = System.currentTimeMillis();

        // Start with initial workers
        for (int i = 1; i <= mineSize; i++) {
            Worker worker = new WorkerImpl(i);
            workers.add(worker);
            executor.submit(worker);
        }

        // Schedule resource check task to run every second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(mineSize);
        scheduler.scheduleAtFixedRate(() -> {
            if (totalResourcesInMine <= 0) {
                System.out.println("Ресурсите в мината са изчерпани !");
                printStatistic();
                endTime = System.currentTimeMillis();
                executor.shutdownNow(); // Stop all workers
                scheduler.shutdown(); // Stop the scheduler
            }
        }, 0, 1, TimeUnit.MICROSECONDS); // Run every second

        // Listen for commands to add or remove workers
        while (true) {
            String input = scanner.next();
            String[] command = input.split("");

            if (command.length == 1) {
                switch (command[0]) {
                    case "+":
                        if (mineSize < MAX_MINE_SIZE) {
                            mineSize++;
                            int newWorkerId = workers.size() + 1;
                            Worker newWorker = new WorkerImpl(newWorkerId);
                            workers.add(newWorker);
                            executor.submit(newWorker);
                            System.out.println("\n--->Добавихте нов работник (Worker " + newWorkerId + ")\n");
                        } else {
                            System.out.println("\nДостигнахте максималния брой работници за мината\n");
                        }
                        break;
                    case "q":
                        printStatistic();
                        executor.shutdownNow();
                        endTime = System.currentTimeMillis();
                        totalResourcesInMine =0;
                        return;
                    default:
                        System.out.println("\nНевалидна команда !\n");
                }
            } else if (command.length == 2 && command[0].equals("-")) {
                int workerId = Integer.parseInt(command[1]);
                if (workerId > 0 && workerId <= workers.size()) {
                    Worker fireWorker = workers.get(workerId - 1);
                    fireWorker.stopWorker();
                    System.out.println("\n<---Отстранен работник (Worker " + fireWorker.getId() + ")\n");
                    mineSize--;
                } else {
                    System.out.println("\nНевалиден идентификатор на работник!\n");
                }
            } else {
                System.out.println("\nНевалидна команда !\n"+input);
            }
        }
    }

    public static void printStatistic() {
        System.out.println("------  СТАТИСТИКА -------");
        System.out.println("Начало на добива: " + formatTime(startTime));
        System.out.println("Край на добива: " + formatTime(endTime));
        System.out.println("Мината е разполагала с " + totalResourcesInMine + " ресурса.");
        System.out.println("Общо време за добиването на всички ресурси: " + getTotalMiningTime());

        System.out.println("-------- РАБОТНИЦИ --------");
        for (Worker worker : workers) {
            System.out.println("Миньор " + worker.getId());
            System.out.println("Начало на работа: " + formatTime(worker.getHireTime()));
            System.out.println("Участвал в добива на " + worker.getTotalMinedResources()+" от общия брой ресурси");
            System.out.println("Получени пари: $" + worker.getTotalReceivedMoney());
            System.out.println("Изработено време: " + worker.getTotalWorkingTime() + " сек.");
            System.out.println("Време в почивка: " + worker.getTotalRestingTime() + " сек.");
            if (worker.isStopped()) {
                System.out.println("Уволнен на: " + formatTime(worker.getFireTime()));
            }
            System.out.println();
        }
    }

    private static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timeInMillis));
    }

    private static String getTotalMiningTime() {
        long totalTimeSeconds = (endTime - startTime) / 1000;
        long hours = totalTimeSeconds / 3600;
        long minutes = (totalTimeSeconds % 3600) / 60;
        long seconds = totalTimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
