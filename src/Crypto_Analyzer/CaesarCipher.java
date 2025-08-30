package Crypto_Analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

public class CaesarCipher {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,«»':!?&#@^+* ";

    private String shift(String text, int key) {
        StringBuilder result = new StringBuilder();
        int lenght = ALPHABET.length();
        for (int i = 0; i < text.length(); i++) {
            char currnetChar = text.charAt(i);
            int oldPosition = ALPHABET.indexOf(currnetChar);
            if (oldPosition != -1) {
                int newPosition = (oldPosition + key + lenght) % lenght;
                result.append(ALPHABET.charAt(newPosition));
            } else {
                result.append(currnetChar);
            }

        }

        return result.toString();
    }

    public String encrypt(String text, int key) {

        return shift(text, key);
    }

    public String decrypt(String text, int key) {

        return shift(text, -key);
    }

    public Map<Integer, String> bruteForce(String text) {
        LinkedHashMap<Integer, String> result = new LinkedHashMap<>();
        for (int key = 1; key <= ALPHABET.length(); key++) {
            String decrypted = decrypt(text, key);
            result.put(key, decrypted);
        }

        return result;
    }
}
