package bot.events;

import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.StringJoiner;

/**
 * Обработка событий по команде бота /event
 *
 * @author Aleksandr Nevsky
 */
public class EventParser {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(EventParser.class);

    public EventParser(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void parseEvent(Bot bot, Message message) {
        String welcomeMessage = "Всем добра, достопочтенные джентельмены. \nСегодняшние праздники:\n";
        try {

            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(message.getChatId());

            sendMessage.setText(welcomeMessage + getCurrentDayEvents());
            bot.execute(sendMessage);
        } catch (Exception ex) {
            logger.error("parseEvent error.", ex);
        }
    }

    /**
     * Получаем все праздники за сегодня.
     *
     * @return все праздники за сегодня.
     */
    private String getCurrentDayEvents() {
        int nowDay = LocalDate.now().getDayOfMonth();
        int nowMonth = LocalDate.now().getMonth().getValue();
        StringJoiner eventsJoiner = new StringJoiner("\n");
        jdbcTemplate.query(
                "SELECT id, month, day, event_name FROM events WHERE month = ? AND day = ?;", new Object[]{nowMonth, nowDay},
                (rs, rowNum) -> new Events(rs.getInt("id"), rs.getInt("month"), rs.getInt("day"), rs.getString("event_name"))
        ).forEach(event -> eventsJoiner.add(event.getEvent_name()));
        return eventsJoiner.toString();
    }

}
