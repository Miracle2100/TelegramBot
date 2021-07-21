import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
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
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;


public class CurrencyChHisBot extends TelegramLongPollingBot{
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
        }
        catch (TelegramApiException e) {
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

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Press the buttons below");
                    break;
                case "USD->KZT":
                    try {
                        sendMsg(message, f("USD"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "EUR->KZT":
                    try {
                        sendMsg(message, f("EUR"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "RUB->KZT":
                    try {
                        sendMsg(message, f("RUB"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public String f(String button) throws IOException {
        String result = "";
        Document doc = null;

        try {
            if(button == "USD") {
                doc = Jsoup.connect("https://ru.exchange-rates.org/history/KZT/USD/T/").get();
            }
            else if (button == "RUB") {
                doc = Jsoup.connect("https://ru.exchange-rates.org/history/KZT/RUB/T/").get();
            }
            else {
                doc = Jsoup.connect("https://ru.exchange-rates.org/history/KZT/EUR/T/").get();
            }

            int day = 0;
            for (Element table : doc.select("table[class=table table-striped table-hover table-hover-solid-row table-simple history-data]")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.isEmpty()) { // Header <tr> with only <th>s
                        continue;
                    }

                    System.out.println(tds.get(0).text() + "->" + tds.get(1).text() +  "->" + tds.get(2).text() +  "->" + tds.get(3).text());
                    result += "<b>" + tds.get(0).text() + "</b>" + " - " + tds.get(2).text() + '\n' + tds.get(1).text() + '\n' +'\n';
                    day++;
                    if(day == 10) {
                        return result;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
