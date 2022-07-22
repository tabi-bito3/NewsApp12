package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView tv_name;
    TextView tv_emailID;
    TextView user_name;

    Button bt_logout;
    FirebaseAuth fb_Auth;
    GoogleSignInClient gsc;
    Toolbar tb;
    de.hdodenhof.circleimageview.CircleImageView round_profile;
    private FirebaseFirestore firebaseFirestore;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_name=view.findViewById(R.id.tv_name);
        tv_emailID=view.findViewById(R.id.tv_emailID);
        user_name=view.findViewById(R.id.ed_userName);

        bt_logout=view.findViewById(R.id.bt_logout);
        tb=view.findViewById(R.id.profile_toolbar);
        round_profile=view.findViewById(R.id.profile_pic);

        fb_Auth= FirebaseAuth.getInstance();

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
                            Glide.with(view.getContext())
                                    .load(image)
                                    .into(round_profile);
                        }
                        else{
                            Glide.with(view.getContext())
                                    .load(fb_User.getPhotoUrl())
                                    .into(round_profile);
                        }
                    }

                }
            });



            tv_name.setText("Name : "+fb_User.getDisplayName());
            tv_emailID.setText("Email ID: "+fb_User.getEmail());
        }

        gsc= GoogleSignIn.getClient(view.getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            fb_Auth.signOut();

                            Toast.makeText(view.getContext(), "Logged out succesfully",Toast.LENGTH_SHORT).show();

                            getActivity().finish();
                            startActivity(new Intent(view.getContext(),RegisterActivity.class));
                        }
                    }
                });
            }
        });
    }
}