package com.example.tom.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Chen on 13/10/2016.
 */

public class MusicPlayer extends AppCompatActivity {
    private MusicService mBoundService;
    private TextView title;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((MusicService.LocalBinder)service).getService();
            title.setText(mBoundService.getSongName());
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        if(mBoundService == null) {
            Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
            //if(getIntent().getData() == null)
            serviceIntent.putExtra("location", getIntent().getIntExtra("location", -1));
            //else
            //    serviceIntent.putExtra("songDirectory",getIntent().getData().getPath());
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        Button prev = (Button)findViewById(R.id.prev);
        Button st_ps = (Button)findViewById(R.id.st_ps);
        Button nxt = (Button)findViewById(R.id.nxt);
        title = (TextView)findViewById(R.id.textView);


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.previousMusic();
                title.setText(mBoundService.getSongName());
            }
        });

        st_ps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.startStopMusic();
                title.setText(mBoundService.getSongName());
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.nextMusic();
                title.setText(mBoundService.getSongName());
            }
        });
    }
}