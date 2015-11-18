package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServerListener;
import edu.njit.fall15.team1.cs673messenger.R;

public class LoginActivity extends Activity implements FacebookServerListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        FacebookServer.INSTANCE.addListeners(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FacebookServer.INSTANCE.removeListeners(this);
    }

    /**
     * Button Login recall
     * @param v
     */
    public void login(View v){
        Log.d(this.getClass().getSimpleName(),"Login button.");
        String userName = getETString(R.id.fbUsername);
        String password = getETString(R.id.fbPassword);
        if (!FacebookServer.INSTANCE.isConnected()){
            FacebookServer.INSTANCE.connect(getString(R.string.facebook_server));
            Log.d(this.getClass().getSimpleName(), "Login button: is not connected.");
        }else {
            FacebookServer.INSTANCE.login(userName, password);
            Log.d(this.getClass().getSimpleName(), "Login button: login.");
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
        Log.d(getClass().getSimpleName(),"Connection :"+isConnected);
    }

    @Override
    public void facebookLogined(Boolean isLogin) {
        Log.d(getClass().getSimpleName(),"Login :"+isLogin);

        if (isLogin){
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            this.finish();
        }else {
            FacebookServer.INSTANCE.disConnect();
            FacebookServer.INSTANCE.connect(getString(R.string.facebook_server));
            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void facebookReceivedMessage(int type, String from, String message, Date time) {

    }

}
