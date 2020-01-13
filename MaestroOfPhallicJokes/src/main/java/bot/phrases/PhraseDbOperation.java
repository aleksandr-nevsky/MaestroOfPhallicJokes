package bot.phrases;

import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;

/**
 * Работа с фразами.
 */
public class PhraseDbOperation {
    private final JdbcTemplate jdbcTemplate;
    private Logger logger = LoggerFactory.getLogger(PhraseDbOperation.class);

    public PhraseDbOperation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Добавление, удаление фраз.
     *
     * @param bot         Бот.
     * @param message     объект сообщения.
     * @param messageText текст сообщения.
     */
    public void phraseManage(Bot bot, Message message, String messageText) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());

            messageText = messageText.replace("/phrasemanage", "");
            List<String> items = Arrays.asList(messageText.split("\\s*,\\s*"));
            if (items.size() == 3 && "add".equals(items.get(0).trim())) {
                sendMessage.setText("add " + insertPhrase(items.get(1), items.get(2)));

            } else if (items.size() == 2 && "delete".equals(items.get(0).trim())) {
                sendMessage.setText("delete " + deletePhrase(items.get(1)));
            } else {
                sendMessage.setText("Нихуя не понял.");
            }

            bot.execute(sendMessage);
        } catch (Exception ex) {
            logger.error("phraseManage error.", ex);
        }
    }

    /**
     * Добавление фразы в БД.
     *
     * @param request фраза.
     * @param answer  ответ на фразу.
     * @return результат INSERT.
     */
    public int insertPhrase(String request, String answer) {
        return jdbcTemplate.update("INSERT INTO phrases (request, answer)\n" +
                "VALUES (?, ?);", request, answer);

    }

    /**
     * Удаление фразы.
     *
     * @param request фраза.
     * @return результат DELETE.
     */
    private int deletePhrase(String request) {
        return jdbcTemplate.update("DELETE\n" +
                "FROM phrases\n" +
                "WHERE request = ?;", request);

    }


}
