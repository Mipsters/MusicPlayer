package com.example.tom.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Chen on 13/10/2016.
 */

public class MusicPlayer extends AppCompatActivity {
    private TextView title;
    private Intent previousIntent, playIntent, nextIntent;

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

        previousIntent = new Intent(this, MusicService.class);
        previousIntent.setAction("com.example.tom.musicplayer.action.prev");

        playIntent = new Intent(this, MusicService.class);
        playIntent.setAction("com.example.tom.musicplayer.action.play");

        nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction("com.example.tom.musicplayer.action.next");


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(previousIntent);
            }
        });

        st_ps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(playIntent);
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(nextIntent);
            }
        });
    }
}