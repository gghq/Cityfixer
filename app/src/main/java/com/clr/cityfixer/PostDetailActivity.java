package com.clr.cityfixer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {
    TextView textViewStatus, textViewCategory, textViewDate, textViewDescription, textViewLocation, textViewUsername;
    ImageView imageView;
    Button btnApprove, btnIncresePriority;
    Post thisPost;
    DB db;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        textViewCategory = (TextView)findViewById(R.id.textViewCategory);
        textViewDate = (TextView)findViewById(R.id.textViewDate);
        textViewLocation = (TextView)findViewById(R.id.textViewLocation);
        textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        textViewUsername = (TextView)findViewById(R.id.textViewUsername);
        textViewDescription = (TextView)findViewById(R.id.textViewDescription);

        btnApprove = (Button)findViewById(R.id.btnApprove);
        btnIncresePriority = (Button)findViewById(R.id.btnIncresePriority);

        imageView = (ImageView)findViewById(R.id.imageView);

        db = new DB();

        String id = getIntent().getStringExtra("id");
        final boolean isAdmin = Boolean.valueOf(getIntent().getStringExtra("isAdmin"));

        db.DownloadPost(new DB.FirebaseCallbackPost() {
            @Override
            public void CallBack(Post post) {
                thisPost = post;
                db.DownloadImageBefore(new DB.FirebaseCallbackImg() {
                    @Override
                    public void CallBack(Bitmap img) {
                        imageView.setImageBitmap(img);
                    }
                }, post.getId());
                textViewCategory.setText(post.getCategory());
                textViewDate.setText(post.getDate());
                textViewDescription.setText(post.getDescription());
                textViewLocation.setText(post.getLocation().getLatitude() + " - " + post.getLocation().getLongitude());
                textViewStatus.setText(String.valueOf(post.isApproved()));
                textViewUsername.setText(post.getUser().getUserName());
            }
        }, id);

        if(isAdmin){
            btnApprove.setVisibility(View.VISIBLE);
            btnIncresePriority.setVisibility(View.INVISIBLE);
        }
        else{
            btnIncresePriority.setVisibility(View.VISIBLE);
            btnApprove.setVisibility(View.INVISIBLE);
        }

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
