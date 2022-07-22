package com.example.newsapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView postListView;
    private List<BlogPost> blog_list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mRef, postRef;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        blog_list=new ArrayList<>();
        // Inflate the layout for this fragment
        postListView = view.findViewById(R.id.post_list_view);
        blogRecyclerAdapter=new BlogRecyclerAdapter(blog_list);
        postListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        postListView.setAdapter(blogRecyclerAdapter);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            Query orderQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING);

            orderQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot DocumentSnapshots, FirebaseFirestoreException error) {
                    for (DocumentChange doc : DocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blog_list.add(blogPost);
                            blogRecyclerAdapter.setBlog_list(blog_list);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        return view;

    }
}