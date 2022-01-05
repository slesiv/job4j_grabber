package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    public static void main(String[] args) {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(getSecInterval())
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

    private static int getSecInterval() {
        Properties propRabbit = loadProp("./src/main/resources/rabbit.properties");
        return Integer.parseInt(propRabbit.getProperty("rabbit.interval"));
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

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(currentTimeMillis());
            Properties properties = loadProp("./src/main/resources/app.properties");
            try {
                Class.forName(properties.getProperty("postgres.driver"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try (Connection cn = DriverManager.getConnection(
                    properties.getProperty("postgres.url"),
                    properties.getProperty("postgres.user"),
                    properties.getProperty("postgres.password")
            )) {
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO rabbit(created_date) VALUES (?)")) {
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.executeUpdate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}