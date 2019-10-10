package com.clr.cityfixer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(Splashscreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withLogo(R.mipmap.clr_logo);



        View  view = config.create();
        setContentView(view);
//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);
//        finish();
    }
}
