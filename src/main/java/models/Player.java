package models;

import lombok.Builder;
import lombok.Getter;
import util.ParseDateUtil;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
public class Player {

    /* Sort latest descending */
    public static final Comparator<String> COMPARATOR = Comparator.reverseOrder();

    private final String name;
    private final int age;
    private final int id;

    // key = 'yyyy-MM', value=KTC_value
    private final TreeMap<String, String> values;

    public String prettyPrint() {
        return values.entrySet().stream()
                .map(this::row)
                .collect(Collectors.joining("\n"));
    }

    public String csv(LocalDate minDate) {

        TreeMap<String, String> filteredValues = values.entrySet().stream()
                .filter(x -> minDate == null || ParseDateUtil.toDate(x.getKey()).isAfter(minDate))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, replacement) -> existing,
                                TreeMap::new
                        )
                );

        filteredValues = getOneScorePerWeek(filteredValues);

        return "ID,NAME,DATE,VALUE"
                + System.lineSeparator()
                + getKtcValues(filteredValues);
    }

    private String getKtcValues(TreeMap<String, String> values) {
        return values.entrySet().stream()
                .map(x -> String.format("%d,\"%s\",\"%s\",%s",
                                id, name, x.getKey(), x.getValue()
                        )
                )
                .collect(Collectors.joining("\n"));
    }

    private TreeMap<String, String> getOneScorePerWeek(Map<String, String> ktcValues) {
        return ktcValues.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> {
                            LocalDate date = ParseDateUtil.toDate(entry.getKey());
                            WeekFields weekFields = WeekFields.of(Locale.getDefault());
                            int year = date.get(weekFields.weekBasedYear());
                            int week = date.get(weekFields.weekOfWeekBasedYear());
                            return year + "-W" + week; // Group by year and week
                        }
                ))
                .entrySet().stream()
                .map(weekEntry -> weekEntry.getValue().stream()
                        .min(Comparator.comparing(entry -> ParseDateUtil.toDate(entry.getKey()))) // Get the earliest date in each week
                        .orElseThrow()
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        TreeMap::new
                ));
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
