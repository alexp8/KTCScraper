package main;

import models.Player;
import models.PlayerUrl;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import selenium.SeleniumHelper;
import util.FileHelper;
import util.PlayerHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static selenium.KTCSelenium.selenium;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String... args) {


        try {
//            scrapePlayer();
//            scrapePlayerUrls();
            scrapeAllPlayers();
        } finally {
            SeleniumHelper.tear();
        }
    }

    private static void scrapeAllPlayers() {

        selenium().openKtc();
        selenium().closeKtCPopup();

        for (PlayerUrl playerUrl : PlayerHelper.getAllPlayerUrls()) {

            String playerNameCleansed = playerUrl.getName().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = String.format("%s-%s.csv", playerNameCleansed, playerUrl.getId());

            Path outputPath = Paths.get(FileHelper.SRC_MAIN_RESOURCES.toString(), "player_data", fileName);

            if (outputPath.toFile().exists())
                continue;

            Player player = selenium().scrapePlayerData(playerUrl.getName(), playerUrl.getUrl());

            FileHelper.write(outputPath, player.csv());
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
    }
}
