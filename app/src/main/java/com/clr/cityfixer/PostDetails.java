package com.clr.cityfixer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PostDetails extends Fragment {
    TextView textViewStatus, textViewCategory, textViewDate, textViewDescription, textViewLocation, textViewUsername;
    ImageView imageView;
    Button btnApprove, btnIncresePriority;
    Post thisPost;
    DB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_details,container,false);

        textViewCategory = (TextView)v.findViewById(R.id.textViewCategory);
        textViewDate = (TextView)v.findViewById(R.id.textViewDate);
        textViewStatus = (TextView)v.findViewById(R.id.textViewStatus);
        textViewUsername = (TextView)v.findViewById(R.id.textViewUsername);
        textViewDescription = (TextView)v.findViewById(R.id.textViewDescription);

        btnApprove = (Button)v.findViewById(R.id.btnApprove);
        btnIncresePriority = (Button)v.findViewById(R.id.btnIncresePriority);

        imageView = (ImageView)v.findViewById(R.id.imageView);

        String id = getArguments().getString("id");

        db = new DB();

        db.DownloadPost(new DB.FirebaseCallbackPost() {
            @Override
            public void CallBack(Post post) {
                thisPost = post;
            }
        }, id);

        return v;
    }
}
