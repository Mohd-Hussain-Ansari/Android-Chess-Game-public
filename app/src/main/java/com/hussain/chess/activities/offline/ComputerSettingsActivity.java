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
import com.hussain.chess.database.TempSavedComputerGame;
import com.hussain.chess.database.TempSavedComputerGameDao;

import java.util.regex.Pattern;

public class ComputerSettingsActivity extends AppCompatActivity {
    private Spinner timerSpinner, selectPieceSpinner;
    private SwitchCompat switchShowAvailableMove,switchUndoRedo,switchShowLastMove,switchSound,switchChangeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getDbInstance(ComputerSettingsActivity.this);
        TempSavedComputerGameDao tempSavedComputerGameDao = db.tempSavedComputerGameDao();
        TempSavedComputerGame tempSavedComputerGame = tempSavedComputerGameDao.getById(1);
        // is temporary save game is not there
        if(tempSavedComputerGame==null){
            setContentView(R.layout.activity_computer_settings);
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
            findViewById(R.id.linearLayoutPlayer1).getLayoutParams().height= size.y/10;

            selectPieceSpinner =findViewById(R.id.spinnerSelectPiece);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapterSelectPiece = ArrayAdapter.createFromResource(this,
                    R.array.select_piece_menu, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapterSelectPiece.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            selectPieceSpinner.setAdapter(adapterSelectPiece);


            timerSpinner=findViewById(R.id.timerDropdown);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapterTimerSpinner = ArrayAdapter.createFromResource(this,
                    R.array.timer_menu, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapterTimerSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            timerSpinner.setAdapter(adapterTimerSpinner);
            timerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


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
            });


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

        else {
            Intent intent = new Intent(getApplicationContext(), ComputerGameActivity.class);
            intent.putExtra("tempSavedGameID", tempSavedComputerGame.id);
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




    public void onStartClick(View view) {
        TextView playerName=findViewById(R.id.editTextPlayer);


        Intent intent = new Intent(getApplicationContext(),ComputerGameActivity.class);

        intent.putExtra("playerName",playerName.getText().toString());
        intent.putExtra("availableMove",switchShowAvailableMove.isChecked());
        intent.putExtra("lastMove",switchShowLastMove.isChecked());
        intent.putExtra("sound",switchSound.isChecked());
        intent.putExtra("undoRedo",switchUndoRedo.isChecked());
        intent.putExtra("changeSettings",switchChangeSettings.isChecked());
        intent.putExtra("selectPiece", selectPieceSpinner.getSelectedItem().toString());
        String timerSelectedItem=timerSpinner.getSelectedItem().toString();
        if(timerSelectedItem.equalsIgnoreCase("Custom")){
            EditText editTextTimer=findViewById(R.id.editTextTimer);

            if(Pattern.matches("^([1-9]|[1-5][0-9]|60)", editTextTimer.getText().toString().trim())){
                intent.putExtra("timer", editTextTimer.getText().toString().trim());
                startActivity(intent);
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ComputerSettingsActivity.this);
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