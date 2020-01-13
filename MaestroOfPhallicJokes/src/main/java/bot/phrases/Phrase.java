package bot.phrases;

/**
 * Класс данных для БД.
 *
 * @author Aleksandr Nevsky
 */
public class Phrase {
    private int id;
    private String request;
    private String answer;

    public Phrase(int id, String request, String answer) {
        this.id = id;
        this.request = request;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "id=" + id +
                ", request='" + request + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
