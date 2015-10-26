package edu.njit.fall15.team1.cs673messenger.models;

import java.util.Date;

/**
 * Created by jack on 10/25/15.
 */
public class MessageModel {
    private Friend friend;
    private MessageType type;
    private Date time;
    private String message;

    public enum MessageType{
        From,
        To
    }

    public MessageModel(MessageType type, Friend friend, Date time, String message) {
        this.friend = friend;
        this.type = type;
        this.time = time;
        this.message = message;
    }

    public Friend getFriend() {
        return friend;
    }

    public MessageType getType() {
        return type;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
