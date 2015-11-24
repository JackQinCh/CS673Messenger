package edu.njit.fall15.team1.cs673messenger.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jack on 10/25/15.
 */
public final class Message {
    private final String name;

    public String getName() {
        return name;
    }

    private final String chatID;

    public String getChatID() {
        return chatID;
    }

    private final List<Friend> friends;
    private final int direction;
    private final Date time;
    private final String messageContent;
    private final int command;
    private final int type;
    private final String extra;
    private final String packetString;

    public String getPacketString() {
        return packetString;
    }

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
    public final static String HEADER_NAME = "<NAME:";
    public final static String HEADER_ID = "<ID:";
    public final static String HEADER_COMMAND = "<COMMAND:";
    public final static String HEADER_MEMBERS = "<MEMBERS:";
    public final static String HEADER_EXTRA = "<EXTRA:";
    public final static String HEADER_END = ">";

    private Message(String name, String chatID, int type, int command, int direction, List<Friend> friends, Date time, String messageContent, String extra) {
        this.name = name;
        this.chatID = chatID;
        this.type = type;
        this.command = command;
        this.direction = direction;
        this.friends = friends;
        this.time = time;
        this.messageContent = messageContent;
        this.extra = extra;

        //Packet string
        if (type == TYPE_CHAT)
            packetString = messageContent;
        else if (type == TYPE_GROUP){
            String membersStr = "";
            for (Friend f:friends)
                membersStr += f.getUser() + ",";
            packetString = HEADER_TYPE + type + HEADER_END
                    + HEADER_NAME + name + HEADER_END
                    + HEADER_ID + chatID + HEADER_END
                    + HEADER_COMMAND + command + HEADER_END
                    + HEADER_MEMBERS + membersStr + HEADER_END
                    + HEADER_EXTRA + extra + HEADER_END
                    + messageContent;
        }else packetString = "";
    }

    public static Message createPersonalMessage(int direction, Friend friend, String messageContent){
        List<Friend> friends = new ArrayList<>();
        friends.add(friend);
        Message message = new Message(
                friend.getProfileName(),
                friend.getUser(),
                TYPE_CHAT,
                COMMAND_NONE,
                direction,
                friends,
                new Date(),
                messageContent,
                "");
        return message;
    }

    public static Message createGroupMessage(String name,String chatID, int command, int direction, List<Friend> friends, Date time, String messageContent, String extra){
        Message message = new Message(
                name,
                chatID,
                TYPE_GROUP,
                command,
                direction,
                friends,
                time,
                messageContent,
                extra);
        return message;

    }


//    public void addFriend(Friend friend){
//        if (!friends.contains(friend))
//            friends.add(friend);
//    }
//    /**
//     * Static Generation Factory
//     * @param direction
//     * @param s
//     * @param userId
//     * @return
//     */
    public static Message createWithReceivedMessage( String s, Friend friend, Date time ){
        List<Friend> friends = new ArrayList<>();
        friends.add(friend);
        if (friend == null)
            return null;

        String message = "";
        int command = COMMAND_NONE;
        String extra = "";
        //Type
        if (s.startsWith(HEADER_TYPE+TYPE_GROUP+HEADER_END)){//Type: group chat
            s.replace(HEADER_TYPE+TYPE_GROUP+HEADER_END,"");
        }else{//Type: chat
            return new Message(friend.getProfileName(), friend.getUser(), TYPE_CHAT, COMMAND_NONE, Message.DIRECTION_FROM, friends, time, s, "");
        }

//        if (s.startsWith(HEADER_NAME)){
//            String nameStr = s.substring(0, s.indexOf(HEADER_END));
//        }
//
//        if (s.startsWith(HEADER_COMMAND)){
//            String commandStr = s.substring(0, 10);
//            command = Integer.parseInt(commandStr.substring(9, 10));
//            s.replace(HEADER_COMMAND+command+HEADER_END,"");
//        }
//        if (command == Message.COMMAND_CREATE_GROUP){
//
//        }

        //To be continue...
//        return new Message(name, type, command, direction, f, time, message, extra);
        return null;
    }

    public static Message createMessage(String name,String chatID, int type, int command, int direction, List<Friend> friends, Date time, String message, String extra){
        return new Message(name, chatID, type, command, direction, friends, time, message, extra);
    }

    public List<Friend> getFriend() {
        List<Friend> list = new ArrayList<>();
        list.addAll(friends);
        return list;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return messageContent;
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
        String withWho = "With: ";
        for (Friend f:friends){
            withWho += f.getProfileName() + ",";
        }
        String timeStr = "Time: " + time.toString();
        String messageStr = "Content: " + messageContent;
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
