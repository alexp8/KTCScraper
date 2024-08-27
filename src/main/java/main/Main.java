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
            Player player = selenium().scrapePlayerData(playerUrl.getName(), playerUrl.getUrl());

            Path outputPath = Paths.get(FileHelper.SRC_MAIN_RESOURCES.toString(), "player_data", String.format("%s-%s.csv", playerUrl.getName().replace(" ", "_"), playerUrl.getId()));
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
