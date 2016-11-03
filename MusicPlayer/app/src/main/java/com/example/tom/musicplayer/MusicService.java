package com.example.tom.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;

public class MusicService extends Service {
    public static final String MAIN = "com.example.tom.musicplayer.action.main";
    public static final String PREV = "com.example.tom.musicplayer.action.prev";
    public static final String PLAY = "com.example.tom.musicplayer.action.play";
    public static final String NEXT = "com.example.tom.musicplayer.action.next";
    public static final String KILL = "com.example.tom.musicplayer.action.kill";
    public static final String URI  = "com.example.tom.musicplayer.action.uri" ;
    public static final String CALL_PLAY  = "com.example.tom.musicplayer.action.call.play";
    public static final String CALL_STOP  = "com.example.tom.musicplayer.action.call.stop";

    private Integer location;
    private MediaPlayer mediaPlayer;
    private Notification notification;
    private Intent previousIntent, playIntent, nextIntent, killIntent;
    private boolean isExStorage;
    private String title;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        generateIntents();

        notification = new Notification.Builder(this)
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();
    }

    private void generateIntents() {
        previousIntent = new Intent(this, MusicService.class);
        previousIntent.setAction(PREV);

        playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(PLAY);

        nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(NEXT);

        killIntent = new Intent(this, MusicService.class);
        killIntent.setAction(KILL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case MAIN:
                if(location == null)
                    location = intent.getIntExtra("location", -1);
                if(mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(this, getSong());
                else if(isExStorage){
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(this, getSong());
                }
                mediaPlayer.start();

                isExStorage = false;

                title = getSongName();
                replaceData();
                break;
            case URI:
                //TODO: When activity's screen rotates song restarts
                Uri uri = intent.getParcelableExtra("uri");

                if(mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(this, uri);
                else {
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(this, uri);
                }
                mediaPlayer.start();

                isExStorage = true;

                title = uri.toString();
                replaceData();
                break;
            case PREV:
                if(!isExStorage)
                    previousMusic();
                break;
            case PLAY:
                startStopMusic();
                break;
            case NEXT:
                if(!isExStorage)
                    nextMusic();
                break;
            case KILL:
                mediaPlayer.stop();
                mediaPlayer.release();
                stopForeground(true);
                stopSelf();
                break;
            case CALL_PLAY:
                if(mediaPlayer != null)
                    mediaPlayer.start();
                break;
            case CALL_STOP:
                if(mediaPlayer != null)
                    mediaPlayer.pause();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startStopMusic(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            replaceData();
        }
        else {
            mediaPlayer.start();
            replaceData();
        }
    }

    public void nextMusic(){
        location = (location == 2 ? 0 : location + 1);
        title = getSongName();

        boolean isPlaying = mediaPlayer.isPlaying();

        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),getSong());

        if(isPlaying)
            mediaPlayer.start();

        replaceData();
    }

    public void previousMusic(){
        location = (location == 0 ? 2 : location - 1);
        title = getSongName();

        boolean isPlaying = mediaPlayer.isPlaying();

        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),getSong());

        if(isPlaying)
            mediaPlayer.start();

        replaceData();
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void replaceData(){
        boolean isPlaying = mediaPlayer != null && !mediaPlayer.isPlaying();
        String album = "album";

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, album);
        contentView.setImageViewResource(R.id.imageButtonPrev, R.drawable.ic_skip_previous_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonPrev,
                PendingIntent.getService(this, 0, previousIntent, 0));
        contentView.setImageViewResource(R.id.imageButtonStop, isPlaying ? R.drawable.ic_play_arrow_black_36dp : R.drawable.ic_pause_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonStop,
                PendingIntent.getService(this, 0, playIntent, 0));
        contentView.setImageViewResource(R.id.imageButtonNext, R.drawable.ic_skip_next_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonNext,
                PendingIntent.getService(this, 0, nextIntent, 0));
        contentView.setImageViewResource(R.id.imageButtonExit, R.drawable.ic_close_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonExit,
                PendingIntent.getService(this, 0, killIntent, 0));

        notification.contentView = contentView;

        RemoteViews contentViewBig = new RemoteViews(getPackageName(), R.layout.custom_notification_big);
        contentViewBig.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentViewBig.setTextViewText(R.id.title, title);
        contentViewBig.setTextViewText(R.id.text, album);
        contentViewBig.setImageViewResource(R.id.imageButtonPrev, R.drawable.ic_skip_previous_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonPrev,
                PendingIntent.getService(this, 0, previousIntent, 0));
        contentViewBig.setImageViewResource(R.id.imageButtonStop, isPlaying ? R.drawable.ic_play_arrow_black_36dp : R.drawable.ic_pause_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonStop,
                PendingIntent.getService(this, 0, playIntent, 0));
        contentViewBig.setImageViewResource(R.id.imageButtonNext, R.drawable.ic_skip_next_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonNext,
                PendingIntent.getService(this, 0, nextIntent, 0));
        contentViewBig.setImageViewResource(R.id.imageButtonExit, R.drawable.ic_close_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonExit,
                PendingIntent.getService(this, 0, killIntent, 0));

        notification.bigContentView = contentViewBig;

        startForeground(101, notification);
    }
}
