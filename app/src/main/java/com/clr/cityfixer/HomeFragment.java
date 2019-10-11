package com.clr.cityfixer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

import static com.clr.cityfixer.utils.Constants.*;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    GoogleMap map;
    private boolean locationPermissionGranted;
    private boolean cameraIsOnUser;
    private LatLng lastKnownLocation;
    private Marker lastKnownMarker;
    private FusedLocationProviderClient fusedLocationClient;
//    private DB db;
    private ArrayList<Post> postArrayList;
    User loginedUser;
    DB db = new DB();

    CircleButton btnAddPost;

    private boolean addWasAsked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        this.cameraIsOnUser = false;

        db = new DB();
        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
            @Override
            public void CallBack(ArrayList<Post> postList) {
                postArrayList = postList;
                displayPosts();
            }
        });

        btnAddPost = (CircleButton)v.findViewById(R.id.btnAddPost);
        btnAddPost.setVisibility(((MainActivity)getActivity()).buttonVisible ? View.VISIBLE : View.INVISIBLE);

        return v;
    }

    public void hideButton() {
        this.btnAddPost.setVisibility(View.INVISIBLE);
    }

    public void showButton() {
        this.btnAddPost.setVisibility(View.VISIBLE);
    }

    private void updatePosts() {
        db.DownloadPosts(new DB.FirebaseCallbackPosts() {
            @Override
            public void CallBack(ArrayList<Post> postList) {
                postArrayList = postList;
                displayPosts();
            }
        });
    }

    private void displayPosts() {
        for (Post post : postArrayList) {
            if(post != null)
            placeMarker(post);
        }
    }

    private void placeMarker(Post post) {
        if(map != null)
        map.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(post.getLocation().getLatitude()), Double.valueOf(post.getLocation().getLongitude())))
                .title("Hello world"));
    }

    private void placeMarker(LatLng position) {
        map.addMarker(new MarkerOptions()
                .position(position)
                .title("Hello world"));
    }

    private void placeLastKnownMarker() {
        if(map != null)
        if(lastKnownMarker != null)
            lastKnownMarker.setVisible(false);

        lastKnownMarker = map.addMarker(new MarkerOptions()
                .position(lastKnownLocation)
                .title("Hello world"));

        lastKnownMarker.setVisible(true);
    }
    private void placeLastKnownMarker(LatLng position) {
        if(map != null)
        if(lastKnownMarker != null) {
            lastKnownLocation = position;
            lastKnownMarker.setVisible(false);
            lastKnownMarker = map.addMarker(new MarkerOptions()
                .position(position)
                .title("Hello world"));
            lastKnownMarker.setVisible(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addWasAsked) {
                    if(((MainActivity)getActivity()).userEmail != null) {
                        db.DownloadUser(new DB.FirebaseCallbackUser() {
                            @Override
                            public void CallBack(User user) {
                                loginedUser = user;
                                ((MainActivity) getActivity()).loginedUser = user;
                                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                                intent.putExtra("latitude", String.valueOf(lastKnownLocation.latitude));
                                intent.putExtra("longitude", String.valueOf(lastKnownLocation.longitude));
                                intent.putExtra("username", user.getUserName());
                                intent.putExtra("email", user.getUserEmail());
                                startActivity(intent);
                            }
                        }, ((MainActivity) getActivity()).userEmail);
                    }
                    addWasAsked = false;
//                    updatePosts();
                }
                else {
                    updateLastLocation();
                    moveCamera(lastKnownLocation);
                    placeLastKnownMarker();

                    Toast.makeText(getActivity().getApplicationContext(),
                        "Дана позиція є правильною? Якщо ні - оберіть потрібну позицію довгим натиском на марі", Toast.LENGTH_LONG)
                        .show();
                    addWasAsked = true;
                }
            }
        });
//        if(((MainActivity)getActivity()).userEmail != null){
//            btnAddPost.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    db.DownloadUser(new DB.FirebaseCallbackUser() {
//                        @Override
//                        public void CallBack(User user) {
//                            loginedUser = user;
//                            ((MainActivity)getActivity()).loginedUser = user;
//                            Intent intent = new Intent(getActivity(), AddPostActivity.class);
//                            updateLastLocation();
//                            intent.putExtra("latitude", String.valueOf(lastKnownLocation.latitude));
//                            intent.putExtra("longitude", String.valueOf(lastKnownLocation.longitude));
//                            intent.putExtra("username", user.getUserName());
//                            intent.putExtra("email", user.getUserEmail());
//                            startActivity(intent);
//                        }
//                    }, ((MainActivity)getActivity()).userEmail);
//                }
//            });
//        }
//        else{
//            Toast.makeText(getActivity(), "You are not loggined", Toast.LENGTH_LONG);
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnMapLongClickListener(this);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        this.lastKnownLocation = DEFAULT_POSITION;
        updateLastLocation();

        this.addWasAsked = false;

        if(locationPermissionGranted) {
            updateLastLocation();
            initialWorkWithMap();
        }
//        recursiveCameraCheck();
    }

    private void recursiveCameraCheck() {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!cameraIsOnUser) {
                    cameraIsOnUser = true;
                    moveToDeviceLocation();
                    recursiveCameraCheck();
                }
            }
        }, 1200);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(checkMapServices()) {
            if(locationPermissionGranted) {
                initialWorkWithMap();
                updatePosts();
            }
            else {
                getLocationPermission();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        if(addWasAsked) {
            placeLastKnownMarker(point);
        }
//        map.addMarker(new MarkerOptions().position(point).title(
//                point.toString()));
//
//        Toast.makeText(getActivity().getApplicationContext(),
//                "New marker added@" + point.toString(), Toast.LENGTH_LONG)
//                .show();
    }

    private void initialWorkWithMap() {
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        moveToDeviceLocation();
    }

    private void moveToDeviceLocation() {
        updateLastLocation();
        moveCamera(this.lastKnownLocation);
        this.cameraIsOnUser = true;
//        try {
//            if (locationPermissionGranted) {
//                Task locationResult = new FusedLocationProviderClient(getActivity()).updateLastLocation();
//                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            Location mLastKnownLocation = (Location)task.getResult();
//                            if(mLastKnownLocation == null) {
//                                Log.d("MainActivity", "Variable of last location is null");
//                                cameraIsOnUser = false;
//                                moveCamera();
//                                map.getUiSettings().setMyLocationButtonEnabled(false);
//                                return;
//                            }
//                            lastKnownLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//                            moveCamera(mLastKnownLocation);
//                            cameraIsOnUser = true;
//                        } else {
//                            Log.d("MainActivity", "Current location is null. Using defaults.");
//                            Log.e("MainActivity", "Exception: %s", task.getException());
//                            moveCamera();
//                        }
//                    }
//                });
//            }
//        } catch(SecurityException e)  {
//            Log.e("MainActivity: %s", e.getMessage());
//        }
    }

    private void updateLastLocation() {
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        );

    }

    private void moveCamera(LatLng position, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }
    private void moveCamera(LatLng position) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
    }
    private void moveCamera(Location position) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.getLatitude(), position.getLongitude()), DEFAULT_ZOOM));
    }
    private void moveCamera() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_POSITION, DEFAULT_ZOOM));
    }






    // *************************** functions to check if everything is ok w/ phone

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Для коректної робити програми потрібно активувати навігацію. Активувати?")
                .setCancelable(false)
                .setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton("Ні, вийти", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        finishAffinity();
                        System.exit(0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.i("MainActivity", "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d("MainActivity", "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d("MainActivity", "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (locationPermissionGranted) {

                } else {
                    getLocationPermission();
                }
            }
        }
    }

}
