package edu.njit.fall15.team1.cs673messenger.APIs;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * RecentChatsManager is a Singleton, manages recent chat lists.
 * Created by jack on 10/25/15.
 */
public enum  RecentChatsManager implements FacebookServerListener{
    INSTANCE;

    private List<Messages> recentChats = new LinkedList<>();
    private List<RecentChatsListener> listeners = new LinkedList<>();

    RecentChatsManager() {
        FacebookServer.INSTANCE.addListeners(this);
    }

    public List<Messages> getRecentChats() {
        List<Messages> list = new ArrayList<>();
        if (recentChats.size() != 0)
            for (Messages messages:recentChats){
                if (messages.getType() == Messages.PERSONAL_CHAT)
                    list.add(messages);
            }
        return list;
    }

    public List<Messages> getGroupChats(){
        List<Messages> list = new ArrayList<>();
        if (recentChats.size() != 0)
            for (Messages messages:recentChats){
                if (messages.getType() == Messages.GROUP_CHAT)
                    list.add(messages);
            }
        return list;
    }

    public void addListener(RecentChatsListener listener) {
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public void removeListener(RecentChatsListener listener){
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    /**
     * Get Messgaes with chatId.
     * @param chatId
     * @return Messages
     */
    public Messages getMessages(String chatId){
        if (recentChats.size() != 0) {
            for (Messages messages : recentChats) {
                if (messages.checkId(chatId)) {
                    return messages;
                }
            }
        }
        return null;
    }

    /**
     * Add a message into Recent messages list.
     * @param message
     */
    public void addMessage(Message message){
        if (recentChats.size() != 0){//If recent chats is not empty, find the chat and add the message into it.
            for (Messages ms:recentChats){
                if(ms.getChatId().equals(message.getChatID())){
                    ms.addMessage(message);

                    if (message.getDirection() == Message.DIRECTION_TO){
                        FacebookServer.INSTANCE.sendMessage(message);
                        if (listeners.size() != 0) {
                            Log.d(getClass().getSimpleName(), "Notify GUI listeners.");
                            for (RecentChatsListener listener : listeners)
                                listener.updateGUI();
                        }
                    }else {
                        doMessageCommond(message);
                        if (listeners.size() != 0) {
                            Log.d(getClass().getSimpleName(), "Notify GUI listeners.");
                            for (RecentChatsListener listener : listeners)
                                listener.receivedMessage(message);
                        }
                    }
                    Log.i(getClass().getSimpleName(),toString());
                    return;
                }
            }
        }//If recent chats is empty or didn't find the chat, create a chat. When receive a message.
        Log.d(getClass().getSimpleName() + "->addMessage():", "Didn't find the chat: " + message.getChatID());
        Messages messages = null;
        if (message.getType() == Message.TYPE_CHAT){
            messages = Messages.newPersonalMessages(message.getFriend().get(0));
        }else if (message.getType() == Message.TYPE_GROUP){
            messages = Messages.newGroupMessages(message.getName(), message.getChatID(), message.getFriend());
        }
        messages.addMessage(message);
        recentChats.add(messages);

        if (message.getDirection() == Message.DIRECTION_TO){
            FacebookServer.INSTANCE.sendMessage(message);
            if (listeners.size() != 0) {
                Log.d(getClass().getSimpleName(), "Notify GUI listeners.");
                for (RecentChatsListener listener : listeners)
                    listener.updateGUI();
            }
        }else {
            if (listeners.size() != 0) {
                Log.d(getClass().getSimpleName(), "listeners size:"+listeners.size());
                for (RecentChatsListener listener : listeners)
                    listener.receivedMessage(message);
            }
        }

        Log.i(getClass().getSimpleName(),toString());
    }

    private void doMessageCommond(Message message) {
        if (message.getCommand() == Message.COMMAND_CREATE_TASK){
            TaskManager.INSTANCE.addTask(message.getChatID(), message.getExtra());
        }else if (message.getCommand() == Message.COMMAND_REMOVE_TASK){
            TaskManager.INSTANCE.removeTask(message.getChatID(), message.getExtra());
        }
    }

    @Override
    public void facebookReceivedMessage(String from, String messageText, Date time) {
        Log.d(getClass().getSimpleName(), "Received a Message from FacebookServer.");

        Friend friend = FriendsManager.checkFriend(from);
        if (friend != null){
            Message message = Message.createWithReceivedMessage(messageText, friend, time);
            addMessage(message);

        }else{
            Log.e(getClass().getSimpleName(), "Didn't find this friend.");
        }
    }



    @Override
    public String toString() {
        String recentChatsString = "Recent chats list:\n";
        if (recentChats.size() != 0){
            for (Messages messages:recentChats){
                recentChatsString += messages.getChatId()+"\n";
                for (Friend f:messages.getMembers()){
                    recentChatsString += f.getProfileName() + ", ";
                }
                recentChatsString += "\n";
                if (messages.getMessages().size() != 0){
                    for (Message message:messages.getMessages()){
                        recentChatsString += message.toString() + "\n";
                    }
                }
            }
        }
        return recentChatsString;
    }

    @Override
    public void facebookConnected(Boolean isConnected) {

    }

    @Override
    public void facebookLogined(Boolean isLogin) {

    }

    public void addMessages(Messages messages) {
        recentChats.add(messages);
    }
}
