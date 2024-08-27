package models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerUrl {
    private final String id;
    private final String name;
    private final String url;
}
