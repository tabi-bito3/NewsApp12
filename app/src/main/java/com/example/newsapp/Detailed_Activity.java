package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ImageView likeBtn, dislikeBtn;
    private TextView likeCnt;
    private TextView titleView;
    String user_id;
    private int count = 0, dlCount = 0;
    private String mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mUser = firebaseAuth.getCurrentUser().getUid();

        username=findViewById(R.id.usernameDesc);
        profileImg=findViewById(R.id.profile_img_desc);
        postDate = findViewById(R.id.post_date_desc);
        postImageView = findViewById(R.id.post_img_desc);
        titleView = findViewById(R.id.post_title_desc);
        descView = findViewById(R.id.detailed_desc);
        likeBtn = findViewById(R.id.likeBtn_desc);
        dislikeBtn= findViewById(R.id.dislikeBtn_desc);
        likeCnt = findViewById(R.id.like_count_desc);

        String postId = getIntent().getExtras().getString("post_id");

        // Get Likes & Dislikes Count
        firebaseFirestore.collection("Posts/" + postId +"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    count = value.size();
                    likeCnt.setText(""+(count-dlCount));
                }
                else{
                    count = 0;
                    likeCnt.setText(""+(count-dlCount));
                }
            }
        });
        firebaseFirestore.collection("Posts/" + postId +"/Dislikes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    dlCount = value.size();
                    likeCnt.setText(""+ (count-dlCount));
                }
                else{
                    dlCount = 0;
                    likeCnt.setText(""+(count-dlCount));
                }
            }
        });
        // Get likes
        firebaseFirestore.collection("Posts/" + postId +"/Likes").document(mUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    likeBtn.setImageDrawable(Detailed_Activity.this.getDrawable(R.drawable.thumb_up_red));
                    //holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down));
                }
                else{
                    likeBtn.setImageDrawable(Detailed_Activity.this.getDrawable(R.drawable.thumb_up));
                    //holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down_blue));
                }
            }
        });

        firebaseFirestore.collection("Posts/" + postId +"/Dislikes").document(mUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    dislikeBtn.setImageDrawable(Detailed_Activity.this.getDrawable(R.drawable.thumb_down_blue));
                    //holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down));
                }
                else{
                    dislikeBtn.setImageDrawable(Detailed_Activity.this.getDrawable(R.drawable.thumb_down));
                    //holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down_blue));
                }
            }
        });




        firebaseFirestore.collection("Posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String title = task.getResult().getString("title");
                    String thumb = task.getResult().getString("thumb_img");
                    Date timestamp = task.getResult().getTimestamp("timestamp").toDate();
                    String desc = task.getResult().getString("desc");
                    user_id=task.getResult().getString("user_id");

                    String pattern = "dd MMMM yyyy";

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                    String date = simpleDateFormat.format(timestamp);




                    titleView.setText(title);
                    descView.setText(desc);
                    postDate.setText(date);
                    Glide.with(Detailed_Activity.this).load(thumb).into(postImageView);


                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                String user_name=task.getResult().getString("username");
                                String pro_im=task.getResult().getString("profile_img");
                                username.setText(user_name);
                                Glide.with(Detailed_Activity.this).load(pro_im).into(profileImg);



                            }

                        }


                    });
                    likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            firebaseFirestore.collection("Posts/" + postId + "/Likes").document(mUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (!task.getResult().exists()) {
                                        Map<String, Object> likesMap = new HashMap<>();
                                        likesMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Posts/" + postId + "/Likes").document(mUser).set(likesMap);
                                        firebaseFirestore.collection("Posts/" + postId + "/Dislikes").document(mUser).delete();
                                        // holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_up_red));

                                    } else {
                                        firebaseFirestore.collection("Posts/" + postId + "/Likes").document(mUser).delete();
                                        // holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_up));
                                    }
                                }
                            });
                        }

                        });

                    dislikeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            firebaseFirestore.collection("Posts/" + postId +"/Dislikes").document(mUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(!task.getResult().exists()){
                                        Map<String, Object> dislikesMap =new HashMap<>();
                                        dislikesMap.put("timestamp", FieldValue.serverTimestamp());
                                        firebaseFirestore.collection("Posts/" + postId +"/Dislikes").document(mUser).set(dislikesMap);
                                        firebaseFirestore.collection("Posts/" + postId +"/Likes").document(mUser).delete();
                                        // holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down_blue));

                                    }
                                    else{
                                        firebaseFirestore.collection("Posts/" + postId +"/Dislikes").document(mUser).delete();
                                        // holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down));
                                    }
                                }
                            });


                        }
                    });




                }

            }


        });



    }
}