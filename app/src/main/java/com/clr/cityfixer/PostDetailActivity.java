package com.clr.cityfixer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

public class PostDetailActivity extends AppCompatActivity {
    TextView textViewStatus, textViewCategory, textViewDate, textViewDescription, textViewUsername, textViewPriority;
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
        textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        textViewUsername = (TextView)findViewById(R.id.textViewUsername);
        textViewDescription = (TextView)findViewById(R.id.textViewDescription);
        textViewPriority = (TextView)findViewById(R.id.textViewPriority);

        btnApprove = (Button)findViewById(R.id.btnApprove);
        btnIncresePriority = (Button)findViewById(R.id.btnIncresePriority);

        imageView = (ImageView)findViewById(R.id.imageView);

        db = new DB();

        String id = getIntent().getStringExtra("id");
        final boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        DownloadPost(id);

        if(isAdmin){
            btnApprove.setVisibility(View.VISIBLE);
            btnIncresePriority.setVisibility(View.INVISIBLE);

            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else{
            btnIncresePriority.setVisibility(View.VISIBLE);
            btnApprove.setVisibility(View.INVISIBLE);

            btnIncresePriority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.DownloadPost(new DB.FirebaseCallbackPost() {
                        @Override
                        public void CallBack(final Post post) {
                            thisPost = post;
                            db.DownloadUser(new DB.FirebaseCallbackUser() {
                                @Override
                                public void CallBack(User user) {
                                    thisPost.setPriority(thisPost.getPriority()+1);
                                    if(user.getUserPoints() >= 1){
                                        user.setUserPoints(user.getUserPoints()-1);
                                        db.UpdateUser(user, thisPost.getId());
                                        db.UpdatePost(thisPost);
                                        Toast.makeText(getApplicationContext(), "Пріоритет успішно збільшений", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Недостатньо балів", Toast.LENGTH_LONG).show();
                                    }
                                    DownloadPost(thisPost.getId());
                                }
                            }, thisPost.getUser().getUserEmail());
                        }
                    }, thisPost.getId());
                }
            });
        }
    }

    private void DownloadPost(String id){
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
                //textViewLocation.setText(post.getLocation().getLatitude() + " - " + post.getLocation().getLongitude());
                String approved;
                if(post.isApproved()){
                    approved = "Підтверджено";
                }
                else{
                    approved = "Не підтверджено";
                }
                textViewStatus.setText(approved);
                textViewUsername.setText(post.getUser().getUserName());
                textViewPriority.setText(String.valueOf(post.getPriority()));
            }
        }, id);
    }
}
