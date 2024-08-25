package service;

import lombok.Getter;

@Getter
public enum KTCUrl {
    DYNASTY_RANKINGS("https://keeptradecut.com/dynasty-rankings");

    private final String url;

    KTCUrl(String url) {
        this.url = url;
    }
}
