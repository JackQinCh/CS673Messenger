package edu.njit.fall15.team1.cs673messenger.APIs;

import java.util.Date;
import java.util.LinkedList;

import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;

/**
 * RecentChatsManager is a Singleton, manages recent chat lists.
 * Created by jack on 10/25/15.
 */
public class RecentChatsManager implements FacebookServerListener{
    private LinkedList<MessageModels> recentChats = new LinkedList<>();
    private LinkedList<RecentChatsListener> listeners = new LinkedList<>();

    public LinkedList<MessageModels> getRecentChats() {
        return recentChats;
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

    @Override
    public void facebookConnected(Boolean isConnected) {

    }

    @Override
    public void facebookLogined(Boolean isLogin) {

    }


    private static class RecentChatsManagerHolder{
        private static RecentChatsManager instance = new RecentChatsManager();
    }

    private RecentChatsManager() {
        FacebookServer.getInstance().addListeners(this);
    }

    public static RecentChatsManager getInstance(){
        return RecentChatsManagerHolder.instance;
    }

    public MessageModels getMessageModels(Friend withWho){
        if (recentChats.size() != 0){
            for(MessageModels models:recentChats){
                if (models.getWithWho().equals(withWho)){
                    return models;
                }
            }
        }
        MessageModels models = new MessageModels(withWho);
        recentChats.add(models);
        return models;
    }

    public void sendMessage(MessageModel messageModel){
        if (recentChats.size() != 0){
            for (MessageModels ms:recentChats){
                if(ms.getWithWho().equals(messageModel.getFriend())){
                    ms.addMessage(messageModel);
                }
            }
        }
    }

    @Override
    public void facebookReceivedMessage(String from, String message, Date time) {
        if (recentChats.size() != 0) {
            for (MessageModels models : recentChats) {
                if (models.getWithWho().getUser().equals(from)) {
                    MessageModel model = new MessageModel(
                            MessageModel.MessageType.From,
                            models.getWithWho(),
                            time,
                            message);
                    models.addMessage(model);
                    if (listeners.size() != 0) {
                        for (RecentChatsListener listener : listeners)
                            listener.receivedMessage(model);
                    }
                }
            }
        }
    }
}
