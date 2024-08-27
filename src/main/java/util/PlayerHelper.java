package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayerHelper {
    private static final Path PLAYER_URLS_PATH = Paths.get("src", "main", "resources", "player_urls.csv");

    public static String getPlayerUrl(String findPlayerName) {

        if (findPlayerName == null)
            return null;

        try (BufferedReader br = new BufferedReader(new FileReader(PLAYER_URLS_PATH.toString()))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] columns = line.split(",");

                String curPlayerId = columns[0];
                String curPlayerName = columns[1];
                String curPlayerUrl = columns[2];

                String curPlayerNameCleansed = cleanse(curPlayerName).toUpperCase();
                String findPlayerNameCleansed = cleanse(findPlayerName).toUpperCase();

                if (curPlayerNameCleansed.contains(findPlayerNameCleansed))
                    return curPlayerUrl;

                String cleansedCurPlayerUrl = cleanse(curPlayerUrl);
                if (cleansedCurPlayerUrl.contains(findPlayerNameCleansed))
                    return curPlayerUrl;
            }

            return null;

        } catch (IOException e) {
            throw new RuntimeException("Failed to find player url for player: " + findPlayerName, e);
        }
    }

    private static String cleanse(String text) {
        return text
                .replace(".", "")
                .replaceAll("\\\\s+", "")
                .trim();
    }
}
