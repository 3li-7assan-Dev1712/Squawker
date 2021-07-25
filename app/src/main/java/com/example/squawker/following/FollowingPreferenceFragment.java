package com.example.squawker.following;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.squawker.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class FollowingPreferenceFragment  extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = FollowingPreferenceFragment.class.getName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        Log.d(LOG_TAG, "setting to front-end for the settings activity");
        addPreferencesFromResource(R.xml.following_squawker);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "SharedPreference has changed ");
        Toast.makeText(getContext(), "Shared Preference has changed", Toast.LENGTH_SHORT).show();
        Preference p = findPreference(key);
        if (p instanceof SwitchPreferenceCompat){
            boolean isFollowing = sharedPreferences.getBoolean(key, false);
            if (isFollowing){
                Log.d(LOG_TAG, "Subscribing to " + key);
                FirebaseMessaging.getInstance().subscribeToTopic(key);
            }
            else {
                Log.d(LOG_TAG, "Un-Subscribing to " + key);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Fragment has created");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    //
//
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//
//        Preference p = findPreference(key);
//
//        Log.d("Class", "Preference has changed");
//        if (p instanceof SwitchPreferenceCompat){
//
//            boolean switchIsOpen = sharedPreferences.getBoolean(key, false);
//            Log.d(LOG_TAG, "Subscribing to " + key);
//            if (switchIsOpen){
//                FirebaseMessaging.getInstance().subscribeToTopic(key);
//            }else{
//                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
//                Log.d(LOG_TAG, "Un-subscribing to " + key);
//            }
//        }

        /*
        boolean isOn = sharedPreferences.getBoolean(key, false);
            if (isOn) {
                // The preference key matches the following key for the associated instructor in
                // FCM. For example, the key for Lyla is key_lyla (as seen in
                // following_squawker.xml). The topic for Lyla's messages is /topics/key_lyla

                // Subscribe
                FirebaseMessaging.getInstance().subscribeToTopic(key);
                Log.d(LOG_TAG, "Subscribing to " + key);
            } else {
                // Un-subscribe
                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                Log.d(LOG_TAG, "Un-subscribing to " + key);
            }
       }
         */

}
