package com.hussain.chess.activities.offline;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;

import com.hussain.chess.R;

public class PlayOfflineOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_play_offline_option);


        ActionBar actionBar;
        actionBar = getSupportActionBar();
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));

        // Set BackgroundDrawable
        if (actionBar != null){
            actionBar.setBackgroundDrawable(colorDrawable);
            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true);

            // showing title to action bar
            actionBar.setTitle(R.string.play_offline);
        }

        Button btnPassAndPlay=findViewById(R.id.btnPassAndPlay);
        btnPassAndPlay.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MatchSettingActivity.class);
            startActivity(intent);
        });

        Button btnComputer=findViewById(R.id.btnComputer);
        btnComputer.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ComputerSettingsActivity.class);
            startActivity(intent);
        });
        Button btnLoadGame=findViewById(R.id.btnLoadGame);
        btnLoadGame.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoadSavedGameActivity.class);
            startActivity(intent);
        });

        Button btnSetting=findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }
}