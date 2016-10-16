package com.example.tom.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private int location;
    MediaPlayer mediaPlayer;

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        location = intent.getIntExtra("location",-1);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),getSong());

        return mBinder;
    }

    private int getSong(){
        switch (location){
            case 0:
                return R.raw.cockroach_king;
            case 1:
                return R.raw.lone_digger;
            case 2:
                return R.raw.non_stop;
        }
        return 0;
    }

    public void startStopMusic(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.start();
    }

    public void nextMusic(){
        location = (location == 2 ? 0 : location + 1);

        boolean isPlaying = mediaPlayer.isPlaying();

        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),getSong());
        if(isPlaying)
            mediaPlayer.start();
    }

    public void previousMusic(){
        location = (location == 0 ? 2 : location - 1);

        boolean isPlaying = mediaPlayer.isPlaying();

        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),getSong());
        if(isPlaying)
            mediaPlayer.start();
    }

    public String getSongName(){
        switch (location){
            case 0:
                return "Cockroach King";
            case 1:
                return "Lone Digger";
            case 2:
                return "Non Stop";
        }
        return "Song Name";
    }
}