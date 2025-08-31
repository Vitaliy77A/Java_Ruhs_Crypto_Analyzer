import Crypto_Analyzer.CaesarCipher;
import Crypto_Analyzer.FileService;
import java.util.Map;

public class Runner {
    private static final String ENCRYPT = "ENCRYPT";
    private static final String DECRYPT = "DECRYPT";
    private static final String BRUTE_FORCE = "BRUTE_FORCE";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Надано недостатньо аргументів.");

            return;
        }
        CaesarCipher caesarCipher = new CaesarCipher();
        FileService fileService = new FileService();
        String command = args[0].toUpperCase();
        String iputFilePath = args[1];
        String content = fileService.readFile(iputFilePath);
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
                int key = Integer.parseInt(args[2]);
                String resultText;
                String tag;
                if (command.equals(ENCRYPT)) {
                    resultText = caesarCipher.encrypt(content, key, alphabet);
                    tag = "[ENCRYPTED]";
                } else {
                    resultText = caesarCipher.decrypt(content, key,alphabet);
                    tag = "[DECRYPTED]";
                }
                String outputFile = iputFilePath + tag + ".txt";
                fileService.writeFile(outputFile, resultText);
                System.out.println("Операція " + command + " успішно завершена.");
                System.out.println("Результат збережено у файл: " + outputFile);

                break;

            case BRUTE_FORCE:
                if (args.length != 2) {
                    System.out.println("Помилка: для команди " + command +
                            " потрібно 2 аргументи (команда, файл).");
                }
                Map<Integer, String> allDecryptions = caesarCipher.bruteForce(content, alphabet);
                String bestDecryption = "";
                int maxScore = -1;
                for (String decryptedText : allDecryptions.values()) {
                    int currentScore = getTextScore(decryptedText);
                    if (currentScore > maxScore) {
                        maxScore = currentScore;
                        bestDecryption = decryptedText;
                    }
                }

                String outputFilePath = iputFilePath + "[BRUTE_FORCED].txt";
                fileService.writeFile(outputFilePath, bestDecryption);
                System.out.println("Готово! Brute Force завершено. Результат тут: " + outputFilePath);

                break;
        }

    }

    private static int getTextScore(String text) {
        int score = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                score++;
            }
        }

        return score;
    }

}