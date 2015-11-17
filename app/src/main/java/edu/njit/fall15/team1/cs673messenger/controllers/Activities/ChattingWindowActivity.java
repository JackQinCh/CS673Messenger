package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.ChattingAdapter;
import edu.njit.fall15.team1.cs673messenger.models.ChatMessage;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Friends;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingWindowActivity extends Activity implements RecentChatsListener{
    private ListView chatHistoryLv;
    private EditText textEditor;
    private ChattingAdapter chatHistoryAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingwindow);

        Intent intent = getIntent();
        String friendUser = intent.getStringExtra("FriendUser");

        friend = findFriend(friendUser);
        RecentChatsManager.getInstance().addListener(this);

        Log.d(getLocalClassName(), "Chatting with " + friend.getProfileName());

        chatHistoryLv = (ListView) findViewById(R.id.chatting_history_lv);
        setAdapterForThis();

        textEditor = (EditText) findViewById(R.id.text_editor);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecentChatsManager.getInstance().removeListener(this);
    }

    /**
     * Find friend by his userID.
     * @param user String
     * @return Friend
     */
    private Friend findFriend(String user){
        Friends friends = new Friends();
        LinkedList<Friend> friendLinkedList = friends.getFriends();

        if(friendLinkedList.size() != 0){
            for(Friend f:friendLinkedList){
                if (f.getUser().equals(user)){
                    return f;
                }
            }
        }

        return null;
    }

    // Set up adapter
    private void setAdapterForThis() {
        initMessages();
        chatHistoryAdapter = new ChattingAdapter(this, messages);
        chatHistoryLv.setAdapter(chatHistoryAdapter);
    }

    // Initial message list data.
    private void initMessages() {
        messages.clear();
        messages.addAll(getData());
    }

    public void sendMessage(View v){
        //Send message.
        String messageTest = textEditor.getText().toString();
        Message message = new Message(Message.MessageType.To,
                friend, new Date(), messageTest);
        RecentChatsManager.getInstance().sendMessage(message);
        //Display message.
        initMessages();
        chatHistoryAdapter.notifyDataSetChanged();
        //Clear input text view.
        textEditor.setText("");

    }

    /**
     * Received a message
     * @param messageModel
     */
    @Override
    public void receivedMessage(Message messageModel) {
        Friend friend = messageModel.getFriend();
        String message = messageModel.getMessage();
        if (friend.equals(this.friend)){
            Log.d(getLocalClassName(), "Chatting window received a message from " + friend.getProfileName() + ":" + message);
            initMessages();
            chatHistoryAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Get data for listView
     * @return
     */
    private List<ChatMessage> getData(){
        List<ChatMessage> list = new LinkedList<>();

        Messages messages = RecentChatsManager.getInstance().getMessages(friend);

        if (messages.getMessages().size() != 0){
            for(Message model: messages.getMessages()){
                list.add(new ChatMessage(model.getType().value(), model.getMessage()));
            }
        }
        return list;
    }

}
