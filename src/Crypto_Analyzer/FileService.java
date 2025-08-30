package Crypto_Analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {
    public String readFile(String filePath) {
        try {

            return
                    Files.readString(Path.of(filePath));
        } catch (IOException e) {
            System.out.println("Не вдалося прочитати файл: " + filePath);
            e.printStackTrace();

            return "";
        }
    }

    public void writeFile(String filePath, String content) {
        try {
            Files.writeString(Path.of(filePath), content);
        } catch (IOException e) {
            System.out.println("Не вдалося записати файл: " + filePath);
            e.printStackTrace();
        }
    }

    public String getNewFilePath(String originalPath, String tag) {
        int dotIndex = originalPath.lastIndexOf('.');
        if (dotIndex == -1) {

            return originalPath + tag;

        } else {
            String name = originalPath.substring(0, dotIndex);
            String extepsion = originalPath.substring(dotIndex);

            return name + tag + extepsion;
        }
    }
}
