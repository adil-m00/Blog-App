package com.example.adil.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistreActivity extends AppCompatActivity {

    private EditText mail,pass,conPass;
    private Button signUp,existAccount;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);

        mail=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        conPass=findViewById(R.id.confimrPass);
        signUp=findViewById(R.id.createAnAccount);
        existAccount=findViewById(R.id.AlreadAccount);

        progressBar=findViewById(R.id.loginProgress);

        mAuth=FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mail.getText().toString();
                String password=pass.getText().toString();
                String ConfPass=conPass.getText().toString();
                if(!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password) && !TextUtils.isEmpty(ConfPass))
                {

                    if(password.equals(ConfPass))
                    {
                        progressBar.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent=new Intent(RegistreActivity.this,SetupActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        String error=task.getException().getMessage();
                                        Toast.makeText(RegistreActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                    }
                    else
                    {
                        conPass.setError("Password does not match");
                        Toast.makeText(RegistreActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        existAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistreActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            sendToMain();
        }
    }

    private void sendToMain() {

        Intent intent=new Intent(RegistreActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
