package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.models.Message;

/**
 * Created by jack on 15/11/25.
 */
public class SampleSearchActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1));

        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // call detail activity for clicked entry
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = intent.getBundleExtra(SearchManager.APP_DATA);
            String chatId = bundle.getString("Chat ID");
            Log.i(getClass().getSimpleName(), "Chat ID:"+chatId);
            doSearch(query, chatId);
        }
    }

    private void doSearch(String queryStr, String chatId) {
        // get a Cursor, prepare the ListAdapter
        // and set it
        Log.i(getClass().getSimpleName(), "Searching:"+queryStr);
        List<Message> messages = RecentChatsManager.INSTANCE.getMessages(chatId).getMessages();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
        if (messages.size() != 0){
            for (Message message: messages){
                if (message.getMessage().contains(queryStr)){
                    adapter.add(message.getMessage());
                }
            }
        }
        if (adapter.getCount() == 0)
            adapter.add("--Sorry, I didn't find it.--");
        adapter.notifyDataSetChanged();
    }
}
