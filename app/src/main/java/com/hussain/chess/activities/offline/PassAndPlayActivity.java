package com.hussain.chess.activities.offline;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hussain.chess.Model.GameStatus;
import com.hussain.chess.activities.MainActivity;
import com.hussain.chess.database.TempSavedOfflineGameDao;
import com.hussain.chess.utils.InterfaceAdapter;
import com.hussain.chess.Model.Move;
import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Player;
import com.hussain.chess.Model.Spot;
import com.hussain.chess.R;
import com.hussain.chess.database.AppDatabase;
import com.hussain.chess.database.SavedGameDao;
import com.hussain.chess.utils.*;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PassAndPlayActivity extends AppCompatActivity {

    GridLayout gridLayout;
    int width;
    private PassAndPlay game;
    private Spot spot;
    private List<String> validAvailableMove;
    private ImageView changePieceImageView;
    private AlertDialog selectPieceDialog;
    private AlertDialog endWindowDialog;
    private ImageView selectedPieceImage;
   private ImageView[] lastMovedImageview = new ImageView[2];
    private Piece[] lastMovedPiece = new Piece[2];
    private CountDownTimer whiteCountDownTimer;
    private CountDownTimer blackCountDownTimer;
    private long whiteTimer;
    private long blackTimer;
    private boolean isShowValidAvailableMove;
    private boolean isShowLastMove;
    private boolean isSound;
    private boolean isUndoRedo;
    private boolean isSavedGame;
    private boolean isChangeSettings;
    private long timerLimit;
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Point size = new Point();
        ImageView imageView;
        Spot spot;
        Piece piece;
        // Disable dark mode on the apk
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_and_play);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));

       if( actionBar != null){
           // Set BackgroundDrawable

           actionBar.setBackgroundDrawable(colorDrawable);
           // showing title to action bar
           actionBar.setTitle(R.string.pass_and_play);

            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        TextView txtWhiteTimer = findViewById(R.id.txtWhiteTimer);
        TextView txtBlackTimer = findViewById(R.id.txtBlackTimer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("id")){
                 int id = extras.getInt("id");
                AppDatabase db = AppDatabase.getDbInstance(PassAndPlayActivity.this);

                SavedGameDao savedGameDao = db.savedGameDao();
                String json= savedGameDao.getById(id).gameObject;
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
                Gson gson = gsonBuilder.create();

                game=gson.fromJson(json,PassAndPlay.class);
                game.setSaveGameId(id);

                blackTimer=game.getBlackTimer();
                whiteTimer=game.getWhiteTimer();
                // set setting value
                isShowValidAvailableMove =game.isShowAvailableMove();
                isShowLastMove=game.isShowLastMove();
                isSound=game.isSound();
                isUndoRedo=game.isUndoRedo();
                isChangeSettings=game.isChangeSettings();
                timerLimit =game.getTimerLimit();
                for (Piece pieceKilled:
                        game.getAllKilledPiece()) {
                    addInKilledPieceBox(pieceKilled);
                }







            }
            else if(extras.containsKey("tempSavedGameID")){
                int id = extras.getInt("tempSavedGameID");
                AppDatabase db = AppDatabase.getDbInstance(PassAndPlayActivity.this);

                TempSavedOfflineGameDao tempSavedOfflineGameDao = db.tempSavedGameDao();
                String json= tempSavedOfflineGameDao.getById(id).gameObject;
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
                Gson gson = gsonBuilder.create();

                game=gson.fromJson(json,PassAndPlay.class);

                blackTimer=game.getBlackTimer();
                whiteTimer=game.getWhiteTimer();
                // set setting value
                isShowValidAvailableMove =game.isShowAvailableMove();
                isShowLastMove=game.isShowLastMove();
                isSound=game.isSound();
                isUndoRedo=game.isUndoRedo();
                isChangeSettings=game.isChangeSettings();
                timerLimit =game.getTimerLimit();
                for (Piece pieceKilled:
                        game.getAllKilledPiece()) {
                    addInKilledPieceBox(pieceKilled);
                }






            }
         else{
                // set setting value
                isShowValidAvailableMove =extras.getBoolean("availableMove");
                isShowLastMove=extras.getBoolean("lastMove");
                isSound=extras.getBoolean("sound");
                isUndoRedo=extras.getBoolean("undoRedo");
                isChangeSettings=extras.getBoolean("changeSettings");

                Player p1, p2;
                p1 = new Player(true);
                p2 = new Player(false);
                p1.name=extras.getString("player1");
                p2.name=extras.getString("player2");


                game = new PassAndPlay();
                game.initialize(p1, p2);

                TextView txtPlayer1= findViewById(R.id.txtPlayer1);
                txtPlayer1.setText(game.getPlayerName(0));
                TextView txtPlayer2= findViewById(R.id.txtPlayer2);
                txtPlayer2.setText(game.getPlayerName(1));

                String timer=extras.getString("timer");
                if(!timer.equals("None")){
                    long milliSecond=TimeUnit.MINUTES.toMillis(Long.parseLong(timer));
                    // Time is in millisecond so 10 min = 600000 I have used
                    // countdown Interval is 1sec = 1000 I have used

                    blackTimer=milliSecond;
                    whiteTimer=milliSecond;
                    timerLimit =milliSecond;
                }



            }

         // set undo redo button is clickable or not
                findViewById(R.id.btnBack).setClickable(isUndoRedo);
                findViewById(R.id.btnForWard).setClickable(isUndoRedo);

            // show current turn to the user
            showCurrentTurn();

            // get screen size
            getWindowManager().getDefaultDisplay().getSize(size);
            width = size.x;

            gridLayout = findViewById(R.id.grid_layout);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 8, width / 8);
            ImageView imgPlayer1=findViewById(R.id.imgPlayer1);
            ImageView imgPlayer2=findViewById(R.id.imgPlayer2);
            // set player image in a square box
            imgPlayer1.getLayoutParams().height=width/10;
            imgPlayer2.getLayoutParams().height=width/10;
            String cellColor="light";
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setLayoutParams(params);

                    imageView.setId(Integer.parseInt(""  + i + j));
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
                                imageView.setBackgroundResource(R.drawable.rbp);
                            } else if (pieceName.equalsIgnoreCase("Rook")) {
                                imageView.setBackgroundResource(R.drawable.rbr);
                            } else if (pieceName.equalsIgnoreCase("Knight")) {
                                imageView.setBackgroundResource(R.drawable.rbn);
                            } else if (pieceName.equalsIgnoreCase("Bishop")) {
                                imageView.setBackgroundResource(R.drawable.rbb);
                            } else if (pieceName.equalsIgnoreCase("Queen")) {
                                imageView.setBackgroundResource(R.drawable.rbq);
                            } else if (pieceName.equalsIgnoreCase("King")) {
                                imageView.setBackgroundResource(R.drawable.rbk);
                            }


                        }

                    }
                    gridLayout.addView(imageView);

                    if(j!=7){
                        if(cellColor.equalsIgnoreCase("light")){
                            cellColor="dark";
                        }
                        else{
                            cellColor="light";
                        }
                    }

                }
            }


            if(timerLimit==0){
                findViewById(R.id.txtWhiteTimer).setVisibility(View.GONE);
                findViewById(R.id.txtBlackTimer).setVisibility(View.GONE);

            }
            else{
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");

                long min = (whiteTimer / 60000) % 60;
                long sec = (whiteTimer / 1000) % 60;
                txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));

                txtBlackTimer.setText(txtWhiteTimer.getText());

                createAndOnTimer();
            }



            // is user select to show last move and is already saved game is loaded in this activity
            if(isShowLastMove && (extras.containsKey("id") || extras.containsKey("tempSavedGameID")) ){

                // show last move
                Move move=game.getLastMove();

                ImageView startPositionImageview =gridLayout.findViewById(Integer.parseInt("" + move.getStart().getX() + move.getStart().getY()));
                ImageView endPositionImageview =gridLayout.findViewById(Integer.parseInt("" + move.getEnd().getX() + move.getEnd().getY()));
                if(move.isCastlingMove()){
                    // get rook before castling imageview
                    ImageView rookBeforeCastlingImageview=gridLayout.findViewById(Integer.parseInt(move.getBeforeMovingPosition().get(1)));

                    // show king last move
                    showCastlingLastMove(startPositionImageview,0);
                    // show rook last move
                    showCastlingLastMove(rookBeforeCastlingImageview,1);
                }
                else{
                    // set spot to moving piece for showing last move
                    this.spot= move.getEnd();


                    // show last move in the board
                    showLastMove(startPositionImageview,endPositionImageview);


                    // set spot null
                    this.spot=null;
                }

            }

            if(isSound){
                MediaPlayer music;
                music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.move_self);
                music.start();
                // release  music object from the memory
                music.setOnCompletionListener(MediaPlayer::release);
            }
        }
        else{
            Intent intent = new Intent(getApplicationContext(), MatchSettingActivity.class);
            startActivity(intent);
        }


    }


    // this event will enable the back
    // function to the button on press
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // sometimes onDestroy() method is not call when activity killed
    //so  we use onPause and onResume methods to save the game  when user by mistake kill an activity.
    //  user pause activity
    protected void onPause() {

        super.onPause();
        // is user can save the game
        if(!isSavedGame && game.isUserCanSaveGame()){
            // temporary  saved  the game in database
            game.tempSaveOfflineGame(getApplicationContext(),timerLimit,blackTimer,whiteTimer, isShowValidAvailableMove,isShowLastMove,isSound,isUndoRedo,isChangeSettings);

        }




    }

    //  user resume activity
    protected void onResume() {

        super.onResume();

        AppDatabase db=AppDatabase.getDbInstance(PassAndPlayActivity.this);
        TempSavedOfflineGameDao tempSavedOfflineGameDao = db.tempSavedGameDao();
        tempSavedOfflineGameDao.deleteById(1);



    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void movePiece(View view) {
        try {

            // is game has not been end
            if (!game.isEnd()) {

                String coordinate =String.valueOf(view.getId()) ;
                int x,y;
                int firstIndex = Integer.parseInt(String.valueOf(coordinate.charAt(0)));
                if(coordinate.length()==1){
                     x = 0;
                     y = firstIndex;
                }
                else{
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

    //showing available move of a piece
    private void showValidAvailableMove(Piece movingPiece, int x, int y, ImageView imagePiece) {

        Drawable[] layers = new Drawable[2];
        if(imagePiece.getTag()=="light"){
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
        }
        else{
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
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
        // if piece is not have a current turn don't show available move
        else if (movingPiece.isWhite() != game.getCurrentTurn().isWhiteSide()) {
            spot = null;
        }
        // is user select to show available move and is piece is there
        if (isShowValidAvailableMove && spot != null) {
            //show available move of a piece

            validAvailableMove = game.getValidAvailableMove(spot);

            if (validAvailableMove != null) {
                for (String element : validAvailableMove) {
                    ImageView img =gridLayout.findViewById(Integer.parseInt(element));
                    img.setImageResource(R.drawable.circle);
                }

            } else {
                spot = null;
            }


        }
    }

    // removing available move from a piece
    private void removeAvailableMove() {
        if (selectedPieceImage != null ) {
            // remove background color for selected piece
            changePiece(spot.getPiece(), selectedPieceImage);

            if(isShowValidAvailableMove && validAvailableMove !=null){
                // removing circle image from each imageview
                for (String element : validAvailableMove) {
                    ImageView img = gridLayout.findViewById(Integer.parseInt(element));
                    img.setImageResource(0);
                }

                validAvailableMove = null;
            }




        }
    }


    // changing piece position
    @RequiresApi(api = Build.VERSION_CODES.M)
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
                    // is user select to show last move
                    if(isShowLastMove){
                        // show last move
                        showCastlingLastMove(beforeImage,i);
                    }
                    else{
                        beforeImage.setBackground(null);
                    }
                }


            }
            else if (move.isRevivingMove()) {

                changePieceImageView = (ImageView) view;
                ImageView imageView;
                AlertDialog.Builder dialog = new AlertDialog.Builder(PassAndPlayActivity.this);

                LinearLayout linearLayout = new LinearLayout(PassAndPlayActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);

                // layout param for setting width and height in imageview
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(width / 8, width / 8);

                if (move.getEnd().getPiece().isWhite()) {

                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setLayoutParams(params1);
                    imageView.setImageResource(R.drawable.wq);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("queen");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setImageResource(R.drawable.wn);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("knight");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setImageResource(R.drawable.wr);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("rook");
                    linearLayout.addView(imageView);


                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setImageResource(R.drawable.wb);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("bishop");
                    linearLayout.addView(imageView);
                }
                else {
                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setLayoutParams(params1);
                    imageView.setImageResource(R.drawable.rbq);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("queen");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setImageResource(R.drawable.rbn);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("knight");
                    linearLayout.addView(imageView);

                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setImageResource(R.drawable.rbr);
                    imageView.setLayoutParams(params1);
                    imageView.setOnClickListener(this::revivePiece);
                    imageView.setTag("rook");
                    linearLayout.addView(imageView);


                    imageView = new ImageView(PassAndPlayActivity.this);
                    imageView.setImageResource(R.drawable.rbb);
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
            }
            else {
                // remove previous move background
                removeLastMove();
                if (move.isEnpersandMove()) {
                    List<String> beforeMovingPosition = move.getBeforeMovingPosition();
                    List<String> afterMovingPosition = move.getAfterMovingPosition();
                    gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(0))).setBackgroundResource(0);

                    beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(1)));
                    afterImage = gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(1)));


                }
                else {
                    beforeImage =gridLayout.findViewById(Integer.parseInt("" + spot.getX() + spot.getY()));
                    afterImage = (ImageView) view;


                }

                afterImage.setBackground(beforeImage.getBackground());
                // is user select to show last move
                if(isShowLastMove){

                    // show last move
                    showLastMove(beforeImage,afterImage);
                }

                else{
                     beforeImage.setBackground(null);
                }
            }

            Piece killedPiece=move.getPieceKilled();
                if(killedPiece!=null){
                    addInKilledPieceBox(killedPiece);
                    if(isSound){
                        MediaPlayer music;
                        music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.capture);
                        music.start();
                        music.setOnCompletionListener(MediaPlayer::release);
                    }

                }
                else if(isSound){
                        MediaPlayer music;
                    if(game.isCheck()){
                        music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.capture);
                    }
                    else {
                        music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.move_self);
                    }
                        music.start();
                    // release  music object from the memory
                    music.setOnCompletionListener(MediaPlayer::release);
                    }





            if (game.getGameStatus() != GameStatus.ACTIVE) {
              game.deleteSavedGame(PassAndPlayActivity.this);
                // is user select to show timer
                  if(!(whiteTimer==0 && blackTimer==0)){
                    whiteCountDownTimer.cancel();
                    blackCountDownTimer.cancel();
                }

                flag=false;
                 endWindow();
            }

            // show current turn to the user
            showCurrentTurn();


        }
        if (flag) {
             if(blackTimer!=0 && whiteTimer!=0){
                createAndOnTimer();
            }

            spot = null;

        }





    }

    private void selectRevivingPieceDismiss(DialogInterface dialogInterface) {
        if (spot != null) {
            game.undoRevivingMove();
            spot = null;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void endWindow() {

        int id= game.getSaveGameId();
        if(id!=0){
            AppDatabase db=AppDatabase.getDbInstance(PassAndPlayActivity.this);
            SavedGameDao savedGameDao = db.savedGameDao();
            savedGameDao.deleteById(id);
        }
        if(!isUndoRedo){
            findViewById(R.id.btnBack).setClickable(true);
            findViewById(R.id.btnForWard).setClickable(true);
            isUndoRedo=true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(PassAndPlayActivity.this);
        GameStatus status = game.getGameStatus();
        String winPlayer = null;
        if (status == GameStatus.WHITE_WINS) {
            winPlayer = game.getPlayerName(0);
            builder.setTitle("White Wins");
        } else if (status == GameStatus.BLACK_WINS) {
            winPlayer = game.getPlayerName(1);
            builder.setTitle("Black Wins");
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
        LinearLayout linearLayout = new LinearLayout(PassAndPlayActivity.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        Button btnResetBoard = new Button(PassAndPlayActivity.this);
        btnResetBoard.setText(R.string.reset_board);
        Button btnMainMenu = new Button(PassAndPlayActivity.this);
        btnMainMenu.setText(R.string.main_menu);


        linearLayout.addView(btnResetBoard);

        linearLayout.addView(btnMainMenu);
        builder.setView(linearLayout);

        endWindowDialog = builder.create();
        endWindowDialog.show();

        btnResetBoard.setOnClickListener(this::resetBoard);

        btnMainMenu.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(), PlayOfflineOptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            PassAndPlayActivity.this.finish();
        });

        if(isSound){
            MediaPlayer music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.notify);
            music.start();
            music.setOnCompletionListener(MediaPlayer::release);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void resetBoard(View view) {
        this.spot = null;
        gridLayout.removeAllViewsInLayout();
        Player p1, p2;
        p1 =game.getPlayer(0);
        p2 = game.getPlayer(1);
        game = new PassAndPlay();


        game.initialize(p1, p2);

        ImageView imageView;
        Piece piece;
        Spot spot;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 8, width / 8);
        String cellColor="light";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                imageView = new ImageView(PassAndPlayActivity.this);
                imageView.setLayoutParams(params);
                imageView.setId(Integer.parseInt(""  + i + j));
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
                            imageView.setBackgroundResource(R.drawable.rbp);
                        } else if (pieceName.equalsIgnoreCase("Rook")) {
                            imageView.setBackgroundResource(R.drawable.rbr);
                        } else if (pieceName.equalsIgnoreCase("Knight")) {
                            imageView.setBackgroundResource(R.drawable.rbn);
                        } else if (pieceName.equalsIgnoreCase("Bishop")) {
                            imageView.setBackgroundResource(R.drawable.rbb);
                        } else if (pieceName.equalsIgnoreCase("Queen")) {
                            imageView.setBackgroundResource(R.drawable.rbq);
                        } else if (pieceName.equalsIgnoreCase("King")) {
                            imageView.setBackgroundResource(R.drawable.rbk);
                        }


                    }

                }
                gridLayout.addView(imageView);

                if(j!=7){
                    if(cellColor.equalsIgnoreCase("light")){
                        cellColor="dark";
                    }
                    else{
                        cellColor="light";
                    }
                }

            }
        }


        LinearLayout blackKilledPiece1=findViewById(R.id.blackKilledPiece1);
        LinearLayout blackKilledPiece2=findViewById(R.id.blackKilledPiece2);
        LinearLayout whiteKilledPiece1=findViewById(R.id.whiteKilledPiece1);
        LinearLayout whiteKilledPiece2=findViewById(R.id.whiteKilledPiece2);

        blackKilledPiece1.removeAllViews();
        blackKilledPiece2.removeAllViews();
        whiteKilledPiece1.removeAllViews();
        whiteKilledPiece2.removeAllViews();

        if(timerLimit!=0){
            TextView txtWhiteTimer = findViewById(R.id.txtWhiteTimer);
            TextView txtBlackTimer = findViewById(R.id.txtBlackTimer);

            // Used for formatting digit to be in 2 digits only
            NumberFormat f = new DecimalFormat("00");

            long min = (timerLimit / 60000) % 60;
            long sec = (timerLimit / 1000) % 60;
            txtWhiteTimer.setText(f.format(min) + ":" + f.format(sec));

            txtBlackTimer.setText(txtWhiteTimer.getText());
            blackTimer=timerLimit;
            whiteTimer=timerLimit;
            createAndOnTimer();
        }

        endWindowDialog.dismiss();
        if(isSound){
            MediaPlayer music;
            music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.move_self);
            music.start();
            music.setOnCompletionListener(MediaPlayer::release);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void  onOptionClick(View view){
        showOptions();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PassAndPlayActivity.this);
        LinearLayout linearLayout = new LinearLayout(PassAndPlayActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Button btnSaveGame = new Button(PassAndPlayActivity.this);
        btnSaveGame.setText(R.string.save_game);

        Button btnResign = new Button(PassAndPlayActivity.this);
        btnResign.setText(R.string.resign);

        LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1.5f);
        params1.setMargins(50,0,50,0);

        LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,.5f);
        params2.setMargins(50,0,50,0);


        linearLayout.addView(btnSaveGame);
        linearLayout.addView(btnResign);

        if(isChangeSettings){
            //undoRedoMoveLayout
            LinearLayout undoRedoMoveLayout=new LinearLayout(PassAndPlayActivity.this);
            undoRedoMoveLayout.setOrientation(LinearLayout.HORIZONTAL);
            undoRedoMoveLayout.setWeightSum(2);

            TextView txtUndoRedoMove = new TextView(PassAndPlayActivity.this);
            txtUndoRedoMove.setText(R.string.undo_and_redo_move);
            txtUndoRedoMove.setLayoutParams(params1);

            SwitchCompat switchUndoRedoMove=new SwitchCompat(PassAndPlayActivity.this);
            switchUndoRedoMove.setChecked(isUndoRedo);
            switchUndoRedoMove.setLayoutParams(params2);

            undoRedoMoveLayout.addView(txtUndoRedoMove);
            undoRedoMoveLayout.addView(switchUndoRedoMove);

            //showAvailableMoveLayout
            LinearLayout showAvailableMoveLayout=new LinearLayout(PassAndPlayActivity.this);
            showAvailableMoveLayout.setOrientation(LinearLayout.HORIZONTAL);
            showAvailableMoveLayout.setWeightSum(2);

            TextView txtAvailableMove = new TextView(PassAndPlayActivity.this);
            txtAvailableMove.setText(R.string.show_valid_piece_move);
            txtAvailableMove.setLayoutParams(params1);

            SwitchCompat switchAvailableMove=new SwitchCompat(PassAndPlayActivity.this);
            switchAvailableMove.setChecked(isShowValidAvailableMove);
            switchAvailableMove.setLayoutParams(params2);

            showAvailableMoveLayout.addView(txtAvailableMove);
            showAvailableMoveLayout.addView(switchAvailableMove);

            //showLastMoveLayout
            LinearLayout showLastMoveLayout=new LinearLayout(PassAndPlayActivity.this);
            showLastMoveLayout.setOrientation(LinearLayout.HORIZONTAL);
            showLastMoveLayout.setWeightSum(2);

            TextView txtLastMove = new TextView(PassAndPlayActivity.this);
            txtLastMove.setText(R.string.show_last_moved_piece);
            txtLastMove.setLayoutParams(params1);

            SwitchCompat switchLastMove=new SwitchCompat(PassAndPlayActivity.this);
            switchLastMove.setChecked(isShowLastMove);
            switchLastMove.setLayoutParams(params2);

            showLastMoveLayout.addView(txtLastMove);
            showLastMoveLayout.addView(switchLastMove);

            linearLayout.addView(undoRedoMoveLayout);
            linearLayout.addView(showAvailableMoveLayout);
            linearLayout.addView(showLastMoveLayout);

            switchUndoRedoMove.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isUndoRedo=isChecked;

                findViewById(R.id.btnBack).setClickable(isUndoRedo);
                findViewById(R.id.btnForWard).setClickable(isUndoRedo);
            });

            switchAvailableMove.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if(!isChecked){
                    removeAvailableMove();
                }
                isShowValidAvailableMove =isChecked;
            });

            switchLastMove.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isShowLastMove=isChecked;
                if(isShowLastMove){
                    // show last move
                    Move move=game.getLastMove();

                    ImageView beforeImage;
                    ImageView afterImage;

                    if (move.isCastlingMove()) {
                        List<String> beforeMovingPosition = move.getBeforeMovingPosition();
                        for (int i = 0; i < 2; i++) {
                            beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(i)));
                            if(beforeImage.getTag()=="light"){
                                beforeImage.setBackgroundResource(R.color.light_yellow);
                            }
                            else{
                                beforeImage.setBackgroundResource(R.color.dark_green);
                            }

                            lastMovedImageview[0]=beforeImage;
                            lastMovedImageview[1]=null;

                        }


                    }
                    else {
                        if (move.isEnpersandMove()) {
                            List<String> beforeMovingPosition = move.getBeforeMovingPosition();
                            List<String> afterMovingPosition = move.getAfterMovingPosition();
                            gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(0))).setBackgroundResource(0);

                            beforeImage = gridLayout.findViewById(Integer.parseInt(beforeMovingPosition.get(1)));
                            afterImage = gridLayout.findViewById(Integer.parseInt(afterMovingPosition.get(1)));


                        }
                        else {
                            Spot start=move.getStart();
                            Spot end=move.getEnd();
                            beforeImage =gridLayout.findViewById(Integer.parseInt("" + start.getX() + start.getY()));
                            afterImage =gridLayout.findViewById(Integer.parseInt("" + end.getX() + end.getY()));


                        }

                        if(beforeImage.getTag()=="light"){
                            beforeImage.setBackgroundResource(R.color.light_yellow);
                        }
                        else{
                            beforeImage.setBackgroundResource(R.color.dark_green);
                        }
                        // last move background
                         Drawable[] layers = new Drawable[2];
                        if(afterImage.getTag()=="light"){
                            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
                        }
                        else{
                            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
                        }
                        layers[1] = afterImage.getBackground();
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                        afterImage.setBackground(layerDrawable);

                        lastMovedImageview[0]=beforeImage;
                        lastMovedImageview[1]=afterImage;
                        lastMovedPiece[0]=null;
                        lastMovedPiece[0]=move.getPieceMoved();



                    }
                }
                else{
                    removeLastMove();
                }


            });
        }

        //soundLayout
        LinearLayout soundLayout=new LinearLayout(PassAndPlayActivity.this);
        soundLayout.setOrientation(LinearLayout.HORIZONTAL);
        soundLayout.setWeightSum(2);

        TextView txtSound = new TextView(PassAndPlayActivity.this);
        txtSound.setText(R.string.sound);
        txtSound.setLayoutParams(params1);

        SwitchCompat switchSound=new SwitchCompat(PassAndPlayActivity.this);
        switchSound.setChecked(isSound);
        switchSound.setLayoutParams(params2);

        soundLayout.addView(txtSound);
        soundLayout.addView(switchSound);



        linearLayout.addView(soundLayout);
        builder.setView(linearLayout);

        AlertDialog dialog = builder.create();
        dialog.show();



        btnSaveGame.setOnClickListener(v -> {
            if(game.isUserCanSaveGame()){

                game.saveGame(getApplicationContext(),timerLimit,blackTimer,whiteTimer, isShowValidAvailableMove,isShowLastMove,isSound,isUndoRedo,isChangeSettings);
                isSavedGame=true;
                Intent intent = new Intent(getApplicationContext(),PlayOfflineOptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                PassAndPlayActivity.this.finish();
            }
            else{
                Toast.makeText(PassAndPlayActivity.this, "You can't save game until you make 3 moves", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();

        });

        btnResign.setOnClickListener(v -> {
            game.setCurrentPlayerResign();
            if(blackCountDownTimer!=null){
                blackCountDownTimer.cancel();
            }
            if(whiteCountDownTimer!=null){
                whiteCountDownTimer.cancel();
            }

            endWindow();
            dialog.dismiss();
        });



        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> isSound=isChecked);
    }




    //  user pressed back button
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public  void onBackPressed(){
        if(game.isUserCanSaveGame()){
            showOptions();
        }
        else{
            this.finish();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void revivePiece(View view) {
        // remove previous move background
        removeLastMove();
        ImageView imageView = (ImageView) view;
        String tag = imageView.getTag().toString();
        String coordinate =String.valueOf(changePieceImageView.getId()) ;
        int x,y;
        int firstIndex = Integer.parseInt(String.valueOf(coordinate.charAt(0)));
        if(coordinate.length()==1){
            x = 0;
            y = firstIndex;
        }
        else{
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
                changePieceImageView.setBackgroundResource(R.drawable.rbq);
                piece = new Queen(false);
            } else if (tag.equalsIgnoreCase("rook")) {
                changePieceImageView.setBackgroundResource(R.drawable.rbr);
                piece = new Rook(false);
            } else if (tag.equalsIgnoreCase("knight")) {
                changePieceImageView.setBackgroundResource(R.drawable.rbn);
                piece = new Knight(false);
            } else {
                changePieceImageView.setBackgroundResource(R.drawable.rbb);
                piece = new Bishop(false);
            }
        }
        // is user select to show last move
        if(isShowLastMove){
            // last move background

            showRevivingLastMove(piece);
        }
        else{
             gridLayout.findViewById(Integer.parseInt(""  + spot.getX() + spot.getY())).setBackground(null);

        }

        spot = null;
        selectPieceDialog.dismiss();

        if(game.isEnd()){
            endWindow();

        }

        else if(blackTimer!=0 && whiteTimer!=0){
            createAndOnTimer();
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
                    imageView.setBackgroundResource(R.drawable.rbr);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("knight")) {
                    imageView.setBackgroundResource(R.drawable.rbn);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("bishop")) {
                    imageView.setBackgroundResource(R.drawable.rbb);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("queen")) {
                    imageView.setBackgroundResource(R.drawable.rbq);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("king")) {
                    imageView.setBackgroundResource(R.drawable.rbk);
                } else if (piece.getClass().getSimpleName().equalsIgnoreCase("pawn")) {
                    imageView.setBackgroundResource(R.drawable.rbp);
                }
            }
        }
        else {
                imageView.setBackgroundResource(0);
        }

    }

    private void createAndOnTimer(){
        TextView txtBlackTimer=findViewById(R.id.txtBlackTimer);
        TextView txtWhiteTimer=findViewById(R.id.txtWhiteTimer);
        if(game.getCurrentTurn().isWhiteSide()){
            txtWhiteTimer.setBackgroundResource(R.drawable.green_border_in_black_king_imageview);
            txtBlackTimer.setBackgroundResource(R.drawable.red_border_in_white_king_imageview);
            if(blackCountDownTimer!=null){
                blackCountDownTimer.cancel();
            }
            whiteCountDownTimer=new CountDownTimer(whiteTimer, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    whiteTimer = millisUntilFinished;
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
                    endWindow();

                }
            }.start();

        }
        else{
            txtWhiteTimer.setBackgroundResource(R.drawable.red_border_in_black_king_imageview);
            txtBlackTimer.setBackgroundResource(R.drawable.green_border_in_white_king_imageview);
            if(whiteCountDownTimer!=null){
                whiteCountDownTimer.cancel();
            }
            blackCountDownTimer=new CountDownTimer(blackTimer, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    blackTimer = millisUntilFinished;
                    txtBlackTimer.setText(f.format(min) + ":" + f.format(sec));
                }
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onFinish() {
                    //txtBlackTimer.setText("00:00.0");
                    txtBlackTimer.setText("00:00");
                    game.setCurrentTurnPlayerLostByTimer();
                    endWindow();
                }
            }.start();

        }
    }

    private void addInKilledPieceBox(@NonNull Piece piece){

        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        ImageView imageView=new ImageView(this);
        imageView.setLayoutParams(layoutParams);

        LinearLayout layoutKilledPiece;
        if(piece.isWhite()){
            layoutKilledPiece=findViewById(R.id.blackKilledPiece1);
            // is layout has 8 child
            if(layoutKilledPiece.getChildCount()==8){
                layoutKilledPiece=findViewById(R.id.blackKilledPiece2);
            }
            if(piece instanceof Pawn){
                imageView.setImageResource(R.drawable.wp);
            }
            else if(piece instanceof Rook){
                imageView.setImageResource(R.drawable.wr);
            }
            else if(piece instanceof Knight){
                imageView.setImageResource(R.drawable.wn);
            }
            else if(piece instanceof Bishop){
                imageView.setImageResource(R.drawable.wb);
            }
            else if(piece instanceof Queen){
                imageView.setImageResource(R.drawable.wq);
            }
        }
        else{
            layoutKilledPiece=findViewById(R.id.whiteKilledPiece1);
            // is layout has 8 child
            if(layoutKilledPiece.getChildCount()==8){
                layoutKilledPiece=findViewById(R.id.whiteKilledPiece2);
            }
            if(piece instanceof Pawn){
                imageView.setImageResource(R.drawable.bp);
            }
            else if(piece instanceof Rook){
                imageView.setImageResource(R.drawable.br);
            }
            else if(piece instanceof Knight){
                imageView.setImageResource(R.drawable.bn);
            }
            else if(piece instanceof Bishop){
                imageView.setImageResource(R.drawable.bb);
            }
            else if(piece instanceof Queen){
                imageView.setImageResource(R.drawable.bq);
            }

        }
        layoutKilledPiece.addView(imageView);
    }

    private void removeFromKilledPieceBox(@NonNull Piece piece){

        LinearLayout layoutKilledPiece;
        if(piece.isWhite()){
            layoutKilledPiece=findViewById(R.id.blackKilledPiece1);
            // is layout has 8 child
            if(layoutKilledPiece.getChildCount()==8){
                layoutKilledPiece=findViewById(R.id.blackKilledPiece2);
            }

        }
        else{
            layoutKilledPiece=findViewById(R.id.whiteKilledPiece1);
            // is layout has 8 child
            if(layoutKilledPiece.getChildCount()==8){
                layoutKilledPiece=findViewById(R.id.whiteKilledPiece2);
            }


        }
        layoutKilledPiece.removeViewAt(layoutKilledPiece.getChildCount()-1);
    }

    private void showRevivingLastMove(Piece piece){
        ImageView imageView = gridLayout.findViewById(Integer.parseInt(""  + spot.getX() + spot.getY()));
        if(imageView.getTag()=="light"){
            imageView.setBackgroundResource(R.color.light_yellow);
        }
        else{
            imageView.setBackgroundResource(R.color.dark_green);
        }

        Drawable[] layers = new Drawable[2];
        if(changePieceImageView.getTag()=="light"){
            layers[0] =  ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
        }
        else{
            layers[0] =  ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
        }
        layers[1] = changePieceImageView.getBackground();
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        changePieceImageView.setBackground(layerDrawable);

        lastMovedImageview[0]=imageView;
        lastMovedImageview[1]=changePieceImageView;
        lastMovedPiece[0]=null;
        lastMovedPiece[1]=piece;


    }

    private void showLastMove(ImageView beforeImage,ImageView afterImage){
        if(beforeImage.getTag()=="light"){
            beforeImage.setBackgroundResource(R.color.light_yellow);
        }
        else{
            beforeImage.setBackgroundResource(R.color.dark_green);
        }


        Drawable[] layers = new Drawable[2];
        if(afterImage.getTag()=="light"){
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.light_yellow, null);
        }
        else{
            layers[0] = ResourcesCompat.getDrawable(getResources(), R.color.dark_green, null);
        }
        layers[1] = afterImage.getBackground();
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        afterImage.setBackground(layerDrawable);

        lastMovedImageview[0]=beforeImage;
        lastMovedImageview[1]=afterImage;
        lastMovedPiece[0]=null;
        lastMovedPiece[1]=spot.getPiece();


    }



    private void showCastlingLastMove(ImageView imageView,int index){
        if(imageView.getTag()=="light"){
            imageView.setBackgroundResource(R.color.light_yellow);
        }
        else{
            imageView.setBackgroundResource(R.color.dark_green);
        }

        lastMovedImageview[index]=imageView;
        lastMovedPiece[index]=null;


    }

    private void removeLastMove(){

        if (!(lastMovedImageview[0]==null && lastMovedImageview[1]==null)) {
            for (int i = 0; i < 2; i++) {
                assert lastMovedImageview[i] != null;
                changePiece(lastMovedPiece[i], lastMovedImageview[i]);
            }
            lastMovedImageview = new ImageView[2];
             lastMovedPiece = new Piece[2];
        }


    }

    public void onUndoPressed(View view) {
        // get undo move
        Move undoMove = game.undoMove();
        // is move not  null
        if(undoMove!=null){
            // remove the last played move
            removeLastMove();
            // is spot not null
            if(spot!=null){
                // remove showing available move
                removeAvailableMove();
            }

            // get start and  end  move position  imageview
            ImageView startPositionImageview=gridLayout.findViewById(Integer.parseInt("" + undoMove.getStart().getX() + undoMove.getStart().getY()));
            ImageView  endPositionImageview =gridLayout.findViewById(Integer.parseInt("" + undoMove.getEnd().getX() + undoMove.getEnd().getY()));



            // set undo moved piece to its start position
            changePiece(undoMove.getPieceMoved(),startPositionImageview);
            // is undo move is  En passant move
            if(undoMove.isEnpersandMove()){
                // set null piece at undo piece position
                changePiece(null,endPositionImageview);

            }
            else {
                // set killed piece at undo piece position
                changePiece(undoMove.getPieceKilled(),endPositionImageview);

            }
            // ia undo move is castling move
            if(undoMove.isCastlingMove()){
                // get rook position before castling
                ImageView rookBeforeCastling = gridLayout.findViewById(Integer.parseInt(undoMove.getBeforeMovingPosition().get(1)));
                // get rook position after castling
                ImageView rookAfterCastling = gridLayout.findViewById(Integer.parseInt(undoMove.getAfterMovingPosition().get(1)));
               // remove the rook from the board
                rookAfterCastling.setBackgroundResource(0);

                // get castling rookG
                Piece rook=new Rook(undoMove.getPieceMoved().isWhite());

                // show the rook on the board
                changePiece(rook,rookBeforeCastling);



            }

            // is piece has been kill in that move
            if(undoMove.getPieceKilled()!=null){
              // remove that piece from killed piece box
                removeFromKilledPieceBox(undoMove.getPieceKilled());

            }



                // get one more move to show last move or to show   En passant killed piece
                Move lastMove = game.getLastMove();
                // is last move is not null means user play any move
                if(lastMove!=null){



                        // get  end move position  imageview of the last move
                        endPositionImageview=gridLayout.findViewById(Integer.parseInt("" + lastMove.getEnd().getX() + lastMove.getEnd().getY()));

                        // is undo move is  En passant move
                        if(undoMove.isEnpersandMove()){
                            // is last piece moved is white piece
                            if(lastMove.getPieceMoved().isWhite()){
                                // show white pawn in the board
                                endPositionImageview.setBackgroundResource(R.drawable.wp);
                            }
                            //last piece moved is black piece
                            else{
                                // show black pawn in the board
                                endPositionImageview.setBackgroundResource(R.drawable.rbp);
                            }
                        }
                        // is user selected to show last move
                        if(isShowLastMove) {
                            // is last move is castling move
                            if(lastMove.isCastlingMove()){
                                // get king position before castling
                                ImageView kingBeforeCastling = gridLayout.findViewById(Integer.parseInt(lastMove.getBeforeMovingPosition().get(0)));
                                // get rook position before castling
                                ImageView rookABeforeCastling = gridLayout.findViewById(Integer.parseInt(lastMove.getBeforeMovingPosition().get(1)));

                                // show both piece last move on the board
                                showCastlingLastMove(kingBeforeCastling,0);
                                showCastlingLastMove(rookABeforeCastling,1);
                            }
                            else{
                                // get  start move position  imageview of the last move
                                startPositionImageview=gridLayout.findViewById(Integer.parseInt("" + lastMove.getStart().getX() + lastMove.getStart().getY()));

                                // assign a spot to a last move
                                spot = new Spot(lastMove.getEnd().getX(), lastMove.getEnd().getY(), lastMove.getPieceMoved());

                                // show last move to the game board
                                showLastMove(startPositionImageview, endPositionImageview);

                                // set null value to a spot
                                spot = null;
                            }

                        }


                }

            // show current turn to the user
            showCurrentTurn();

            // is none of the timer is 0 and game is not end
            if(blackTimer!=0 && whiteTimer!=0 && !game.isEnd()){
                // start timer
                createAndOnTimer();
            }

            if(isSound){
                MediaPlayer music;
            if(undoMove.getPieceKilled()==null){


                    music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.move_self);


            }
            else{


                    music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.capture);


            }
                music.start();
                // release  music object from the memory
                music.setOnCompletionListener(MediaPlayer::release);
            }

        }
    }

    public void onForwardPressed(View view) {
        // get forward move
        Move redoMove= game.redoMove();
        if(redoMove!=null){
            // remove the last played move
            removeLastMove();
            // is spot not null
            if(spot!=null){
                // remove showing available move
                removeAvailableMove();
            }

            // get start and  end  move position  imageview
            ImageView startPositionImageview=gridLayout.findViewById(Integer.parseInt("" + redoMove.getStart().getX() + redoMove.getStart().getY()));


            ImageView  endPositionImageview =gridLayout.findViewById(Integer.parseInt("" + redoMove.getEnd().getX() + redoMove.getEnd().getY()));

            // show piece on the board
            changePiece(null,startPositionImageview);
            // is move is reviving move
            if(redoMove.isRevivingMove()){
                changePiece(redoMove.getRevivingPiece(),endPositionImageview);
            }
            else {
                changePiece(redoMove.getPieceMoved(),endPositionImageview);
            }


            // is any piece kill in the redo move
            if(redoMove.getPieceKilled()!=null){
                addInKilledPieceBox(redoMove.getPieceKilled());
            }
            // is redo move is  En passant move
            if(redoMove.isEnpersandMove()){
                // get killed piece imageview
                ImageView  enPassantKilledPiece =gridLayout.findViewById(Integer.parseInt(redoMove.getBeforeMovingPosition().get(0)));

                // remove killed piece from the board
                changePiece(null,enPassantKilledPiece);


            }
            // is redo move is castling move
            else if(redoMove.isCastlingMove()){
                // get rook after castling imageview
                ImageView rookAfterCastlingImageview=gridLayout.findViewById(Integer.parseInt(redoMove.getAfterMovingPosition().get(1)));

                // create rook after castling piece
                Piece rook= new Rook(redoMove.getPieceMoved().isWhite());

                // show piece on the board
                changePiece(rook,rookAfterCastlingImageview);
            }

            // is user selected to show last move
            if(isShowLastMove){
                // is redo move is castling move
                if(redoMove.isCastlingMove()){
                    // get rook before castling imageview
                    ImageView rookBeforeCastlingImageview=gridLayout.findViewById(Integer.parseInt(redoMove.getBeforeMovingPosition().get(1)));

                    // show king last move
                    showCastlingLastMove(startPositionImageview,0);
                    // show rook last move
                    showCastlingLastMove(rookBeforeCastlingImageview,1);
                }
                else{
                    // set spot to moving piece for showing last move
                    spot= redoMove.getEnd();


                        // show last move in the board
                        showLastMove(startPositionImageview,endPositionImageview);


                    // set spot null
                    spot=null;
                }

            }

            // show current turn to the user
            showCurrentTurn();

            // is none of the timer is 0 and game is not end
            if(blackTimer!=0 && whiteTimer!=0 && !game.isEnd()){
                // start timer
                createAndOnTimer();
            }

            if(isSound){
                MediaPlayer music;
                if(redoMove.getPieceKilled()==null){


                    music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.move_self);


                }
                else{

                    music = MediaPlayer.create(PassAndPlayActivity.this, R.raw.capture);


                }
                music.start();
                music.setOnCompletionListener(MediaPlayer::release);
            }
        }

    }

    // show current turn by changing border color in player image
    public void showCurrentTurn(){
        ImageView whiteKingImage=  findViewById(R.id.imgPlayer1);
        ImageView blackKingImage=  findViewById(R.id.imgPlayer2);
        // is current turn is white side
        if(game.getCurrentTurn().isWhiteSide()){
            // set white king border green
            whiteKingImage.setBackgroundResource(R.drawable.green_border_in_white_king_imageview);
            // set black king border red
            blackKingImage.setBackgroundResource(R.drawable.red_border_in_black_king_imageview);

        }
        //current turn is black side
        else{
            // set white king border red
            whiteKingImage.setBackgroundResource(R.drawable.red_border_in_white_king_imageview);
            // set black king border green
            blackKingImage.setBackgroundResource(R.drawable.green_border_in_black_king_imageview);
        }
    }
}

