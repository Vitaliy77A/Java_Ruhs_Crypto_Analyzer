package crypto_analyzer;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

public class CaesarCipher {

    private static final String FREQUENT_UA = "оаеинртсл";
    private static final String FREQUENT_EN = "etaoinshrdl";


    private static final String[] STOP_WORDS = {" и ", " в ", " на ", " не ", " це ", " та ", " the ", " and ", " to "};
    private static final String ALPHABET_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,«»':!?&#@^+* ";
    private static final String ALPHABET_UA = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯабвгґдеєжзиіїйклмнопрстуфхцчшщьюя.,«»':!?&#@^+* ";

    public String shift(String text, int key, boolean shiftMode) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String alphabet = detectAlphabet(text);
        int alphLen = alphabet.length();
        key = normalizeKey(key, alphLen);
        if (shiftMode) {
            key = -key;
        }

        StringBuilder result = new StringBuilder();
        for (char currentChar : text.toCharArray()) {
            int oldPos = alphabet.indexOf(currentChar);
            if (oldPos != -1) {
                int newPos = (oldPos + key + alphLen) % alphLen;
                result.append(alphabet.charAt(newPos));
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    public String bruteForce(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        String alphabet = detectAlphabet(encryptedText);
        String decryptedByFrequency = decryptByFrequencyAnalysis(encryptedText, alphabet);

        if (isTextPlausible(decryptedByFrequency)) {
            return decryptedByFrequency;
        } else {
            System.out.println("Частковий аналіз дав сумнівний результат -> повний перебір ключів...");
            return findBestMatchByBruteForce(encryptedText, alphabet);
        }
    }

    public String detectAlphabet(String text) {
        int uaChars = 0;
        int enChars = 0;

        for (int i = 0; i < Math.min(text.length(), 100); i++) {
            String alphabet = getAlphabetForChar(text.charAt(i));
            if (alphabet.equals(ALPHABET_UA)) {
                uaChars++;
            } else if (alphabet.equals(ALPHABET_EN)) {
                enChars++;
            }
        }

        return (uaChars > enChars) ? ALPHABET_UA : ALPHABET_EN;
    }

    private int normalizeKey(int key, int alphabetLength) {
        return ((key % alphabetLength) + alphabetLength) % alphabetLength;
    }
    private String getAlphabetForChar(char c) {
        if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
            return ALPHABET_UA;
        } else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            return ALPHABET_EN;
        } else {
            return "";
        }
    }

    private String decryptByFrequencyAnalysis(String encryptedText, String alphabet) {
        String frequent = alphabet.contains("А") ? FREQUENT_UA : FREQUENT_EN;
        char mostFrequentInText = findMostFrequentCharInText(encryptedText);

        if (mostFrequentInText == ' ') {
            return encryptedText;
        }

        int alphLen = alphabet.length();
        int idxTextMost = alphabet.indexOf(mostFrequentInText);
        if (idxTextMost == -1) {
            System.out.println(" Часта буква у алфавіті не знайдена: " + mostFrequentInText);
            return encryptedText;
        }

        for (int i = 0; i < frequent.length(); i++) {
            int idxCandidate = alphabet.indexOf(frequent.charAt(i));
            if (idxCandidate == -1) {
                continue;
            }

            int key = normalizeKey(idxTextMost - idxCandidate, alphLen);
            String decrypted = shift(encryptedText, key, true);

            if (isTextPlausible(decrypted)) {
                return decrypted;
            }
        }

        int fallbackKey = normalizeKey(idxTextMost - alphabet.indexOf(frequent.charAt(0)), alphLen);
        return shift(encryptedText, fallbackKey, true);
    }

    private String findBestMatchByBruteForce(String encryptedText, String alphabet) {
        String bestDecryption = "";
        int maxScore = -1;
        for (int key = 1; key < alphabet.length(); key++) {
            String decryptedText = shift(encryptedText, key, true);
            int score = getTextScore(decryptedText);

            if (score > maxScore) {
                maxScore = score;
                bestDecryption = decryptedText;
            }
        }

        return bestDecryption.isEmpty() ? encryptedText : bestDecryption;
    }

    private char findMostFrequentCharInText(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();

        for (char raw : text.toCharArray()) {
            if (Character.isLetter(raw)) {
                char c = Character.toLowerCase(raw);
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }

        char mostFrequent = ' ';
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }
        return mostFrequent;
    }

    private boolean isTextPlausible(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        int len = text.length();
        if (len < 30) {
            return false;
        }

        int spaceCount = getTextScore(text);
        double spacePercentage = (double) spaceCount / len;
        double minSpace = (len >= 100) ? 0.14 : (len >= 50 ? 0.12 : 0.10);

        return spacePercentage >= minSpace || containsStopWord(text);
    }

    private boolean containsStopWord(String text) {
        if (text == null) {
            return false;
        }

        String lower = text.toLowerCase();
        for (String stopWord : STOP_WORDS) {
            if (lower.contains(stopWord)) {
                return true;
            }
        }
        return false;
    }

    private int getTextScore(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int score = 0;
        String lowerCaseText = text.toLowerCase();

        for (String stopWord : STOP_WORDS) {
            if (lowerCaseText.contains(stopWord)) {
                score += 10;
            }
        }

        for (char c : text.toCharArray()) {
            if (c == ' ') {
                score++;
            }
        }

        return score;
    }
}




