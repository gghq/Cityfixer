package com.clr.cityfixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment accFragment;
    private Fragment listFragment;
    private Fragment helpFragment;
    DB db;
    ArrayList<Post> postsList;
    ArrayList<String> adminsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeFragment = new HomeFragment();
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        db = new DB();

        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
            @Override
            public void CallBack(ArrayList<Post> postList) {
                postsList = postList;
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    androidx.fragment.app.Fragment selectedFragment = null;

                    switch(menuItem.getItemId())
                    {
                        case R.id.nav_home:
                            if(homeFragment  == null)
                                homeFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                            break;
                        case R.id.nav_help:
                            if(helpFragment  == null)
                                helpFragment = new AccountFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, helpFragment).commit();
                            break;
                        case R.id.nav_list:
                            if(accFragment  == null)
                                listFragment = new ListFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
                            break;
                        case R.id.nav_acc:
                            if(accFragment  == null)
                                accFragment = new AccountFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, accFragment).commit();
                            break;
                    }

                    return true;
                }
            };
}

