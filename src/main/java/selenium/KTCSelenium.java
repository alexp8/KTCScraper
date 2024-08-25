package selenium;

import models.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.KTCUrl;
import util.ParseDateUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KTCSelenium {
    private static final Logger logger = LogManager.getLogger();
    private final WebDriverWait wait;
    private final WebDriver driver;
    private final Actions actions;

    public static KTCSelenium selenium() {
        return new KTCSelenium();
    }

    private KTCSelenium() {
        this.driver = SeleniumHelper.getDriver();
        actions = new Actions(driver);
        this.wait = new WebDriverWait(SeleniumHelper.getDriver(), Duration.ofSeconds(3));
    }

    public Player scrapePlayer(String playerName) {

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // get the url for this player
            String playerUrl = selenium().getPlayerUrl(playerName);
            logger.info("Player url found '{}'", playerUrl);
            driver.get(playerUrl);

            // get all results
            WebElement allTimeFilter = driver.findElement(By.id("all-time"));
            actions.click(allTimeFilter).perform();
            sleep(3);

            // load the graph with data
            WebElement graphEle = driver.findElement(By.id("block-value-graph"));

            List<WebElement> hoverGroups = graphEle.findElements(By.cssSelector(".hoverGroup"));

            // parse the values
            Map<String, String> values = new HashMap<>();
            for (int i = 0; i < hoverGroups.size(); i++) {

                WebElement hoverDate = hoverGroups.get(i).findElement(By.cssSelector(".hoverDate"));
                WebElement hoverValue = hoverGroups.get(i).findElement(By.cssSelector(".graphVal.hoverVal"));

                String dateText = (String) js.executeScript("return arguments[0].textContent;", hoverDate);
                String hoverValueText = (String) js.executeScript("return arguments[0].textContent;", hoverValue);

                values.put(dateText, hoverValueText);

                i += 30;
            }

            return Player.builder()
                    .name(playerName)
                    .values(values)
                    .build();

        } catch (WebDriverException e) {
            SeleniumHelper.takeScreenshot();
            throw new RuntimeException(e);
        }
    }

    /**
     * Scrape https://keeptradecut.com/dynasty-rankings, and return the player url of the player passed
     *
     * @param playerName the player name
     * @return the url to the player
     */
    public String getPlayerUrl(String playerName) {

        String url = KTCUrl.DYNASTY_RANKINGS.getUrl();
        logger.info("Opening url '{}'", url);
        driver.get(url);
        closeKtCPopup();

        WebElement searchBar = driver.findElement(By.id("search-rankings"));
        actions.sendKeys(searchBar, playerName).perform();

        // hacky wait
        sleep(2);

        // Wait for search results to load and find the first result
        List<WebElement> results = driver.findElements(By.cssSelector(".single-ranking.odd-tier.tier-start.tier-end"));

        if (results.isEmpty())
            return null;

        // iterate over results
        for (WebElement result : results) {

            WebElement name = result.findElement(By.cssSelector(".player-name"));

            // player found
            if (name.getText().toUpperCase().contains(playerName.toUpperCase())) {

                WebElement href = name.findElement(By.cssSelector("p > a"));
                return href.getAttribute("href");
            }
        }

        return null;
    }

    private void sleep(int sleepDuration) {
        try {
            TimeUnit.SECONDS.sleep(sleepDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeKtCPopup() {

        try {
            WebElement close = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dont-know")));
            actions.click(close).perform();
        } catch (Exception ignored){

        }
    }
}