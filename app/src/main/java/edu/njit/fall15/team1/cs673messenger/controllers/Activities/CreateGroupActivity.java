package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 15/11/18.
 */
public class CreateGroupActivity extends ListActivity{
    private List<Friend> friends;
    private List<String> stringList = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        friends = FriendsManager.getFriends();
        if (friends.size() != 0){
            for (Friend f:friends){
                stringList.add(f.getProfileName());
            }
        }

        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringList));
    }
}
