package ru.job4j.grabber;

import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection connection;

    public PsqlStore(Properties properties) {
        try {
            Class.forName(properties.getProperty("postgres.driver"));
            connection = DriverManager.getConnection(
                    properties.getProperty("postgres.url"),
                    properties.getProperty("postgres.user"),
                    properties.getProperty("postgres.password")
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO post(name, text, link, created) VALUES(?,?,?,?)")) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM post")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                posts.add(getPostFromResultSet(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM post WHERE id = ?")) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                post = getPostFromResultSet(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    private Post getPostFromResultSet(ResultSet rs) {
        Post post = null;
        try {
             post = new Post(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getTimestamp(5).toLocalDateTime()
             );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return post;
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("./src/main/resources/app.properties")) {
            properties.load(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Store store = new PsqlStore(properties);
        Post post = new Post(
                "Ищем Senior WebGL developer (создание 2D движка) ",
                "Какое нибудь описание",
                "https://www.sql.ru/forum/1341601/ishhem-senior-webgl-developer-sozdanie-2d-dvizhka",
                LocalDateTime.of(2022, 01, 20, 11, 44, 00)
        );

        System.out.println(store.findById(1));
    }
}