package com.example.adil.blogapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {
    private Toolbar setupToolbar;
    private  ImageView imageView;

    private Uri imageURI=null;

    private EditText setupName;
    private Button saveChanges;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setupToolbar=findViewById(R.id.setupToolBar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setting");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupName=findViewById(R.id.nameChange);
        saveChanges=findViewById(R.id.nameChangeBtn);
        imageView=findViewById(R.id.imageSetup);
        dialog=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference();

        storageReference=FirebaseStorage.getInstance().getReference();



        firebaseFirestore=FirebaseFirestore.getInstance();


        //Getting an image


        String User_Id=firebaseAuth.getCurrentUser().getUid().toString();


        firebaseFirestore.collection("Users").document(User_Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {

                    if(task.getResult().exists())
                    {
                        String name=task.getResult().getString("name");
                        String image=task.getResult().getString("image");
                        imageURI=Uri.parse(image);
                        setupName.setText(name);





                        Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.def).into(imageView);







                    }
                    else
                    {
                         Toast.makeText(SetupActivity.this, "Please Upload Images", Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {
                    Toast.makeText(SetupActivity.this, "Please Upload an image first", Toast.LENGTH_SHORT).show();
                }


            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                        
                    }
                    else
                    {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(SetupActivity.this);
                    }
                }
            }
        });











    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                 imageURI = result.getUri();

                  imageView.setImageURI(imageURI);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

}
