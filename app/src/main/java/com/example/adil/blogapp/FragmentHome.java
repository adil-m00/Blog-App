package com.example.adil.blogapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FragmentHome extends Fragment {


    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;

    private FirebaseFirestore firebaseFirestore;

    private BlogRecyclerAdapter blogRecyclerAdapter;

    public FragmentHome() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_fragment_home, container, false);
        // Inflate the layout for this fragment
        firebaseFirestore=FirebaseFirestore.getInstance();

        Query firstQuery=firebaseFirestore.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING);
        blog_list=new ArrayList<>();
        blog_list_view=view.findViewById(R.id.blog_list_view);

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);

        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        blog_list_view.setAdapter(blogRecyclerAdapter);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc: documentSnapshots.getDocumentChanges())
                {
                    if(doc.getType()==DocumentChange.Type.ADDED)
                    {
                        String blogPostId=doc.getDocument().getId();
                        BlogPost blogPost=doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                        blog_list.add(blogPost);
                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }



}
