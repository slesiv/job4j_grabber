package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
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

    public static Post postParse(String link) throws IOException {
        SqlRuDateTimeParser dateParser = new SqlRuDateTimeParser();
        Post post = null;
        if (!link.isEmpty() && link != null) {
            Document doc = Jsoup.connect(link).get();
            Elements postElem = doc.select(".msgTable");
            for (Element el : postElem) {
                String title = el.select(".messageHeader").text();
                String description = el.select(".msgBody").text();
                String dateStr = el.select(".msgFooter").text();
                LocalDateTime created = dateParser.parse(
                        dateStr.substring(0, dateStr.indexOf("[")).strip());
                post = new Post(title, link, description, created);
                break;
            }
        }
        return post;
    }

    public static void main(String[] args) throws IOException {
        SqlRuParse.postParse("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
    }
}