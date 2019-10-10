package com.clr.cityfixer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    ListView listViewPosts;
    ArrayList<Post> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);

        listViewPosts = (ListView)v.findViewById(R.id.listViewPosts);

        postList = ((MainActivity)getActivity()).postsList;

        if(postList != null){
            PostsList adapter = new PostsList(getActivity(), postList);
            listViewPosts.setAdapter(adapter);
        }
        return v;
    }


}
