package com.hussain.chess.activities.online;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hussain.chess.Model.FriendGameRoom;
import com.hussain.chess.R;
import com.hussain.chess.activities.MainActivity;

public class PlayWithFriendOptionActivity extends AppCompatActivity {

    FriendGameRoom gameRoom;
    private String userID = FirebaseAuth.getInstance().getUid();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private final DatabaseReference friendGameRoomRef = database.getReference("Friend Game Rooms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_friend_option);
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            // showing title to action bar
            actionBar.setTitle(R.string.play_with_friend);

            // Define ColorDrawable object and parse color
            // using parseColor method
            // with color hash code as its parameter
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#0F9D58"));

            // Set BackgroundDrawable
            actionBar.setBackgroundDrawable(colorDrawable);
        }


        Button btnCreateARoom = findViewById(R.id.btnCreateARoom);
        btnCreateARoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendOptionActivity.this)
                        .setTitle("Creating a game room")
                        .setMessage("Enter new room name and password");


                LinearLayout linearLayout = new LinearLayout(PlayWithFriendOptionActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText editTextRoomName = new EditText(PlayWithFriendOptionActivity.this);
                editTextRoomName.setHint("Room Name");

                EditText editTextPassword = new EditText(PlayWithFriendOptionActivity.this);
                editTextPassword.setHint("Password");

                Button btnSave = new Button(PlayWithFriendOptionActivity.this);
                btnSave.setText("Create Room");

                linearLayout.addView(editTextRoomName);
                linearLayout.addView(editTextPassword);
                linearLayout.addView(btnSave);

                builder.setView(linearLayout);
                AlertDialog dialog = builder.create();
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String roomName = editTextRoomName.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();

                        if (roomName.isEmpty() || password.isEmpty()) {
                            Toast.makeText(PlayWithFriendOptionActivity.this, "room name and password can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            checkConditionAndCreateRoom(roomName, password);
                        }


                    }
                });
            }
        });

        Button btnJoinARoom = findViewById(R.id.btnJoinARoom);
        btnJoinARoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendOptionActivity.this)
                        .setTitle("Joining a game room")
                        .setMessage("Enter  room name and password");


                LinearLayout linearLayout = new LinearLayout(PlayWithFriendOptionActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText editTextRoomName = new EditText(PlayWithFriendOptionActivity.this);
                editTextRoomName.setHint("Room Name");

                EditText editTextPassword = new EditText(PlayWithFriendOptionActivity.this);
                editTextPassword.setHint("Password");

                Button btnJoin = new Button(PlayWithFriendOptionActivity.this);
                btnJoin.setText("Join Room");

                linearLayout.addView(editTextRoomName);
                linearLayout.addView(editTextPassword);
                linearLayout.addView(btnJoin);

                builder.setView(linearLayout);
                AlertDialog dialog = builder.create();
                dialog.show();

                btnJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String roomName = editTextRoomName.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();

                        if (roomName.isEmpty() || password.isEmpty()) {
                            Toast.makeText(PlayWithFriendOptionActivity.this, "room name and password can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            friendGameRoomRef.orderByChild("name").equalTo(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() == null) {

                                        showInvalidCredentialDialog();


                                    } else {

                                        gameRoom = snapshot.getChildren().iterator().next().getValue(FriendGameRoom.class);
                                        if (gameRoom.getPassword().equals(password)) {
                                            if (gameRoom.getPlayer1().equals(userID)) {

                                                if (gameRoom.getPlayer2().equals("null") || gameRoom.getGame() == null) {
                                                    Intent intent = new Intent(PlayWithFriendOptionActivity.this, RoomDetailsActivity.class);
                                                    intent.putExtra("roomID", gameRoom.getId());
                                                    intent.putExtra("roomName", roomName);
                                                    intent.putExtra("password", password);
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(PlayWithFriendOptionActivity.this, PlayWithFriendActivity.class);
                                                    intent.putExtra("ID", gameRoom.getId());
                                                    startActivity(intent);
                                                }


                                            } else if (gameRoom.getPlayer2().equals(userID)) {
                                                if (gameRoom.getGame() == null) {
                                                    dialog.dismiss();
                                                    waitForRoomOwner();
                                                } else {
                                                    Intent intent = new Intent(PlayWithFriendOptionActivity.this, PlayWithFriendActivity.class);
                                                    intent.putExtra("ID", gameRoom.getId());
                                                    startActivity(intent);
                                                }

                                            } else if (gameRoom.getPlayer2().equals("null")) {
                                                gameRoom.setPlayer2(userID);

                                                database.getReference().child("Users").child(userID).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.getValue() != null) {

                                                            // setting up a player2 name
                                                            gameRoom.setPlayer2Name(snapshot.getValue(String.class));

                                                            friendGameRoomRef.child(gameRoom.getId()).child("player2Name").setValue(gameRoom.getPlayer2Name());
                                                            friendGameRoomRef.child(gameRoom.getId()).child("player2").setValue(gameRoom.getPlayer2());
                                                            dialog.dismiss();
                                                            waitForRoomOwner();


                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendOptionActivity.this)
                                                        .setTitle("Can't Join Room")
                                                        .setMessage("Room is full")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                            }
                                                        });

                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        } else {
                                            showInvalidCredentialDialog();
                                        }


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }
                });
            }

        });

    }

    private void showInvalidCredentialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendOptionActivity.this)
                .setTitle("Can't Join Room")
                .setMessage("Invalid Room name and password")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkConditionAndCreateRoom(String roomName, String password) {

        friendGameRoomRef.orderByChild("name").equalTo(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {

                    friendGameRoomRef.orderByChild("player1").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() == null) {
                                createRoomAndStartActivity(roomName, password);

                            } else {
                                gameRoom = snapshot.getValue(FriendGameRoom.class);
                                AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendOptionActivity.this)
                                        .setTitle("Can't create room")
                                        .setMessage("You had already created one room with " + gameRoom.getName() + " and password " + gameRoom.getPassword())
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendOptionActivity.this)
                            .setTitle("Can't create room")
                            .setMessage("Room with the same name already exist")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return false;

    }

    private void createRoomAndStartActivity(String roomName, String password) {
        // create a game room
        gameRoom = new FriendGameRoom();
        gameRoom.setPlayer1(userID);
        gameRoom.setName(roomName);
        gameRoom.setPassword(password);

        // setting up a player1 name
        database.getReference("Users").child(gameRoom.getPlayer1()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // is user not  exist
                if (snapshot.getValue() == null) {

                    // get user back to the main activity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);


                }
                // user  exist
                else {
                    // setting up a player1 name
                    gameRoom.setPlayer1Name(snapshot.getValue(String.class));

                    gameRoom.setId(friendGameRoomRef.push().getKey());
                    // creating a game room
                    friendGameRoomRef.child(gameRoom.getId()).setValue(gameRoom);

                    Intent intent = new Intent(PlayWithFriendOptionActivity.this, RoomDetailsActivity.class);
                    intent.putExtra("roomID", gameRoom.getId());
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("password", password);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void waitForRoomOwner() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Waiting for room owner to start the match");
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        friendGameRoomRef.child(gameRoom.getId()).child("game").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String game = snapshot.getValue(String.class);
                if (game != null && !game.equals("null")) {


                    progress.dismiss();


                    friendGameRoomRef.child(gameRoom.getId()).child("game").removeEventListener(this);
                    Intent intent = new Intent(PlayWithFriendOptionActivity.this, PlayWithFriendActivity.class);
                    intent.putExtra("ID", gameRoom.getId());
                    startActivity(intent);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}