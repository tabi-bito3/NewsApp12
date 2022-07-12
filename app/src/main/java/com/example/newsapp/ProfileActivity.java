package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    ImageView iv_image;
    TextView tv_name;
    TextView tv_emailID;
    EditText ed_userName;
    Button bt_logout;
    FirebaseAuth fb_Auth;
    GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        iv_image=findViewById(R.id.iv_image);
        tv_name=findViewById(R.id.tv_name);
        tv_emailID=findViewById(R.id.tv_emailID);
        ed_userName=findViewById(R.id.ed_userName);
        bt_logout=findViewById(R.id.bt_logout);

        fb_Auth=FirebaseAuth.getInstance();

        FirebaseUser fb_User= fb_Auth.getCurrentUser();

        if(fb_User != null)
        {
            Glide.with(ProfileActivity.this)
                    .load(fb_User.getPhotoUrl())
                    .into(iv_image);

            tv_name.setText(fb_User.getDisplayName());
            tv_emailID.setText(fb_User.getEmail());
        }

        gsc= GoogleSignIn.getClient(ProfileActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            fb_Auth.signOut();

                            Toast.makeText(getApplicationContext(),"Logged out succesfully",Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }
                })
            }
        });


    }
}