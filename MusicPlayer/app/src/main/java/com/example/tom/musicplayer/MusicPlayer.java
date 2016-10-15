package com.example.tom.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chen on 13/10/2016.
 */

public class MusicPlayer extends AppCompatActivity {

    private int location;
    public static MediaPlayer mediaPlayer;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Button prev = (Button)findViewById(R.id.prev);
        Button st_ps = (Button)findViewById(R.id.st_ps);
        Button nxt = (Button)findViewById(R.id.nxt);
        title = (TextView)findViewById(R.id.textView);

        /*
        location = getIntent().getIntExtra("loc",-1);

        int file = getFile(location);
        if(file != 0)
            mediaPlayer = MediaPlayer.create(this, file);
        else
            Toast.makeText(this,"problem receiving the file",Toast.LENGTH_LONG).show();
        */

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicIntentService.prev.run();
            }
        });

        st_ps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicIntentService.st_ps.run();
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicIntentService.next.run();
            }
        });
    }
/*
    private int getFile(int location){
        switch (location){
            case 0:
                title.setText("Cockroach King");
                return R.raw.cockroach_king;
            case 1:
                title.setText("Lone Digger");
                return R.raw.lone_digger;
            case 2:
                title.setText("Non Stop");
                return R.raw.non_stop;
        }
        return 0;
    }

    private int prev(){
        if(location > 0)
            return getFile(--location);
        location = 2;
        return getFile(location);
    }

    private int nxt(){
        if(location < 2)
            return getFile(++location);
        location = 0;
        return getFile(location);
    }
*/
}