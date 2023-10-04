package com.example.client.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.client.R;
import com.example.client.account.Login;

public class Introduce extends AppCompatActivity {
    private ImageView ivIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        ivIntro = findViewById(R.id.ivIntro);
        Glide.with(this).load(R.drawable.intro1).into(ivIntro);

        new Handler().postDelayed((Runnable)(new Runnable() {
            public final void run() {
                Intent intent = new Intent(Introduce.this, Login.class);
                startActivity(intent);
                finish();
            }
        }), 4000);

        // status bar
        getWindow().setStatusBarColor(ContextCompat.getColor(Introduce.this, R.color.bgr_intro));

    }
}