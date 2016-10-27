package com.example.tom.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private Integer location;
    MediaPlayer mediaPlayer;
    private Notification notification;

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case "com.example.tom.musicplayer.action.main":
                if(location == null)
                    location = intent.getIntExtra("location", -1);
                if(mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(this, getSong());

                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction("com.example.tom.musicplayer.action.main");
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                Intent previousIntent = new Intent(this, MusicService.class);
                previousIntent.setAction("com.example.tom.musicplayer.action.prev");

                Notification.Action actionPrev = new Notification.Action.Builder(
                        Icon.createWithResource(this,android.R.drawable.ic_media_previous),
                        "Previous",
                        PendingIntent.getService(this, 0, previousIntent, 0))
                        .build();

                Intent playIntent = new Intent(this, MusicService.class);
                playIntent.setAction("com.example.tom.musicplayer.action.play");

                Notification.Action actionPlay = new Notification.Action.Builder(
                        Icon.createWithResource(this,android.R.drawable.ic_media_play),
                        "Play",
                        PendingIntent.getService(this, 0, playIntent, 0))
                        .build();

                Intent nextIntent = new Intent(this, MusicService.class);
                nextIntent.setAction("com.example.tom.musicplayer.action.next");

                Notification.Action actionNext = new Notification.Action.Builder(
                        Icon.createWithResource(this,android.R.drawable.ic_media_next),
                        "Next",
                        PendingIntent.getService(this, 0, nextIntent, 0))
                        .build();

                Notification notification = new Notification.Builder(this)
                        .setContentTitle("title!")
                        .setContentText("text!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .addAction(actionPrev)
                        .addAction(actionPlay)
                        .addAction(actionNext)
                        .build();

                startForeground(101, notification);
                break;
            case "com.example.tom.musicplayer.action.prev":
                previousMusic();
                break;
            case "com.example.tom.musicplayer.action.play":
                startStopMusic();
                break;
            case "com.example.tom.musicplayer.action.next":
                nextMusic();
                break;
            case "com.example.tom.musicplayer.action.stopforeground":
                Toast.makeText(this,"damn 4",Toast.LENGTH_SHORT).show();
                stopForeground(true);
                stopSelf();
                break;
        }
        /*
        if (intent.getAction().equals("com.example.tom.musicplayer.action.main")) {
            if(location == null)
                location = intent.getIntExtra("location", -1);
            if(mediaPlayer == null)
                mediaPlayer = MediaPlayer.create(this, getSong());

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction("com.example.tom.musicplayer.action.main");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent previousIntent = new Intent(this, MusicService.class);
            previousIntent.setAction("com.example.tom.musicplayer.action.prev");

            Notification.Action actionPrev = new Notification.Action.Builder(
                    Icon.createWithResource(this,android.R.drawable.ic_media_previous),
                    "Previous",
                    PendingIntent.getService(this, 0, previousIntent, 0))
                    .build();

            Intent playIntent = new Intent(this, MusicService.class);
            playIntent.setAction("com.example.tom.musicplayer.action.play");

            Notification.Action actionPlay = new Notification.Action.Builder(
                    Icon.createWithResource(this,android.R.drawable.ic_media_play),
                    "Play",
                    PendingIntent.getService(this, 0, playIntent, 0))
                    .build();

            Intent nextIntent = new Intent(this, MusicService.class);
            nextIntent.setAction("com.example.tom.musicplayer.action.next");

            Notification.Action actionNext = new Notification.Action.Builder(
                    Icon.createWithResource(this,android.R.drawable.ic_media_next),
                    "Next",
                    PendingIntent.getService(this, 0, nextIntent, 0))
                    .build();

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("title!")
                    .setContentText("text!")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .addAction(actionPrev)
                    .addAction(actionPlay)
                    .addAction(actionNext)
                    .build();

            startForeground(101, notification);
        } else if (intent.getAction().equals("com.example.tom.musicplayer.action.prev")) {
            previousMusic();
        } else if (intent.getAction().equals("com.example.tom.musicplayer.action.play")) {
            startStopMusic();
        } else if (intent.getAction().equals("com.example.tom.musicplayer.action.next")) {
            nextMusic();
        } else if (intent.getAction().equals("com.example.tom.musicplayer.action.stopforeground")) {
            Toast.makeText(this,"damn 4",Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
        */
        return super.onStartCommand(intent, flags, startId);
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