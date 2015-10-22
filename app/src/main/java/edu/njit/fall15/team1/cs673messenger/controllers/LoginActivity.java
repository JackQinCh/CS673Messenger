package edu.njit.fall15.team1.cs673messenger.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServerListener;
import edu.njit.fall15.team1.cs673messenger.R;

public class LoginActivity extends AppCompatActivity implements FacebookServerListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        FacebookServer.getInstance().setConnectionListener(this);
        FacebookServer.getInstance().connect("chat.facebook.com");

//        XMPP login = new XMPP("chat.facebook.com", userName, password);
//        login.XMPPconnect();
    }

    /**
     * Button Login recall
     * @param v
     */
    public void login(View v){
        String userName = getETString(R.id.fbUsername);
        String password = getETString(R.id.fbPassword);

        FacebookServer.getInstance().login(userName, password);

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
}
