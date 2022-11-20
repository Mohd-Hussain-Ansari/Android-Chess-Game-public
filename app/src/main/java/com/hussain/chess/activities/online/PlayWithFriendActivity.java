package com.hussain.chess.activities.online;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hussain.chess.Model.GameStatus;
import com.hussain.chess.Model.Move;
import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Player;
import com.hussain.chess.Model.Spot;
import com.hussain.chess.R;
import com.hussain.chess.activities.MainActivity;
import com.hussain.chess.utils.Bishop;
import com.hussain.chess.utils.InterfaceAdapter;
import com.hussain.chess.utils.Knight;
import com.hussain.chess.utils.OnlineGame;
import com.hussain.chess.utils.Pawn;
import com.hussain.chess.utils.Queen;
import com.hussain.chess.utils.Rook;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class PlayWithFriendActivity extends AppCompatActivity {
    private final String userID = FirebaseAuth.getInstance().getUid();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FriendGameRoom gameRoom;
    private final DatabaseReference friendGameRoomRef = database.getReference("Friend Game Rooms");
    private OnlineGame game;
    private GridLayout gridLayout;
    private TextView txtWhiteTimer, txtBlackTimer;

    private CountDownTimer whiteCountDownTimer, blackCountDownTimer;

    private ImageView whiteKingImage, blackKingImage;

    private Spot spot;

    private AlertDialog endWindowDialog;

    private ImageView selectedPieceImage;


    private List<String> validAvailableMove;


    private ImageView[] lastMovedImageview = new ImageView[2];
    private Piece[] lastMovedPiece = new Piece[2];

    private ImageView[] lastPriorityMovedImageview = new ImageView[2];
    private Piece[] lastPriorityMovedPiece = new Piece[2];

    private ImageView changePieceImageView;

    private int width;


    private AlertDialog selectPieceDialog;

    private Gson gson;

    private Player player;

    private Move priorityMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_online);


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
            actionBar.setTitle(R.string.play_with_friend);

        }


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String id = extras.getString("ID");

            friendGameRoomRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    gameRoom = snapshot.getValue(FriendGameRoom.class);
                    gameRoom.setId(id);
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
                    gson = gsonBuilder.create();

                    game = gson.fromJson(gameRoom.getGame(), OnlineGame.class);
                    setGameBoard();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onOptionClick(View view) {
        showOptions();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this);
        LinearLayout linearLayout = new LinearLayout(PlayWithFriendActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Button btnDraw = null;
        if ((gameRoom.getPlayer1().equals(userID) && !gameRoom.playerDraw.get(0)) ||
                (gameRoom.getPlayer2().equals(userID) && !gameRoom.playerDraw.get(1))) {
            btnDraw = new Button(PlayWithFriendActivity.this);
            btnDraw.setText(R.string.draw);
            linearLayout.addView(btnDraw);


        }


        Button btnResign = new Button(PlayWithFriendActivity.this);
        btnResign.setText(R.string.resign);


        linearLayout.addView(btnResign);

        builder.setView(linearLayout);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (btnDraw != null) {
            btnDraw.setOnClickListener(v -> {
                // draw
                dialog.dismiss();

                if (gameRoom.getPlayer1().equals(userID)) {
                    gameRoom.isAskDraw.set(0, true);
                    gameRoom.playerDraw.set(0, true);
                } else {
                    gameRoom.isAskDraw.set(1, true);
                    gameRoom.playerDraw.set(1, true);
                }

                friendGameRoomRef.child(gameRoom.getId()).child("isAskDraw").setValue(gameRoom.isAskDraw);
                friendGameRoomRef.child(gameRoom.getId()).child("playerDraw").setValue(gameRoom.playerDraw);


            });
        }


        btnResign.setOnClickListener(v -> {


            if (userID.equals(gameRoom.getPlayer1())) {
                game.setPlayerResign(gameRoom.isPlayer1White());
            } else {
                game.setPlayerResign(gameRoom.isPlayer2White());
            }

            friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

            gameRoom.setGameStatus(game.getGameStatus());

            friendGameRoomRef.child(gameRoom.getId()).child("gameStatus").setValue(gameRoom.getGameStatus());


            endWindow();
            dialog.dismiss();
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setGameBoard() {

        try {
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Setting up a game board");
            progress.setMessage("Loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();


            if (gameRoom.getPlayer1().equals(userID)) {
                player = new Player(gameRoom.isPlayer1White());
            } else {
                player = new Player(gameRoom.isPlayer2White());
            }

            LinearLayout menuLayout = findViewById(R.id.linearLayoutMenu);
            menuLayout.setVisibility(View.VISIBLE);


            gridLayout = findViewById(R.id.grid_layout);
            gridLayout.setBackgroundResource(R.drawable.board);

            Point size = new Point();
            // get screen size
            getWindowManager().getDefaultDisplay().getSize(size);
            width = size.x;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 8, width / 8);

            ImageView imageView;
            Spot spot;
            Piece piece;

            // is game room's game  is null
            if (gameRoom.getGame() == null) {
                gameRoom.setGame(gson.toJson(game));
                friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gameRoom.getGame());
                friendGameRoomRef.child(gameRoom.getId()).child("player2Name").setValue(gameRoom.getPlayer2Name());
                friendGameRoomRef.child(gameRoom.getId()).child("player2").setValue(gameRoom.getPlayer2());


            } else {
                game = gson.fromJson(gameRoom.getGame(), OnlineGame.class);
            }


            TextView txtPlayer1, txtPlayer2;
            txtPlayer1 = findViewById(R.id.txtPlayer1);

            txtPlayer2 = findViewById(R.id.txtPlayer2);

            boolean isUserBlack = false;
            // Used for formatting digit to be in 2 digits only
            NumberFormat f = new DecimalFormat("00");
            long min, sec;
            if (gameRoom.getPlayer1().equals(userID)) {
                txtPlayer1.setText(gameRoom.getPlayer1Name());
                txtPlayer2.setText(gameRoom.getPlayer2Name());

                // is current user has black piece
                if (!gameRoom.isPlayer1White()) {


                    isUserBlack = true;

                    whiteKingImage = findViewById(R.id.imgPlayer2);
                    blackKingImage = findViewById(R.id.imgPlayer1);

                    txtWhiteTimer = findViewById(R.id.txtTimer2);
                    txtBlackTimer = findViewById(R.id.txtTimer1);


                    // show time in the board
                    min = (gameRoom.getPlayer1Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer1Timer() / 1000) % 60;
                    txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));

                    min = (gameRoom.getPlayer2Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer2Timer() / 1000) % 60;
                    txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));

                } else {
                    txtWhiteTimer = findViewById(R.id.txtTimer1);
                    txtBlackTimer = findViewById(R.id.txtTimer2);

                    whiteKingImage = findViewById(R.id.imgPlayer1);
                    blackKingImage = findViewById(R.id.imgPlayer2);


                    // show timer in the board
                    min = (gameRoom.getPlayer2Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer2Timer() / 1000) % 60;
                    txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));

                    min = (gameRoom.getPlayer1Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer1Timer() / 1000) % 60;
                    txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));
                }

            } else {
                txtPlayer1.setText(gameRoom.getPlayer2Name());
                txtPlayer2.setText(gameRoom.getPlayer1Name());
                if (!gameRoom.isPlayer2White()) {


                    isUserBlack = true;
                    whiteKingImage = findViewById(R.id.imgPlayer2);
                    blackKingImage = findViewById(R.id.imgPlayer1);


                    txtWhiteTimer = findViewById(R.id.txtTimer2);
                    txtBlackTimer = findViewById(R.id.txtTimer1);


                    // show time in the board
                    min = (gameRoom.getPlayer2Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer2Timer() / 1000) % 60;
                    txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));

                    min = (gameRoom.getPlayer1Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer1Timer() / 1000) % 60;
                    txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));
                } else {
                    txtWhiteTimer = findViewById(R.id.txtTimer1);
                    txtBlackTimer = findViewById(R.id.txtTimer2);

                    whiteKingImage = findViewById(R.id.imgPlayer1);
                    blackKingImage = findViewById(R.id.imgPlayer2);

                    // show time in the board
                    min = (gameRoom.getPlayer1Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer1Timer() / 1000) % 60;
                    txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));

                    min = (gameRoom.getPlayer2Timer() / 60000) % 60;
                    sec = (gameRoom.getPlayer2Timer() / 1000) % 60;
                    txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));
                }

            }


            whiteKingImage.setImageResource(R.drawable.wk);
            blackKingImage.setImageResource(R.drawable.bk);

            txtBlackTimer.setTextColor(Color.WHITE);
            txtWhiteTimer.setTextColor(Color.BLACK);

            if (isUserBlack) {
                String cellColor = "dark";
                for (int i = 7; i >= 0; i--) {
                    for (int j = 7; j >= 0; j--) {
                        imageView = new ImageView(PlayWithFriendActivity.this);
                        imageView.setLayoutParams(params);

                        imageView.setId(Integer.parseInt("" + i + j));
                        imageView.setTag(cellColor);
                        imageView.setOnClickListener(this::movePiece);


                        spot = game.getBoard().boxes[i][j];
                        piece = spot.getPiece();
                        if (piece != null) {
                            String pieceName = spot.getPiece().getClass().getSimpleName();

                            if (piece.isWhite()) {
                                if (pieceName.equalsIgnoreCase("Pawn")) {
                                    imageView.setBackgroundResource(R.drawable.wp);
                                } else if (pieceName.equalsIgnoreCase("Rook")) {
                                    imageView.setBackgroundResource(R.drawable.wr);
                                } else if (pieceName.equalsIgnoreCase("Knight")) {
                                    imageView.setBackgroundResource(R.drawable.wn);
                                } else if (pieceName.equalsIgnoreCase("Bishop")) {
                                    imageView.setBackgroundResource(R.drawable.wb);
                                } else if (pieceName.equalsIgnoreCase("Queen")) {
                                    imageView.setBackgroundResource(R.drawable.wq);
                                } else if (pieceName.equalsIgnoreCase("King")) {
                                    imageView.setBackgroundResource(R.drawable.wk);
                                }
                            } else {
                                if (pieceName.equalsIgnoreCase("Pawn")) {
                                    imageView.setBackgroundResource(R.drawable.bp);
                                } else if (pieceName.equalsIgnoreCase("Rook")) {
                                    imageView.setBackgroundResource(R.drawable.br);
                                } else if (pieceName.equalsIgnoreCase("Knight")) {
                                    imageView.setBackgroundResource(R.drawable.bn);
                                } else if (pieceName.equalsIgnoreCase("Bishop")) {
                                    imageView.setBackgroundResource(R.drawable.bb);
                                } else if (pieceName.equalsIgnoreCase("Queen")) {
                                    imageView.setBackgroundResource(R.drawable.bq);
                                } else if (pieceName.equalsIgnoreCase("King")) {
                                    imageView.setBackgroundResource(R.drawable.bk);
                                }


                            }

                        }


                        gridLayout.addView(imageView);

                        if (j != 7) {
                            if (cellColor.equalsIgnoreCase("light")) {
                                cellColor = "dark";
                            } else {
                                cellColor = "light";
                            }
                        }

                    }
                }
            } else {
                String cellColor = "light";
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        imageView = new ImageView(PlayWithFriendActivity.this);
                        imageView.setLayoutParams(params);

                        imageView.setId(Integer.parseInt("" + i + j));
                        imageView.setTag(cellColor);
                        imageView.setOnClickListener(this::movePiece);


                        spot = game.getBoard().boxes[i][j];
                        piece = spot.getPiece();
                        if (piece != null) {
                            String pieceName = spot.getPiece().getClass().getSimpleName();

                            if (piece.isWhite()) {
                                if (pieceName.equalsIgnoreCase("Pawn")) {
                                    imageView.setBackgroundResource(R.drawable.wp);
                                } else if (pieceName.equalsIgnoreCase("Rook")) {
                                    imageView.setBackgroundResource(R.drawable.wr);
                                } else if (pieceName.equalsIgnoreCase("Knight")) {
                                    imageView.setBackgroundResource(R.drawable.wn);
                                } else if (pieceName.equalsIgnoreCase("Bishop")) {
                                    imageView.setBackgroundResource(R.drawable.wb);
                                } else if (pieceName.equalsIgnoreCase("Queen")) {
                                    imageView.setBackgroundResource(R.drawable.wq);
                                } else if (pieceName.equalsIgnoreCase("King")) {
                                    imageView.setBackgroundResource(R.drawable.wk);
                                }
                            } else {
                                if (pieceName.equalsIgnoreCase("Pawn")) {
                                    imageView.setBackgroundResource(R.drawable.bp);
                                } else if (pieceName.equalsIgnoreCase("Rook")) {
                                    imageView.setBackgroundResource(R.drawable.br);
                                } else if (pieceName.equalsIgnoreCase("Knight")) {
                                    imageView.setBackgroundResource(R.drawable.bn);
                                } else if (pieceName.equalsIgnoreCase("Bishop")) {
                                    imageView.setBackgroundResource(R.drawable.bb);
                                } else if (pieceName.equalsIgnoreCase("Queen")) {
                                    imageView.setBackgroundResource(R.drawable.bq);
                                } else if (pieceName.equalsIgnoreCase("King")) {
                                    imageView.setBackgroundResource(R.drawable.bk);
                                }


                            }

                        }


                        gridLayout.addView(imageView);

                        if (j != 7) {
                            if (cellColor.equalsIgnoreCase("light")) {
                                cellColor = "dark";
                            } else {
                                cellColor = "light";
                            }
                        }

                    }
                }
            }

            // set player image in a square box
            whiteKingImage.getLayoutParams().height = width / 10;
            blackKingImage.getLayoutParams().height = width / 10;


            showCurrentTurn();

            createAndOnTimer();

            // get all piece killed
            for (Piece pieceKilled :
                    game.getAllKilledPiece()) {
                addInKilledPieceBox(pieceKilled);
            }

            progress.dismiss();


            final boolean[] flag = {false};
            friendGameRoomRef.child(gameRoom.getId()).child("turn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String turn = snapshot.getValue(String.class);
                    // error path
                    if (turn == null) {
                        friendGameRoomRef.removeEventListener(this);
                    } else if (turn.equals(userID) && flag[0]) {
                        friendGameRoomRef.child(gameRoom.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                gameRoom = snapshot.getValue(FriendGameRoom.class);
                                gameRoom.setId(snapshot.getKey());
                                game = gson.fromJson(gameRoom.getGame(), OnlineGame.class);

                                Move move = game.getLastMove();
                                if (move != null) {
                                    showMove(move);

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Move move = game.getLastMove();
                        if (move != null) {
                            ImageView beforeImg = gridLayout.findViewById(Integer.parseInt("" + move.getStart().getX() + move.getStart().getY()));
                            ImageView afterImg = gridLayout.findViewById(Integer.parseInt("" + move.getEnd().getX() + move.getEnd().getY()));
                            showLastMove(beforeImg, afterImg, move.getPieceMoved());
                        }
                        flag[0] = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            friendGameRoomRef.child(gameRoom.getId()).child("gameStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    GameStatus gameStatus = snapshot.getValue(GameStatus.class);

                    if (gameStatus == null) {
                        friendGameRoomRef.removeEventListener(this);
                    } else if (gameStatus != GameStatus.ACTIVE) {
                        friendGameRoomRef.removeEventListener(this);
                        friendGameRoomRef.child(gameRoom.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                gameRoom = snapshot.getValue(FriendGameRoom.class);
                                game = gson.fromJson(gameRoom.getGame(), OnlineGame.class);


                                endWindow();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            friendGameRoomRef.child(gameRoom.getId()).child("isAskDraw").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Boolean> isAskDraw = (List<Boolean>) snapshot.getValue();

                    ValueEventListener drawEventListener = this;

                    if (isAskDraw == null) {
                        friendGameRoomRef.removeEventListener(this);
                    } else if ((gameRoom.getPlayer1().equals(userID) && isAskDraw.get(1))) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this)
                                .setTitle(gameRoom.getPlayer2Name() + " is asking for a draw")
                                .setMessage("Do you want to draw")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        game.setDrawByAgreement();

                                        friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

                                        gameRoom.setGameStatus(game.getGameStatus());

                                        friendGameRoomRef.child(gameRoom.getId()).child("gameStatus").setValue(gameRoom.getGameStatus());

                                        friendGameRoomRef.removeEventListener(drawEventListener);
                                        endWindow();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        gameRoom.isAskDraw.set(1, false);
                                        gameRoom.playerDraw.set(1, true);

                                        friendGameRoomRef.child(gameRoom.getId()).child("isAskDraw").setValue(gameRoom.isAskDraw);

                                        friendGameRoomRef.child(gameRoom.getId()).child("playerDraw").setValue(gameRoom.playerDraw);
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();


                    } else if (gameRoom.getPlayer2().equals(userID) && isAskDraw.get(0)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this)
                                .setTitle(gameRoom.getPlayer1Name() + " is ask for a draw")
                                .setMessage("Do you want to draw")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        game.setDrawByAgreement();

                                        friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

                                        gameRoom.setGameStatus(game.getGameStatus());

                                        friendGameRoomRef.child(gameRoom.getId()).child("gameStatus").setValue(gameRoom.getGameStatus());

                                        friendGameRoomRef.removeEventListener(drawEventListener);
                                        endWindow();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        gameRoom.isAskDraw.set(0, false);
                                        gameRoom.playerDraw.set(0, true);

                                        friendGameRoomRef.child(gameRoom.getId()).child("isAskDraw").setValue(gameRoom.isAskDraw);

                                        friendGameRoomRef.child(gameRoom.getId()).child("playerDraw").setValue(gameRoom.playerDraw);
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            final boolean[] priorityFlag = {false};

            friendGameRoomRef.child(gameRoom.getId()).child("priorityMove").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String priorityMoveString = snapshot.getValue(String.class);

                    if (priorityMoveString == null) {
                        friendGameRoomRef.removeEventListener(this);
                    } else if (priorityMoveString.equals("null")) {

                        if (priorityFlag[0]) {
                            if (!gameRoom.getTurn().equals(userID)) {
                                if (!(lastPriorityMovedImageview[0] == null && lastPriorityMovedImageview[1] == null)) {
                                    removeLastMove();

                                    if (lastPriorityMovedImageview[0].getTag() == "light") {
                                        lastPriorityMovedImageview[0].setBackgroundResource(R.color.light_yellow);
                                    } else {
                                        lastPriorityMovedImageview[0].setBackgroundResource(R.color.dark_green);
                                    }


                                    Drawable[] layers = new Drawable[2];
                                    if (lastPriorityMovedImageview[1].getTag() == "light") {
                                        layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
                                    } else {
                                        layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
                                    }

                                    changePiece(lastPriorityMovedPiece[1], lastPriorityMovedImageview[1]);

                                    layers[1] = lastPriorityMovedImageview[1].getBackground();
                                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                                    lastPriorityMovedImageview[1].setBackground(layerDrawable);

                                    lastMovedImageview[0] = lastPriorityMovedImageview[0];
                                    lastMovedImageview[1] = lastPriorityMovedImageview[1];
                                    lastMovedPiece[0] = lastPriorityMovedPiece[0];
                                    lastMovedPiece[1] = lastPriorityMovedPiece[1];


                                    lastPriorityMovedImageview = new ImageView[2];
                                    lastPriorityMovedPiece = new Piece[2];
                                }

                                priorityMove = null;
                                // killed piece left
                                friendGameRoomRef.child(gameRoom.getId()).child("game").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        gameRoom.setGame(snapshot.getValue(String.class));

                                        game = gson.fromJson(gameRoom.getGame(), OnlineGame.class);

                                        Move move = game.getSecondLastMove();

                                        if (move != null) {
                                            showSecondLastMove(move);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                        } else {
                            priorityFlag[0] = true;
                        }

                    } else {
                        gameRoom.setPriorityMove(priorityMoveString);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            MediaPlayer music;
            music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.move_self);
            music.start();
            // release  music object from the memory
            music.setOnCompletionListener(MediaPlayer::release);
        } catch (Exception ex) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this);
            builder.setTitle("Error");
            builder.setMessage(ex.getMessage() + ex.getStackTrace()[0].getFileName() + ex.getStackTrace()[0].getLineNumber());
            AlertDialog dialog = builder.create();
            dialog.show();
            ex.printStackTrace();
        }


    }

    private void showSecondLastMove(Move move) {


        // get start and  end  move position  imageview
        ImageView startPositionImageview = gridLayout.findViewById(Integer.parseInt("" + move.getStart().getX() + move.getStart().getY()));


        ImageView endPositionImageview = gridLayout.findViewById(Integer.parseInt("" + move.getEnd().getX() + move.getEnd().getY()));

        // show piece on the board
        changePiece(null, startPositionImageview);
        // is move is reviving move
        if (move.isRevivingMove()) {
            changePiece(move.getRevivingPiece(), endPositionImageview);
        } else {
            changePiece(move.getPieceMoved(), endPositionImageview);
        }


        // is any piece kill in the redo move
        if (move.getPieceKilled() != null) {
            addInKilledPieceBox(move.getPieceKilled());
        }
        // is redo move is  En passant move
        if (move.isEnpersandMove()) {
            // get killed piece imageview
            ImageView enPassantKilledPiece = gridLayout.findViewById(Integer.parseInt(move.getBeforeMovingPosition().get(0)));

            // remove killed piece from the board
            changePiece(null, enPassantKilledPiece);


        }
        // is redo move is castling move
        else if (move.isCastlingMove()) {
            // get rook after castling imageview
            ImageView rookAfterCastlingImageview = gridLayout.findViewById(Integer.parseInt(move.getAfterMovingPosition().get(1)));

            // create rook after castling piece
            Piece rook = new Rook(move.getPieceMoved().isWhite());

            // show piece on the board
            changePiece(rook, rookAfterCastlingImageview);
        }


        Piece killedPiece = move.getPieceKilled();
        if (killedPiece != null) {
            addInKilledPieceBox(killedPiece);

            MediaPlayer music;
            music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
            music.start();
            music.setOnCompletionListener(MediaPlayer::release);


        } else {
            MediaPlayer music;

            if (game.isCheck()) {
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
            } else {
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.move_self);

            }
            music.start();
            // release  music object from the memory
            music.setOnCompletionListener(MediaPlayer::release);
        }
        GameStatus status = game.getGameStatus();

        if (status != GameStatus.ACTIVE) {


            endWindow();


        }


    }

    private void changePiece(Piece piece, @NonNull ImageView imageView) {
        if (piece != null) {
            if (piece.isWhite()) {
                if (piece.getClass().getSimpleName().equalsIgnoreCase("rook")) {
                    imageView.setBackgroundResource(R.drawable.wr);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("knight")) {
                    imageView.setBackgroundResource(R.drawable.wn);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("bishop")) {
                    imageView.setBackgroundResource(R.drawable.wb);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("queen")) {
                    imageView.setBackgroundResource(R.drawable.wq);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("king")) {
                    imageView.setBackgroundResource(R.drawable.wk);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("pawn")) {
                    imageView.setBackgroundResource(R.drawable.wp);
                }
            } else {

                if (piece.getClass().getSimpleName().equalsIgnoreCase("rook")) {
                    imageView.setBackgroundResource(R.drawable.br);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("knight")) {
                    imageView.setBackgroundResource(R.drawable.bn);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("bishop")) {
                    imageView.setBackgroundResource(R.drawable.bb);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("queen")) {
                    imageView.setBackgroundResource(R.drawable.bq);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("king")) {
                    imageView.setBackgroundResource(R.drawable.bk);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("pawn")) {
                    imageView.setBackgroundResource(R.drawable.bp);
                }
            }
        } else {
            imageView.setBackgroundResource(0);
        }

    }

    private void removeLastMove() {

        if (!(lastMovedImageview[0] == null && lastMovedImageview[1] == null)) {
            for (int i = 0; i < 2; i++) {
                assert lastMovedImageview[i] != null;
                changePiece(lastMovedPiece[i], lastMovedImageview[i]);
            }
            lastMovedImageview = new ImageView[2];
            lastMovedPiece = new Piece[2];
        }


    }

    private void endWindow() {

        try {
            if (whiteCountDownTimer != null) {
                whiteCountDownTimer.cancel();

            }
            if (blackCountDownTimer != null) {
                blackCountDownTimer.cancel();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this);
            GameStatus status = game.getGameStatus();
            String winPlayer = null;
            if (status == GameStatus.WHITE_WINS) {
                builder.setTitle("White Wins");
                if (gameRoom.isPlayer1White()) {
                    winPlayer = gameRoom.getPlayer1Name();
                } else {
                    winPlayer = gameRoom.getPlayer2Name();
                }

            } else if (status == GameStatus.BLACK_WINS) {
                builder.setTitle("Black Wins");
                if (gameRoom.isPlayer1White()) {
                    winPlayer = gameRoom.getPlayer2Name();
                } else {
                    winPlayer = gameRoom.getPlayer1Name();
                }
            } else {
                builder.setTitle(game.getGameStatus().toString());
                //builder.setTitle(game.getGameStatus().toString().replaceAll("_", " "));
            }
            String byStatus = game.getByStatus().toString().toLowerCase();
            if (winPlayer == null) {
                builder.setMessage("Game drawn by " + byStatus.replaceAll("_", " "));

            } else {
                builder.setMessage(winPlayer + " won by " + byStatus);
            }
            LinearLayout linearLayout = new LinearLayout(PlayWithFriendActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);

            Button btnResetBoard = new Button(PlayWithFriendActivity.this);
            btnResetBoard.setText(R.string.reset_board);
            Button btnMainMenu = new Button(PlayWithFriendActivity.this);
            btnMainMenu.setText(R.string.main_menu);


            linearLayout.addView(btnResetBoard);
            linearLayout.addView(btnMainMenu);
            builder.setView(linearLayout);

            endWindowDialog = builder.create();
            endWindowDialog.show();
            btnResetBoard.setOnClickListener(this::resetBoard);

            btnMainMenu.setOnClickListener(v -> {
                // delete game room from database
                friendGameRoomRef.child(gameRoom.getId()).removeValue();

                Intent intent = new Intent(getApplicationContext(), PlayOnlineOptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                PlayWithFriendActivity.this.finish();
            });

            friendGameRoomRef.child(gameRoom.getId()).child("rematchBy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String rematchBy = snapshot.getValue(String.class);
                    if (rematchBy != null && gameRoom.getRematchBy() == null) {
                        friendGameRoomRef.removeEventListener(this);
                        gameRoom.setRematchBy(rematchBy);
                        endWindowDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(PlayWithFriendActivity.this)
                                .setTitle("Your opponent want a rematch")
                                .setMessage("Do you want to accept it")
                                .setCancelable(false)
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        gameRoom.setRematchAccepted(true);
                                        friendGameRoomRef.child(gameRoom.getId()).child("isRematchAccepted").setValue(gameRoom.isRematchAccepted());
                                        // String id=gameRoom.getId();
                                        gameRoom = gameRoom.copy();
                                        friendGameRoomRef.child(gameRoom.getId()).setValue(gameRoom);

                                        gridLayout.removeAllViewsInLayout();
                                        LinearLayout blackKilledPiece1 = findViewById(R.id.blackKilledPiece1);
                                        LinearLayout blackKilledPiece2 = findViewById(R.id.blackKilledPiece2);
                                        LinearLayout whiteKilledPiece1 = findViewById(R.id.whiteKilledPiece1);
                                        LinearLayout whiteKilledPiece2 = findViewById(R.id.whiteKilledPiece2);

                                        blackKilledPiece1.removeAllViews();
                                        blackKilledPiece2.removeAllViews();
                                        whiteKilledPiece1.removeAllViews();
                                        whiteKilledPiece2.removeAllViews();

                                        setupGame();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        gameRoom.setRematchAccepted(false);
                                        friendGameRoomRef.child(gameRoom.getId()).child("isRematchAccepted").setValue(gameRoom.isRematchAccepted());
                                        endWindow();
                                    }
                                });
                        AlertDialog dialog = builder1.create();
                        dialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            MediaPlayer music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.notify);
            music.start();
            music.setOnCompletionListener(MediaPlayer::release);


        } catch (Exception ex) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this);
            builder.setTitle("Error");
            builder.setMessage(ex.getMessage() + ex.getStackTrace()[0].getFileName() + ex.getStackTrace()[0].getLineNumber());
            AlertDialog dialog = builder.create();
            dialog.show();
            ex.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        game = new OnlineGame();
        game.initialize(p1, p2);


        progress.dismiss();

        setGameBoard();

    }

    private void resetBoard(View view) {

        friendGameRoomRef.child(gameRoom.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlayWithFriendActivity.this)
                            .setTitle("Can't do a rematch")
                            .setMessage("Your opponent exited from the game")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    gameRoom.setRematchBy(userID);

                    friendGameRoomRef.child(gameRoom.getId()).child("rematchBy").setValue(gameRoom.getRematchBy());


                    friendGameRoomRef.child(gameRoom.getId()).child("isRematchAccepted").setValue(gameRoom.isRematchAccepted());

                    final boolean[] flag = {false};

                    friendGameRoomRef.child(gameRoom.getId()).child("isRematchAccepted").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.getValue() == null) {

                                friendGameRoomRef.child(gameRoom.getId()).removeEventListener(this);
                            } else {
                                boolean isRematchAccepted = snapshot.getValue(boolean.class);

                                if (isRematchAccepted) {
                                    endWindowDialog.dismiss();
                                    friendGameRoomRef.child(gameRoom.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            gameRoom = snapshot.getValue(FriendGameRoom.class);
                                            gameRoom.setId(snapshot.getKey());
                                            game = gson.fromJson(gameRoom.getGame(), OnlineGame.class);
                                            gridLayout.removeAllViewsInLayout();
                                            LinearLayout blackKilledPiece1 = findViewById(R.id.blackKilledPiece1);
                                            LinearLayout blackKilledPiece2 = findViewById(R.id.blackKilledPiece2);
                                            LinearLayout whiteKilledPiece1 = findViewById(R.id.whiteKilledPiece1);
                                            LinearLayout whiteKilledPiece2 = findViewById(R.id.whiteKilledPiece2);

                                            blackKilledPiece1.removeAllViews();
                                            blackKilledPiece2.removeAllViews();
                                            whiteKilledPiece1.removeAllViews();
                                            whiteKilledPiece2.removeAllViews();

                                            setGameBoard();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else if (flag[0]) {
                                    friendGameRoomRef.removeEventListener(this);
                                    endWindowDialog.dismiss();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PlayWithFriendActivity.this)
                                            .setTitle("Can't do a  rematch")
                                            .setMessage("Your opponent decline your rematch request")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.M)
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    endWindow();
                                                }
                                            });

                                    AlertDialog dialog = builder1.create();
                                    dialog.show();
                                } else {
                                    flag[0] = true;
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void showLastMove(ImageView beforeImage, ImageView afterImage, Piece piece) {
        if (beforeImage.getTag() == "light") {
            beforeImage.setBackgroundResource(R.color.light_yellow);
        } else {
            beforeImage.setBackgroundResource(R.color.dark_green);
        }


        Drawable[] layers = new Drawable[2];
        if (afterImage.getTag() == "light") {
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
        } else {
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
        }
        layers[1] = afterImage.getBackground();
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        afterImage.setBackground(layerDrawable);

        lastMovedImageview[0] = beforeImage;
        lastMovedImageview[1] = afterImage;
        lastMovedPiece[0] = null;
        lastMovedPiece[1] = piece;


    }

    private void showMove(Move move) {
        // remove the last played move
        removeLastMove();


        // get start and  end  move position  imageview
        ImageView startPositionImageview = gridLayout.findViewById(Integer.parseInt("" + move.getStart().getX() + move.getStart().getY()));


        ImageView endPositionImageview = gridLayout.findViewById(Integer.parseInt("" + move.getEnd().getX() + move.getEnd().getY()));

        // show piece on the board
        changePiece(null, startPositionImageview);
        // is move is reviving move
        if (move.isRevivingMove()) {
            changePiece(move.getRevivingPiece(), endPositionImageview);
        } else {
            changePiece(move.getPieceMoved(), endPositionImageview);
        }


        // is redo move is  En passant move
        if (move.isEnpersandMove()) {
            // get killed piece imageview
            ImageView enPassantKilledPiece = gridLayout.findViewById(Integer.parseInt(move.getBeforeMovingPosition().get(0)));

            // remove killed piece from the board
            changePiece(null, enPassantKilledPiece);


        }
        // is redo move is castling move
        else if (move.isCastlingMove()) {
            // get rook after castling imageview
            ImageView rookAfterCastlingImageview = gridLayout.findViewById(Integer.parseInt(move.getAfterMovingPosition().get(1)));

            // create rook after castling piece
            Piece rook = new Rook(move.getPieceMoved().isWhite());

            // show piece on the board
            changePiece(rook, rookAfterCastlingImageview);
        }


        // is redo move is castling move
        if (move.isCastlingMove()) {
            // get rook before castling imageview
            ImageView rookBeforeCastlingImageview = gridLayout.findViewById(Integer.parseInt(move.getBeforeMovingPosition().get(1)));

            // show king last move
            showCastlingLastMove(startPositionImageview, 0);
            // show rook last move
            showCastlingLastMove(rookBeforeCastlingImageview, 1);
        } else {


            // show last move in the board
            showLastMove(startPositionImageview, endPositionImageview, move.getEnd().getPiece());


        }

        Piece killedPiece = move.getPieceKilled();
        if (killedPiece != null) {
            addInKilledPieceBox(killedPiece);

            MediaPlayer music;
            music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
            music.start();
            music.setOnCompletionListener(MediaPlayer::release);


        } else {
            MediaPlayer music;

            if (game.isCheck()) {
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
            } else {
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.move_self);

            }
            music.start();
            // release  music object from the memory
            music.setOnCompletionListener(MediaPlayer::release);
        }
        GameStatus status = game.getGameStatus();

        if (status != GameStatus.ACTIVE) {


            endWindow();


        } else {

            // on the timer
            createAndOnTimer();

        }

        // show current turn to the user
        showCurrentTurn();


    }

    private void showCastlingLastMove(ImageView imageView, int index) {
        if (gameRoom.getTurn().equals(userID)) {
            if (imageView.getTag() == "light") {
                imageView.setBackgroundResource(R.color.light_yellow);
            } else {
                imageView.setBackgroundResource(R.color.dark_green);
            }

            lastMovedImageview[index] = imageView;
            lastMovedPiece[index] = null;
        } else {
            if (imageView.getTag() == "light") {
                imageView.setBackgroundResource(R.color.light_pink);
            } else {
                imageView.setBackgroundResource(R.color.dark_red);
            }

            lastPriorityMovedImageview[index] = imageView;
            lastPriorityMovedPiece[index] = null;
        }

    }

    private void addInKilledPieceBox(@NonNull Piece piece) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);

        LinearLayout layoutKilledPiece;


        if (piece.isWhite()) {
            layoutKilledPiece = findViewById(R.id.blackKilledPiece1);
            // is layout has 8 child
            if (layoutKilledPiece.getChildCount() == 8) {
                layoutKilledPiece = findViewById(R.id.blackKilledPiece2);
            }
            if (piece instanceof Pawn) {
                imageView.setImageResource(R.drawable.wp);
            } else if (piece instanceof Rook) {
                imageView.setImageResource(R.drawable.wr);
            } else if (piece instanceof Knight) {
                imageView.setImageResource(R.drawable.wn);
            } else if (piece instanceof Bishop) {
                imageView.setImageResource(R.drawable.wb);
            } else if (piece instanceof Queen) {
                imageView.setImageResource(R.drawable.wq);
            }
        } else {
            layoutKilledPiece = findViewById(R.id.whiteKilledPiece1);
            // is layout has 8 child
            if (layoutKilledPiece.getChildCount() == 8) {
                layoutKilledPiece = findViewById(R.id.whiteKilledPiece2);
            }
            if (piece instanceof Pawn) {
                imageView.setImageResource(R.drawable.bp);
            } else if (piece instanceof Rook) {
                imageView.setImageResource(R.drawable.br);
            } else if (piece instanceof Knight) {
                imageView.setImageResource(R.drawable.bn);
            } else if (piece instanceof Bishop) {
                imageView.setImageResource(R.drawable.bb);
            } else if (piece instanceof Queen) {
                imageView.setImageResource(R.drawable.bq);
            }

        }
        layoutKilledPiece.addView(imageView);
    }

    private void createAndOnTimer() {
        if (game.getCurrentTurn().isWhiteSide()) {
            txtWhiteTimer.setBackgroundResource(R.drawable.green_border_in_black_king_imageview);
            txtBlackTimer.setBackgroundResource(R.drawable.red_border_in_white_king_imageview);

            // Used for formatting digit to be in 2 digits only
            NumberFormat f = new DecimalFormat("00");

            long player2Timer = gameRoom.getPlayer2Timer();
            long min = (player2Timer / 60000) % 60;
            long sec = (player2Timer / 1000) % 60;
            txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));

            if (blackCountDownTimer != null) {
                blackCountDownTimer.cancel();
            }
            whiteCountDownTimer = new CountDownTimer(gameRoom.getPlayer1Timer(), 1000) {
                public void onTick(long millisUntilFinished) {
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    gameRoom.setPlayer1Timer(millisUntilFinished);
                    txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));
                   /* if(sec<20){
                    NumberFormat f1 = new DecimalFormat("0");
                        long milliSec =  (millisUntilFinished / 100) % 60;
                        txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec) +"." + f1.format(milliSec).charAt(0));
                    }
                    else{

                        txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));
                    }*/
                }

                // When the task is over it will print 00:00:00 there
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onFinish() {
                    //txtWhiteTimer.setText("00:00.0");
                    txtWhiteTimer.setText("00:00");

                    game.setCurrentTurnPlayerLostByTimer();

                    friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

                    gameRoom.setGameStatus(game.getGameStatus());

                    friendGameRoomRef.child(gameRoom.getId()).child("gameStatus").setValue(gameRoom.getGameStatus());

                    endWindow();

                }
            }.start();

        } else {
            txtWhiteTimer.setBackgroundResource(R.drawable.red_border_in_black_king_imageview);
            txtBlackTimer.setBackgroundResource(R.drawable.green_border_in_white_king_imageview);

            // Used for formatting digit to be in 2 digits only
            NumberFormat f = new DecimalFormat("00");

            long player1Timer = gameRoom.getPlayer1Timer();
            long min = (player1Timer / 60000) % 60;
            long sec = (player1Timer / 1000) % 60;
            txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));

            if (whiteCountDownTimer != null) {
                whiteCountDownTimer.cancel();
            }
            blackCountDownTimer = new CountDownTimer(gameRoom.getPlayer2Timer(), 1000) {
                public void onTick(long millisUntilFinished) {

                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;

                    gameRoom.setPlayer2Timer(millisUntilFinished);
                    txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onFinish() {
                    //txtBlackTimer.setText("00:00.0");
                    txtBlackTimer.setText("00:00");
                    game.setCurrentTurnPlayerLostByTimer();

                    friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

                    gameRoom.setGameStatus(game.getGameStatus());

                    friendGameRoomRef.child(gameRoom.getId()).child("gameStatus").setValue(gameRoom.getGameStatus());

                    endWindow();

                }
            }.start();

        }
    }

    // show current turn by changing border color in player image
    public void showCurrentTurn() {

        // is current turn is white side
        if (game.getCurrentTurn().isWhiteSide()) {
            // set white king border green
            whiteKingImage.setBackgroundResource(R.drawable.green_border_in_white_king_imageview);
            // set black king border red
            blackKingImage.setBackgroundResource(R.drawable.red_border_in_black_king_imageview);

        }
        //current turn is black side
        else {
            // set white king border red
            whiteKingImage.setBackgroundResource(R.drawable.red_border_in_white_king_imageview);
            // set black king border green
            blackKingImage.setBackgroundResource(R.drawable.green_border_in_black_king_imageview);
        }
    }

    public void movePiece(View view) {
        try {

            // is game has not been end
            if (!game.isEnd()) {

                if (gameRoom.getTurn().equals(userID)) {
                    String coordinate = String.valueOf(view.getId());
                    int x, y;
                    int firstIndex = Integer.parseInt(String.valueOf(coordinate.charAt(0)));
                    if (coordinate.length() == 1) {
                        x = 0;
                        y = firstIndex;
                    } else {
                        x = firstIndex;
                        y = Integer.parseInt(String.valueOf(coordinate.charAt(1)));
                    }


                    // int x=Integer.parseInt(view.getTag(R.string.row).toString());
                    //int y=Integer.parseInt(view.getTag(R.string.column).toString());

                    //get current tap piece
                    Piece movingPiece = game.getBoard().boxes[x][y].getPiece();
                    // is user selected current turn piece
                    if (movingPiece != null && movingPiece.isWhite() == game.getCurrentTurn().isWhiteSide()) {

                        if (spot != null) {
                            // remove previous selected piece move
                            removeAvailableMove();

                            spot = null;
                        }
                        // show all the move the piece can make
                        showValidAvailableMove(movingPiece, x, y, (ImageView) view);

                    }
                    //  user selected opposite turn piece
                    else if (spot != null) {
                        // remove previous selected piece move
                        removeAvailableMove();
                        changePosition(view, x, y);
                    }


                } else {
                    String coordinate = String.valueOf(view.getId());
                    int x, y;
                    int firstIndex = Integer.parseInt(String.valueOf(coordinate.charAt(0)));
                    if (coordinate.length() == 1) {
                        x = 0;
                        y = firstIndex;
                    } else {
                        x = firstIndex;
                        y = Integer.parseInt(String.valueOf(coordinate.charAt(1)));
                    }


                    // int x=Integer.parseInt(view.getTag(R.string.row).toString());
                    //int y=Integer.parseInt(view.getTag(R.string.column).toString());

                    //get current tap piece
                    Piece movingPiece = game.getBoard().boxes[x][y].getPiece();
                    // is user selected current turn piece
                    if (movingPiece != null && movingPiece.isWhite() != game.getCurrentTurn().isWhiteSide()) {

                        if (spot != null) {
                            // remove previous selected piece move
                            removeAvailableMove();

                            spot = null;
                        }
                        // show all the move the piece can make
                        showValidAvailableMove(movingPiece, x, y, (ImageView) view);

                    }
                    //  user selected opposite turn piece
                    else if (spot != null) {
                        // remove previous selected piece move
                        removeAvailableMove();
                        changePriorityPosition(view, x, y);
                    }
                }

            }

            //  game has  been end
            else {
                endWindow();

            }

        } catch (Exception ex) {

          /*  AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
            builder.setTitle("Error");
            builder.setMessage(ex.getMessage() + ex.getStackTrace()[0].getFileName() + ex.getStackTrace()[0].getLineNumber());
            AlertDialog dialog = builder.create();
            dialog.show();
            ex.printStackTrace();*/
        }


    }

    private void changePriorityPosition(View view, int x, int y) throws Exception {
        boolean flag = true;

        Spot startBox = game.getBoard().getBox(spot.getX(), spot.getY());
        Spot endBox = game.getBoard().getBox(x, y);
        Move move = new Move(player, startBox, endBox);

        // it player tap on a valid move position then change position
        if (game.isValidMove(move, player)) {

            if (priorityMove != null) {
                removeLastPriorityMove();
            }


            priorityMove = move;

            gameRoom.setPriorityMove(gson.toJson(priorityMove));

            friendGameRoomRef.child(gameRoom.getId()).child("priorityMove").setValue(gameRoom.getPriorityMove());


            ImageView beforeImage;
            ImageView afterImage;
            if (priorityMove.isCastlingMove()) {

                List<String> beforeMovingPosition = priorityMove.getBeforeMovingPosition();
                List<String> afterMovingPosition = priorityMove.getAfterMovingPosition();
                for (int i = 0; i < 2; i++) {
                    beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(i)));
                    afterImage = gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(i)));
                    afterImage.setBackground(beforeImage.getBackground());


                    // show last move
                    showCastlingLastMove(beforeImage, i);

                }


            } else if (priorityMove.isRevivingMove()) {

                changePieceImageView = (ImageView) view;
                ImageView imageView;
                AlertDialog.Builder dialog = new AlertDialog.Builder(PlayWithFriendActivity.this);

                LinearLayout linearLayout = new LinearLayout(PlayWithFriendActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);

                // layout param for setting width and height in imageview
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(width / 8, width / 8);

                if (priorityMove.getEnd().getPiece().isWhite()) {

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setLayoutParams(params1);
                    imageView.setImageResource(R.drawable.wq);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("queen");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.wn);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("knight");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.wr);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("rook");
                    linearLayout.addView(imageView);


                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.wb);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("bishop");
                    linearLayout.addView(imageView);
                } else {
                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setLayoutParams(params1);
                    imageView.setImageResource(R.drawable.bq);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("queen");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.bn);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("knight");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.br);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("rook");
                    linearLayout.addView(imageView);


                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.bb);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("bishop");
                    linearLayout.addView(imageView);
                }
                dialog.setView(linearLayout);
                selectPieceDialog = dialog.create();
                selectPieceDialog.show();
                selectPieceDialog.setOnCancelListener(this::selectRevivingPieceDismiss);
                Window window = selectPieceDialog.getWindow();
                window.setLayout(width / 4, width / 2 + 100);
                flag = false;
            } else {

                if (priorityMove.isEnpersandMove()) {
                    List<String> beforeMovingPosition = priorityMove.getBeforeMovingPosition();
                    List<String> afterMovingPosition = priorityMove.getAfterMovingPosition();
                    gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(0))).setBackgroundResource(0);

                    beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(1)));
                    afterImage = gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(1)));


                } else {
                    beforeImage = gridLayout.findViewById(Integer.parseInt("" + spot.getX() + spot.getY()));
                    afterImage = (ImageView) view;


                }

                afterImage.setBackground(beforeImage.getBackground());
                // show last move
                showLastMove(beforeImage, afterImage);


            }

            Piece killedPiece = priorityMove.getPieceKilled();
            if (killedPiece != null) {
                addInKilledPieceBox(killedPiece);

                MediaPlayer music;
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
                music.start();
                music.setOnCompletionListener(MediaPlayer::release);


            } else {
                MediaPlayer music;
                if (game.isCheck()) {
                    music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
                } else {
                    music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.move_self);
                }
                music.start();
                // release  music object from the memory
                music.setOnCompletionListener(MediaPlayer::release);
            }

        }
        if (flag) {

            spot = null;

        }


    }

    private void showLastMove(ImageView beforeImage, ImageView afterImage) {

        if (gameRoom.getTurn().equals(userID)) {
            if (beforeImage.getTag() == "light") {
                beforeImage.setBackgroundResource(R.color.light_yellow);
            } else {
                beforeImage.setBackgroundResource(R.color.dark_green);
            }


            Drawable[] layers = new Drawable[2];
            if (afterImage.getTag() == "light") {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
            } else {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
            }
            layers[1] = afterImage.getBackground();
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            afterImage.setBackground(layerDrawable);

            lastMovedImageview[0] = beforeImage;
            lastMovedImageview[1] = afterImage;
            lastMovedPiece[0] = null;
            lastMovedPiece[1] = spot.getPiece();
        } else {
            if (beforeImage.getTag() == "light") {
                beforeImage.setBackgroundResource(R.color.light_pink);
            } else {
                beforeImage.setBackgroundResource(R.color.dark_red);
            }


            Drawable[] layers = new Drawable[2];
            if (afterImage.getTag() == "light") {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_pink, null);
            } else {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_red, null);
            }
            layers[1] = afterImage.getBackground();
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            afterImage.setBackground(layerDrawable);

            lastPriorityMovedImageview[0] = beforeImage;
            lastPriorityMovedImageview[1] = afterImage;
            lastPriorityMovedPiece[0] = null;
            lastPriorityMovedPiece[1] = spot.getPiece();
        }


    }

    private void selectRevivingPieceDismiss(DialogInterface dialogInterface) {
        if (spot != null) {
            game.undoRevivingMove();
            spot = null;
        }


    }

    private void revivePiece(View view) {
        // remove previous move background
        removeLastMove();
        ImageView imageView = (ImageView) view;
        String tag = imageView.getTag().toString();
        String coordinate = String.valueOf(changePieceImageView.getId());
        int x, y;
        int firstIndex = Integer.parseInt(String.valueOf(coordinate.charAt(0)));
        if (coordinate.length() == 1) {
            x = 0;
            y = firstIndex;
        } else {
            x = firstIndex;
            y = Integer.parseInt(String.valueOf(coordinate.charAt(1)));
        }

        boolean isWhite = game.changePiece(x, y, tag);
        Piece piece;
        if (isWhite) {

            if (tag.equalsIgnoreCase("queen")) {
                changePieceImageView.setBackgroundResource(R.drawable.wq);
                piece = new Queen(true);
            } else if (tag.equalsIgnoreCase("rook")) {
                changePieceImageView.setBackgroundResource(R.drawable.wr);
                piece = new Rook(true);
            } else if (tag.equalsIgnoreCase("knight")) {
                changePieceImageView.setBackgroundResource(R.drawable.wn);
                piece = new Knight(true);
            } else {
                changePieceImageView.setBackgroundResource(R.drawable.wb);
                piece = new Bishop(true);
            }
        } else {
            if (tag.equalsIgnoreCase("queen")) {
                changePieceImageView.setBackgroundResource(R.drawable.bq);
                piece = new Queen(false);
            } else if (tag.equalsIgnoreCase("rook")) {
                changePieceImageView.setBackgroundResource(R.drawable.br);
                piece = new Rook(false);
            } else if (tag.equalsIgnoreCase("knight")) {
                changePieceImageView.setBackgroundResource(R.drawable.bn);
                piece = new Knight(false);
            } else {
                changePieceImageView.setBackgroundResource(R.drawable.bb);
                piece = new Bishop(false);
            }
        }
        // is user select to show last move

        // last move background

        showRevivingLastMove(piece);


        spot = null;
        selectPieceDialog.dismiss();

        DatabaseReference currentGameRoomRef = friendGameRoomRef.child(gameRoom.getId());


        long stopTime = System.currentTimeMillis() / 1000;
        long timeDifer = stopTime - gameRoom.getTimerStartTime();


        if (game.getCurrentTurn().isWhiteSide() == gameRoom.isPlayer1White()) {
            gameRoom.setTurn(gameRoom.getPlayer1());

            gameRoom.setPlayer2Timer(gameRoom.getPlayer2Timer() - timeDifer);

            currentGameRoomRef.child("player2Timer").setValue(gameRoom.getPlayer2Timer());

        } else {
            gameRoom.setTurn(gameRoom.getPlayer2());

            gameRoom.setPlayer1Timer(gameRoom.getPlayer1Timer() - timeDifer);

            currentGameRoomRef.child("player1Timer").setValue(gameRoom.getPlayer1Timer());

        }

        currentGameRoomRef.child("game").setValue(gson.toJson(game));

        currentGameRoomRef.child("turn").setValue(gameRoom.getTurn());

        gameRoom.setTimerStartTime(stopTime);
        currentGameRoomRef.child("timerStartTime").setValue(gameRoom.getTimerStartTime());

        if (game.isEnd()) {
            endWindow();

        } else {
            createAndOnTimer();
        }


    }

    private void showRevivingLastMove(Piece piece) {
        ImageView imageView = gridLayout.findViewById(Integer.parseInt("" + spot.getX() + spot.getY()));
        if (imageView.getTag() == "light") {
            imageView.setBackgroundResource(R.color.light_yellow);
        } else {
            imageView.setBackgroundResource(R.color.dark_green);
        }

        Drawable[] layers = new Drawable[2];
        if (changePieceImageView.getTag() == "light") {
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
        } else {
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
        }
        layers[1] = changePieceImageView.getBackground();
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        changePieceImageView.setBackground(layerDrawable);

        lastMovedImageview[0] = imageView;
        lastMovedImageview[1] = changePieceImageView;
        lastMovedPiece[0] = null;
        lastMovedPiece[1] = piece;


    }

    private void removeLastPriorityMove() {
        lastPriorityMovedImageview = new ImageView[2];
        lastPriorityMovedPiece = new Piece[2];

        // is spot not null
        if (spot != null) {
            // remove showing available move
            removeAvailableMove();
        }


        // get start and  end  move position  imageview
        ImageView startPositionImageview = gridLayout.findViewById(Integer.parseInt("" + priorityMove.getStart().getX() + priorityMove.getStart().getY()));
        ImageView endPositionImageview = gridLayout.findViewById(Integer.parseInt("" + priorityMove.getEnd().getX() + priorityMove.getEnd().getY()));


        // set undo moved piece to its start position
        changePiece(priorityMove.getPieceMoved(), startPositionImageview);
        // is undo move is  En passant move
        if (priorityMove.isEnpersandMove()) {
            // set null piece at undo piece position
            changePiece(null, endPositionImageview);

        } else {
            // set killed piece at undo piece position
            changePiece(priorityMove.getPieceKilled(), endPositionImageview);

        }
        // ia undo move is castling move
        if (priorityMove.isCastlingMove()) {
            // get rook position before castling
            ImageView rookBeforeCastling = gridLayout.findViewById(Integer.parseInt(priorityMove.getBeforeMovingPosition().get(1)));
            // get rook position after castling
            ImageView rookAfterCastling = gridLayout.findViewById(Integer.parseInt(priorityMove.getAfterMovingPosition().get(1)));
            // remove the rook from the board
            rookAfterCastling.setBackgroundResource(0);

            // get castling rookG
            Piece rook = new Rook(priorityMove.getPieceMoved().isWhite());

            // show the rook on the board
            changePiece(rook, rookBeforeCastling);


        }


    }

    private void changePosition(View view, int x, int y) throws Exception {
        boolean flag = true;

        // it player tap on a valid move position then change position
        if (game.playerMove(game.getCurrentTurn(), spot.getX(), spot.getY(), x, y)) {


            ImageView beforeImage;
            ImageView afterImage;
            Move move = game.getLastMove();
            if (move.isCastlingMove()) {
                // remove previous move background
                removeLastMove();
                List<String> beforeMovingPosition = move.getBeforeMovingPosition();
                List<String> afterMovingPosition = move.getAfterMovingPosition();
                for (int i = 0; i < 2; i++) {
                    beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(i)));
                    afterImage = gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(i)));
                    afterImage.setBackground(beforeImage.getBackground());


                    // show last move
                    showCastlingLastMove(beforeImage, i);

                }


            } else if (move.isRevivingMove()) {

                changePieceImageView = (ImageView) view;
                ImageView imageView;
                AlertDialog.Builder dialog = new AlertDialog.Builder(PlayWithFriendActivity.this);

                LinearLayout linearLayout = new LinearLayout(PlayWithFriendActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);

                // layout param for setting width and height in imageview
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(width / 8, width / 8);

                if (move.getEnd().getPiece().isWhite()) {

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setLayoutParams(params1);
                    imageView.setImageResource(R.drawable.wq);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("queen");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.wn);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("knight");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.wr);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("rook");
                    linearLayout.addView(imageView);


                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.wb);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("bishop");
                    linearLayout.addView(imageView);
                } else {
                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setLayoutParams(params1);
                    imageView.setImageResource(R.drawable.bq);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("queen");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.bn);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("knight");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.br);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("rook");
                    linearLayout.addView(imageView);


                    imageView = new ImageView(PlayWithFriendActivity.this);
                    imageView.setImageResource(R.drawable.bb);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("bishop");
                    linearLayout.addView(imageView);
                }
                dialog.setView(linearLayout);
                selectPieceDialog = dialog.create();
                selectPieceDialog.show();
                selectPieceDialog.setOnCancelListener(this::selectRevivingPieceDismiss);
                Window window = selectPieceDialog.getWindow();
                window.setLayout(width / 4, width / 2 + 100);
                flag = false;
            } else {
                // remove previous move background
                removeLastMove();
                if (move.isEnpersandMove()) {
                    List<String> beforeMovingPosition = move.getBeforeMovingPosition();
                    List<String> afterMovingPosition = move.getAfterMovingPosition();
                    gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(0))).setBackgroundResource(0);

                    beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(1)));
                    afterImage = gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(1)));


                } else {
                    beforeImage = gridLayout.findViewById(Integer.parseInt("" + spot.getX() + spot.getY()));
                    afterImage = (ImageView) view;


                }

                afterImage.setBackground(beforeImage.getBackground());
                // show last move
                showLastMove(beforeImage, afterImage);


            }

            Piece killedPiece = move.getPieceKilled();
            if (killedPiece != null) {
                addInKilledPieceBox(killedPiece);

                MediaPlayer music;
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
                music.start();
                music.setOnCompletionListener(MediaPlayer::release);


            } else {
                MediaPlayer music;
                if (game.isCheck()) {
                    music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
                } else {
                    music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.move_self);
                }
                music.start();
                // release  music object from the memory
                music.setOnCompletionListener(MediaPlayer::release);
            }


            if (game.getGameStatus() != GameStatus.ACTIVE) {


                flag = false;
                endWindow();
            }

            // show current turn to the user
            showCurrentTurn();

            if (!move.isRevivingMove()) {
                DatabaseReference currentGameRoomRef = friendGameRoomRef.child(gameRoom.getId());


                if (gameRoom.getPriorityMove().equals("null")) {
                    long stopTime = System.currentTimeMillis() / 1000;
                    long timeDifer = stopTime - gameRoom.getTimerStartTime();

                    if (game.getCurrentTurn().isWhiteSide() == gameRoom.isPlayer1White()) {
                        gameRoom.setTurn(gameRoom.getPlayer1());

                        gameRoom.setPlayer2Timer(gameRoom.getPlayer2Timer() - timeDifer);

                        currentGameRoomRef.child("player2Timer").setValue(gameRoom.getPlayer2Timer());

                    } else {
                        gameRoom.setTurn(gameRoom.getPlayer2());

                        gameRoom.setPlayer1Timer(gameRoom.getPlayer1Timer() - timeDifer);

                        currentGameRoomRef.child("player1Timer").setValue(gameRoom.getPlayer1Timer());


                    }


                    gameRoom.setTimerStartTime(stopTime);
                    currentGameRoomRef.child("timerStartTime").setValue(gameRoom.getTimerStartTime());

                    currentGameRoomRef.child("game").setValue(gson.toJson(game));

                    currentGameRoomRef.child("turn").setValue(gameRoom.getTurn());


                    if (flag) {
                        createAndOnTimer();

                        spot = null;

                    }


                } else {

                    Move priorityMove = gson.fromJson(gameRoom.getPriorityMove(), Move.class);

                    if (game.playerMove(game.getCurrentTurn(), priorityMove.getStart().getX(), priorityMove.getStart().getY(),
                            priorityMove.getEnd().getX(), priorityMove.getEnd().getY())) {
                        movedPriorityMove(priorityMove);


                    }
                }


            }


        }


    }

    private void movedPriorityMove(Move move) {

        if (move != null) {
            // remove the last played move
            removeLastMove();


            // get start and  end  move position  imageview
            ImageView startPositionImageview = gridLayout.findViewById(Integer.parseInt("" + move.getStart().getX() + move.getStart().getY()));


            ImageView endPositionImageview = gridLayout.findViewById(Integer.parseInt("" + move.getEnd().getX() + move.getEnd().getY()));

            // show piece on the board
            changePiece(null, startPositionImageview);
            // is move is reviving move
            if (move.isRevivingMove()) {
                changePiece(move.getRevivingPiece(), endPositionImageview);
            } else {
                changePiece(move.getPieceMoved(), endPositionImageview);
            }


            // is any piece kill in the redo move
            if (move.getPieceKilled() != null) {
                addInKilledPieceBox(move.getPieceKilled());
            }
            // is redo move is  En passant move
            if (move.isEnpersandMove()) {
                // get killed piece imageview
                ImageView enPassantKilledPiece = gridLayout.findViewById(Integer.parseInt(move.getBeforeMovingPosition().get(0)));

                // remove killed piece from the board
                changePiece(null, enPassantKilledPiece);


            }
            // is redo move is castling move
            else if (move.isCastlingMove()) {
                // get rook after castling imageview
                ImageView rookAfterCastlingImageview = gridLayout.findViewById(Integer.parseInt(move.getAfterMovingPosition().get(1)));

                // create rook after castling piece
                Piece rook = new Rook(move.getPieceMoved().isWhite());

                // show piece on the board
                changePiece(rook, rookAfterCastlingImageview);
            }


            // is redo move is castling move
            if (move.isCastlingMove()) {
                // get rook before castling imageview
                ImageView rookBeforeCastlingImageview = gridLayout.findViewById(Integer.parseInt(move.getBeforeMovingPosition().get(1)));

                // show king last move
                showCastlingLastMove(startPositionImageview, 0);
                // show rook last move
                showCastlingLastMove(rookBeforeCastlingImageview, 1);
            } else {


                // show last move in the board
                showLastMove(startPositionImageview, endPositionImageview, move.getEnd().getPiece());


            }

            Piece killedPiece = move.getPieceKilled();
            if (killedPiece != null) {
                addInKilledPieceBox(killedPiece);

                MediaPlayer music;
                music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
                music.start();
                music.setOnCompletionListener(MediaPlayer::release);


            } else {
                MediaPlayer music;

                if (game.isCheck()) {
                    music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.capture);
                } else {
                    music = MediaPlayer.create(PlayWithFriendActivity.this, R.raw.move_self);

                }
                music.start();
                // release  music object from the memory
                music.setOnCompletionListener(MediaPlayer::release);
            }
            GameStatus status = game.getGameStatus();

            if (status != GameStatus.ACTIVE) {


                endWindow();


            } else {

                // on the timer
                createAndOnTimer();

            }

            // show current turn to the user
            showCurrentTurn();

            friendGameRoomRef.child(gameRoom.getId()).child("game").setValue(gson.toJson(game));

            friendGameRoomRef.child(gameRoom.getId()).child("priorityMove").setValue("null");


        }
    }

    //showing available move of a piece
    private void showValidAvailableMove(Piece movingPiece, int x, int y, ImageView imagePiece) {

        Drawable[] layers = new Drawable[2];

        if (gameRoom.getTurn().equals(userID)) {
            if (imagePiece.getTag() == "light") {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
            } else {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
            }
        } else {
            if (imagePiece.getTag() == "light") {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_pink, null);
            } else {
                layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_red, null);
            }
        }


        layers[1] = imagePiece.getBackground();
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        imagePiece.setBackground(layerDrawable);

        selectedPieceImage = imagePiece;

        spot = new Spot(x, y, movingPiece);
        // if piece is not there don't show available move
        if (movingPiece == null) {
            spot = null;
        }
        // is user select to show available move and is piece is there
        if (spot != null) {
            //show available move of a piece

            validAvailableMove = game.getValidAvailableMove(spot);

            if (validAvailableMove != null) {
                for (String element : validAvailableMove) {
                    ImageView img = gridLayout.findViewById(Integer.parseInt(element));
                    img.setImageResource(R.drawable.circle);
                }

            } else {
                spot = null;
            }


        }
    }


    // removing available move from a piece
    private void removeAvailableMove() {
        if (selectedPieceImage != null) {
            // remove background color for selected piece
            changePiece(spot.getPiece(), selectedPieceImage);

            if (validAvailableMove != null) {
                // removing circle image from each imageview
                for (String element : validAvailableMove) {
                    ImageView img = gridLayout.findViewById(Integer.parseInt(element));
                    img.setImageResource(0);
                }

                validAvailableMove = null;
            }


        }
    }
}