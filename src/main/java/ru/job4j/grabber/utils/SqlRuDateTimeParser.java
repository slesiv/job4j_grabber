package ru.job4j.grabber.utils;

import ru.job4j.html.SqlRuParse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SqlRuDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime ldt;
        String[] dateTime = parse.split(",");
        if (dateTime.length == 2 && !dateTime[0].isEmpty() && !dateTime[1].isEmpty()) {
            ldt = LocalDateTime.of(parseDate(dateTime[0]), parseTime(dateTime[1].trim()));
        } else {
            throw new IllegalArgumentException("Получен неизвестный формат даты или времени \"dd MMM yy, HH:mm\" -> " + parse);
        }
        return ldt;
    }

    private static LocalDate parseDate(String date) {
        String[] dayMonthYear = date.split(" ");
        LocalDate correctDate = null;
        try {
            if (dayMonthYear.length == 1) {
                if ("вчера".equals(date)) {
                    correctDate = LocalDate.now().minusDays(1);
                } else if ("сегодня".equals(date)) {
                    correctDate = LocalDate.now();
                }
            } else if (dayMonthYear.length == 3) {
                int month = getIntMonth(dayMonthYear[1]);
                if (month > 0 && month <= 12) {
                    correctDate = LocalDate.of(
                            Integer.parseInt("20" + dayMonthYear[2]),
                            month,
                            Integer.parseInt(dayMonthYear[0]));
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Получен неизвестный формат даты \"dd MMM yy\" -> " + date);
        }
        return correctDate;
    }

    private static LocalTime parseTime(String time) {
        LocalTime correctTime;
        try {
            correctTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Получен неизвестный формат времени \"HH:mm\" -> " + time);
        }
        return correctTime;
    }

    private static int getIntMonth(String month) {
        String[] ruMonths = {"январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"};
        int intMonth = 0;
        int i = 1;
        for (String m : ruMonths) {
            if (m.contains(month.trim().toLowerCase().substring(0, 2))) {
                intMonth = i;
            }
            i++;
        }
        return intMonth;
    }

    public static void main(String[] args) throws IOException {
        DateTimeParser parser = new SqlRuDateTimeParser();
        for (String dt : SqlRuParse.startParse()) {
            System.out.println(parser.parse(dt));
        }
    }
}