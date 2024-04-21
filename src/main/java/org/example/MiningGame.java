package org.example;

import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiningGame {

    private static final int MAX_MINE_SIZE = 10;
    public static int totalResourcesInMine = 0;
    private static final List<Worker> workers = new ArrayList<>();
    private static long startTime;
    private static long endTime;
    private static Worker workerInterface;

    public MiningGame(Worker workerInterface) {
        MiningGame.workerInterface = workerInterface;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
                            System.out.println("Добавихте нов работник (Worker " + newWorkerId + ")");
                        } else {
                            System.out.println("Достигнахте максималния брой работници за мината");
                        }
                        break;
                    case "q":
                        printStatistic();
                        executor.shutdownNow();
                        endTime = System.currentTimeMillis();
                        return;
                    default:
                        System.out.println("Невалидна команда !");
                }
            } else if (command.length == 2 && command[0].equals("-")) {
                int workerId = Integer.parseInt(command[1]);
                if (workerId > 0 && workerId <= workers.size()) {
                    Worker fireWorker = workers.get(workerId - 1);
                    fireWorker.stopWorker();
                    System.out.println("Отстранен работник (Worker " + fireWorker.getId() + ")");
                    mineSize--;
                } else {
                    System.out.println("Невалиден идентификатор на работник!");
                }
            } else {
                System.out.println("Невалидна команда !");
            }
        }
    }

    public static void printStatistic() {
        System.out.println("------  СТАТИСТИКА -------");
        System.out.println("Начало на копаенето: " + formatTime(startTime));
        System.out.println("Край на копаенето: " + formatTime(endTime));
        System.out.println("Мината е разполагала с " + totalResourcesInMine + " ресурса.");
        System.out.println("Изработено време: " + getTotalMiningTime());

        System.out.println("-------- РАБОТНИЦИ --------");
        for (Worker worker : workers) {
            System.out.println("Миньор " + worker.getId());
            System.out.println("Начало на работа: " + formatTime(worker.getHireTime()));
            System.out.println("Изкопани ресурси: " + worker.getTotalMinedResources());
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
