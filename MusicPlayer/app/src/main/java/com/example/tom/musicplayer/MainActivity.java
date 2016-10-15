package com.example.tom.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listview);

        ArrayAdapter<String> arr = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arr.add("Cockroach King");
        arr.add("Lone Digger");
        arr.add("Non-Stop");
        listView.setAdapter(arr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,MusicIntentService.class);
                intent.putExtra("loc",position);
                startService(intent);
                startActivity(new Intent(MainActivity.this,MusicPlayer.class));
            }
        });
    }
}