package com.clr.cityfixer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import gr.net.maroulis.library.EasySplashScreen;

public class Splashscreen extends AppCompatActivity {
    DB db = new DB();
    public static User loginedUser;
    SharedPreferences preferences;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(Splashscreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withLogo(R.mipmap.clr_logo);

        preferences = getSharedPreferences("MODEL_PREFERENCES", Context.MODE_PRIVATE);
        if(preferences.getString("currentUser",null) != null)
        {
            userEmail = preferences.getString("currentUser",null).split("/")[0];
        }

        db.DownloadUser(new DB.FirebaseCallbackUser() {
            @Override
            public void CallBack(User user) {
                loginedUser = user;
            }
        }, userEmail);

        View  view = config.create();
        setContentView(view);
//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);
//        finish();
    }
}
