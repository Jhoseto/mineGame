package org.example;

import java.util.concurrent.TimeUnit;

public class WorkerImpl implements Worker {

    private int id;
    private int totalMinedResources;
    private int totalReceivedMoney;
    private int totalWorkingTime;
    private int totalRestingTime;
    private long hireTime;
    private long fireTime;
    private volatile boolean isStopped = false; //volatile Гарантира видимост от всички нишки
    private volatile boolean mineExhausted = false;

    public WorkerImpl(int id) {
        this.id = id;
        this.hireTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            while (!mineExhausted && !isStopped) {
                if (MiningGame.totalResourcesInMine <= 0) {
                    mineExhausted = true;
                    break;
                }

                mine();
                MiningGame.totalResourcesInMine -= 10;
                rest();
                System.out.println("Left resources " + MiningGame.totalResourcesInMine);
            }
        } catch (InterruptedException e) {
            System.out.println("Worker " + id + " has left");
        } finally {
            fireTime = System.currentTimeMillis();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getTotalMinedResources() {
        return totalMinedResources;
    }

    @Override
    public int getTotalReceivedMoney() {
        return totalReceivedMoney;
    }

    @Override
    public void addResources(int resources) {
        totalMinedResources += resources;
    }

    @Override
    public void removeMoney(int amount) {
        totalReceivedMoney -= amount;
    }

    @Override
    public int getTotalWorkingTime() {
        return totalWorkingTime;
    }

    @Override
    public int getTotalRestingTime() {
        return totalRestingTime;
    }

    public void stopWorker() {
        isStopped = true;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public long getHireTime() {
        return hireTime;
    }

    public long getFireTime() {
        return fireTime;
    }

    public boolean isMineExhausted() {
        return mineExhausted;
    }

    private void mine() throws InterruptedException {
        if (!isStopped && !mineExhausted) {
            System.out.println("Worker " + id + " is mining...");
            TimeUnit.SECONDS.sleep(5);
            totalMinedResources += 10;
            totalWorkingTime += 10;
            addResources(10);
            System.out.println("Worker " + id + " has received 10 resources");
        }
    }

    public void rest() throws InterruptedException {
        if (!isStopped) {
            System.out.println("Worker " + id + " is resting...");
            TimeUnit.SECONDS.sleep(3);
            totalRestingTime += 3;
            paySalary();
        }
    }

    public void paySalary() {
        int salary = 10 / 2; // 2.5 $ for every 5 sec on work
        totalReceivedMoney += salary;
        System.out.println("Worker " + id + " has received $" + salary + " for total " + totalMinedResources + " mined resources");
    }
}
