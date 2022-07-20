package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detailed_Activity extends AppCompatActivity {

    public List<BlogPost> blog_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView username;
    private CircleImageView profileImg;
    private TextView descView;
    private TextView postDate;
    private ImageView postImageView;
    private ImageButton likeBtn, dislikeBtn;
    private TextView likeCnt;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        username=findViewById(R.id.usernameDesc);
        profileImg=findViewById(R.id.profile_img_desc);
        postDate = findViewById(R.id.post_date_desc);
        postImageView = findViewById(R.id.post_img_desc);
        titleView = findViewById(R.id.post_title_desc);
        descView = findViewById(R.id.detailed_desc);
        likeBtn = findViewById(R.id.likeBtn_desc);
        dislikeBtn= findViewById(R.id.dislikeBtn);
        likeCnt = findViewById(R.id.like_count_desc);

        String postId = getIntent().getExtras().getString("post_id");

        firebaseFirestore.collection("Posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String title = task.getResult().getString("title");
                    String thumb = task.getResult().getString("thumb_img");
                    String timestamp = task.getResult().getString("timestamp");
                    String desc = task.getResult().getString("desc");

                    titleView.setText(title);
                    descView.setText(desc);
                }
            }
        });


    }
}