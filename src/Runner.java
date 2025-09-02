import Crypto_Analyzer.CaesarCipher;
import Crypto_Analyzer.FileService;

public class Runner {
    private static final String ENCRYPT = "ENCRYPT";
    private static final String DECRYPT = "DECRYPT";
    private static final String BRUTE_FORCE = "BRUTE_FORCE";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Надано недостатньо аргументів. Приклад: ENCRYPT C:\\file.txt 5");
            return;
        }
        CaesarCipher caesarCipher = new CaesarCipher();
        FileService fileService = new FileService();
        String command = args[0].toUpperCase();
        String inputFilePath = args[1];
        String content = fileService.readFile(inputFilePath);
        String alphabet = caesarCipher.detectAlphabet(content);
        System.out.println("Автоматично визначено мову: " + (alphabet.contains("А") ? "Українська" : "Англійська"));


        switch (command) {
            case ENCRYPT:
            case DECRYPT:
                if (args.length != 3) {
                    System.out.println("Помилка: для команди " + command +
                            " потрібно 3 аргументи (команда, файл, ключ).");
                    break;
                }
                try {
                    int key = Integer.parseInt(args[2]);
                    String resultText;
                    String tag;
                    if (command.equals(ENCRYPT)) {
                        resultText = caesarCipher.encrypt(content, key, alphabet);
                        tag = "[ENCRYPTED]";
                    } else {
                        resultText = caesarCipher.decrypt(content, key, alphabet);
                        tag = "[DECRYPTED]";
                    }
                    String outputFileName = getOutputFileName(inputFilePath, tag);
                    fileService.writeFile(outputFileName, resultText);
                    System.out.println("Операція " + command + " успішно завершена.");
                    System.out.println("Результат збережено у файл: " + outputFileName);
                } catch (NumberFormatException e) {
                    System.out.println("Помилка: ключ має бути цілим числом.");
                }
                break;

            case BRUTE_FORCE:
                if (args.length != 2) {
                    System.out.println("Помилка: для команди " + command +
                            " потрібно 2 аргументи (команда, файл).");
                    break;
                }

                String bestDecryption = caesarCipher.bruteForce(content, alphabet);
                String outputFileName = getOutputFileName(inputFilePath, "[BRUTE_FORCED]");
                fileService.writeFile(outputFileName, bestDecryption);
                System.out.println("Готово! Brute Force завершено. Результат тут: " + outputFileName);
                break;

            default:
                System.out.println("Невідома команда: " + command);
                System.out.println("Доступні команди: ENCRYPT, DECRYPT, BRUTE_FORCE");
                break;
        }
    }

    /**
     * Вспомогательный метод для создания имени выходного файла.
     * Например, из "C:\docs\file.txt" и "[TAG]" сделает "C:\docs\file[TAG].txt"
     */
    private static String getOutputFileName(String originalPath, String tag) {
        int dotIndex = originalPath.lastIndexOf('.');
        if (dotIndex == -1) {
            return originalPath + tag;

        } else {
            String name = originalPath.substring(0, dotIndex);
            String extension = originalPath.substring(dotIndex);
            return name + tag + extension;
        }
    }
}

