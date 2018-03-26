package com.example.fazlulhoque.iiucbustracking.Student;

/**
 * Created by Fazlul Hoque on 12/10/2017.
 */

public class StudentUser {
    private String email,password,name,phone;

    StudentUser(){
    }

    public StudentUser(String email, String password) {
        this.email = email;
        this.password = password;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
