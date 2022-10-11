package com.hussain.chess.activities.offline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hussain.chess.R;
import com.hussain.chess.database.AppDatabase;
import com.hussain.chess.database.TempSavedOfflineGame;
import com.hussain.chess.database.TempSavedOfflineGameDao;

import java.util.regex.Pattern;

public class MatchSettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
   private Spinner timerSpinner;
    private  SwitchCompat switchShowAvailableMove,switchUndoRedo,switchShowLastMove,switchSound,switchChangeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getDbInstance(MatchSettingActivity.this);
        TempSavedOfflineGameDao tempSavedOfflineGameDao = db.tempSavedGameDao();
        TempSavedOfflineGame tempSavedOfflineGame = tempSavedOfflineGameDao.getById(1);
        // is temporary save game is not there
        if(tempSavedOfflineGame ==null){
            setContentView(R.layout.activity_main);
            setContentView(R.layout.activity_match_setting);

            // calling the action bar
            ActionBar actionBar = getSupportActionBar();

            // showing the back button in action bar
            if( actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(true);

                // showing title to action bar
                actionBar.setTitle(R.string.math_setting_header);

                // Define ColorDrawable object and parse color
                // using parseColor method
                // with color hash code as its parameter
                ColorDrawable colorDrawable
                        = new ColorDrawable(Color.parseColor("#0F9D58"));

                // Set BackgroundDrawable
                actionBar.setBackgroundDrawable(colorDrawable);
            }


            Point size = new Point();
            // get screen size
            getWindowManager().getDefaultDisplay().getSize(size);
            int height = size.y/10;
            findViewById(R.id.linearLayoutPlayer1).getLayoutParams().height=height;
            findViewById(R.id.linearLayoutPlayer2).getLayoutParams().height=height;
            TextView txtVs= findViewById(R.id.txtVs);
            txtVs.setTextSize(TypedValue.COMPLEX_UNIT_SP,height/10);


            timerSpinner=findViewById(R.id.timerDropdown);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.timer_menu, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            timerSpinner.setAdapter(adapter);
            timerSpinner.setOnItemSelectedListener(this);



            switchShowAvailableMove=findViewById(R.id.switchShowAvailableMove);
            switchUndoRedo=findViewById(R.id.switchUndoRedo);
            switchShowLastMove=findViewById(R.id.switchShowLastMove);
            switchSound=findViewById(R.id.switchSound);
            switchChangeSettings=findViewById(R.id.switchChangeSetting);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Resources res = getResources();

            boolean isShowAvailableMove = sharedPref.getBoolean(getString(R.string.show_valid_piece_move), res.getBoolean(R.bool.show_available_move));
            boolean isLastMove = sharedPref.getBoolean(getString(R.string.show_last_moved_piece), res.getBoolean(R.bool.show_last_move));
            boolean isSound = sharedPref.getBoolean(getString(R.string.sound), res.getBoolean(R.bool.sound));
            boolean isUndoRedoMove = sharedPref.getBoolean(getString(R.string.undo_and_redo_move), res.getBoolean(R.bool.undo_redo_move));
            boolean isChangeSettings = sharedPref.getBoolean(getString(R.string.change_settings), res.getBoolean(R.bool.change_settings));

            switchShowAvailableMove.setChecked(isShowAvailableMove);
            switchShowLastMove.setChecked(isLastMove);
            switchSound.setChecked(isSound);
            switchUndoRedo.setChecked(isUndoRedoMove);
            switchChangeSettings.setChecked(isChangeSettings);



        }
        else{
            Intent intent = new Intent(getApplicationContext(), PassAndPlayActivity.class);
            intent.putExtra("tempSavedGameID", tempSavedOfflineGame.id);
            startActivity(intent);
        }


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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout linearLayout=findViewById(R.id.linearLayoutTimerOption);
       if(timerSpinner.getSelectedItem().toString().equalsIgnoreCase("Custom")){
           linearLayout.setWeightSum(3);
       }
       else{
            linearLayout.setWeightSum(2);
           EditText editTextTimer=findViewById(R.id.editTextTimer);
           editTextTimer.setText("");
       }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onStartClick(View view) {
        TextView player1Name=findViewById(R.id.editTextPlayer1);
        TextView player2Name=findViewById(R.id.editTextPlayer2);

        Intent intent = new Intent(getApplicationContext(), PassAndPlayActivity.class);
        intent.putExtra("player1",player1Name.getText().toString());
        intent.putExtra("player2",player2Name.getText().toString());
       intent.putExtra("availableMove",switchShowAvailableMove.isChecked());
        intent.putExtra("lastMove",switchShowLastMove.isChecked());
        intent.putExtra("sound",switchSound.isChecked());
        intent.putExtra("undoRedo",switchUndoRedo.isChecked());
        intent.putExtra("changeSettings",switchChangeSettings.isChecked());

        String timerSelectedItem=timerSpinner.getSelectedItem().toString();
        if(timerSelectedItem.equalsIgnoreCase("Custom")){
            EditText editTextTimer=findViewById(R.id.editTextTimer);

            if(Pattern.matches("^([1-9]|[1-5][0-9]|60)", editTextTimer.getText().toString().trim())){
                intent.putExtra("timer", editTextTimer.getText().toString().trim());
                startActivity(intent);
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MatchSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("you can write only numbers from 1 to 60 in game timer");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("Ok", (dialog1, id) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }


        }
        else{

            intent.putExtra("timer",timerSelectedItem.split(" ")[0]);
            startActivity(intent);
        }

    }
}