package com.hussain.chess.activities.online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hussain.chess.Model.User;
import com.hussain.chess.R;
import com.hussain.chess.activities.MainActivity;

import java.text.DecimalFormat;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
            actionBar.setTitle(R.string.profile_info);
        }

        TextView txtUsername = findViewById(R.id.txtUsername);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database.getReference("Users").child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user == null) {
                    database.getReference("Users").removeEventListener(this);
                } else {
                    txtUsername.setText(user.getName());


                    // win count
                    TextView txtWinCount = findViewById(R.id.txtWinCount);
                    txtWinCount.setText(String.valueOf(user.getWinCount()));


                    // lost count
                    TextView txtLostCount = findViewById(R.id.txtLostCount);
                    txtLostCount.setText(String.valueOf(user.getLostCount()));


                    // draw count
                    TextView txtDrawCount = findViewById(R.id.txtDrawCount);
                    txtDrawCount.setText(String.valueOf(user.getDrawCount()));


                    // Match count
                    TextView txtMatchCount = findViewById(R.id.txtMatchCount);
                    txtMatchCount.setText(String.valueOf(user.getMatchCount()));


                    final DecimalFormat df = new DecimalFormat("0.00");

                    TextView txtWinRate = findViewById(R.id.txtWinRate);
                    TextView txtLostRate = findViewById(R.id.txtLostRate);

                    if (user.getMatchCount() == 0) {
                        txtWinRate.setText("100%");
                        txtLostRate.setText("0%");
                    } else {
                        // Win rate
                        float winRate = ((float) user.getWinCount() / (float) user.getMatchCount()) * 100;
                        txtWinRate.setText(df.format(winRate) + "%");

                        // Lost rate
                        float lostRate = ((float) user.getLostCount() / (float) user.getMatchCount()) * 100;
                        txtLostRate.setText(df.format(lostRate) + "%");

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView imgEdit = findViewById(R.id.imgEdit);
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Enter your Username")
                        .setMessage("try to avoid using your name in username");


                LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText editTextUsername = new EditText(ProfileActivity.this);
                editTextUsername.setText(txtUsername.getText());

                Button btnUpdate = new Button(ProfileActivity.this);
                btnUpdate.setText("Update");

                linearLayout.addView(editTextUsername);
                linearLayout.addView(btnUpdate);

                builder.setView(linearLayout);
                AlertDialog dialog = builder.create();
                dialog.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = editTextUsername.getText().toString().trim();
                        if (name.isEmpty()) {
                            Toast.makeText(ProfileActivity.this, "name can't be empty", Toast.LENGTH_SHORT).show();
                        } else {

                            database.getReference().child("Users").child(mAuth.getUid()).child("name").setValue(name);
                            Toast.makeText(ProfileActivity.this, "name updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        Button btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Log out confirmation")
                        .setMessage("Are you sure you want to logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Delete Account Confirmation")
                        .setMessage("Are you sure you want to delete your account")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                database.getReference().child("Users").child(mAuth.getUid()).removeValue();

                                FirebaseUser user = mAuth.getCurrentUser();

                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}