package selenium;

import models.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ParseDateUtil;
import util.PlayerHelper;

import java.time.Duration;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static service.KTCUrl.DYNASTY_RANKINGS;
import static service.KTCUrl.KTC_HOST;

public class KTCSelenium {
    private static final Logger logger = LogManager.getLogger();
    private final WebDriverWait threeSecondWait;
    private final WebDriverWait fiveSecondWait;
    private final WebDriver driver;
    private final Actions actions;

    public static KTCSelenium selenium() {
        return new KTCSelenium();
    }

    private KTCSelenium() {
        this.driver = SeleniumHelper.getDriver();
        actions = new Actions(driver);
        this.threeSecondWait = new WebDriverWait(SeleniumHelper.getDriver(), Duration.ofSeconds(3));
        this.fiveSecondWait = new WebDriverWait(SeleniumHelper.getDriver(), Duration.ofSeconds(5));
    }

    public Player scrapePlayerFromPlayerName(String playerName) {

        try {
            // get the url for this player
            String playerUrl = PlayerHelper.getPlayerUrl(playerName);
            return scrapePlayerData(playerName, playerUrl);

        } catch (WebDriverException e) {
            SeleniumHelper.takeScreenshot();
            throw new RuntimeException("Error processing player data for " + playerName, e);
        }
    }

    public Player scrapePlayerData(String playerName, String playerUrl) {

        logger.info("Player url found '{}'", KTC_HOST.getUrl() + playerUrl);
        driver.get(KTC_HOST.getUrl() + playerUrl);

        // get all results
        WebElement allTimeFilter = driver.findElement(By.id("all-time"));
        actions.click(allTimeFilter).perform();
        sleep(3);

        // load the graph with data
        WebElement graphEle = driver.findElement(By.id("block-value-graph"));
        String html = graphEle.getAttribute("outerHTML");

        // Regex pattern to match hoverDate and hoverValue
        Pattern pattern = Pattern.compile("<text class=\"hoverDate\"[^>]*>([^<]+)<\\/text>.*?<text class=\"graphVal hoverVal\"[^>]*>([^<]+)<\\/text>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        // Find and print the hoverDate and hoverValue
        TreeMap<String, String> values = new TreeMap<>(Player.COMPARATOR);
        while (matcher.find()) {
            String dateText = matcher.group(1);
            String hoverValueText = matcher.group(2);

            values.put(ParseDateUtil.parseDate(dateText), hoverValueText);
        }

        return Player.builder()
                .name(playerName)
                .values(values)
                .build();
    }

    /**
     * Scrape https://keeptradecut.com/dynasty-rankings, and return the player url of the player passed
     *
     * @param playerName the player name
     * @return the url to the player
     */
    public String getPlayerUrl(String playerName) {

        String url = KTC_HOST.getUrl() + DYNASTY_RANKINGS.getUrl();
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

    public String getPlayerUrls() {

        closeKtCPopup();

        StringBuilder playerUrlData = new StringBuilder();
        playerUrlData.append("id,name,url").append(System.lineSeparator());

        int pageNumber = 1;
        do {
            String url = String.format("%s?page=%s&filters=QB|WR|RB|TE|RDP&format=2", KTC_HOST.getUrl() + DYNASTY_RANKINGS.getUrl(), pageNumber++);
            logger.info("Opening url '{}'", url);
            driver.get(url);

            // Wait for search results to load and find the first result
            WebElement resultsEle = fiveSecondWait.until(ExpectedConditions.presenceOfElementLocated(By.id("rankings-page-rankings")));
            String resultHtml = resultsEle.getAttribute("outerHTML");

            // parse the links to each player
            Pattern pattern = Pattern.compile("<a\\s+href=\"([^\"]+)\"[^>]*>([^<]+)<\\/a>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(resultHtml);

            while (matcher.find()) {
                String playerUrl = matcher.group(1);
                String playerName = matcher.group(2);

                String id = playerUrl.split("-")[playerUrl.split("-").length - 1];

                playerUrlData.append(id).append(",").append(playerName).append(",").append(playerUrl).append(System.lineSeparator());
            }

        } while (pageNumber < 10);

        return playerUrlData.toString();
    }

    private void sleep(int sleepDuration) {
        try {
            TimeUnit.SECONDS.sleep(sleepDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void openKtc() {
        driver.get(KTC_HOST.getUrl() + DYNASTY_RANKINGS.getUrl());
        sleep(1);
    }

    public void closeKtCPopup() {

        try {
            WebElement close = threeSecondWait.until(ExpectedConditions.presenceOfElementLocated(By.id("dont-know")));
            actions.click(close).perform();
        } catch (Exception ignored) {

        }
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("var popup = document.getElementById('dont-know'); if (popup) { popup.remove(); }");

    }
}