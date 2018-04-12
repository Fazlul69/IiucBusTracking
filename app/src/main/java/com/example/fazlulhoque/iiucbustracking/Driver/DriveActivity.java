package com.example.fazlulhoque.iiucbustracking.Driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fazlulhoque.iiucbustracking.Driver.User;
import com.example.fazlulhoque.iiucbustracking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DriveActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    RelativeLayout rootLayout;
    
    /*EditText eBusId,eDriverLoginId;*/
    Button btnLogin,btnRegister;

    String aa;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        /*eBusId=(EditText)findViewById(R.id.eBusId);
        eDriverLoginId=(EditText)findViewById(R.id.eDriverLoginId);*/

        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        getidcheckvalue();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });


        getidvalue();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLoginDialog();

            }
        });







    }

    private void getidcheckvalue() {
        DatabaseReference r2=FirebaseDatabase.getInstance().getReference().child("driverData");
        r2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot eachDriver: dataSnapshot.getChildren()){
                        id=eachDriver.child("id").getValue().toString();
                        Log.d("idfound1111","idfound1111="+ id);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_bus_login,null);

        final MaterialEditText edtMail = login_layout.findViewById(R.id.edtMail);
        final MaterialEditText edtId = login_layout.findViewById(R.id.edtId);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);

        dialog.setView(login_layout);

        //set button


        // String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();




            dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    //validation check

                    if (TextUtils.isEmpty(edtMail.getText().toString())) {
                        Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(edtId.getText().toString())) {
                        Snackbar.make(rootLayout, "Please enter your id", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                        Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (edtPassword.getText().toString().length() < 6) {
                        Snackbar.make(rootLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    //login

                    if(aa.equals(id)) {
                        auth.signInWithEmailAndPassword(edtMail.getText().toString(), edtPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        startActivity(new Intent(DriveActivity.this, GenderActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(DriveActivity.this, "driveridnotfound", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("RESITER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_bus_registration,null);

        final MaterialEditText edtMail = register_layout.findViewById(R.id.edtMail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtId = register_layout.findViewById(R.id.edtId);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        //set button

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //validation check

                if(TextUtils.isEmpty(edtMail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(edtPhone.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter phone no.",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(edtPassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout,"Password too short",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //register new user
                auth.createUserWithEmailAndPassword(edtMail.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                //user bd save
                                User user = new User();
                                user.setEmail(edtMail.getText().toString());
                                user.setPassword(edtPassword.getText().toString());
                                user.setId(edtId.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPhone(edtPhone.getText().toString());

                                //use email to key
                                /*if(edtId.equals(db.getReference().child("driverData").child("").child("id"))){}*/
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout,"Register success fully",Snackbar.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();


    }

    private void getidvalue() {


        DatabaseReference r1=FirebaseDatabase.getInstance().getReference().child("Users");
        r1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    for (DataSnapshot eachDriver: dataSnapshot.getChildren()){

                        String ss=eachDriver.child("id").getValue().toString();
                        if(ss.equals(id))
                        {
                            aa=ss.toString();
                            Log.d("idfound","idfound="+ aa);
                        }

                    }


                }
                else
                {
                    Toast.makeText(DriveActivity.this, "idnull", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
