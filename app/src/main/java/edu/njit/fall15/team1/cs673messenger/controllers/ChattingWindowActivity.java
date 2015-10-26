package edu.njit.fall15.team1.cs673messenger.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.ChatMessage;
import edu.njit.fall15.team1.cs673messenger.models.ChattingAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Friends;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;
import edu.njit.fall15.team1.cs673messenger.models.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.models.RecentChatsManager;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingWindowActivity extends Activity implements RecentChatsListener{
    private ListView chatHistoryLv;
    private EditText textEditor;
    private ChattingAdapter chatHistoryAdapter;
    private List<ChatMessage> messages = new LinkedList<>();
    private Friend friend;
    private MessageModels messageModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingwindow);

        chatHistoryLv = (ListView) findViewById(R.id.chatting_history_lv);
        setAdapterForThis();

        textEditor = (EditText) findViewById(R.id.text_editor);

        Intent intent = getIntent();
        String friendUser = intent.getStringExtra("FriendUser");

        friend = findFriend(friendUser);
        messageModels = RecentChatsManager.getInstance().getMessageModels(friend);
        RecentChatsManager.getInstance().setListener(this);

        Log.d("Jack","Chatting with "+friend.getProfileName());

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
//        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "I'm Zhonghua. You can call me Jack."));
//        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
//        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "welcome me blog:http://jakeq.github.io/"));

    }

    public void sendMessage(View v){
        //Send message.
        String message = textEditor.getText().toString();
        MessageModel messageModel = new MessageModel(MessageModel.MessageType.To,
                friend, new Date(), message);
        RecentChatsManager.getInstance().sendMessage(messageModel);
        //Display message.
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, message));
        ((ChattingAdapter)chatHistoryLv.getAdapter()).notifyDataSetChanged();
        //Clear input text view.
        textEditor.setText("");

    }

    @Override
    public void receivedMessage(MessageModel messageModel) {
        Friend friend = messageModel.getFriend();
        String message = messageModel.getMessage();
        if (friend.equals(this.friend)){
            Log.d("Jack", "Chatting window received a message from "+friend.getProfileName()+":"+message);
            messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, message));
            ((ChattingAdapter)chatHistoryLv.getAdapter()).notifyDataSetChanged();
        }
    }
}
