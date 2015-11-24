package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.ChattingAdapter;
import edu.njit.fall15.team1.cs673messenger.models.ChatMessage;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingWindowActivity extends Activity implements RecentChatsListener{
    private ListView chatHistoryLv;
    private EditText textEditor;
    private ChattingAdapter chatHistoryAdapter;
    private List<ChatMessage> messageLines = new ArrayList<>();
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingwindow);

        Intent intent = getIntent();
        chatId = intent.getStringExtra(getString(R.string.chat_id));

        RecentChatsManager.INSTANCE.addListener(this);

        Log.d(getLocalClassName(), "Chat:" + chatId);

        chatHistoryLv = (ListView) findViewById(R.id.chatting_history_lv);
        updateMessages();

        textEditor = (EditText) findViewById(R.id.text_editor);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecentChatsManager.INSTANCE.removeListener(this);
    }

    // Initial message list data.
    private void updateMessages() {
        messageLines.clear();
        messageLines.addAll(getData());
        if (chatHistoryAdapter == null){
            chatHistoryAdapter = new ChattingAdapter(this, messageLines);
            chatHistoryLv.setAdapter(chatHistoryAdapter);
        }
        chatHistoryAdapter.notifyDataSetChanged();
    }

    public void sendMessage(View v){
        //Send message.
        String messageText = textEditor.getText().toString();
        Friend friend = FriendsManager.checkFriend(chatId);
        if (friend == null)
            return;// Need to implement...
        Message message = Message.createPersonalMessage(Message.DIRECTION_TO, friend, messageText);

        int type = Messages.PERSONAL_CHAT;
        RecentChatsManager.INSTANCE.addMessage(message);
        //Display message.
        updateMessages();
        //Clear input text view.
        textEditor.setText("");
    }

    /**
     * Received a message
     * @param message
     */
    @Override
    public void receivedMessage(Message message) {
        Friend friend = message.getFriend().get(0);
        if (chatId == friend.getUser()){
            Log.d(getLocalClassName(), "Chatting window received a message from " + friend.getProfileName() + ":" + message);
            updateMessages();
        }
    }

    /**
     * Get data for listView
     * @return
     */
    private List<ChatMessage> getData(){
        List<ChatMessage> list = new LinkedList<>();

        int type = Messages.PERSONAL_CHAT;
        Messages messages = RecentChatsManager.INSTANCE.getMessages(type, chatId);

        if (messages.getMessages().size() != 0){
            for(Message message: messages.getMessages()){
                list.add(new ChatMessage(message.getDirection(), message.getMessage()));
            }
        }
        return list;
    }

}
