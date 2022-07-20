package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private int count = 0, dlCount = 0;

    public BlogRecyclerAdapter(List<BlogPost> blog_list){
        this.blog_list=blog_list;
    }

    public void setBlog_list(List<BlogPost> blog_list){
        this.blog_list=blog_list;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( BlogRecyclerAdapter.ViewHolder holder, int position) {

        String blogPostId = blog_list.get(position).blogPostId;
        String current_user_id = firebaseAuth.getCurrentUser().getUid();

        String user_id = blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    String name = task.getResult().getString("username");
                    String image = task.getResult().getString("profile_img");

                    holder.setUser(name, image);
                }
            }
        });

        String desc_data = blog_list.get(position).getTitle();
        holder.setDescText(desc_data);

        String thumbImg = blog_list.get(position).getThumb_img();
        holder.setBlogImage(thumbImg);
        String ago = "";
        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            long now = System.currentTimeMillis();
            ago = DateUtils.getRelativeTimeSpanString(millisecond, now, DateUtils.MINUTE_IN_MILLIS).toString();
            String dateString = DateFormat.getDateInstance().format(millisecond).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.setTime(ago);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Detailed_Activity.class);
                intent.putExtra("post_id", blogPostId);
                view.getContext().startActivity(intent);
            }
        });

        // Get Likes & Dislikes Count
        firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    count = value.size();
                    holder.setLikeCnt(count-dlCount);
                }
                else{
                    count = 0;
                    holder.setLikeCnt(count-dlCount);
                }
            }
        });

        firebaseFirestore.collection("Posts/" + blogPostId +"/Dislikes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    dlCount = value.size();
                    holder.setLikeCnt(count-dlCount);
                }
                else{
                    dlCount = 0;
                    holder.setLikeCnt(count-dlCount);
                }
            }
        });

        //holder.setLikeCnt(count);

        // Get likes
        firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_up_red));
                    //holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down));
                }
                else{
                    holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_up));
                    //holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down_blue));
                }
            }
        });

        // Get dislikes
        firebaseFirestore.collection("Posts/" + blogPostId +"/Dislikes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down_blue));
                }
                else{
                    holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down));
                }
            }
        });

        // Likes Feature

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap =new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(current_user_id).set(likesMap);
                            firebaseFirestore.collection("Posts/" + blogPostId +"/Dislikes").document(current_user_id).delete();
                            // holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_up_red));

                        }
                        else{
                            firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(current_user_id).delete();
                            // holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_up));
                        }
                    }
                });


            }
        });

        // Dislikes feature

        holder.dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + blogPostId +"/Dislikes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> dislikesMap =new HashMap<>();
                            dislikesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/" + blogPostId +"/Dislikes").document(current_user_id).set(dislikesMap);
                            firebaseFirestore.collection("Posts/" + blogPostId +"/Likes").document(current_user_id).delete();
                            // holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down_blue));

                        }
                        else{
                            firebaseFirestore.collection("Posts/" + blogPostId +"/Dislikes").document(current_user_id).delete();
                            // holder.dislikeBtn.setImageDrawable(context.getDrawable(R.drawable.thumb_down));
                        }
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView username;
        private CircleImageView profileImg;
        private TextView descView;
        private TextView postDate;
        private ImageView postImageView;
        private ImageButton likeBtn, dislikeBtn;
        private TextView likeCnt;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            likeBtn = mView.findViewById(R.id.likeBtn);
            dislikeBtn = mView.findViewById(R.id.dislikeBtn);

        }
        public void setDescText(String descText){
            descView= mView.findViewById(R.id.post_title);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUrl){
            postImageView = mView.findViewById(R.id.post_img);
            Glide.with(context).load(downloadUrl).into(postImageView);
        }

        public void setTime(String date){
            postDate = mView.findViewById(R.id.post_date);
            postDate.setText(date);
        }

        public void setUser(String name, String image){
            username = mView.findViewById(R.id.username);
            profileImg = mView.findViewById(R.id.profile_img);

            username.setText(name);

            RequestOptions placeHolderOption = new RequestOptions();
            placeHolderOption.placeholder(R.drawable.account_circle);

            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(profileImg);

        }

        public void setLikeCnt(Integer count) {
            likeCnt = mView.findViewById(R.id.like_count);
            likeCnt.setText(count.toString());
        }
    }
}
