package edu.njit.fall15.team1.cs673messenger.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServerListener;
import edu.njit.fall15.team1.cs673messenger.R;

public class LoginActivity extends Activity implements FacebookServerListener{
    final String hostAddress = "chat.facebook.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);


        FacebookServer.getInstance().addListeners(this);
        FacebookServer.getInstance().connect(hostAddress);
//        XMPP login = new XMPP(hostAddress, userName, password);
//        login.XMPPconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FacebookServer.getInstance().removeListeners(this);
    }

    /**
     * Button Login recall
     * @param v
     */
    public void login(View v){
        Log.d("Jack","Login button.");
        String userName = getETString(R.id.fbUsername);
        String password = getETString(R.id.fbPassword);
        if (!FacebookServer.getInstance().isConnected()){
            FacebookServer.getInstance().connect(hostAddress);
            Log.d("Jack", "Login button: is not connected.");
        }else {
            FacebookServer.getInstance().login(userName, password);
            Log.d("Jack", "Login button: login.");
        }
    }

    /**
     * Get string from EditText By Id
     * @param resId
     * @return
     */
    private String getETString(int resId){
        EditText widget = (EditText) this.findViewById(resId);
        return widget.getText().toString();
    }

    @Override
    public void facebookConnected(Boolean isConnected) {
        Log.d("Jack","Connection :"+isConnected);
    }

    @Override
    public void facebookLogined(Boolean isLogin) {
        Log.d("Jack","Login :"+isLogin);
        if (isLogin){
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            this.finish();
        }else {
            FacebookServer.getInstance().disConnect();
            FacebookServer.getInstance().connect("chat.facebook.com");
        }
    }

    @Override
    public void facebookReceivedMessage(String from, String message, Date time) {

    }

}
