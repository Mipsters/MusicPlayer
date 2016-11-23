package com.example.tom.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MusicService extends Service {
    public static final String MAIN = "com.example.tom.musicplayer.action.main";
    public static final String PREV = "com.example.tom.musicplayer.action.prev";
    public static final String PLAY = "com.example.tom.musicplayer.action.play";
    public static final String NEXT = "com.example.tom.musicplayer.action.next";
    public static final String KILL = "com.example.tom.musicplayer.action.kill";
    public static final String URI  = "com.example.tom.musicplayer.action.uri" ;
    public static final String CALL_PLAY  = "com.example.tom.musicplayer.action.call.play";
    public static final String CALL_STOP  = "com.example.tom.musicplayer.action.call.stop";

    public static final String LOC  = "com.example.tom.musicplayer.data.location";
    public static final String URI_DATA  = "com.example.tom.musicplayer.data.uri";

    private static final int NOTIFICATION_ID = 101;

    private Integer location;
    private MediaPlayer mediaPlayer;
    private Notification notification;
    private Intent previousIntent, playIntent, nextIntent, killIntent, phonePlayIntent,stopIntent;
    private boolean isExStorage;
    private String title;
    private RemoteViews contentView, contentViewBig;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
        
        initNotData();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_RINGING:
                        stopIntent = new Intent(getApplicationContext(), MusicService.class);
                        stopIntent.setAction(MusicService.CALL_STOP);
                        getApplicationContext().startService(stopIntent);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        phonePlayIntent = new Intent(getApplicationContext(), MusicService.class);
                        phonePlayIntent.setAction(MusicService.CALL_PLAY);
                        getApplicationContext().startService(phonePlayIntent);
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case MAIN:
                if(location == null)
                    location = intent.getIntExtra(LOC, -1);
                if(mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(this, getSong());
                else if(isExStorage){
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(this, getSong());
                }
                mediaPlayer.start();

                isExStorage = false;

                title = getSongName();
                replaceNotData();
                break;
            case URI:
                Uri uri = intent.getParcelableExtra(URI_DATA);

                if(mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(this, uri);
                else if(!title.equals(uri.toString())){
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(this, uri);
                }
                mediaPlayer.start();

                isExStorage = true;

                title = uri.toString();
                replaceNotData();
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
        if(mediaPlayer != null)
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                replaceNotData();
            }
            else {
                mediaPlayer.start();
                replaceNotData();
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

        replaceNotData();
    }

    public void previousMusic(){
        location = (location == 0 ? 2 : location - 1);
        title = getSongName();

        boolean isPlaying = mediaPlayer.isPlaying();

        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),getSong());

        if(isPlaying)
            mediaPlayer.start();

        replaceNotData();
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

    private  void initNotData(){
        contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);

        contentView.setImageViewResource(R.id.imageButtonPrev,R.drawable.ic_skip_previous_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonPrev,
                PendingIntent.getService(this, 0, previousIntent, 0));
        contentView.setOnClickPendingIntent(R.id.imageButtonStop,
                PendingIntent.getService(this, 0, playIntent, 0));
        contentView.setImageViewResource(R.id.imageButtonNext, R.drawable.ic_skip_next_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonNext,
                PendingIntent.getService(this, 0, nextIntent, 0));
        contentView.setImageViewResource(R.id.imageButtonExit, R.drawable.ic_close_black_36dp);
        contentView.setOnClickPendingIntent(R.id.imageButtonExit,
                PendingIntent.getService(this, 0, killIntent, 0));


        contentViewBig = new RemoteViews(getPackageName(), R.layout.custom_notification_big);

        contentViewBig.setImageViewResource(R.id.imageButtonPrev, R.drawable.ic_skip_previous_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonPrev,
                PendingIntent.getService(this, 0, previousIntent, 0));
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonStop,
                PendingIntent.getService(this, 0, playIntent, 0));
        contentViewBig.setImageViewResource(R.id.imageButtonNext, R.drawable.ic_skip_next_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonNext,
                PendingIntent.getService(this, 0, nextIntent, 0));
        contentViewBig.setImageViewResource(R.id.imageButtonExit, R.drawable.ic_close_black_36dp);
        contentViewBig.setOnClickPendingIntent(R.id.imageButtonExit,
                PendingIntent.getService(this, 0, killIntent, 0));
    }

    private void replaceNotData(){
        boolean isPlaying = mediaPlayer != null && !mediaPlayer.isPlaying();
        String album = "album";

        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, album);
        contentView.setImageViewResource(R.id.imageButtonStop,
                isPlaying ? R.drawable.ic_play_arrow_black_36dp : R.drawable.ic_pause_black_36dp);

        notification.contentView = contentView;


        contentViewBig.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentViewBig.setTextViewText(R.id.title, title);
        contentViewBig.setTextViewText(R.id.text, album);
        contentViewBig.setImageViewResource(R.id.imageButtonStop,
                isPlaying ? R.drawable.ic_play_arrow_black_36dp : R.drawable.ic_pause_black_36dp);

        notification.bigContentView = contentViewBig;

        startForeground(NOTIFICATION_ID, notification);
    }
}
