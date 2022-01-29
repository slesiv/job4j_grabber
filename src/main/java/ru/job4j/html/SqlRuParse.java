package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse {
    public static List<String> startParse(int countPage) throws IOException {
        List<String> dateAndTime = new ArrayList<>();

        for (int page = 1; page <= countPage; page++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + page).get();
            Elements row = doc.select(".postslisttopic");
            for (Element tr : row) {
                Element date = tr.parent().child(5);
                System.out.print(date.text());
                System.out.print("   " + tr.child(0).text());
                System.out.println("   " + tr.child(0).attr("href"));
                dateAndTime.add(date.text());
            }
        }
        return dateAndTime;
    }

    public static void main(String[] args) throws IOException {
        SqlRuParse.startParse(5);
    }
}