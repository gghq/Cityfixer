package com.clr.cityfixer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
    private DatabaseReference databaseReferenceU = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference databaseReferenceA = FirebaseDatabase.getInstance().getReference("admins");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("images");

    public void SaveUser(User user){
        String id = databaseReferenceU.child("users").push().getKey();
        databaseReferenceU.child(id).setValue(user);
    }

    public void UpdateUser(User user, String id){
        databaseReferenceU.child(id).setValue(user);
    }

    public void DownloadUsers(final FirebaseCallbackUsers firebaseCallback){
        final ArrayList<User> users = new ArrayList<User>();
        databaseReferenceU.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    return;
                }
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    User user = dS.getValue(User.class);
                    users.add(user);
                }
                firebaseCallback.CallBack(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void DownloadUser(final FirebaseCallbackUser firebaseCallback, final String email){
        databaseReferenceU.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    return;
                }
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    User user = dS.getValue(User.class);
                    if(user.getUserEmail().equals(email)){
                        firebaseCallback.CallBack(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("!!!!!!!", databaseError.getMessage());
            }
        });
    }

    public void DownloadUserById(final FirebaseCallbackUser firebaseCallback, final String id){
        databaseReferenceU.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    return;
                }
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    User user = dS.getValue(User.class);
                    if(user.getUserId().equals(id)){
                        firebaseCallback.CallBack(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("!!!!!!!", databaseError.getMessage());
            }
        });
    }

    public void SaveAdmin(String email){
        String id = databaseReferenceA.child("admins").push().getKey();
        databaseReferenceA.child(id).setValue(email);
    }

    public void DownloadAdmins(final FirebaseCallbackAdmins firebaseCallback){
        final ArrayList<String> admins = new ArrayList<String>();
        databaseReferenceA.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    String admin = dS.getValue(String.class);
                    admins.add(admin);
                }
                firebaseCallback.CallBack(admins);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void SaveImageBefore(Intent data, String id){
        StorageReference storageRef = storageReference.child(id + "/before.jpg");

        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] file = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    public void SaveImageAfter(Intent data, String id){
        StorageReference storageRef = storageReference.child(id + "/after.jpg");

        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] file = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    public void DownloadImageBefore(final FirebaseCallbackImg firebaseCallback, String id){
        StorageReference storageRef = storageReference.child(id + "/before.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = ByteArrayToBitmap(bytes);
                firebaseCallback.CallBack(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void DownloadImageAfter(final FirebaseCallbackImg firebaseCallback, String id){
        StorageReference storageRef = storageReference.child(id + "after.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = ByteArrayToBitmap(bytes);
                firebaseCallback.CallBack(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void SavePost(Post post, Intent img){
        String id = databaseReference.child("posts").push().getKey();
        post.setImage(id);
        post.setId(id);
        databaseReference.child(id).setValue(post);
        SaveImageBefore(img, id);
    }

    public void DownloadPost(final FirebaseCallbackPost firebaseCallback, final String id){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    Post post = dS.getValue(Post.class);
                    if(post.getId().equals(id)){
                        firebaseCallback.CallBack(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void UpdatePost(final Post post){
        databaseReference.child(post.getId()).setValue(post);
    }

    public void DownloadPost(final FirebaseCallbackPost firebaseCallback, final LatLng location){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    Post post = dS.getValue(Post.class);
                    if(post.getLocation().equals(location)){
                        firebaseCallback.CallBack(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void DownloadPosts(final FirebaseCallbackPosts firebaseCallback){
        final ArrayList<Post> postList = new ArrayList<Post>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    Post post = dS.getValue(Post.class);
                    postList.add(post);
                }
                firebaseCallback.CallBack(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public interface FirebaseCallbackImg{
        void CallBack(Bitmap img);
    }

    public interface FirebaseCallbackPost{
        void CallBack(Post post);
    }

    public interface FirebaseCallbackPosts{
        void CallBack(ArrayList<Post> postList);
    }

    public interface FirebaseCallbackAdmins{
        void CallBack(ArrayList<String> admins);
    }

    public interface FirebaseCallbackUsers{
        void CallBack(ArrayList<User> users);
    }

    public interface FirebaseCallbackUser{
        void CallBack(User user);
    }

    private Bitmap ByteArrayToBitmap(byte[] byteArray){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }
}

