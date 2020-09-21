package com.example.adil.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
        private EditText mEmail,mPAssword;
        private Button mLogin,mRegistration;
    private ProgressBar mProgressBar;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        mEmail=findViewById(R.id.email);
        mPAssword=findViewById(R.id.password);

        mLogin=findViewById(R.id.createAnAccount);

        mRegistration=findViewById(R.id.registration);
        mProgressBar = findViewById(R.id.loginProgress);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistreActivity.class);
                startActivity(intent);

            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=mEmail.getText().toString();
                String password=mPAssword.getText().toString();
                if(email.isEmpty())
                {
                    mEmail.setError("Please Enter Email");
                }
                if(password.isEmpty())
                {
                    mPAssword.setError("Please Enter Email");
                }
                else
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                            }


                            else
                            {
                                String errorMsg=task.getException().getMessage();

                                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                            }

                            mProgressBar.setVisibility(View.INVISIBLE);


                        }
                    });

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
