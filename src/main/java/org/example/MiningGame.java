package org.example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MiningGame {

    private int mineSize;
    private int totalResourcesInMine;
    private int totalResourcesInMineOnStart = 0;
    private long startTime;
    private long endTime;
    private final List<Worker> workers = new ArrayList<>();
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public MiningGame(int totalResourcesInMine, int mineSize) throws InterruptedException {
        setMineSize(mineSize);
        setTotalResourcesInMine(totalResourcesInMine);
    }

    public void startGame(int mineSize) {
        setTotalResourcesInMineOnStart(totalResourcesInMine);
        Scanner scanner = new Scanner(System.in);
        executor = Executors.newFixedThreadPool(10);
        startTime = System.currentTimeMillis();

        // Start with initial workers from mineSize
        for (int i = 1; i <= mineSize; i++) {
            Worker worker = new Worker(i, this);
            workers.add(worker);
            executor.submit(worker);
        }

        while (getTotalResourcesInMine() > 0) {

            String input = scanner.next();
            String[] command = input.split("");

            if (command.length == 1) {
                switch (command[0]) {
                    case "+":
                        if (mineSize < 10) {
                            mineSize++;
                            int newWorkerId = workers.size() + 1;
                            Worker newWorker = new Worker(newWorkerId, this);
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
                        totalResourcesInMine = 0;
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
                System.out.println("\nНевалидна команда !\n" + input);
            }
        }
        printStatistic();
    }

    public void printStatistic() {
        System.out.println("------  СТАТИСТИКА -------");
        System.out.println("Начало на добива: " + formatTime(startTime));
        System.out.println("Край на добива: " + formatTime(endTime));
        System.out.println("Мината е разполагала с " + totalResourcesInMineOnStart + " ресурса.");
        System.out.println("Общо време за добиването на всички ресурси: " + getTotalMiningTime());

        System.out.println("-------- РАБОТНИЦИ --------");
        for (Worker worker : workers) {
            System.out.println("Миньор " + worker.getId());
            System.out.println("Начало на работа: " + formatTime(worker.getHireTime()));
            System.out.println("Участвал в добива на " + worker.getTotalMinedResources() + " от общия брой ресурси");
            System.out.println("Получени пари: $" + worker.getTotalReceivedMoney());
            System.out.println("Изработено време: " + worker.getTotalWorkingTime() + " сек.");
            System.out.println("Време в почивка: " + worker.getTotalRestingTime() + " сек.");
            if (worker.isStopped()) {
                System.out.println("Уволнен на: " + formatTime(worker.getFireTime()));
            }
            System.out.println();
        }
    }

    private String formatTime(long timeInMillis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.format(new Date(timeInMillis));
    }

    private String getTotalMiningTime() {
        long totalTimeMillis = endTime - startTime;
        long totalSeconds = totalTimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public int getTotalResourcesInMine() {
        return totalResourcesInMine;
    }

    public synchronized void setTotalResourcesInMine(int totalResourcesInMine) throws InterruptedException {
        if (totalResourcesInMine <= 0){
            endTime = System.currentTimeMillis();
            executor.shutdownNow();
            printStatistic();
        }
        this.totalResourcesInMine = totalResourcesInMine;
    }

    public MiningGame setTotalResourcesInMineOnStart(int totalResourcesInMineOnStart) {
        this.totalResourcesInMineOnStart = totalResourcesInMineOnStart;
        return this;
    }

    public MiningGame setMineSize(int mineSize) {
        this.mineSize = mineSize;
        return this;
    }
}

