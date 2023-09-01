package com.example.diana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button registroButton, loginbutton;
    private EditText editTextUser, editTextPassword;

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        registroButton = findViewById(R.id.buttonRegistro);
        loginbutton = findViewById(R.id.buttonLogin);
        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextApellidos);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    signIn(editTextUser.getText().toString(), editTextPassword.getText().toString(),
                            view);
            }
        });

        registroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambioActivityEmailPassword(view);
            }
        });
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("Current User On Start MainActivity", "creado el usuario actual "+ currentUser.getEmail());
        if(currentUser != null){
            reload();
            Log.d("Current User On Start MainActivity", "usuario on start main");
        } else{
            Log.d("Current User On Start MainActivity","null");
        }
    }
    // [END on_start_check_user]

    private void signIn(String email, String password, View view) {
        // [START sign_in_with_email]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

       // if(user.isEmailVerified()){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("Current User On Complete MainActivity", "usuario on complete"+user.getEmail());
                                updateUI(user);
                                cambioActivityEmpty(view);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        /*}else{
            Toast.makeText(MainActivity.this, "El email no ha sido verificado",
                    Toast.LENGTH_SHORT).show();
        }*/

        // [END sign_in_with_email]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }

    public void cambioActivityEmailPassword(View view){
        Intent intent = new Intent(this, EmailPasswordActivity.class);
        startActivity(intent);
    }

    public void cambioActivityEmpty(View view){
        Intent intent = new Intent(this, TorneosActivity.class);
        startActivity(intent);
    }

}