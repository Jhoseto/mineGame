package org.example;



public interface Worker extends Runnable {

    int getId();

    int getTotalMinedResources();

    int getTotalReceivedMoney();

    void addResources(int resources);

    void removeMoney(int amount);

    int getTotalWorkingTime();

    int getTotalRestingTime();

    void rest() throws InterruptedException;

    void paySalary() throws InterruptedException;

    void stopWorker();

    boolean isStopped();

    long getHireTime(); // Метод за вземане на времето на стартиране

    long getFireTime();

    boolean isMineExhausted();
}