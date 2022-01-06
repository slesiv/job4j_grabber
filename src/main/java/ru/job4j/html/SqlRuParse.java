package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse {
    public static List<String> startParse() throws IOException {
        List<String> dateAndTime = new ArrayList<>();
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        for (Element tr : row) {
            Element date = tr.parent().child(5);
            dateAndTime.add(date.text());
        }
        return dateAndTime;
    }
}