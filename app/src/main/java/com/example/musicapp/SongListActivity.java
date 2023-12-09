package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
public class SongListActivity extends AppCompatActivity {

    private String[] songNames = {"Nụ hôn Bisou", "À Lôi", "Đánh đổi", "Không phải gu", "Là anh", "Anh luôn như vậy",
            "id1011", "No love no life"};
    private String[] artistNames = {"Mikelodic", "Double 2T", "Obito", "HIEUTHUHAI", "No Tacgia", "Bray",
            "Duong Know", "HIEUTHUHAI"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        ListView listView = findViewById(R.id.lvSong);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getSongsNamesWithAuthors());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedSongIndex", position);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private ArrayList<String> getSongsNamesWithAuthors() {
        ArrayList<String> songsWithAuthors = new ArrayList<>();
        for (int i = 0; i < songNames.length; i++) {
            songsWithAuthors.add(songNames[i] + "\n" + artistNames[i]);
        }
        return songsWithAuthors;
    }

}