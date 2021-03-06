package com.example.tripplannew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tripplannew.data.webservice.Place;
import com.example.tripplannew.viewmodels.PlaceOfInterestViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.Executor;

public class FindPlacesFragment extends Fragment implements OnMapReadyCallback {

    private PlaceOfInterestViewModel mPlaceOfInterestViewModel;

    private GoogleMap mGoogleMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LatLng mMyLocation;

    private int locationRequestCode = 1000;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private boolean isGPS = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_places, container, false);

        mPlaceOfInterestViewModel = new ViewModelProvider(getActivity()).get(PlaceOfInterestViewModel.class);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        mLocationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null)
                {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        findNearbyPlaces(location);
                        if (mFusedLocationProviderClient != null) {
                            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                        }
                    }
                }
            }
        };


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
        }
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null)
                {
                    findNearbyPlaces(location);
                }
                else
                {
                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener((Executor) this, location -> {
                        if (location != null)
                        {
                            findNearbyPlaces(location);
                        }
                        else
                        {
                            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void findNearbyPlaces(Location location)
    {
        mMyLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mGoogleMap.addMarker(new MarkerOptions().position(mMyLocation).title("Vị trí của tôi"));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMyLocation, 15));

        String placeType = mPlaceOfInterestViewModel.getPlaceType();

        mPlaceOfInterestViewModel.getNearbyPlaces(mMyLocation.latitude, mMyLocation.longitude, placeType, 20000)
                .observe(getActivity(), placeList -> {
                    for(int i = 0; i < placeList.size(); ++i)
                    {
                        Place place = placeList.get(i);
                        LatLng latLng = new LatLng(place.getLat(), place.getLon());

                        String name = "Unknown";

                        if(place.getName() != null)
                        {
                            name = place.getName();
                        }

                        mGoogleMap.addMarker(
                                new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                        .alpha(0.8f)
                                        .title(name)

                        );
                    }
                });;
    }
}