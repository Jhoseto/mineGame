package org.example;


import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiningGame {
    public static int totalResources = 0;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете общия капацитет на ресурси в мината: ");
        totalResources = scanner.nextInt();

        System.out.print("Въведете минималния брой работници: ");
        int mineSize = scanner.nextInt();

        ExecutorService executor = Executors.newFixedThreadPool(mineSize);

        // Start with 1 worker
        for (int i = 1; i <= mineSize; i++) {
            executor.submit(new Worker(i));
        }

        // With '+' add a new worker
        while (true) {
            String input = scanner.next();
            if (input.equals("+")) {
                mineSize++;
                executor.submit(new Worker(mineSize));
                System.out.println("Добавихте още един работник (Работник " + mineSize + ")");
            }
        }
    }
}