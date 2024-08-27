package service;

import lombok.Getter;

@Getter
public enum KTCUrl {
    KTC_HOST("https://keeptradecut.com"),
    DYNASTY_RANKINGS("/dynasty-rankings");

    private final String url;

    KTCUrl(String url) {
        this.url = url;
    }
}
