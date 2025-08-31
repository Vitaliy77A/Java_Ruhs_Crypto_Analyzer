package Crypto_Analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

public class CaesarCipher {
    private static final String ALPHABET_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,«»':!?&#@^+* ";
    private static final String ALPHABET_UA = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯабвгґдеєжзиіїйклмнопрстуфхцчшщьюя.,«»':!?&#@^+* ";

    private String shift(String text, int key, String alphabet) {
        StringBuilder result = new StringBuilder();
        int lenght = alphabet.length();
        for (int i = 0; i < text.length(); i++) {
            char currnetChar = text.charAt(i);
            int oldPosition = alphabet.indexOf(currnetChar);
            if (oldPosition != -1) {
                int newPosition = (oldPosition + key + lenght) % lenght;
                result.append(alphabet.charAt(newPosition));
            } else {
                result.append(currnetChar);
            }

        }

        return result.toString();
    }

    public String encrypt(String text, int key, String alphabet) {

        return shift(text, key, alphabet);
    }

    public String decrypt(String text, int key, String alphabet) {

        return shift(text, -key, alphabet);
    }

    public Map<Integer, String> bruteForce(String text, String alphabet) {
        LinkedHashMap<Integer, String> result = new LinkedHashMap<>();
        for (int key = 1; key <= alphabet.length(); key++) {
            String decrypted = decrypt(text, key, alphabet);
            result.put(key, decrypted);
        }

        return result;
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
        if (uaChars > enChars) {

            return ALPHABET_UA;

        } else {

            return ALPHABET_EN;
        }

    }
}
