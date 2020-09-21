package com.example.adil.blogapp;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class NewPostActivity extends AppCompatActivity {
    private static int MAX_LENGTH=100;
    private Toolbar toolbar;
    private ImageView setupImage;
    private EditText description;
    private Button postImageBtn;

    private Uri imageURI;


    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog dialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbar=findViewById(R.id.new_post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new Posts");
        dialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference();

        storageReference=FirebaseStorage.getInstance().getReference();



        firebaseFirestore=FirebaseFirestore.getInstance();

        postImageBtn=findViewById(R.id.Post_btn);
        setupImage=findViewById(R.id.new_post_image);
        description=findViewById(R.id.description);



        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(NewPostActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(NewPostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


                    }
                    else
                    {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(NewPostActivity.this);
                    }
                }

            }
        });

        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String UserId=firebaseAuth.getCurrentUser().getUid().toString();
                final String Description=description.getText().toString().toString();
                if(!TextUtils.isEmpty(Description))
                {
                    dialog.setTitle("Post Uploading");
                    dialog.setMessage("Please wait........");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    String storages=random();

                    StorageReference image_path=storageReference.child("Post_image").child(storages +".jpg");

                    image_path.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                    Uri  download_url=task.getResult().getDownloadUrl();
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                String randomName=random();


                                HashMap<String, String> map=new HashMap<>();
                                map.put("Post_image",download_url.toString());
                                map.put("description",Description);
                                map.put("timeStamp",currentDateTimeString);
                                map.put("UserId",UserId);
                                firebaseFirestore.collection("Posts").document(randomName).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            dialog.dismiss();

                                            Toast.makeText(NewPostActivity.this, "Post has been upload", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(NewPostActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }

                                        else

                                        {
                                            dialog.dismiss();
                                            Toast.makeText(NewPostActivity.this, "Post is not upload", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(NewPostActivity.this, "Got an Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }



            }
        });
















    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageURI = result.getUri();

                setupImage.setImageURI(imageURI);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    public static String random() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
