package com.clr.cityfixer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;

import static android.content.ContentValues.TAG;

public class AccountFragment extends Fragment {
    private Activity context;
    private List<Post> postsList;
    private static final int RC_SIGN_IN = 1;
    private Button mGoogleBtn;
    private Button logOutButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String currentUser;
    private AppCompatAutoCompleteTextView text;
    private ImageView image;
    private ProgressBar progressBar;
    private SharedPreferences.Editor editor;
    private  SharedPreferences preferences;
    private User appUser;
    private ArrayList<User> usersList;
    private DB db;
    ListView Listuserpost;

    private HomeFragment homeFragment;

    ArrayList<Post> postList;
    ArrayList<String> adminsList;
    String user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_acc,container,false);

        Listuserpost = (ListView)v.findViewById(R.id.listuser);

        postList = ((MainActivity)getActivity()).postsList;
        adminsList = ((MainActivity)getActivity()).adminsList;

        preferences = getContext().getSharedPreferences("MODEL_PREFERENCES", Context.MODE_PRIVATE);
        if(preferences.getString("currentUser",null) != null)
        {
            user = preferences.getString("currentUser",null).split("/")[0];
        }

        //if(((MainActivity)getActivity()).loginedUser != null){
        //user = ((MainActivity)getActivity()).loginedUser;
        Listuserpost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(getActivity(), PostDetailActivity.class);
                myIntent.putExtra("id", postList.get(position).getId());
                myIntent.putExtra("isAdmin", isAdmin(user));//  String.valueOf(isAdmin("admtgrsein@gmail.com")));
                startActivity(myIntent);
            }
        });
        //}

//        if(postList != null){
//            PostsList adapter = new PostsList(getActivity(), postList);
//            Listuserpost.setAdapter(adapter);
//        }

        if(postList != null){
            ArrayList<Post> userPosts=new ArrayList<Post>();
            for(Post post:postList)
            {
                if(post.getUser().getUserEmail()==user)
                {
                    userPosts.add(post);
                }
            }
            PostsList adapter = new PostsList(getActivity(), userPosts);
            Listuserpost.setAdapter(adapter);
            Listuserpost.refreshDrawableState();
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(getActivity(),gso);
        mAuth = FirebaseAuth.getInstance();

        usersList = ((MainActivity)getActivity()).usersList;

        db = new DB();
        return v;
    }
    public boolean isAdmin(String email){
        for(int i = 0; i < adminsList.size(); i++){
            if(adminsList.get(i).equals(email)){
                return true;
            }
        }
        return false;
    }


    public void initField(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //    mGoogleBtn=(SignInButton) getView().findViewById(R.id.googleBtn);
        mGoogleBtn = (Button) getView().findViewById(R.id.login);

        text = (AppCompatAutoCompleteTextView) getView().findViewById(R.id.text);
        image = (ImageView)getView().findViewById(R.id.image);
        progressBar = (ProgressBar)getView().findViewById(R.id.progress);
        logOutButton = (Button)getView().findViewById(R.id.logout);
        Listuserpost = (ListView)getView().findViewById(R.id.listuser);
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
           preferences = getContext().getSharedPreferences("MODEL_PREFERENCES",Context.MODE_PRIVATE);
            if(preferences.getString("currentUser",null)!= null)
            {
                currentUser = (preferences.getString("currentUser",null));

                String[] arr = currentUser.split("/");
                String email=arr[0];

                if(usersList != null){

                    appUser = FindUserByEmail(email);

                    if(appUser != null){
                        text.setText("");
                        text.append(appUser.getUserEmail()+"\n");
                        text.append(appUser.getUserName()+"\n");
                    }
                }

                logOutButton.setVisibility(View.VISIBLE);
                mGoogleBtn.setVisibility(View.INVISIBLE);
            }
        editor = getContext().getSharedPreferences("MODEL_PREFERENCES",Context.MODE_PRIVATE).edit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                //            currentUser=user;
                        //    gson.toJson(user);
                          //  editor.putString("currentUser",gson.toJson(user.getEmail()+"/"+user.getDisplayName()+"/"+user.getPhotoUrl()));
                            editor.putString("currentUser",(user.getEmail()+"/"+user.getDisplayName()+"/"+String.valueOf(user.getPhotoUrl())));


                            editor.apply();
                            if(FindUserByEmail(user.getEmail()) == null){
                                appUser = new User(user.getEmail(), user.getDisplayName(), 1);
                                db.SaveUser(appUser);
                            }
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);

                        }

                        // ...
                    }
                });
    }

    private void SerializeUser(FirebaseUser user)
    {
        try {
            FileOutputStream fos = getActivity().getApplicationContext().openFileOutput("currentUser", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(user);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private FirebaseUser DeserializeUser()
    {
        try {


            FileInputStream fis = getActivity().getApplicationContext().openFileInput("currentUser");
            ObjectInputStream is = new ObjectInputStream(fis);
            FirebaseUser user = (FirebaseUser) is.readObject();
            is.close();
            fis.close();
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private void updateUI(FirebaseUser user)
    {
        if(user != null)
        {
            String name=user.getDisplayName();
            String email=user.getEmail();
            String photo=String.valueOf(user.getPhotoUrl());
            Picasso.get().load(photo).into(image);
            text.setText("");
            text.append(name+"\n");
            text.append(email+"\n");
            logOutButton.setVisibility(View.VISIBLE);
            mGoogleBtn.setVisibility(View.INVISIBLE);
            Listuserpost.setVisibility((View.VISIBLE));

            homeFragment.showButton();
            ((MainActivity)getActivity()).buttonVisible = true;

            if(postList != null){
                ArrayList<Post> userPosts=new ArrayList<Post>();
                for(Post post:postList)
                {
                    if(post.getUser().getUserEmail()==email)
                    {
                        userPosts.add(post);
                    }
                }
                PostsList adapter = new PostsList(getActivity(), userPosts);
                Listuserpost.setAdapter(adapter);
                Listuserpost.refreshDrawableState();
            }
            
        }
        else{
            text.setText("Login");
            logOutButton.setVisibility(View.INVISIBLE);
            mGoogleBtn.setVisibility(View.VISIBLE);
            Listuserpost.setVisibility((View.INVISIBLE));
            homeFragment.hideButton();
            ((MainActivity)getActivity()).buttonVisible = false;
        }

    }

    private void Logout()
    {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
        editor.putString("currentUser",null);
        editor.apply();
    }

    private User FindUserByEmail(String email){
        if(usersList == null){
            return null;
        }
        for(User user: usersList){
            if(user.getUserEmail().equals(email)){
                return user;
            }
        }
        return null;
    }
}