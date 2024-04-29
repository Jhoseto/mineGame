package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("""
                КОМАНДИ:
                За добавяне на нов работник  [+]\s
                За премахване на работник [-2] (минус/цифра. Цифрата е Идентификатора(ID) на съответния работник)
                За изход [q]""");
        System.out.println();

        System.out.print("Изберете капацитета на мината (брой ресурси): ");
        int totalResourcesInMine = scanner.nextInt();

        System.out.print("Изберете брой работници (минимум един), с които да започнете: ");
        int mineSize = scanner.nextInt();
        MiningGame miningGame = new MiningGame(totalResourcesInMine,mineSize);
        miningGame.startGame(mineSize);
    }
}
