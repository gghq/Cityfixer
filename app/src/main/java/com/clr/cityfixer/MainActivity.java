package com.clr.cityfixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    private Fragment errorListFragment;

    DB db;
    ArrayList<Post> postsList;
    ArrayList<String> adminsList;
    ArrayList<User> usersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeFragment = new HomeFragment();
        accFragment = new AccountFragment();
        listFragment = new ListFragment();
        helpFragment = new HelpFragment();

        errorListFragment = new ErrorConnection();

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        db = new DB();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final androidx.fragment.app.Fragment selectedFragment = null;

                switch(menuItem.getItemId())
                {
                    case R.id.nav_home:
                        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
                            @Override
                            public void CallBack(ArrayList<Post> postList) {
                                db.DownloadAdmins(new DB.FirebaseCallbackAdmins() {
                                    @Override
                                    public void CallBack(ArrayList<String> admins) {
                                        adminsList = admins;
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                                    }
                                });
                            }
                        });
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                        break;
                    case R.id.nav_help:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, helpFragment).commit();
                        break;
                    case R.id.nav_list:
                        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
                            @Override
                            public void CallBack(ArrayList<Post> postList) {
                                postsList = postList;

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
                            }
                        });
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                        break;
                    case R.id.nav_acc:
                        db.DownloadUsers(new DB.FirebaseCallbackUsers() {
                            @Override
                            public void CallBack(ArrayList<User> users) {
                                usersList = users;
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, accFragment).commit();
                            }
                        });
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                        break;
                }
                return true;
            }
        };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

