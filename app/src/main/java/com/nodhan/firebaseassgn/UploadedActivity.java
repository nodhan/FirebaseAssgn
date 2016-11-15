package com.nodhan.firebaseassgn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UploadedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ImageAdapter imageAdapter = new ImageAdapter(getUris(), getApplicationContext()); // getting adapter

        recyclerView.setAdapter(imageAdapter); // setting adapter
    }

    /**
     * Generates list of all urls in shared preferences
     *
     * @return a list of strings if not null
     */
    private List<String> getUris() {
        String SHARED_PREF = "uploads";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        int count = sharedPreferences.getInt("count", 0);
        if (count > 0) {
            List<String> uris = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String uri = sharedPreferences.getString("uri" + i, null);
                if (uri != null) {
                    uris.add(uri);
                    Log.d("URI:UPLOAD ACT", uri);
                }
            }
            return uris;
        }
        return null;
    }
}
