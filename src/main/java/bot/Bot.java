package bot;

import bot.events.EventParser;
import bot.phrases.PhraseDbOperation;
import bot.phrases.PhraseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс бота.
 *
 * @author Aleksandr Nevsky
 */
public class Bot extends TelegramLongPollingBot {

    Logger logger = LoggerFactory.getLogger(Bot.class);

    private EventParser eventParser;
    private PhraseParser phraseParser;
    private PhraseDbOperation phraseDbOperation;
    private String botUsername;
    private String botToken;

    Bot(DefaultBotOptions botOptions, JdbcTemplate jdbcTemplate, String botUsername, String botToken) {
        super(botOptions);
        this.eventParser = new EventParser(jdbcTemplate);
        this.phraseParser = new PhraseParser(jdbcTemplate);
        this.phraseDbOperation = new PhraseDbOperation(jdbcTemplate);
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        Integer updateId = update.getUpdateId();
        logger.info("updateId + " + updateId);

        Message message = update.getMessage();
        String receivedMessage;
        if (message == null) {
            logger.info("message == null");
            return;
        }

        receivedMessage = message.getText();
        if (receivedMessage == null) {
            logger.info("receivedMessage == null");
            return;
        }
        logger.info("receivedMessage = " + receivedMessage);
        receivedMessage = receivedMessage.toLowerCase();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());

        sendMessage.setReplyToMessageId(message.getMessageId());

        try {
            if (receivedMessage.equals("/event")) {
                eventParser.parseEvent(this, message);
                return;
            } else if (receivedMessage.contains("/phrasemanage")) {
                phraseDbOperation.phraseManage(this, message, receivedMessage);
                return;
            } else if (receivedMessage.contains("маэстро")) {
                phraseParser.parseEvent(this, message, receivedMessage);
                return;
            } else if (receivedMessage.contains("sudo")) {
                sendMessage.setText("Okay :(");
            } else if ((receivedMessage.contains("жопа") || receivedMessage.contains("жопу")) && receivedMessage.contains("хуй")) {
                sendMessage.setText("ПОШЁЛ НА ХУЙ!!! ДА Я ТЕБЯ В ЖОПУ ВЫЕБУ!!!");
            } else if (receivedMessage.contains("жопа") || receivedMessage.contains("жопу")) {
                sendMessage.setText("В ЖОПУ БЛЕАТЬ!");
            } else if (receivedMessage.contains("мебель")) {
                sendMessage.setText("МЕБЕЛЬ БЛЕАТЬ!");
            } else if (receivedMessage.contains("добро") && receivedMessage.contains("утр")) {
                sendMessage.setText("Доброе утро!");
            } else if (receivedMessage.contains("хуй")) {
                sendMessage.setText("ПОШЁЛ НА ХУЙ!!!");
            } else {
                phraseDbOperation.insertPhrase(receivedMessage, receivedMessage);
                return;
            }

            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Bot received message error.", e);
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return botToken;
    }
}
