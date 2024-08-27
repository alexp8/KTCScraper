package models;

import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@Builder
public class Player {

    /* Sort latest descending */
    public static final Comparator<String> COMPARATOR = Comparator.reverseOrder();

    private final String name;
    private final int age;

    // key = 'yyyy-MM'
    private final TreeMap<String, String> values;

    public String prettyPrint() {
        return values.entrySet().stream()
                .map(this::row)
                .collect(Collectors.joining("\n"));
    }

    public String csv() {
        return "DATE,VALUE" + System.lineSeparator() +
                values.entrySet().stream()
                        .map(x -> x.getKey() + "," + x.getValue())
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
