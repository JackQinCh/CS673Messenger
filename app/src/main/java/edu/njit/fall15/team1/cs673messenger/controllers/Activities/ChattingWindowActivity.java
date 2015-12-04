package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
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
import edu.njit.fall15.team1.cs673messenger.models.Tasks;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingWindowActivity extends Activity implements RecentChatsListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView chatHistoryLv;
    private EditText textEditor;
    private ChattingAdapter chatHistoryAdapter;
    private ArrayList<Message> messageLines = new ArrayList<>();
    private String chatId;
    private static final float MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingwindow);

        Intent intent = getIntent();
        chatId = intent.getStringExtra(getString(R.string.chat_id));

        RecentChatsManager.INSTANCE.addListener(this);

        Log.d(getLocalClassName(), "Chat:" + chatId);

        chatHistoryLv = (ListView) findViewById(R.id.chatting_history_lv);
        chatHistoryLv.setOnItemClickListener(this);
        chatHistoryLv.setOnItemLongClickListener(this);
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

        if (chatHistoryAdapter != null) {
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
    public void sendMessage(View v) {
        //Send message.
        String messageText = textEditor.getText().toString();

        Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
        if (messages.getType() == Messages.PERSONAL_CHAT) {
            Message message = Message.createPersonalMessage(Message.DIRECTION_TO, messages.getMembers().get(0), messageText);
            RecentChatsManager.INSTANCE.addMessage(message);
        } else if (messages.getType() == Messages.GROUP_CHAT) {
            List<Friend> members = new ArrayList<>();
            if (isPrivateChat) {
                members.add(privateWith);
            } else {
                members = messages.getMembers();
            }
            Message message = Message.createGroupMessage(
                    messages.getName(),
                    chatId,
                    Message.COMMAND_GROUP_CHAT,
                    Message.DIRECTION_TO,
                    members,
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
        if (chatId.equals(message.getChatID())) {
            Log.d(getClass().getSimpleName(), "Received a message from " + friend.getProfileName() + ":" + message);
            updateMessages();
        }
    }

    /**
     * Get data for listView
     * @return
     */
    private List<Message> getData() {
        return RecentChatsManager.INSTANCE.getMessages(chatId).getMessages();
    }

    public void onSearch(View v) {
        onSearchRequested();
    }

    @Override
    public boolean onSearchRequested() {
        Bundle searchData = new Bundle();
        searchData.putString("Chat ID", chatId);
        startSearch(null, false, searchData, false);
        return true;
    }

    public void onCreateNewTask(View v) {
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
                                if (taskStr.equals("")) {
                                    return;
                                }
                                TaskManager.INSTANCE.addTask(chatId, taskStr);

                                Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
                                if (messages.getType() == Messages.GROUP_CHAT) {
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

    public void onCheckTask(View v) {
        Tasks tasks = TaskManager.INSTANCE.findTasks(chatId);
        if (tasks == null)
            return;
        List<String> tasklist = tasks.getTaskList();
        if (tasklist.size() == 0)
            return;
        final String[] tasksString = new String[tasklist.size()];
        tasklist.toArray(tasksString);
        new AlertDialog.Builder(this)
                .setTitle("Touch to finish the task.")
                .setItems(tasksString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String taskStr = TaskManager.INSTANCE.findTasks(chatId).getTaskList().get(which);
                        if (taskStr.endsWith("(Finished)"))
                            return;

                        Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
                        if (messages.getType() == Messages.GROUP_CHAT) {
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


    public void onCreateNewEvent(View v) {
        // get the current date
        final Calendar c = Calendar.getInstance();
        final int[] dateTimeArray = new int[5];
        dateTimeArray[0] = c.get(Calendar.YEAR);
        dateTimeArray[1] = c.get(Calendar.MONTH);
        dateTimeArray[2] = c.get(Calendar.DAY_OF_MONTH);
        dateTimeArray[3] = c.get(Calendar.HOUR_OF_DAY);
        dateTimeArray[4] = c.get(Calendar.MINUTE);
        View view = LayoutInflater.from(this).inflate(R.layout.new_event_creator, null);

        final Button dateButton = (Button) view.findViewById(R.id.dateET);
        dateButton.setText(dateTimeArray[0] + "-" + (dateTimeArray[1] + 1) + "-" + dateTimeArray[2]);

        final DatePickerDialog.OnDateSetListener datelistener = new DatePickerDialog.OnDateSetListener() {
            /**params：view：该事件关联的组件
             * params：myyear：当前选择的年
             * params：monthOfYear：当前选择的月
             * params：dayOfMonth：当前选择的日
             */
            @Override
            public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
                //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
                dateTimeArray[0] = myyear;
                dateTimeArray[1] = monthOfYear;
                dateTimeArray[2] = dayOfMonth;
                //更新日期
                dateButton.setText(dateTimeArray[0] + "-" + (dateTimeArray[1] + 1) + "-" + dateTimeArray[2]);
            }
        };

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ChattingWindowActivity.this,
                        datelistener,
                        dateTimeArray[0], dateTimeArray[1], dateTimeArray[2]);
                dpd.show();
            }
        });

        final Button timeButton = (Button) view.findViewById(R.id.timeET);
        timeButton.setText(dateTimeArray[3] + ":" + dateTimeArray[4]);

        final TimePickerDialog.OnTimeSetListener timelistener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTimeArray[3] = hourOfDay;
                dateTimeArray[4] = minute;

                timeButton.setText(dateTimeArray[3] + ":" + dateTimeArray[4]);
            }
        };

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(ChattingWindowActivity.this,
                        timelistener,
                        dateTimeArray[3], dateTimeArray[4], true);
                tpd.show();
            }
        });


        final TextView event = (TextView) view.findViewById(R.id.eventET);


        new AlertDialog.Builder(this)
                .setTitle("Create an event.")
                .setView(view)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (event.getText().toString().equals("")) {
                            return;
                        }
                        c.set(dateTimeArray[0], dateTimeArray[1], dateTimeArray[2], dateTimeArray[3], dateTimeArray[4]);

                        long startTime = c.getTimeInMillis();
                        long endTime = c.getTimeInMillis() + 60 * 60 * 1000;

                        Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
                        Message message = Message.createGroupMessage(
                                messages.getName(),
                                messages.getChatId(),
                                Message.COMMAND_CREATE_EVENT,
                                Message.DIRECTION_TO,
                                messages.getMembers(),
                                new Date(),
                                "I create an event.",
                                event.getText().toString() + "," + startTime + "," + endTime
                        );
                        RecentChatsManager.INSTANCE.addMessage(message);

                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", startTime);
                        intent.putExtra("allDay", true);
                        intent.putExtra("rrule", "FREQ=YEARLY");
                        intent.putExtra("endTime", endTime);
                        intent.putExtra("title", event.getText().toString());
                        startActivity(intent);
                    }
                }).show();

    }

    public void onShareLocation(View v) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        double latitude = 40.7436556;
        double longitude = -74.1780795;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = null;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER.toString(), MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
            if (locationManager != null) {

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }else {
            Log.d(getClass().getSimpleName(), "No NETWORK_PROVIDER.");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER.toString(), MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
                if (locationManager != null) {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            if (location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }else Log.d(getClass().getSimpleName(), "No GPS_PROVIDER.");
        }

        Log.i(getClass().getSimpleName(), "GPS Latitude:" + latitude + ", Longitude" + longitude);

        Messages messages = RecentChatsManager.INSTANCE.getMessages(chatId);
        Message message = Message.createGroupMessage(
                messages.getName(),
                messages.getChatId(),
                Message.COMMAND_SHARE_LOCATION,
                Message.DIRECTION_TO,
                messages.getMembers(),
                new Date(),
                "I am sharing my location.",
                latitude + "," + longitude
        );
        RecentChatsManager.INSTANCE.addMessage(message);

    }




    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Message message = messageLines.get(position);
        if (message.getCommand() == Message.COMMAND_CREATE_EVENT){
            String[] extra = message.getExtra().split(",");
            String event = extra[0];
            long startTime = Long.valueOf(extra[1]);
            long endTime = Long.valueOf(extra[2]);
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", startTime);
            intent.putExtra("allDay", true);
            intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("endTime", endTime);
            intent.putExtra("title", event);
            startActivity(intent);
        }else if (message.getCommand() == Message.COMMAND_SHARE_LOCATION){
            String[] extra = message.getExtra().split(",");
            double latitude = Double.valueOf(extra[0]);
            double longitude = Double.valueOf(extra[1]);

            Uri uri = Uri.parse("geo:"+latitude+","+longitude);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        }
    }

    /**
     * Callback method to be invoked when an item in this view has been
     * clicked and held.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent   The AbsListView where the click happened
     * @param view     The view within the AbsListView that was clicked
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(getClass().getSimpleName(), "Long touch.");

        Message message = messageLines.get(position);

        if (message.getType() == Message.TYPE_CHAT && message.getDirection() != Message.DIRECTION_FROM){
            return false;
        }
        textEditor.setText("@"+message.getFriend().get(0).getProfileName()+":");
        isPrivateChat = true;
        privateWith = message.getFriend().get(0);

        return true;
    }

    private boolean isPrivateChat = false;
    private Friend privateWith;
}
