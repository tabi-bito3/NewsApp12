package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(mainToolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Campus News");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout_btn) {
            logOut();
            return true;
        }
        return false;
    }

    private void sendToLogin() {
        Intent intent= new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

}