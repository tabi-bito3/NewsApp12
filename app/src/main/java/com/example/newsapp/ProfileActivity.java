package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    //ImageView iv_image;
    TextView tv_name;
    TextView tv_emailID;
    TextView user_name;

    Button bt_logout;
    FirebaseAuth fb_Auth;
    GoogleSignInClient gsc;
    Toolbar tb;
    de.hdodenhof.circleimageview.CircleImageView round_profile;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //commit test

        //iv_image=findViewById(R.id.iv_image);
        tv_name=findViewById(R.id.tv_name);
        tv_emailID=findViewById(R.id.tv_emailID);
        user_name=findViewById(R.id.ed_userName);

        bt_logout=findViewById(R.id.bt_logout);
        tb=findViewById(R.id.profile_toolbar);
        round_profile=findViewById(R.id.profile_pic);

        fb_Auth=FirebaseAuth.getInstance();

        FirebaseUser fb_User= fb_Auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(fb_User != null)
        {

               firebaseFirestore.collection("Users").document(fb_User.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.isSuccessful()) {
                           String name = task.getResult().getString("username");

                           user_name.setText("Hello, "+name+"!");

                           String image = task.getResult().getString("profile_img");
                           if(image!=null){
                               Glide.with(ProfileActivity.this)
                                       .load(image)
                                       .into(round_profile);
                           }
                           else{
                               Glide.with(ProfileActivity.this)
                                       .load(fb_User.getPhotoUrl())
                                       .into(round_profile);
                           }
                       }

                   }
               });



            tv_name.setText("Name : "+fb_User.getDisplayName());
            tv_emailID.setText("Email ID: "+fb_User.getEmail());
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
                            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                        }
                    }
                });
            }
        });


    }
}