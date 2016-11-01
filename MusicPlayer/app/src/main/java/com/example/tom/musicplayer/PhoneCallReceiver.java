package com.example.tom.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class PhoneCallReceiver extends BroadcastReceiver {
    public PhoneCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        Intent playIntent = new Intent(context, MusicService.class);
        playIntent.setAction("com.example.tom.musicplayer.action.call.play");

        Intent stopIntent = new Intent(context, MusicService.class);
        playIntent.setAction("com.example.tom.musicplayer.action.call.stop");

        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING) ||
           state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Toast.makeText(context,"hey 1",Toast.LENGTH_LONG).show();
            context.startService(stopIntent);
        }/*
        else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Toast.makeText(context,"hey 2",Toast.LENGTH_LONG).show();
            context.startService(playIntent);
        }*/
    }
}
