package com.zynar.starvoca.info;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.zynar.starvoca.R;

import java.lang.reflect.Field;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        /*Field field;
        try{
            field = PreferenceManager.class.getDeclaredField("mList");
            field.setAccessible(true);
            ((ListView) field.get(this)).setVerticalScrollBarEnabled(false);
        } catch (Exception e) {}*/

        ListView listView = (ListView)findViewById(android.R.id.list);
        if (listView != null) listView.setVerticalScrollBarEnabled(false);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @NonNull
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            SharedPreferences preferences;
            preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

            /*Boolean darkTheme = preferences.getBoolean("notifi_theme", false);
            Boolean adsNotifi = preferences.getBoolean("notifi_event", false);
            Boolean wordsStudyNotifi = preferences.getBoolean("notifi_words", false);
            Boolean notiNotifi = preferences.getBoolean("notifi_noti", false);
            Boolean friendsAddNotifi = preferences.getBoolean("notifi_friends", false);
            Boolean messageNotifi = preferences.getBoolean("notifi_message", false);
            Boolean replyNotifi = preferences.getBoolean("notifi_comm_write_reply", false);
            Boolean rreplyNotifi = preferences.getBoolean("notifi_comm_reply_reply", false);
            String genderSound = preferences.getString("sound_gender", "");
            String playSound = preferences.getString("sound_play", "");
            Boolean checkNotifi = preferences.getBoolean("notifi_check", false);*/


            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
    }
}