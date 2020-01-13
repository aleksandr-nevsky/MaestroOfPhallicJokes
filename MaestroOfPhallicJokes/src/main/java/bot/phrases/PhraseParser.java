package bot.phrases;

import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PhraseParser {

    private Logger logger = LoggerFactory.getLogger(PhraseParser.class);

    private final JdbcTemplate jdbcTemplate;

    public PhraseParser(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void parseEvent(Bot bot, Message message, String messageText) {
        try {

            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(message.getChatId());

            String botAnswer = getPhrase(messageText.replace("маэстро", "").trim());
            if (!botAnswer.isEmpty()) {
                sendMessage.setText(botAnswer);
                bot.execute(sendMessage);
            }

        } catch (Exception ex) {
            logger.error("parseEvent error.", ex);
        }
    }

    private String getPhrase(String message) {
        List<String> answerList = new ArrayList<>();

        jdbcTemplate.query(
                "SELECT id,\n" +
                        "       request,\n" +
                        "       ts_rank(to_tsvector(request), plain) AS ts_rank,\n" +
                        "       answer\n" +
                        "FROM phrases,\n" +
                        "     plainto_tsquery(?) AS plain\n" +
                        "WHERE to_tsvector(request) @@ plain\n" +
                        "ORDER BY ts_rank DESC\n" +
                        "LIMIT 10;", new Object[]{message},
                (rs, rowNum) -> new Phrase(rs.getInt("id"), rs.getString("request"), rs.getString("answer"))
        ).forEach(phrase -> answerList.add(phrase.getAnswer()));

        if (answerList.size() > 0) {
            return answerList.get(ThreadLocalRandom.current().nextInt(0, answerList.size()));
        } else {
            return "";
        }
    }
}
