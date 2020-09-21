package com.example.adil.blogapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;



    public BlogRecyclerAdapter(List<BlogPost> blog_list)
    {
        this.blog_list=blog_list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final String blogPostId=blog_list.get(position).BlogPostId;
        String desc_data=blog_list.get(position).getDescription();
        holder.setDescText(desc_data);
        final String CurrentUserId=firebaseAuth.getCurrentUser().getUid().toString();


        String image_url=blog_list.get(position).getPost_image();
        holder.setImage(image_url);


        String userId=blog_list.get(position).getUserId();
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if(task.isSuccessful())
           {
               String name=task.getResult().getString("name");
               String profileImage=task.getResult().getString("image");
               holder.setData(name,profileImage);

           }

           else
           {

           }
            }
        });
        //get Likes Count
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty())
                {
                    int count=documentSnapshots.size();

                    holder.updateLikesCount(count);


                }
                else
                {
                    holder.updateLikesCount(0);

                }
            }
        });



        //Get Likes

        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(CurrentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                         holder.blogLikeBtn.setImageResource(R.drawable.red);


                }
                else

                {
                    holder.blogLikeBtn.setImageResource(R.drawable.ic_favorite_black_24dp);

                }
            }
        });



            //Likes feature

        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(CurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists())
                        {

                            Map<String,Object>  likesMap=new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(CurrentUserId).set(likesMap);

                        }
                        else
                        {
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(CurrentUserId).delete();

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

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mView;
        private TextView descview;
        private ImageView imageView;
        private TextView Uname;
        private CircleImageView circleImageView;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;


        public ViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

            blogLikeBtn=mView.findViewById(R.id.dilkaRaja);

        }
        public void setDescText(String descText)
        {
            descview=mView.findViewById(R.id.blog_desc);
            descview.setText(descText);
        }

        public void setImage(String downloadUri)
        {
          imageView=mView.findViewById(R.id.blog_image);

            Picasso.with(context).load(downloadUri).placeholder(R.drawable.def).into(imageView);


        }
        public void setData(String pname,String pImage)
        {
            Uname=mView.findViewById(R.id.blog_user_name);
            circleImageView=mView.findViewById(R.id.blog_user_image);
            Uname.setText(pname);
            Picasso.with(context).load(pImage).placeholder(R.drawable.def).into(circleImageView);

        }
        public void updateLikesCount(int count)
        {
            blogLikeCount=mView.findViewById(R.id.likes);
            blogLikeCount.setText(count + " Likes");
        }





    }
}
