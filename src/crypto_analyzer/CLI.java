package crypto_analyzer;

import java.io.IOException;
import java.util.Scanner;

import static crypto_analyzer.OperationType.*;

// CLI.java
import java.util.Scanner;

public class CLI {

    private final CaesarCipher caesarCipher = new CaesarCipher();
    private final FileService fileService = new FileService();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n===== Головне меню =====");
            System.out.println("1. Зашифрувати файл.");
            System.out.println("2. Розшифрувати файл ключем.");
            System.out.println("3. Підібрати ключ.");
            System.out.println("0. Вихід з програми.");
            System.out.print("Ваш вибір: ");

            String choice = scanner.nextLine().trim();

            OperationType operation = getOperationByChoice(choice);

            if (operation == null) {
                System.out.println("Не вірний вибір. Спробуйте ще раз.");
                continue;
            }

            switch (operation) {
                case ENCRYPT:
                    performOperationByKey(false);
                    break;
                case DECRYPT:
                    performOperationByKey(true);
                    break;
                case BRUTE_FORCE:
                    performBruteForce();
                    break;
                case EXIT:
                    System.out.println("Дякую за використання! До побачення.");
                    return;
            }

        }
    }

    private OperationType getOperationByChoice(String choice) {
        switch (choice) {
            case "1":
                return ENCRYPT;
            case "2":
                return DECRYPT;
            case "3":
                return BRUTE_FORCE;
            case "0":
                return EXIT;
            default:
                return null;
        }
    }


    private void performBruteForce() {
        System.out.print("Введіть шлях до файлу для взлому: ");
        String filePath = scanner.nextLine();
        try {
            String content = fileService.readFile(filePath);
            String alphabet = caesarCipher.detectAlphabet(content);
            String bruteForcedText = caesarCipher.bruteForce(content);
            String outputFileName = fileService.getNewFilePath(filePath, "[BRUTE_FORCED]");
            fileService.writeFile(outputFileName, bruteForcedText);
            System.out.println("Взлом завершено!");
            System.out.println("Результат збережено у файл: " + outputFileName);
        } catch (Exception e) {
            System.out.println("Сталася помилка при роботі з файлом: " + e.getMessage());
        }
    }

    private void performOperationByKey(boolean shiftMode) {

        String operationName = shiftMode ? "Розшифрування" : "Шифрування";
        String fileTag = shiftMode  ? "[DECRYPTED]" : "[ENCRYPTED]";

        System.out.print("Введіть шлях до файлу для " + operationName + ": ");
        String filePath = scanner.nextLine();

        System.out.print("Введіть ключ (ціле число): ");
        String keyLine = scanner.nextLine().trim();

        try {
            int key = Integer.parseInt(keyLine);

            String content = fileService.readFile(filePath);
            String alphabet = caesarCipher.detectAlphabet(content);

            String resultText = caesarCipher.shift(content, key, shiftMode);

            String outputFileName = fileService.getNewFilePath(filePath, fileTag);
            fileService.writeFile(outputFileName, resultText);
            System.out.println(shiftMode + " успішно завершено!");
            System.out.println("Результат збережено у файл: " + outputFileName);

        } catch (NumberFormatException e) {
            System.out.println("Помилка: ключ має бути цілим числом.");
        } catch (IOException e) {
            System.out.println("Сталася помилка при роботі з файлом: " + e.getMessage());
        }
    }
}

