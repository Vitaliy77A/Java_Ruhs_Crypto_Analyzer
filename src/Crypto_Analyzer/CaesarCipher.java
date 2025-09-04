package Crypto_Analyzer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CaesarCipher {
    private static final String FREQUENT_UA = "оаеинртсл";
    private static final String FREQUENT_EN = "etaoinshrdl";

    private static final String[] STOP_WORDS = {" и ", " в ", " на ", " не ", " це ", " та ", " the ", " and ", " to "};

    private static final String ALPHABET_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,«»':!?&#@^+* ";
    private static final String ALPHABET_UA = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯабвгґдеєжзиіїйклмнопрстуфхцчшщьюя.,«»':!?&#@^+* ";


    public String encrypt(String text, int key, String alphabet) {
        return shift(text, key, alphabet);
    }

    public String decrypt(String text, int key, String alphabet) {
        return shift(text, -key, alphabet);
    }

    public String bruteForce(String encryptedText, String alphabet) {
        if (encryptedText == null || encryptedText.isEmpty()) return encryptedText;

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
            char c = text.charAt(i);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                uaChars++;
            } else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                enChars++;
            }
        }
        return (uaChars > enChars) ? ALPHABET_UA : ALPHABET_EN;
    }

    private String shift(String text, int key, String alphabet) {
        StringBuilder result = new StringBuilder();
        int alphLen = alphabet.length();
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            int oldPosition = alphabet.indexOf(currentChar);
            if (oldPosition != -1) {
                int newPosition = (oldPosition + key + alphLen) % alphLen;
                result.append(alphabet.charAt(newPosition));
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    private String decryptByFrequencyAnalysis(String encryptedText, String alphabet) {
        String frequent = (alphabet.indexOf('А') != -1) ? FREQUENT_UA : FREQUENT_EN;
        char mostFrequentInText = findMostFrequentCharInText(encryptedText);
        if (mostFrequentInText == ' ') {
            return encryptedText;
        }
        int alphLen = alphabet.length();
        int idxTextMost = indexInAlphabet(alphabet, mostFrequentInText);
        if (idxTextMost == -1) {
            System.out.println("Увага: часта буква у алфавіті не знайдена: " + mostFrequentInText);
            return encryptedText;
        }

        for (int i = 0; i < frequent.length(); i++) {
            char candidate = frequent.charAt(i);
            int idxCandidate = indexInAlphabet(alphabet, candidate);
            if (idxCandidate == -1) continue;

            int key = (idxTextMost - idxCandidate) % alphLen;
            if (key < 0) key += alphLen;

            String decrypted = decrypt(encryptedText, key, alphabet);
            if (isTextPlausible(decrypted)) {
                return decrypted;
            }
        }
        int idxCandidate = indexInAlphabet(alphabet, frequent.charAt(0));
        if (idxCandidate == -1) return encryptedText;
        int key = (idxTextMost - idxCandidate) % alphLen;
        if (key < 0) key += alphLen;
        return decrypt(encryptedText, key, alphabet);
    }

    private int indexInAlphabet(String alphabet, char ch) {
        int idx = alphabet.indexOf(ch);
        if (idx != -1) return idx;
        char low = Character.toLowerCase(ch);
        idx = alphabet.indexOf(low);
        if (idx != -1) return idx;
        char up = Character.toUpperCase(ch);
        return alphabet.indexOf(up);
    }

    private String findBestMatchByBruteForce(String encryptedText, String alphabet) {
        String bestDecryption = "";
        int maxScore = -1;
        for (int key = 1; key < alphabet.length(); key++) {
            String decryptedText = decrypt(encryptedText, key, alphabet);
            int currentScore = getTextScore(decryptedText);
            if (currentScore > maxScore) {
                maxScore = currentScore;
                bestDecryption = decryptedText;
            }
        }

        return (bestDecryption.isEmpty()) ? encryptedText : bestDecryption;
    }

    private char findMostFrequentCharInText(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char raw : text.toCharArray()) {
            if (Character.isLetter(raw)) {
                char c = Character.toLowerCase(raw);
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }
        }
        if (frequencyMap.isEmpty()) {
            return ' ';
        }
        char mostFrequent = ' ';
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }
        return mostFrequent;
    }

    private boolean isTextPlausible(String text) {
        if (text == null || text.isEmpty()) return false;
        int len = text.length();
        if (len < 30) return false;

        int spaceCount = getTextScore(text);
        double spacePercentage = (double) spaceCount / len;

        if (len >= 100) {
            return spacePercentage >= 0.14 || containsStopWord(text);
        } else if (len >= 50) {
            return spacePercentage >= 0.12 || containsStopWord(text);
        } else {
            return spacePercentage >= 0.10 || containsStopWord(text);
        }
    }

    private boolean containsStopWord(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();

        for (String stopWord : STOP_WORDS) {
            if (lower.contains(stopWord)) {
                return true;
            }
        }
        return false;
    }

    private int getTextScore(String text) {
        if (text == null || text.isEmpty()) return 0;

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



