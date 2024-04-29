package org.example;

public class Worker extends Thread {

    private long id;
    private int totalMinedResources;
    private double totalReceivedMoney;
    private int totalWorkingTime;
    private int totalRestingTime;
    private long hireTime;
    private long fireTime;
    private volatile boolean isStopped = false; //volatile Гарантира видимост от всички нишки
    private final MiningGame miningGame;

    public Worker(int id,
                  MiningGame miningGame) {
        this.id = id;
        this.miningGame = miningGame;
        this.hireTime = System.currentTimeMillis();
    }

    public synchronized void run() {
        try {
            while (miningGame.getTotalResourcesInMine() > 0 && !isStopped) {
                mine();
                rest();
                paySalary();
                System.out.println("Left resources " + miningGame.getTotalResourcesInMine());
            }
        } catch(InterruptedException e){

            System.out.println("Worker " + id + " has left");
        } finally{
            fireTime = System.currentTimeMillis();
        }
    }

    private synchronized void mine() throws InterruptedException {
        if (!isStopped) {
            System.out.println("Worker " + id + " is mining...");
            Thread.sleep(5000);
            System.out.println("Worker " + id + " has received 10 resources");
        }
    }

    public synchronized void rest() throws InterruptedException {
        if (!isStopped) {
            addResources(10);
            totalMinedResources += 10;
            totalWorkingTime += 5;
            miningGame.setTotalResourcesInMine(miningGame.getTotalResourcesInMine()-10);
            System.out.println("Left"+miningGame.getTotalResourcesInMine());
            System.out.println("Worker " + id + " is resting...");
            Thread.sleep(3000);
            totalRestingTime += 3;
        }
    }

    public void paySalary() {
        double salary = 2.5; // 2.5 $ for every 5 sec on work
        totalReceivedMoney += salary;
        System.out.println("Worker " + id + " has received $" + totalReceivedMoney + " for total " + totalMinedResources + " mined resources");
    }
    public long getId() {
        return id;
    }


    public int getTotalMinedResources() {
        return totalMinedResources;
    }


    public double getTotalReceivedMoney() {
        return totalReceivedMoney;
    }


    public void addResources(int resources) {
        totalMinedResources += resources;
    }


    public int getTotalWorkingTime() {
        return totalWorkingTime;
    }


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

}
