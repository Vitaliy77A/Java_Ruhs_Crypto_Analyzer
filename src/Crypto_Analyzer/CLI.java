package Crypto_Analyzer;

import java.util.Scanner;

public class CLI {

    private static final String ENCRYPT = "1";
    private static final String DECRYPT = "2";
    private static final String BRUTE_FORCE = "3";
    private static final String EXIT = "0";

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
            System.out.println("Ваш вибір: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case ENCRYPT:
                    performEncrypt();
                    break;
                case DECRYPT:
                    performDecrypt();
                    break;
                case BRUTE_FORCE:
                    performBruteForce();
                    break;
                case EXIT:
                    System.out.println("Дякую за використання! До побачення.");
                    return;
                default:
                    System.out.println("Не вірний вибір. Спробуйте ще раз.");
            }
        }

    }

    private void performEncrypt() {

        performOperationByKey("encrypt");

    }

    private void performDecrypt() {
        performOperationByKey("decrypt");
    }

    private void performBruteForce() {
        System.out.println("Введить шлях до файлу для взлому: ");
        String filePath = scanner.nextLine();
        try {
            String content = fileService.readFile(filePath);
            String alphabet = caesarCipher.detectAlphabet(content);
            String bruteForcedText = caesarCipher.bruteForce(content, alphabet);
            String outputFileName = fileService.getNewFilePath(filePath, "[BRUTE_FORCED]");
            fileService.writeFile(outputFileName, bruteForcedText);
            System.out.println("Взлом завершено!");
            System.out.println("Результат збережено у файл: " + outputFileName);
        } catch (Exception e) {
            System.out.println("Сталася помилка при роботі з файлом: " + e.getMessage());
        }

    }

    private void performOperationByKey(String operationType) {
        String operationName;
        String fileTag;
        if (operationType.equals("encrypt")) {
            operationName = "Шифрування";
            fileTag = "[ENCRYPTED]";
        } else {
            operationName = "Розшифрування";
            fileTag = "[DECRYPTED]";
        }
        System.out.println("Введіть шлях до файлу для " + operationName + ":");
        String filePath = scanner.nextLine();
        System.out.println("Введіть ключ(ціле число): ");
        try {
            int key = Integer.parseInt(scanner.nextLine());
            String content = fileService.readFile(filePath);
            String alphabet = caesarCipher.detectAlphabet(content);
            String resultText;
            if (operationType.equals("encrypt")) {
                resultText = caesarCipher.encrypt(content, key, alphabet);
            } else {
                resultText = caesarCipher.decrypt(content, key, alphabet);
            }

            String outputFileName = fileService.getNewFilePath(filePath, fileTag);
            fileService.writeFile(outputFileName, resultText);
            String capitalizedOperation = operationName.substring(0, 1).toUpperCase() + operationName.substring(1);
            System.out.println(capitalizedOperation + " успішно завершено!");
            System.out.println("Результат збережено у файл: " + outputFileName);

        } catch (NumberFormatException e) {
            System.out.println("Помилка: ключ має бути цілим числом.");
        } catch (Exception e) {
            System.out.println("Сталася помилка при роботі з файлом: " + e.getMessage());
        }
    }

}
