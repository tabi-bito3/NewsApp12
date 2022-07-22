package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button gButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        gButton = (Button) findViewById(R.id.google_login);

        // Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("562224797374-d850td2kmcei3ni71cce6hgnpfbsuknq.apps.googleusercontent.com")
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        gButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            navigateToMainActivity();
        }
    }

    private void signIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100) {
            Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(task.isSuccessful()) {

                Toast.makeText(getApplicationContext(), "Google sign in successful", Toast.LENGTH_SHORT).show();
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount=task
                            .getResult(ApiException.class);
                    // Check condition
                    if(googleSignInAccount!=null)
                    {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        ,null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if(task.isSuccessful())
                                        {
                                            // When task is successful
                                            navigateToMainActivity();
                                            // Display Toast
                                            Toast.makeText(getApplicationContext(), "Firebase authentication successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            // When task is unsuccessful
                                            try {
                                                Toast.makeText(getApplicationContext(), "Authentication Failed :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            catch (Exception e){
                                                e.getStackTrace();
                                            }
                                        }
                                    }
                                });

                    }
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Authentication Failed :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToMainActivity() {
        finish();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
            startActivity(intent);
        }
        else if(user != null){
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}