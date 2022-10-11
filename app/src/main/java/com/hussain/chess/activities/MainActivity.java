package com.hussain.chess.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hussain.chess.Model.User;
import com.hussain.chess.R;
import com.hussain.chess.activities.offline.PlayOfflineOptionActivity;
import com.hussain.chess.activities.offline.SettingsActivity;
import com.hussain.chess.activities.online.PlayOnlineOptionActivity;


public class MainActivity extends AppCompatActivity {
    //The m is here to indicate a member variable
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN=100;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));

        // Set BackgroundDrawable
       if(actionBar!=null){
           actionBar.setBackgroundDrawable(colorDrawable);
       }


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        Button btnPlayOnline=findViewById(R.id.btnPlayOnline);
        btnPlayOnline.setOnClickListener(view -> {
            ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(getResources().getString(R.string.internet_connection_error))
                        .setPositiveButton("OK", null).show();
            }
            else{
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if(currentUser==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("You need to sign in first to play online")
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                        startActivityForResult(signInIntent, RC_SIGN_IN);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                        checkUserExistenceAndStartActivity();

                }
            }


        });

        Button btnPlayOffline=findViewById(R.id.btnPlayOffline);
        btnPlayOffline.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PlayOfflineOptionActivity.class);
            startActivity(intent);
        });





    }

    private void checkUserExistenceAndStartActivity() {
        database.getReference().child("Users").orderByKey().equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                if(snapshot.getValue()==null){
                    createUserInDatabase();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, PlayOnlineOptionActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            // check user is new or existing
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                Toast.makeText(MainActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                createUserInDatabase();

                            }
                            else{
                                checkUserExistenceAndStartActivity();
                            }

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Can't login", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }



    private void createUserInDatabase(){
        FirebaseUser user = mAuth.getCurrentUser();

        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter your Username")
                .setMessage("try to avoid using your name in username")
                .setCancelable(false);


        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText editTextUsername = new EditText(MainActivity.this);
        editTextUsername.setText(user.getDisplayName());

        Button btnSave = new Button(MainActivity.this);
        btnSave.setText("Save");

        linearLayout.addView(editTextUsername);
        linearLayout.addView(btnSave);

        builder.setView(linearLayout);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=editTextUsername.getText().toString().trim();
                if(name.isEmpty()){
                    Toast.makeText(MainActivity.this, "name can't be empty", Toast.LENGTH_SHORT).show();
                }
                else{

                    Uri photo=user.getPhotoUrl();

                    User user1= new User(name);
                    String id =mAuth.getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference().child("Users").child(id).setValue(user1);


                    //Store image in Firebase database
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference reference = storage.getReference().child("Profile_image").child(FirebaseAuth.getInstance().getUid());
                    reference.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(LoginActivity.this, "Profile Photo Save", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("Users").child(mAuth.getUid()).child("profile").setValue(uri.toString());
                                }
                            });
                        }
                    });

                    Intent intent = new Intent(MainActivity.this,PlayOnlineOptionActivity.class);
                    startActivity(intent);
                }

            }
        });

    }




}