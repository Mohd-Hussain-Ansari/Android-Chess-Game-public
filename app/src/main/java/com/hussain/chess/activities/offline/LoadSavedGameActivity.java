package com.hussain.chess.activities.offline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hussain.chess.R;
import com.hussain.chess.database.AppDatabase;
import com.hussain.chess.database.SavedGame;
import com.hussain.chess.database.SavedGameDao;
import com.hussain.chess.utils.Game;
import com.hussain.chess.utils.InterfaceAdapter;
import com.hussain.chess.Model.Piece;

import java.util.List;

public class LoadSavedGameActivity extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_game);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        if( actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);

            // showing title to action bar
            actionBar.setTitle(getString(R.string.load_saved_game));

            // Define ColorDrawable object and parse color
            // using parseColor method
            // with color hash code as its parameter
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#0F9D58"));

            // Set BackgroundDrawable
            actionBar.setBackgroundDrawable(colorDrawable);
        }



        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
        Gson gson = gsonBuilder.create();
        AppDatabase db = AppDatabase.getDbInstance(LoadSavedGameActivity.this);
        SavedGameDao savedGameDao = db.savedGameDao();
        List<SavedGame> savedGames = savedGameDao.getAll();


        LinearLayout savedGameLinearLayout=findViewById(R.id.savedGameLinearLayout);

        if(savedGames.size()==0){
            ScrollView scrollView= findViewById(R.id.scrollView);
            scrollView.setFillViewport(true);
            scrollView.getLayoutParams().height= LinearLayout.LayoutParams.WRAP_CONTENT;
            TextView textView=new TextView(this);
            textView.setText(R.string.no_saved_game);
            textView.setGravity(Gravity.CENTER);
            savedGameLinearLayout.addView(textView);

        }
        else{
            Point size = new Point();
            // get screen size
            getWindowManager().getDefaultDisplay().getSize(size);
            int height = size.y/15;

            //Drawable img1 = getResources().getDrawable( android.R.drawable.ic_menu_delete );
            //Drawable img2 = getResources().getDrawable( R.drawable.bk );

            LinearLayout.LayoutParams gameNameLayoutParams = new LinearLayout.LayoutParams(0,
                    height, 9.0f);


            LinearLayout.LayoutParams gameNameChildParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(50,50,50,50);



            for (SavedGame savedGame : savedGames) {
                     Game game=gson.fromJson(savedGame.gameObject,Game.class);

                TextView txtGameName = new TextView(this);
                txtGameName.setText(game.getPlayerName(0) + " Vs " + game.getPlayerName(1));
                txtGameName.setLayoutParams(gameNameChildParams);
                txtGameName.setGravity(Gravity.CENTER);
                txtGameName.setTextColor(Color.parseColor("#FF000000"));

                ImageView whiteKing=new ImageView(LoadSavedGameActivity.this);
                whiteKing.setImageResource(R.drawable.wk);
                whiteKing.setLayoutParams(gameNameChildParams);

                ImageView blackKIng=new ImageView(LoadSavedGameActivity.this);
                blackKIng.setImageResource(R.drawable.bk);
                blackKIng.setLayoutParams(gameNameChildParams);


                // layout for showing game name
                LinearLayout gameNameLayout=new LinearLayout(LoadSavedGameActivity.this);
                gameNameLayout.setLayoutParams(gameNameLayoutParams);
                gameNameLayout.setWeightSum(3);
                gameNameLayout.setOrientation(LinearLayout.HORIZONTAL);
                gameNameLayout.setTag(savedGame.id);
                gameNameLayout.addView(whiteKing);
                gameNameLayout.addView(txtGameName);
                gameNameLayout.addView(blackKIng);
                //gameNameLayout.setBackgroundColor(R.color.white);
                gameNameLayout.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.ll_user_selector, null ));

                gameNameLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), PassAndPlayActivity.class);
                    intent.putExtra("id", (int) v.getTag());
                    startActivity(intent);
                });

                ImageButton btnDelete = new ImageButton(this);
                //btnDelete.setText("Delete");
                btnDelete.setTag(savedGame.id);
                //btnDelete.setGravity(Gravity.CENTER);
                btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
                btnDelete.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ll_user_selector, null));
                btnDelete.setLayoutParams(gameNameChildParams);
                btnDelete.setOnClickListener(v -> {
                  savedGameDao.deleteById((int) v.getTag());
                    startActivity(getIntent());
                    finish();
                });


                 LinearLayout linearLayout=new LinearLayout(LoadSavedGameActivity.this);
                 linearLayout.setLayoutParams(params);
                 linearLayout.setWeightSum(10);
                 linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                linearLayout.addView(gameNameLayout);
                //linearLayout.addView(btnGameName);
                linearLayout.addView(btnDelete);
                savedGameLinearLayout.addView(linearLayout);


            }
        }


    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}