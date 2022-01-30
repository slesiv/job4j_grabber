package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final SqlRuDateTimeParser dateTimeParser;

    public SqlRuParse(SqlRuDateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> listPosts = new ArrayList<>();
        for (int page = 1; page <= 5; page++) {
            Document doc = null;
            try {
                doc = Jsoup.connect(link + page).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements row = doc.select(".postslisttopic");

            for (Element tr : row) {
                if (!tr.text().toLowerCase().contains("javascript")
                        && tr.text().toLowerCase().contains("java")) {
                    listPosts.add(detail(tr.child(0).attr("href")));
                }
            }
        }
        return listPosts;
    }

    @Override
    public Post detail(String link) {
        Post post = null;
        if (!link.isEmpty()) {
            Document doc = null;
            try {
                doc = Jsoup.connect(link).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements postElem = doc.select(".msgTable");
            for (Element el : postElem) {
                String title = el.select(".messageHeader").text();
                String description = el.select(".msgBody").text();
                String dateStr = el.select(".msgFooter").text();
                LocalDateTime created = dateTimeParser.parse(
                        dateStr.substring(0, dateStr.indexOf("[")).strip());
                post = new Post(title, link, description, created);
                break;
            }
        }
        return post;
    }

    public static void main(String[] args) {
        Parse parse = new SqlRuParse(new SqlRuDateTimeParser());
        System.out.println(parse.list("https://www.sql.ru/forum/job-offers/"));
    }
}