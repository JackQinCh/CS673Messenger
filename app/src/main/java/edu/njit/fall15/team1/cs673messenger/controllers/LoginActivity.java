package edu.njit.fall15.team1.cs673messenger.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    //Button Login recall
    public void login(View v){
        Log.d("Jack", "login");
        String userName = getETString(R.id.fbUsername);
        String password = getETString(R.id.fbPassword);
        XMPP login = new XMPP("chat.facebook.com", userName, password);
        login.XMPPconnect();

        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        this.finish();
    }
    //Get string from EditText By Id
    private String getETString(int resId){
        EditText widget = (EditText) this.findViewById(resId);
        return widget.getText().toString();
    }
}
