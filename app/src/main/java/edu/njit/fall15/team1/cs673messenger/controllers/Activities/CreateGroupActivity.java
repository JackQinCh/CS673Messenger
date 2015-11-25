package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.FriendListItemAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Message;

/**
 * Created by jack on 15/11/18.
 */
public class CreateGroupActivity extends ListActivity{
    private List<Friend> friends;
    private List<Friend> members = new LinkedList<>();
    private TextView memberList;
    private EditText groupNameInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        friends = FriendsManager.getFriends();

        setListAdapter(new FriendListItemAdapter(this, friends));

        memberList = (TextView) findViewById(R.id.membersDisplay);
        groupNameInput = (EditText) findViewById(R.id.groupNameET);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Friend friend = friends.get(position);
        if (members.contains(friend)){
            members.remove(friend);
        }else{
            members.add(friend);
        }
        updateMemberDisplay();
    }

    /**
     * Update member string
     */
    private void updateMemberDisplay() {
        String membersStr = "";
        if (members.size()!=0){
            for (Friend f:members){
                membersStr += f.getProfileName()+", ";
            }
        }
        memberList.setText(membersStr);
    }

    /**
     * Create group button action
     * @param v
     */
    public void create(View v){
        String groupName = groupNameInput.getText().toString();
        if (groupName.equals("")){
            Toast.makeText(this, "Please input group name.", Toast.LENGTH_SHORT).show();
        }else{
            if (members.size() < 2){
                Toast.makeText(this, "The number of members can not be less than 3.", Toast.LENGTH_SHORT).show();
            }else {//Create
                Date time = new Date();
                Message message = Message.createGroupMessage(
                        groupName,
                        time.toString(),
                        Message.COMMAND_CREATE_GROUP,
                        Message.DIRECTION_TO,
                        members,
                        time,
                        "I created the group.",
                        "");
                RecentChatsManager.INSTANCE.addMessage(message);
                cancel(v);
            }
        }
    }

    /**
     * Cancel button action
     * @param v
     */
    public void cancel(View v){
        this.finish();
    }
}
