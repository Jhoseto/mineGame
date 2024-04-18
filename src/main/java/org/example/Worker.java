package org.example;

import java.util.concurrent.TimeUnit;

public class Worker implements Runnable{

    private final int id;
    private int totalResourcesMined;

    public Worker(int id) {
        this.id = id;
        this.totalResourcesMined = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                mine();
                rest();
            }
        } catch (InterruptedException e) {
            System.out.println("Worker " + id + " has left");
        }
    }

    private void mine() throws InterruptedException {
        System.out.println("Worker " + id + " is mining...");
        TimeUnit.SECONDS.sleep(5);
        totalResourcesMined += 10;
        System.out.println("Worker " + id + " has received 10 resources");
    }

    private void rest() throws InterruptedException {
        System.out.println("Worker " + id + " is resting...");
        TimeUnit.SECONDS.sleep(3);
        paySalary();
    }

    private void paySalary() throws InterruptedException {
        int salary = totalResourcesMined / 2; // 5 resources for every 10 seconds of mining
        System.out.println("Worker " + id + " has received " + salary + " resources out of " + totalResourcesMined + " resources");

        if (totalResourcesMined >= MiningGame.totalResources) {
            throw new InterruptedException(); // Worker leaves if resources are depleted
        }
    }
}
