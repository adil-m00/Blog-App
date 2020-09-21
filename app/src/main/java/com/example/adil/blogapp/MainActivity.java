package com.example.adil.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
        private Toolbar mainToolbar;

        private FloatingActionButton addPostBtn;
        private String currentUserId;

        private BottomNavigationView mainBottomNav;

        private FragmentHome fragmentHome;
        private NotificationFragment notificationFragment;
        private AccountFragment accountFragment;






    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();



        mainToolbar=findViewById(R.id.mainToolbars);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Photo Blog");

        mainBottomNav=findViewById(R.id.mainBottomNav);

        //fragments
        fragmentHome=new FragmentHome();
        notificationFragment=new NotificationFragment();
        accountFragment=new AccountFragment();

        replaceFragment(fragmentHome);



        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.bottom_action_home:
                        replaceFragment(fragmentHome);
                        return true;
                    case R.id.bottom_notification:
                        replaceFragment(notificationFragment);
                        return true;
                    case R.id.bottom_account:
                        replaceFragment(accountFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });


        addPostBtn=findViewById(R.id.addPostButton);


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,NewPostActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
        {
            sendToLogin();
        }
        else
        {
            currentUserId=mAuth.getCurrentUser().getUid().toString();
            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                            if(!task.getResult().exists())
                            {
                                    Intent intent=new Intent(MainActivity.this,SetupActivity.class);
                                    startActivity(intent);
                                    finish();
                            }
                    }
                    else
                    {
                            String error=task.getException().getMessage();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       getMenuInflater().inflate(R.menu.main_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutBtn:
                logout();
                return true;
            case R.id.action_account_btn:
                setupToolBar();
                return true;
            default:
                return false;


        }



    }

    private void setupToolBar() {
        Intent intent=new Intent(MainActivity.this,SetupActivity.class);
        startActivity(intent);

    }

    private void logout() {
        mAuth.signOut();
        sendToLogin();

    }
    private void sendToLogin() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void replaceFragment(android.support.v4.app.Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);
        fragmentTransaction.commit();


    }
}
