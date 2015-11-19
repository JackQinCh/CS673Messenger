package edu.njit.fall15.team1.cs673messenger.models;

import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;

import java.util.Date;

/**
 * Created by jack on 10/25/15.
 */
public final class Message {
    private final Friend friend;
    private final int direction;
    private final Date time;
    private final String message;
    private final int command;
    private final int type;
    private final String extra;
    private final String packetString;

    public final static int DIRECTION_FROM = 0;
    public final static int DIRECTION_TO   = 1;

    public final static int TYPE_CHAT  = 0;
    public final static int TYPE_GROUP = 1;

    public final static int COMMAND_NONE = 0;
    public final static int COMMAND_CREATE_GROUP = 1;
    public final static int COMMAND_GROUP_CHAT = 2;
    public final static int COMMAND_GROUP_TASK = 3;
    public final static int COMMAND_SHARE_LOCATION = 4;

    public final static String HEADER_TYPE = "<TYPE:";
    public final static String HEADER_COMMAND = "<COMMAND:";
    public final static String HEADER_MEMBERS = "<MEMBERS:";
    public final static String HEADER_END = ">";

    private Message(int type, int command, int direction, Friend friend, Date time, String message, String extra) {
        this.type = type;
        this.command = command;
        this.direction = direction;
        this.friend = friend;
        this.time = time;
        this.message = message;
        this.extra = extra;

        if (type == TYPE_CHAT)
            packetString = message;
        else if (type == TYPE_GROUP){
            packetString = HEADER_TYPE + type + HEADER_END +
                    HEADER_COMMAND + command + HEADER_END +
                    extra + message;
        }else packetString = "";
    }

    /**
     * Static Generation Factory
     * @param direction
     * @param s
     * @param userId
     * @return
     */
    public static Message createMessageWithParseAndUserId(int direction, String s, String userId){
        Friend f = FriendsManager.checkFriend(userId);
        if (f == null)
            return null;

        Date time = new Date();
        String message = "";
        int command = COMMAND_NONE;
        int type = TYPE_GROUP;
        String extra = "";
        //Type
        if (s.startsWith(HEADER_TYPE+TYPE_GROUP+HEADER_END)){//Type: group chat
            type = TYPE_GROUP;
            s.replace(HEADER_TYPE+TYPE_GROUP+HEADER_END,"");
        }else{//Type: chat
            return new Message(TYPE_CHAT, COMMAND_NONE, direction, f, time, s, "");
        }

        if (s.startsWith(HEADER_COMMAND)){
            String commandStr = s.substring(0, 10);
            command = Integer.getInteger( commandStr.substring(9, 10));
        }

        //To be continue...
        return new Message(type, command, direction, f, time, message, extra);
    }

    public static Message createMessage(int type, int command, int direction, Friend friend, Date time, String message, String extra){
        return new Message(type, command, direction, friend, time, message, extra);
    }


    public Friend getFriend() {
        return friend;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public int getDirection() {
        return direction;
    }

    public int getCommand() {
        return command;
    }

    public int getType() {
        return type;
    }

    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        String messageString;
        String commandStr, directionStr, typeStr;
        switch (type){
            case TYPE_CHAT:
                typeStr = "Type: Chat";
                break;
            case TYPE_GROUP:
                typeStr = "Type: Group chat";
                break;
            default:
                typeStr = "null";
        }

        switch (command){
            case COMMAND_CREATE_GROUP:
                commandStr = "Command: Create group";
                break;
            case COMMAND_GROUP_CHAT:
                commandStr = "Command: Normal group chat";
                break;
            case COMMAND_GROUP_TASK:
                commandStr = "Command: Create group task";
                break;
            case COMMAND_SHARE_LOCATION:
                commandStr = "Command: Share location";
                break;
            default:
                commandStr = "null";
        }

        switch (direction){
            case DIRECTION_FROM:
                directionStr = "Direction: From";
                break;
            case DIRECTION_TO:
                directionStr = "Direction: To";
                break;
            default:
                directionStr = "null";
        }
        String withWho = "With: " + friend.getProfileName();
        String timeStr = "Time: " + time.toString();
        String messageStr = "Content: " + message;
        messageString =
                typeStr + ", " +
                commandStr + ", " +
                directionStr + ", " +
                withWho + ", " +
                timeStr + ", " +
                messageStr;
        return messageString;
    }
}
