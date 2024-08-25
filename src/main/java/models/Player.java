package models;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class Player implements FEntity {

    private final String name;
    private final int age;

    // key = 'yyyy-MM-dd'
    private final Map<String, String> values;

    public String prettyPrint() {
       return values.entrySet().stream()
               .map(x-> "Date: " + x.getKey() + ", value: " + x.getValue())
               .collect(Collectors.joining("\n"));
    }

    @Getter
    @Builder
    public static class DynastyValue {
        private final int value;
    }

}
