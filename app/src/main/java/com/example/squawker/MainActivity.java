package com.example.squawker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squawker.data.MyContract;
import com.example.squawker.fcm.MyFirebaseMesseagingService;
import com.example.squawker.following.FollowingPreferenceActivity;
import com.example.squawker.provider.SquawkerContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID_MESSAGES = 0;
    static final String[] MESSAGES_PROJECTION = {
            SquawkerContract.COLUMN_AUTHOR,
            SquawkerContract.COLUMN_AUTHOR_KEY,
            SquawkerContract.COLUMN_MESSAGE,
            SquawkerContract.COLUMN_DATE
    };

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SquawkAdapter mAdapter;

    static final int COL_NUM_AUTHOR = 0;
    static final int COL_NUM_AUTHOR_KEY =1;
    static final int COL_NUM_MESSAGE = 2;
    static final int COL_NUM_DATE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setApplicationId("1:427386830642:android:860f35487d60e18a11612b")
//                .setProjectId("squawk-project")
//                .setApiKey("AIzaSyA0XZpnhcBI6eao5D7q8fR6FHF5vePjuPE")
//                .build();
//        if (FirebaseApp.getApps(this).isEmpty()){
//            FirebaseApp.initializeApp(this, options, "com.example.squawker");
//        }

        int resultCode = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this);
        Log.d("MainActivity.class", "result code is: " + resultCode);
        if (!(resultCode == ConnectionResult.SUCCESS)){
            Toast.makeText(this, "Google play services in not update in this device", Toast.LENGTH_SHORT).show();
            Log.d("MainAcitity.class", "Google play is not update ");
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        Log.w("Messaging.class", "Fetching FCM failed");
                        return;
                    }
                    String token = task.getResult();
                    if (token == null){
                        Toast.makeText(this, "Token is null " , Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity.class", "TOKEN IS NULL!");
                    }else {
                        Log.d("MainActivity.class ", token);
                        Toast.makeText(this, "Token is: " + token, Toast.LENGTH_SHORT).show();
                    }
                });

        // 1:427386830642:android:860f35487d60e18a11612b
        mRecyclerView =  findViewById(R.id.squawks_recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Specify an adapter
        mAdapter = new SquawkAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Start the loader, if there's a bug change from new LoaderManager() => getSupportLoaderManager()
       getSupportLoaderManager().initLoader(LOADER_ID_MESSAGES, null, this);


        // Get token from the ID Service you created and show it in a log

//        String msg = getString(R.string.message_token_format, token);
//        Log.d(LOG_TAG, msg);


        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String selection = SquawkerContract.createSelectionForCurrentFollowers(
                PreferenceManager.getDefaultSharedPreferences(this)
        );
        // Selection is for the how to select the row
        // selection args is what to select exactly
        // you can say SELECT IN as the select param
        // the constructor key as the selction args [key_asser, key_nikita, key_lyla] and so on : )
        Log.d(LOG_TAG, "Selection is " + selection);
        return new CursorLoader(this, MyContract.SquawkEntry.CONTENT_URI,
                MESSAGES_PROJECTION, selection, null, SquawkerContract.COLUMN_DATE + " DESC");

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
        Log.d("MinActivity.class", "loader finished");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_following_preferences){
            Intent intent = new Intent(this, FollowingPreferenceActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // On any single change in the settings the loader will be re-created
        getSupportLoaderManager().restartLoader(LOADER_ID_MESSAGES, null, this);

    }

}