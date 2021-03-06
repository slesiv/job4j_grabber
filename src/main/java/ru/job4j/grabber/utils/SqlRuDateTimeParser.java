package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {

    public static final DateTimeFormatter FORMATTER_HHMM = DateTimeFormatter.ofPattern("HH:mm");
    public static final String TODAY = "сегодня";
    public static final String YESTERDAY = "вчера";

    private static final Map<String, Integer> MONTHS = Map.ofEntries(
            Map.entry("янв", 1),
            Map.entry("фев", 2),
            Map.entry("мар", 3),
            Map.entry("апр", 4),
            Map.entry("май", 5),
            Map.entry("июн", 6),
            Map.entry("июл", 7),
            Map.entry("авг", 8),
            Map.entry("сен", 9),
            Map.entry("окт", 10),
            Map.entry("ноя", 11),
            Map.entry("дек", 12)
    );

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime ldt;
        String[] dateTime = parse.split(",");
        if (!dateTime[0].isEmpty() && !dateTime[1].isEmpty()) {
            LocalDate correctDate;
            if (TODAY.equals(dateTime[0])) {
                correctDate = LocalDate.now();
            } else if (YESTERDAY.equals(dateTime[0])) {
                correctDate = LocalDate.now().minusDays(1);
            } else {
                String[] dayMonthYear = dateTime[0].split(" ");
                correctDate = LocalDate.of(Integer.parseInt(
                        "20" + dayMonthYear[2]),
                        MONTHS.get(dayMonthYear[1]),
                        Integer.parseInt(dayMonthYear[0]));
            }
            LocalTime correctTime = LocalTime.parse(dateTime[1].trim(), FORMATTER_HHMM);
            ldt = LocalDateTime.of(correctDate, correctTime);
        } else {
            throw new IllegalArgumentException(
                    "Получен неизвестный формат даты или времени \"dd MMM yy, HH:mm\" -> " + parse);
        }
        return ldt;
    }
}