package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.ChattingAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Message;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingWindowActivity extends Activity implements RecentChatsListener{
    private ListView chatHistoryLv;
    private EditText textEditor;
    private ChattingAdapter chatHistoryAdapter;
    private ArrayList<Message> messageLines = new ArrayList<>();
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
        messageLines.clear();
        messageLines.addAll(getData());
        chatHistoryAdapter = new ChattingAdapter(this, messageLines);
        chatHistoryLv.setAdapter(chatHistoryAdapter);

        textEditor = (EditText) findViewById(R.id.text_editor);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecentChatsManager.INSTANCE.removeListener(this);
    }

    // Initial message list data.
    private void updateMessages() {

        if (chatHistoryAdapter != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                messageLines.clear();
                messageLines.addAll(getData());
                chatHistoryAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Send message button action.
     * @param v
     */
    public void sendMessage(View v){
        //Send message.
        String messageText = textEditor.getText().toString();
        Friend friend = FriendsManager.checkFriend(chatId);
        if (friend == null)
            return;// Need to implement...
        Message message = Message.createPersonalMessage(Message.DIRECTION_TO, friend, messageText);
        RecentChatsManager.INSTANCE.addMessage(message);
        //Clear input text view.
        textEditor.setText("");
    }

    @Override
    public void updateGUI() {
        updateMessages();
    }

    /**
     * Received a message
     * @param message
     */
    @Override
    public void receivedMessage(Message message) {
        Log.d(getClass().getSimpleName(), "receivedMessage");
        Friend friend = message.getFriend().get(0);
        if (chatId.equals(friend.getUser())){
            Log.d(getClass().getSimpleName(), "Received a message from " + friend.getProfileName() + ":" + message);
            updateMessages();
        }
    }

    /**
     * Get data for listView
     * @return
     */
    private List<Message> getData(){
        return RecentChatsManager.INSTANCE.getMessages(chatId).getMessages();
    }

}
