package com.example.fazlulhoque.iiucbustracking.Admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fazlulhoque.iiucbustracking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin extends AppCompatActivity {

    EditText eAdminID,eAdminpass;
    Button btnAdminLogin, btnRegistration;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthLisener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        auth=FirebaseAuth.getInstance();

        firebaseAuthLisener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {
                   /* Intent intent = new Intent(Admin.this, BusRegistration.class);
                    startActivity(intent);*/
                }
            }
        };

        eAdminID=(EditText)findViewById(R.id.eAdminID);
        eAdminpass=(EditText)findViewById(R.id.eAdminpass);

        btnAdminLogin=(Button)findViewById(R.id.btnAdminLogin);
        btnRegistration=(Button)findViewById(R.id.butRegistration);

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = eAdminID.getText().toString();
                final String password = eAdminpass.getText().toString();
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Admin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(Admin.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }else {
                            String user_id = auth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("user").child("admin").child(user_id);
                            current_user_db.setValue(true);
                        }
                    }
                });
            }
        });

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = eAdminID.getText().toString();
                final String password = eAdminpass.getText().toString();
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Admin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(Admin.this, "sign in error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthLisener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthLisener);
    }
}
