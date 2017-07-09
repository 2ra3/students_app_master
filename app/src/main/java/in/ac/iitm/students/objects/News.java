package in.ac.iitm.students.objects;

/**
 * Created by arunp on 20-Feb-16.
 */
public class News {

    int id;
    String title;
    String summary;
    String content;
    String imageerl;
    Long date;

    public News(int id, String title, String summary, String content, String imageerl, Long date) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.imageerl = imageerl;
        this.date = date;
    }

    public String getImageerl() {
        return imageerl;
    }

    public void setImageerl(String imageerl) {
        this.imageerl = imageerl;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
