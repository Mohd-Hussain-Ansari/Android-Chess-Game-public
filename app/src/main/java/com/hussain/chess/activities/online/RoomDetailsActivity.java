package com.hussain.chess.activities.online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hussain.chess.Model.FriendGameRoom;
import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Player;
import com.hussain.chess.R;
import com.hussain.chess.utils.InterfaceAdapter;
import com.hussain.chess.utils.OnlineGame;

public class RoomDetailsActivity extends AppCompatActivity {
    FriendGameRoom gameRoom;
    private String userID = FirebaseAuth.getInstance().getUid();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private final DatabaseReference friendGameRoomRef = database.getReference("Friend Game Rooms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

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
            actionBar.setTitle(R.string.room_details);
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String roomID = extras.getString("roomID");
            String roomName = extras.getString("roomName");
            String password = extras.getString("password");

            TextView txtRoomName = findViewById(R.id.txtRoomNameValue);
            txtRoomName.setText(roomName);
            TextView txtRoomPassword = findViewById(R.id.txtPasswordValue);
            txtRoomPassword.setText(password);

            TextView txtOpponentName = findViewById(R.id.txtOpponentNameValue);

            Button btnStart = findViewById(R.id.btnStartMatch);
            btnStart.setOnClickListener(view -> {
                friendGameRoomRef.child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            gameRoom = snapshot.getValue(FriendGameRoom.class);
                            setupGame();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

            Button btnDelete = findViewById(R.id.btnDeleteRoom);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    friendGameRoomRef.child(roomID).removeValue();
                    Intent intent = new Intent(RoomDetailsActivity.this, PlayWithFriendOptionActivity.class);
                    startActivity(intent);

                }
            });

            friendGameRoomRef.child(roomID).child("player2Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        btnStart.setEnabled(false);
                    } else {
                        txtOpponentName.setText(snapshot.getValue(String.class));
                        btnStart.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }

    private void setupGame() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Setting up a game ");
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


        // set players
        Player p1, p2;


        if (Math.random() < 0.5) {
            gameRoom.setTurn(gameRoom.getPlayer1());
            p1 = new Player(true);
            p2 = new Player(false);

            gameRoom.setPlayer1White(true);
            gameRoom.setPlayer2White(false);

        } else {
            gameRoom.setTurn(gameRoom.getPlayer2());
            p2 = new Player(true);
            p1 = new Player(false);
            gameRoom.setPlayer2White(true);
            gameRoom.setPlayer1White(false);
        }


        p1.name = gameRoom.getPlayer1Name();
        p2.name = gameRoom.getPlayer2Name();
        friendGameRoomRef.child(gameRoom.getId()).child("turn").setValue(gameRoom.getTurn());
        friendGameRoomRef.child(gameRoom.getId()).child("player1White").setValue(gameRoom.isPlayer1White());
        friendGameRoomRef.child(gameRoom.getId()).child("player2White").setValue(gameRoom.isPlayer2White());

        gameRoom.setTimerStartTime(System.currentTimeMillis() / 1000);
        friendGameRoomRef.child(gameRoom.getId()).child("timerStartTime").setValue(gameRoom.getTimerStartTime());


        // creating a game object
        OnlineGame game = new OnlineGame();
        game.initialize(p1, p2);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
        Gson gson = gsonBuilder.create();

        friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

        progress.dismiss();


        Intent intent = new Intent(RoomDetailsActivity.this, PlayWithFriendActivity.class);
        intent.putExtra("ID", gameRoom.getId());
        startActivity(intent);


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