package com.example.client.me;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteProfile extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private Button btnDeleteUser, btnConfirm;
    private EditText etPassword;
//    private ProgressBar progressBar;
    private String userPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnConfirm = findViewById(R.id.btnConfirm);
        etPassword = findViewById(R.id.etPassword);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        // Disable delete user button until enter password
        btnDeleteUser.setEnabled(false);

    }
}