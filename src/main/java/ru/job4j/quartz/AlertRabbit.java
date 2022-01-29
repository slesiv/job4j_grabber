package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    public static void main(String[] args) {
        Properties propRabbit = loadProp("./src/main/resources/rabbit.properties");
        try (Connection con = getConnection()) {
            List<Long> store = new ArrayList<>();

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", con);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(propRabbit.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static Properties loadProp(String str) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(str)) {
            properties.load(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = loadProp("./src/main/resources/app.properties");
        try {
            Class.forName(properties.getProperty("postgres.driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(
                properties.getProperty("postgres.url"),
                properties.getProperty("postgres.user"),
                properties.getProperty("postgres.password"));
        return connection;
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connect");
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO rabbit(created_date) VALUES (?)")) {
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}