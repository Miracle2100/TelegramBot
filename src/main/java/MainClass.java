import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;


@EnableAsync
public class MainClass {


    @Scheduled(cron = "0 1 1 * * ?")
    public void scheduleTaskUsingCronExpression() {

        long now = System.currentTimeMillis() / 1000;
        System.out.println(
                "schedule tasks using cron jobs - " + now);
    }

    static final String DB_URL = "jdbc:mysql://localhost:3306/spring-web-blog?autoReconnect=true&useSSL=false";
    static final String USER = "root";
    static final String PASS = "";

    public static String check(String cur) {

        String result = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ) {

            ResultSet rs1 = stmt.executeQuery("SELECT * FROM " + cur);
            rs1.absolute(1);
            String costNow = rs1.getString("cost");

            ResultSet rs2 = stmt.executeQuery("SELECT * FROM " + cur);
            rs2.absolute(2);

            String datePre = rs2.getString("date");
            String costPre = rs2.getString("cost");
            costNow.replace(',', '.');
            costPre.replace(',', '.');

            Scanner scan = new Scanner(costNow);
            Scanner scan1 = new Scanner(costPre);

            double c1 = scan.nextDouble();
            double c2 = scan1.nextDouble();

            if (c1 > c2 - c2 * 0.1 || c1 < c2 + c2 * 0.1) {

                result += "Курс " + cur + " за " + datePre + " - " + costPre + '\n' + "Текущий курс - " + costNow + '\n';
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public static void get(String cur) throws IOException {

        String result = "";
        Document doc = null;
        String currentPath = System.getProperty("user.dir");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
        ) {
            if (cur == "USD") {
                ResultSet rs = stmt.executeQuery("SELECT date, cost, day_name FROM USD");

                while (rs.next()) {

                    result += "<b>" + rs.getString("date") + "</b>" + " - " + rs.getString("cost") + '\n' + rs.getString("day_name") + '\n' + '\n';
                }
            } else if (cur == "RUB") {
                ResultSet rs = stmt.executeQuery("SELECT date, cost, day_name FROM RUB");

                while (rs.next()) {

                    result += "<b>" + rs.getString("date") + "</b>" + " - " + rs.getString("cost") + '\n' + rs.getString("day_name") + '\n' + '\n';
                }
            } else if (cur == "EUR") {
                ResultSet rs = stmt.executeQuery("SELECT date, cost, day_name FROM EUR");

                while (rs.next()) {

                    result += "<b>" + rs.getString("date") + "</b>" + " - " + rs.getString("cost") + '\n' + rs.getString("day_name") + '\n' + '\n';
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        FileWriter fileWriter = new FileWriter(currentPath + "/src/main/currency/" + cur + ".txt");
        fileWriter.write(result);
        fileWriter.close();
    }


//    @Scheduled(cron = "0 0 14 * * *")
//    public void customScheduler() {
//        try {
//            // do what ever you want to run repeatedly
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }

    public static void main(String[] args) throws IOException {
        get("EUR");
        get("USD");
        get("RUB");
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new CurrencyChHisBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        String result = "";
        result += check("USD") + check("EUR") + check("RUB");
        if (!result.isEmpty()) {
            result = "Изменения курса более чем на 10%" + result;
            String currentPath = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(currentPath + "/src/main/currency/change10.txt");
            fileWriter.close();

        }


    }
}
