package in.ac.iitm.students.objects;

/**
 * Created by sai_praneeth7777 on 18-Jun-16.
 */
public class ThreadObject {
    private String subject, body, date, time, user, id;
    private String messName, complaintcategory, informed;
    private String solved, solved_by;


    public ThreadObject(String subject, String body, String date, String time, String user, String thread_id, String messName, String solved, String solved_by, String mycomplaintCategory, String informed) {
        this.setSubject(subject);
        this.setBody(body);
        this.setUser(user);
        this.setDate(date);
        this.setTime(time);
        this.setId(thread_id);
        this.setMessName(messName);
        this.setSolved(solved);
        this.setSolved_By(solved_by);
        this.setComplaintcategory(mycomplaintCategory);
        this.setInformed(informed);
    }

    public String getComplaintcategory() {
        return complaintcategory;
    }

    public void setComplaintcategory(String complaintcategory) {
        this.complaintcategory = complaintcategory;
    }

    public String getInformed() {
        return informed;
    }

    public void setInformed(String informed) {
        this.informed = informed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSolved_By(String solved_by) {
        this.solved_by = solved_by;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getSolved_by() {
        return solved_by;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessName() {
        return messName;
    }

    public void setMessName(String messName) {
        this.messName = messName;
    }


}
