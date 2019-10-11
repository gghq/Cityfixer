package com.clr.cityfixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import static com.clr.cityfixer.utils.Constants.*;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private Fragment accFragment;
    private Fragment listFragment;
    private Fragment helpFragment;

    private Fragment errorListFragment;

    DB db;
    ArrayList<Post> postsList;
    ArrayList<String> adminsList;
    ArrayList<User> usersList;
    SharedPreferences preferences;
    public User loginedUser;
    String userEmail;

    public boolean buttonVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeFragment = new HomeFragment();
        accFragment = new AccountFragment();
        ((AccountFragment)accFragment).initField(homeFragment);
        helpFragment = new HelpFragment();

        errorListFragment = new ErrorConnection();

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        db = new DB();

        SharedPreferences sp;
        sp = getSharedPreferences("MODEL_PREFERENCES", MODE_PRIVATE);

        if(sp.getString("currentUser", null) == null)
            buttonVisible = false;
        else buttonVisible = true;

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
                final androidx.fragment.app.Fragment selectedFragment = null;

                switch(menuItem.getItemId())
                {
                    case R.id.nav_home:
                        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
                            @Override
                            public void CallBack(ArrayList<Post> postList) {
                                postsList = postList;
                                db.DownloadUser(new DB.FirebaseCallbackUser() {
                                    @Override
//                                    public void CallBack(ArrayList<String> admins) {
//                                        adminsList = admins;

                                    public void CallBack(User user) {
                                        loginedUser = user;
                                        if(homeFragment  == null)
                                            homeFragment = new HomeFragment();
                                        try {
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                                            if (!isNetworkAvailable())
                                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                                        }catch (Exception e){
                                            e.getMessage();
                                        }
                                    }
                                }, userEmail);

                            }
                        });
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                        break;
//                    case R.id.nav_help:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, helpFragment).commit();
//                        break;
                    case R.id.nav_list:
                        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
                            @Override
                            public void CallBack(ArrayList<Post> postList) {
                                postsList = postList;

//                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
//                                if(!isNetworkAvailable())
//                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
//                            }
//                        });
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                                db.DownloadAdmins(new DB.FirebaseCallbackAdmins() {
                                    @Override
                                    public void CallBack(ArrayList<String> admins) {
                                        adminsList = admins;
                                        if(listFragment  == null)
                                            listFragment = new ListFragment();
                                        try {
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
                                        } catch (Exception ex) {

                                        }
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
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, accFragment).commit();
                                if(!isNetworkAvailable())
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                            }
                        });
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, errorListFragment).commit();
                        break;
                }
                return true;
            }
        };

    public void viewMarker(LatLng position) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        homeFragment.moveCamera(position, BIGGER_ZOOM);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

