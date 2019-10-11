package com.clr.cityfixer;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class AddPostActivity extends AppCompatActivity {
    ImageView imgView;
    Button btnSave;
    Spinner spinnerCategories;
    Intent takenImage;
    public DB db;
    Dexter dexter;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imgView = (ImageView)findViewById(R.id.imgViewPhoto);

        btnSave = (Button)findViewById(R.id.btnSend);

        spinnerCategories = (Spinner)findViewById(R.id.spinnerCategories);

        db = new DB();
        //dexter.withPermission(Manifest.permission.CAMERA);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User(getIntent().getStringExtra("username"), getIntent().getStringExtra("email"), 1);
                String latitude = getIntent().getStringExtra("latitude");
                String longitude = getIntent().getStringExtra("longitude");
                String description = "";
                PostLocation location = new PostLocation(latitude, longitude);
                String date = Calendar.getInstance().getTime().toString();
                String category = spinnerCategories.getSelectedItem().toString();
                int priority = 0;
                boolean approved = false;

                Post post = new Post(user, description, location, date, category, approved, priority);
                db.SavePost(post, takenImage);

                Intent myIntent = new Intent(AddPostActivity.this, MainActivity.class);
                AddPostActivity.this.startActivity(myIntent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            takenImage = data;
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imgView.setImageBitmap(bitmap);
        }
    }
}
