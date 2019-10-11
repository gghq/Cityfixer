package com.clr.cityfixer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class ListFragment extends Fragment {

    ListView listViewPosts;
    ArrayList<Post> postList;
    ArrayList<String> adminsList;
    //User user;
    String user;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);

        listViewPosts = (ListView)v.findViewById(R.id.listViewPosts);

        postList = ((MainActivity)getActivity()).postsList;
        adminsList = ((MainActivity)getActivity()).adminsList;

        preferences = getContext().getSharedPreferences("MODEL_PREFERENCES", Context.MODE_PRIVATE);
        if(preferences.getString("currentUser",null) != null)
        {
            user = preferences.getString("currentUser",null).split("/")[0];
        }

        //if(((MainActivity)getActivity()).loginedUser != null){
            //user = ((MainActivity)getActivity()).loginedUser;
            listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(getActivity(), PostDetailActivity.class);
                    myIntent.putExtra("id", postList.get(position).getId());
                    myIntent.putExtra("isAdmin", isAdmin(user));//  String.valueOf(isAdmin("admtgrsein@gmail.com")));
                    startActivity(myIntent);
                }
            });
        //}

        if(postList != null){
            PostsList adapter = new PostsList(getActivity(), postList);
            listViewPosts.setAdapter(adapter);
        }
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

}
