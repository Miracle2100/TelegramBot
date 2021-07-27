import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.toIntExact;


public class CurrencyChHisBot extends TelegramLongPollingBot {


    @Override
    public String getBotUsername() {
        return "CurrencyChHis_bot";
    }

    @Override
    public String getBotToken() {
        return "1652593990:AAGR2qT4DJO5NLfcxxLnwFFVcraTf2-PybA";
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.enableHtml(true);
        sendMessage.setText(text);

        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowsList = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("EUR->KZT"));
        keyboardSecondRow.add(new KeyboardButton("USD->KZT"));
        keyboardThirdRow.add(new KeyboardButton("RUB->KZT"));

        keyboardRowsList.add(keyboardFirstRow);
        keyboardRowsList.add(keyboardSecondRow);
        keyboardRowsList.add(keyboardThirdRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowsList);

    }


    public String getString(String cur) throws IOException {
        String currentPath = System.getProperty("user.dir");
        String content = Files.lines(Paths.get(currentPath + "/src/main/currency/" + cur), StandardCharsets.UTF_8).collect(Collectors.joining(System.lineSeparator()));
        System.out.println(content);
        return content;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Press the buttons below");
                    break;
                case "USD->KZT":
                    try {
                        sendMsg(message, getString("USD.txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "EUR->KZT":
                    try {
                        sendMsg(message, getString("EUR.txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "RUB->KZT":
                    try {
                        sendMsg(message, getString("RUB.txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
