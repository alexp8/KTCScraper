package main;

import models.Player;
import models.PlayerUrl;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriverException;
import selenium.SeleniumHelper;
import util.FileHelper;
import util.PlayerHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static selenium.KTCSelenium.selenium;
import static util.PlayerHelper.PLAYER_URLS_PATH;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String... args) {


        try {
//            scrapePlayer();
//            scrapePlayerUrls();
//            LocalDate minDate = LocalDate.now().minusWeeks(8);
            scrapeAllPlayers(null);
        } finally {
            SeleniumHelper.tear();
        }
    }

    private static void scrapeAllPlayers(LocalDate minDate) {

        selenium().openKtc();
        selenium().closeKtCPopup();

        for (PlayerUrl playerUrl : PlayerHelper.getAllPlayerUrls()) {

            String playerNameCleansed = playerUrl.getName().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = String.format("%s-%s.csv", playerNameCleansed, playerUrl.getId());

            Path outputPath = Paths.get(FileHelper.SRC_MAIN_RESOURCES.toString(), "player_data", fileName);

            if (outputPath.toFile().exists())
                continue;

            Player player;
            try {
                player = selenium().scrapePlayerData(playerUrl.getName(), playerUrl.getUrl());
                player.trimValuesToWeek();
                player.trimValuesPastMin(minDate);

                FileHelper.write(outputPath, player.csv());
            } catch (WebDriverException e) {
                SeleniumHelper.takeScreenshot();
                logger.warn("Error processing player data for {}, {}",playerNameCleansed, e.getMessage());
            }
        }
    }

    private static void scrapePlayer() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Player player = selenium().scrapePlayerFromPlayerName("Josh Allen");
        stopWatch.stop();
        logger.info("Finished in {} millis", stopWatch.getTime(TimeUnit.MILLISECONDS));

        logger.info("{}\n{}",
                player.getName(),
                player.prettyPrint()
        );
    }

    private static void scrapePlayerUrls() {
        String vals = selenium().getPlayerUrls();
        logger.info("Player urls:\n{}", vals);
        FileHelper.write(PLAYER_URLS_PATH, vals);
    }
}
