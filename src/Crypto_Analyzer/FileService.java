package Crypto_Analyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileService {

    public String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Не вдалося прочитати файл: " + filePath);
            e.printStackTrace();
            return "";
        }
    }

    public void writeFile(String filePath, String content) {
        try {
            Path outPath = Paths.get(filePath);
            Path parent = outPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.writeString(outPath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Не вдалося записати файл: " + filePath);
            e.printStackTrace();
        }
    }

    public String getNewFilePath(String originalPath, String tag) {
        int dotIndex = originalPath.lastIndexOf('.');
        String nameWithoutExt;
        String extension;
        if (dotIndex != -1) {
            nameWithoutExt = originalPath.substring(0, dotIndex);
            extension = originalPath.substring(dotIndex);
        } else {
            nameWithoutExt = originalPath;
            extension = "";
        }

        int lastBracketIndex = nameWithoutExt.lastIndexOf('[');
        String baseName;
        if (lastBracketIndex != -1) {
            baseName = nameWithoutExt.substring(0, lastBracketIndex);
        } else {
            baseName = nameWithoutExt;
        }

        return baseName + tag + extension;
    }
}