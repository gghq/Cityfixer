package com.clr.cityfixer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.content.Intent;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.clr.cityfixer.utils.Constants.*;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    GoogleMap map;
    private boolean locationPermissionGranted;
    private boolean cameraIsOnUser;
    private LatLng location;

    FloatingActionButton btnAddPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        this.cameraIsOnUser = false;

        btnAddPost = (FloatingActionButton)v.findViewById(R.id.btnAddPost);
        return v;
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
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                intent.putExtra("latitude", location.latitude);
                intent.putExtra("longitude", location.longitude);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//        LatLng pp = new LatLng(11,34);
//        MarkerOptions options = new MarkerOptions();
//        options.position(pp).title("qwe");
//        map.addMarker(options);
//        map.moveCamera(CameraUpdateFactory.newLatLng(pp));
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnMapLongClickListener(this);

//        if(checkMapServices()) {
            if(locationPermissionGranted) {
                initialWorkWithMap();
            }
//            else {
//                getLocationPermission();
//            }
//        }
        recursiveCameraCheck();
    }

    private void recursiveCameraCheck() {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!cameraIsOnUser) {
                    cameraIsOnUser = true;
                    getDeviceLocation();
                    recursiveCameraCheck();
                }
            }
        }, 2200);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(checkMapServices()) {
            if(locationPermissionGranted) {
                initialWorkWithMap();
            }
            else {
                getLocationPermission();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {

        map.addMarker(new MarkerOptions().position(point).title(
                point.toString()));

        Toast.makeText(getActivity().getApplicationContext(),
                "New marker added@" + point.toString(), Toast.LENGTH_LONG)
                .show();
    }

    private void initialWorkWithMap() {
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        getDeviceLocation();
    }

    private boolean getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task locationResult = new FusedLocationProviderClient(getActivity()).getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location mLastKnownLocation = (Location)task.getResult();
                            if(mLastKnownLocation == null) {
                                Log.d("MainActivity", "Variable of last location is null");
                                cameraIsOnUser = false;
                                moveCamera();
                                map.getUiSettings().setMyLocationButtonEnabled(false);
                                return;
                            }
                            moveCamera(mLastKnownLocation);
                            cameraIsOnUser = true;
                        } else {
                            Log.d("MainActivity", "Current location is null. Using defaults.");
                            Log.e("MainActivity", "Exception: %s", task.getException());
                            moveCamera();
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("MainActivity: %s", e.getMessage());
        }

        return true;
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
