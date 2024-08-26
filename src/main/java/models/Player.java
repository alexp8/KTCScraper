package models;

import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class Player implements FEntity {

    /* Sort latest descending */
    public static final Comparator<Map.Entry<String, String>> COMPARATOR = Comparator.comparing(Map.Entry<String, String>::getKey).reversed();

    private final String name;
    private final int age;

    // key = 'yyyy-MM'
    private final Map<String, String> values;

    public String prettyPrint() {
        return values.entrySet().stream()
                .sorted(COMPARATOR)
                .map(this::row)
                .collect(Collectors.joining("\n"));
    }

    private String row(Map.Entry<String, String> x) {
        return "Date: " + x.getKey() + ", value: " + x.getValue();
    }

    @Getter
    @Builder
    public static class DynastyValue {
        private final int value;
    }

}
