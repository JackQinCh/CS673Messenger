package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.R;

/**
 * Created by Akhmetov on 10/27/2015.
 */
public class AppPreferencesActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

            Preference username = (Preference)findPreference("usernameDisplay");
            String name;
            //display username
            name = FacebookServer.getInstance().getUserName();
            if (name != null)
                username.setTitle(name);

            sp.registerOnSharedPreferenceChangeListener(this);

            //change status
            FacebookServer.getInstance().setActiveStatus(sp.getBoolean("changeStatusSetting", true));

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * Called when a shared preference is changed, added, or removed. This
         * may be called even if a preference is set to its existing value.
         * <p/>
         * <p>This callback will be run on your main thread.
         *
         * @param sharedPreferences The {@link SharedPreferences} that received
         *                          the change.
         * @param key               The key of the preference that was changed, added, or
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key){
                case "changeStatusSetting":
                    boolean status = sharedPreferences.getBoolean(key, true);
                    Log.d(getClass().getSimpleName(), String.valueOf(status));
                    //change status
                    FacebookServer.getInstance().setActiveStatus(status);
                    break;
                default:
                    break;
            }

        }
    }

}
