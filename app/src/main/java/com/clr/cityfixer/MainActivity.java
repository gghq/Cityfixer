package com.clr.cityfixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment accFragment;
    private Fragment listFragment;
    private Fragment helpFragment;
    DB db;
    ArrayList<Post> postsList;
    ArrayList<String> adminsList;
    ArrayList<User> usersList;
    SharedPreferences preferences;
    public User loginedUser;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeFragment = new HomeFragment();
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        db = new DB();
        //preferences = getBaseContext().getSharedPreferences("MODEL_PREFERENCES", Context.MODE_PRIVATE);
        preferences = getSharedPreferences("MODEL_PREFERENCES", Context.MODE_PRIVATE);
        if(preferences.getString("currentUser",null) != null)
        {
            userEmail = preferences.getString("currentUser",null).split("/")[0];
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                androidx.fragment.app.Fragment selectedFragment = null;

                switch(menuItem.getItemId())
                {
                    case R.id.nav_home:
                        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
                            @Override
                            public void CallBack(ArrayList<Post> postList) {
                                postsList = postList;
                                db.DownloadUser(new DB.FirebaseCallbackUser() {
                                    @Override
                                    public void CallBack(User user) {
                                        loginedUser = user;
                                        if(homeFragment  == null)
                                            homeFragment = new HomeFragment();
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                                    }
                                }, userEmail);
                            }
                        });
                        break;
                    case R.id.nav_help:
                        if(helpFragment  == null)
                            helpFragment = new HelpFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, helpFragment).commit();
                        break;
                    case R.id.nav_list:
                        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
                            @Override
                            public void CallBack(ArrayList<Post> postList) {
                                postsList = postList;
                                db.DownloadAdmins(new DB.FirebaseCallbackAdmins() {
                                    @Override
                                    public void CallBack(ArrayList<String> admins) {
                                        adminsList = admins;
                                        if(listFragment  == null)
                                            listFragment = new ListFragment();
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.nav_acc:
                        db.DownloadUsers(new DB.FirebaseCallbackUsers() {
                            @Override
                            public void CallBack(ArrayList<User> users) {
                                usersList = users;
                                if(accFragment  == null)
                                    accFragment = new AccountFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, accFragment).commit();
                            }
                        });
                        break;
                }

                return true;
            }
        };
}

