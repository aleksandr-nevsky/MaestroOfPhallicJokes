package bot.events;

/**
 * Класс данных для БД.
 *
 * @author Aleksandr Nevsky
 */
public class Events {
    private int id;
    private int month;
    private int day;
    private String event_name;

    public Events(int id, int month, int day, String event_name) {
        this.id = id;
        this.month = month;
        this.day = day;
        this.event_name = event_name;
    }

    public String toString() {
        return String.format(
                "Event[id=%d, month='%s', day='%s', text='%s']",
                id, month, day, event_name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }
}
