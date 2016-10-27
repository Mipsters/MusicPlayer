package com.example.tom.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Chen on 13/10/2016.
 */

public class MusicPlayer extends AppCompatActivity {
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
        //if(getIntent().getData() == null)
        serviceIntent.putExtra("location", getIntent().getIntExtra("location", -1));
        serviceIntent.setAction("com.example.tom.musicplayer.action.main");
        startService(serviceIntent);
        //else
        //    serviceIntent.putExtra("songDirectory",getIntent().getData().getPath());

        Button prev = (Button)findViewById(R.id.prev);
        Button st_ps = (Button)findViewById(R.id.st_ps);
        Button nxt = (Button)findViewById(R.id.nxt);
        title = (TextView)findViewById(R.id.textView);


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        st_ps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}