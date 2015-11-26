package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.APIs.TaskManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.ChattingAdapter;
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

        Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
        if (messages.getType() == Messages.PERSONAL_CHAT){
            Message message = Message.createPersonalMessage(Message.DIRECTION_TO, messages.getMembers().get(0), messageText);
            RecentChatsManager.INSTANCE.addMessage(message);
        }else if (messages.getType() == Messages.GROUP_CHAT){
            Message message = Message.createGroupMessage(
                    messages.getName(),
                    chatId,
                    Message.COMMAND_GROUP_CHAT,
                    Message.DIRECTION_TO,
                    messages.getMembers(),
                    new Date(),
                    messageText,
                    "");
            RecentChatsManager.INSTANCE.addMessage(message);
        }
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
        if (chatId.equals(message.getChatID())){
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

    public void onSearch(View v){
        onSearchRequested();
    }

    @Override
    public boolean onSearchRequested() {
        Bundle searchData= new Bundle();
        searchData.putString("Chat ID", chatId);
        startSearch(null, false, searchData, false);
        return true;
    }

    public void onCreateNewTask(View v){
        final EditText text = new EditText(ChattingWindowActivity.this);
        new AlertDialog.Builder(ChattingWindowActivity.this)
            .setTitle("Please input new task.")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setView(text)
            .setPositiveButton("Create",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String taskStr = text.getText().toString();
                            System.out.println(taskStr);
                            if (taskStr.equals("")){
                                return;
                            }
                            TaskManager.INSTANCE.addTask(chatId, taskStr);

                            Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
                            if (messages.getType() == Messages.GROUP_CHAT){
                                Message message = Message.createGroupMessage(
                                        messages.getName(),
                                        chatId,
                                        Message.COMMAND_CREATE_TASK,
                                        Message.DIRECTION_TO,
                                        messages.getMembers(),
                                        new Date(),
                                        "I created a task:" + taskStr,
                                        taskStr);
                                RecentChatsManager.INSTANCE.addMessage(message);
                            }
                        }
                    })
            .setNegativeButton("Cancel", null).show();
    }

    public void onCheckTask(View v){
        List<String> tasklist = TaskManager.INSTANCE.findTasks(chatId).getTaskList();
        if (tasklist.size() == 0)
            return;
        final String[] tasks = new String[tasklist.size()];
        tasklist.toArray(tasks);
        new AlertDialog.Builder(this)
                .setTitle("Touch to finish the task.")
                .setItems(tasks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String taskStr = TaskManager.INSTANCE.findTasks(chatId).getTaskList().get(which);

                        Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
                        if (messages.getType() == Messages.GROUP_CHAT){
                            Message message = Message.createGroupMessage(
                                    messages.getName(),
                                    chatId,
                                    Message.COMMAND_REMOVE_TASK,
                                    Message.DIRECTION_TO,
                                    messages.getMembers(),
                                    new Date(),
                                    "I finished the task:" + taskStr,
                                    taskStr);
                            RecentChatsManager.INSTANCE.addMessage(message);
                        }
                        TaskManager.INSTANCE.removeTask(chatId, which);
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }
}
