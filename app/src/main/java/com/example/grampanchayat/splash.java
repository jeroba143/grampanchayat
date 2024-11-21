package com.example.grampanchayat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIMEOUT = 3000;  // 3 seconds for splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get the views from the layout
        ImageView imageView = findViewById(R.id.imageView4);

        // Load the fade and zoom-in animation
        Animation fadeZoomAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply the animation to the ImageView and TextView
        imageView.startAnimation(fadeZoomAnimation);


        // Transition to the login activity after the animation finishes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash.this, MainActivity.class);
                startActivity(intent);
                finish();  // Close splash screen activity so it's not in the back stack
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}











