package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private Toolbar mainToolbar;

    private BottomNavigationView mainBottomNavigation;
    private HomeFragment homeFragment;
    private NotificationsFragment notificationsFragment;
    private ProfileFragment profileFragment;
    private FloatingActionButton addPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        addPost = (FloatingActionButton) findViewById(R.id.add_post);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        setSupportActionBar(mainToolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Campus News");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        //FRAGMENTS
        homeFragment = new HomeFragment();
        notificationsFragment = new NotificationsFragment();
        profileFragment = new ProfileFragment();

        replaceFragment(homeFragment);

        mainBottomNavigation = (BottomNavigationView) findViewById(R.id.mainBottomNav);

        mainBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_bottom_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.action_bottom_notif:
                        replaceFragment(notificationsFragment);
                        return true;
                    case R.id.action_bottom_profile:
                        replaceFragment(profileFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPostActivity();
            }
        });
    }

    private void toPostActivity() {
        Intent intent= new Intent(MainActivity.this,NewPostActivity.class);
        startActivity(intent);
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

        else if(item.getItemId()==R.id.action_settings_btn){
            sendToProfile();
            return true;
        }

        return false;
    }

    private void sendToProfile(){
        Intent intent= new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    private void sendToLogin() {
        Intent intent= new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void logOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        sendToLogin();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

}