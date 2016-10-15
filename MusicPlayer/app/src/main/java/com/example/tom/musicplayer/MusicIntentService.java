package com.example.tom.musicplayer;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import static com.example.tom.musicplayer.MusicPlayer.mediaPlayer;

public class MusicIntentService extends IntentService {
    int location;
    static Runnable prev,st_ps,next;

    public MusicIntentService() {
        super("MusicIntentService");

        prev = new Runnable() {
            @Override
            public void run() {
                boolean play = mediaPlayer.isPlaying();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(MusicIntentService.this, prev());
                if (play)
                    mediaPlayer.start();
            }
        };

        st_ps = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                else
                    mediaPlayer.start();
            }
        };

        next = new Runnable() {
            @Override
            public void run() {
                boolean play = mediaPlayer.isPlaying();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(MusicIntentService.this, nxt());
                if(play)
                    mediaPlayer.start();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        location = intent.getIntExtra("loc",-1);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) { }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("damn",Integer.toString(location));
                int file = getFile(location);
                if(file != 0)
                    mediaPlayer = MediaPlayer.create(MusicIntentService.this, file);
                else
                    ;//Toast.makeText(this,"problem receiving the file",Toast.LENGTH_LONG).show();
            }
        }).run();
    }

    private int getFile(int location){
        switch (location){
            case 0:
                //title.setText("Cockroach King");
                return R.raw.cockroach_king;
            case 1:
                //title.setText("Lone Digger");
                return R.raw.lone_digger;
            case 2:
                //title.setText("Non Stop");
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
}
