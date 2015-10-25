package edu.njit.fall15.team1.cs673messenger.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.ChatMessage;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingWindowActivity extends Activity{
    private ListView chatHistoryLv;
    private EditText textEditor;
    private ChattingAdapter chatHistoryAdapter;
    private List<ChatMessage> messages = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingwindow);

        chatHistoryLv = (ListView) findViewById(R.id.chatting_history_lv);
        setAdapterForThis();

        textEditor = (EditText) findViewById(R.id.text_editor);

    }

    // Set up adapter
    private void setAdapterForThis() {
        initMessages();
        chatHistoryAdapter = new ChattingAdapter(this, messages);
        chatHistoryLv.setAdapter(chatHistoryAdapter);
    }

    // Initial message list data.
    private void initMessages() {
        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "I'm Zhonghua. You can call me Jack."));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "welcome me blog:http://blog.csdn.net/feiyangxiaomi"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_FROM, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
        messages.add(new ChatMessage(ChatMessage.MESSAGE_TO, "hello"));
    }
}
