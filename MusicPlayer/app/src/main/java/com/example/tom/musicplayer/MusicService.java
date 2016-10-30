package com.example.tom.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.InputStream;

public class MusicService extends Service {
    private Integer location;
    private MediaPlayer mediaPlayer;
    private Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case "com.example.tom.musicplayer.action.main":
                Log.d("debug","in 1");
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

                Intent playIntent = new Intent(this, MusicService.class);
                playIntent.setAction("com.example.tom.musicplayer.action.play");

                Intent nextIntent = new Intent(this, MusicService.class);
                nextIntent.setAction("com.example.tom.musicplayer.action.next");

                Intent killIntent = new Intent(this, MusicService.class);
                killIntent.setAction("com.example.tom.musicplayer.action.kill");


                if(notification == null) {
                    notification = new Notification.Builder(this)
                            .setContentTitle("title!")
                            .setContentText("text!")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .addAction(R.drawable.ic_skip_previous_black_36dp, "Previous",
                                    PendingIntent.getService(this, 0, previousIntent, 0))
                            .addAction(R.drawable.ic_play_arrow_black_36dp, "Play",
                                    PendingIntent.getService(this, 0, playIntent, 0))
                            .addAction(R.drawable.ic_skip_next_black_36dp, "Next",
                                    PendingIntent.getService(this, 0, nextIntent, 0))
                            .build();

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    Uri path = Uri.parse("android.resource://com.example.tom.musicplayer/raw/" + getSongName());
                    retriever.setDataSource(this,path);
                    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
                    contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
                    contentView.setTextViewText(R.id.title, title);
                    contentView.setTextViewText(R.id.text, album);
                    contentView.setImageViewResource(R.id.imageButtonPrev, R.drawable.ic_skip_previous_black_36dp);
                    contentView.setOnClickPendingIntent(R.id.imageButtonPrev,
                            PendingIntent.getService(this, 0, previousIntent, 0));
                    contentView.setImageViewResource(R.id.imageButtonStop, R.drawable.ic_play_arrow_black_36dp);
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
                    contentViewBig.setImageViewResource(R.id.imageButtonStop, R.drawable.ic_play_arrow_black_36dp);
                    contentViewBig.setOnClickPendingIntent(R.id.imageButtonStop,
                            PendingIntent.getService(this, 0, playIntent, 0));
                    contentViewBig.setImageViewResource(R.id.imageButtonNext, R.drawable.ic_skip_next_black_36dp);
                    contentViewBig.setOnClickPendingIntent(R.id.imageButtonNext,
                            PendingIntent.getService(this, 0, nextIntent, 0));
                    contentViewBig.setImageViewResource(R.id.imageButtonExit, R.drawable.ic_close_black_36dp);
                    contentViewBig.setOnClickPendingIntent(R.id.imageButtonExit,
                            PendingIntent.getService(this, 0, killIntent, 0));

                    notification.bigContentView = contentViewBig;
                }
                /*
                //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Uri path = Uri.parse("android.resource://com.example.tom.musicplayer/raw/" + getSongName());
                //retriever.setDataSource(this,path);

                //retriever.extractMetadata(MediaMetadataRetriever.)
                String[] projections = {MediaStore.Audio.Media.ALBUM_ID};
                Cursor cursor = this.getContentResolver().query(path,projections,null,null,null);
                cursor.close();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                cursor.moveToFirst();

                MediaStore.Audio.Albums.ALBUM_ART
                */
                mediaPlayer.start();

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
            case "com.example.tom.musicplayer.action.kill":
                mediaPlayer.stop();
                mediaPlayer.release();
                stopForeground(true);
                stopSelf();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startStopMusic(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
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
}