package com.example.fazlulhoque.iiucbustracking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fazlulhoque.iiucbustracking.Admin.Admin;
import com.example.fazlulhoque.iiucbustracking.Driver.DriveActivity;
import com.example.fazlulhoque.iiucbustracking.Student.StudentMapActivity;
import com.example.fazlulhoque.iiucbustracking.Student.StudentsLogin;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    Button btnDrive,btnStudent,btnAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Arkhip_font.ttf").setFontAttrId(R.attr.fontPath).build());

        setContentView(R.layout.activity_home);

        btnDrive=(Button)findViewById(R.id.btnDrive);
        btnStudent=(Button)findViewById(R.id.btnStudent);
        btnAdmin=(Button)findViewById(R.id.btnAdmin);

        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this,DriveActivity.class);
                startActivity(intent);
            }
        });

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this, StudentsLogin.class);
                startActivity(intent);
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this,Admin.class);
                startActivity(intent);
            }
        });
    }
}
