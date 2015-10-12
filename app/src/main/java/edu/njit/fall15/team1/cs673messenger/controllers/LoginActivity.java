package edu.njit.fall15.team1.cs673messenger.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.XMPP.XMPP;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View v){
        Log.d("Jack", "login");
        String userName = getETString(R.id.fbUsername);
        String password = getETString(R.id.fbPassword);
        XMPP login = new XMPP("chat.facebook.com", userName, password);
        login.XMPPconnect();

        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }

    private String getETString(int resId){
        EditText widget = (EditText) this.findViewById(resId);
        return widget.getText().toString();
    }
}
