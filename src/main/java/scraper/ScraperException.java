package scraper;

public class ScraperException extends RuntimeException {

    public ScraperException(String msg, Throwable e){
        super(msg, e);
    }

    public ScraperException(String msg){
        super(msg);
    }
}
