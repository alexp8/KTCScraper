package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class ParseDateUtil {

    public static String parseDate(String dateString) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM. dd, yyyy", Locale.ENGLISH);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date = LocalDate.parse(dateString, inputFormatter);

            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(String.format("Unable to parse date: %s%n", dateString), e);
        }
    }

    public static LocalDate toDate(String dateString) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

        try {
            return LocalDate.parse(dateString, inputFormatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(String.format("Unable to parse date: %s%n", dateString), e);
        }
    }
}
