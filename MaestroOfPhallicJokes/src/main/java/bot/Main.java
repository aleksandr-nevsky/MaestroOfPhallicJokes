package bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

/**
 * https://javarush.ru/groups/posts/504-sozdanie-telegram-bota-na-java-ot-idei-do-deploja
 * https://www.surgebook.com/open/book/pishem-telegram-bota-na-java
 * https://habr.com/ru/post/432548/
 */

@SpringBootApplication
public class Main implements CommandLineRunner {
// https://habr.com/en/post/436994/
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Объявляется само через конструктор.
     */
    private final JdbcTemplate jdbcTemplate;


    public Main(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * Запускаем:
     * @param args java -jar Maestro.jar 127.0.0.1 9050 "BotName" "BotToken".
     */
    @Override
    public void run(String... args) {
        if(args.length != 4) {
            throw new RuntimeException("Incorrect run. Use \njava -jar Maestro.jar 127.0.0.1 9050 \"BotName\" \"BotToken\"");
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegram = new TelegramBotsApi();

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        botOptions.setProxyHost(args[0]);
        botOptions.setProxyPort(Integer.parseInt(args[1]));
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);


        Bot bot = new Bot(botOptions, jdbcTemplate, args[2], args[3]);

        try {
            telegram.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            logger.error("Main error.", e);
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
