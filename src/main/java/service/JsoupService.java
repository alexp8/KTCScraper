package service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupService {
    private static final Logger logger = LogManager.getLogger();

    public static Document get(String url) {
        logger.debug("Calling '{}'", url);

        try {
            return Jsoup.connect(url).get();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
