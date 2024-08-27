package main;

import models.Player;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import selenium.SeleniumHelper;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static selenium.KTCSelenium.selenium;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String... args) {

//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        Player player = selenium().scrapePlayer("Josh Allen");
//        stopWatch.stop();
//        logger.info("Finished in {} millis", stopWatch.getTime(TimeUnit.MILLISECONDS));
//
//        logger.info("{}\n{}",
//                player.getName(),
//                player.prettyPrint()
//        );

        try {
            String vals = selenium().getPlayerUrls();
            logger.info("Player urls:\n{}", vals);
        } finally {
            SeleniumHelper.tear();
        }
    }
}
