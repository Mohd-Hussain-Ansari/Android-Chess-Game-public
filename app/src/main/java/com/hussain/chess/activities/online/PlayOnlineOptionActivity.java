package com.hussain.chess.activities.online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hussain.chess.R;

public class PlayOnlineOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_online_option);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));

        // Set BackgroundDrawable
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable);
            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true);

            // showing title to action bar
            actionBar.setTitle(R.string.play_online);
        }


        Button btnPlayWithOther = findViewById(R.id.btnPlayWithOther);
        btnPlayWithOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {

                    new AlertDialog.Builder(PlayOnlineOptionActivity.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage(getResources().getString(R.string.internet_connection_error))
                            .setPositiveButton("OK", null).show();
                } else {

                    Intent intent = new Intent(PlayOnlineOptionActivity.this, PlayWithOtherActivity.class);
                    startActivity(intent);
                }
            }

        });

        Button btnPlayWithFriend = findViewById(R.id.btnPlayWithFriend);
        btnPlayWithFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {

                    new AlertDialog.Builder(PlayOnlineOptionActivity.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage(getResources().getString(R.string.internet_connection_error))
                            .setPositiveButton("OK", null).show();
                } else {

                    Intent intent = new Intent(PlayOnlineOptionActivity.this, PlayWithFriendOptionActivity.class);
                    startActivity(intent);
                }
            }

        });

        Button btnProfileInfo = findViewById(R.id.btnProfileInfo);
        btnProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {

                    new AlertDialog.Builder(PlayOnlineOptionActivity.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage(getResources().getString(R.string.internet_connection_error))
                            .setPositiveButton("OK", null).show();
                } else {

                    Intent intent = new Intent(PlayOnlineOptionActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }


            }
        });
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}