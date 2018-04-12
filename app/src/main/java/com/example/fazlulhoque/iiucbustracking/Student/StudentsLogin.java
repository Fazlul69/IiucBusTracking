package com.example.fazlulhoque.iiucbustracking.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fazlulhoque.iiucbustracking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import retrofit2.http.Url;

public class StudentsLogin extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLogin, mRegistration;
    private FirebaseAuth mAuth;
    FirebaseDatabase sdb;
    DatabaseReference studentUsers;
    RelativeLayout rootLayout;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_login);

        String url="gs://iiucbustrackingapp.appspot.com/studentData.json";
        mAuth = FirebaseAuth.getInstance();
        sdb = FirebaseDatabase.getInstance();
        studentUsers = sdb.getReference("StudentUsers");
        /*eBusId=(EditText)findViewById(R.id.eBusId);
        eDriverLoginId=(EditText)findViewById(R.id.eDriverLoginId);*/

        mLogin=(Button)findViewById(R.id.btnStudentLogin);
        mRegistration = (Button)findViewById(R.id.btnStudentRegistration);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });



        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
    }

    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.students_login,null);

        final MaterialEditText edtMail = login_layout.findViewById(R.id.edtMail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);

        dialog.setView(login_layout);

        //set button

        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //validation check

                if (TextUtils.isEmpty(edtMail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(edtMail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
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
                mAuth.signInWithEmailAndPassword(edtMail.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(StudentsLogin.this,StudentType.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_SHORT).show();
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

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("RESITER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.students_registration,null);

        final MaterialEditText edtMail = register_layout.findViewById(R.id.edtMail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);

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

                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(edtPassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout,"Password too short",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //register new user
                mAuth.createUserWithEmailAndPassword(edtMail.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                //user bd save
                                StudentUser user = new StudentUser();
                                user.setEmail(edtMail.getText().toString());
                                user.setPassword(edtPassword.getText().toString());

                                //use email to key

                                studentUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

      /*  mAuth= FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {

                    Intent intent=new Intent(StudentsLogin.this,StudentMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }
            }
        };


        mEmail=(EditText)findViewById(R.id.email);
        mPassword=(EditText)findViewById(R.id.password);
        mLogin=(Button)findViewById(R.id.login);
        mRegistration=(Button)findViewById(R.id.registration);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String email=mEmail.getText().toString();
                final String password=mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(StudentsLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(StudentsLogin.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String user_id=mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_id= FirebaseDatabase.getInstance().getReference().child("User").child("Customers").child(user_id);
                            current_user_id.setValue(true);

                        }
                    }
                });


            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String email=mEmail.getText().toString();
                final String password=mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(StudentsLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(StudentsLogin.this, "sign in error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
*/
    }

