package hades.smsuploader;

/**
 * Created by hades on 04/07/2014.
 */
public class SMSEntity {
    private String username;
    private String password;
    private String sender;
    private String content;
    private String datetime;

    public SMSEntity(String username, String password, String sender, String content, String datetime) {
        this.username = username;
        this.password = password;
        this.sender = sender;
        this.content = content;
        this.datetime = datetime;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getDatetime() {
        return datetime;
    }
}
