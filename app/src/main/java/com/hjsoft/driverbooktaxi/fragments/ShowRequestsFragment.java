package com.hjsoft.driverbooktaxi.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.KalmanLatLong;
import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.activity.HomeActivity;
import com.hjsoft.driverbooktaxi.activity.MainActivity;
import com.hjsoft.driverbooktaxi.activity.OutStationRideStartActivity;
import com.hjsoft.driverbooktaxi.activity.OutStationTrackRideActivity;
import com.hjsoft.driverbooktaxi.activity.RideLocal;
import com.hjsoft.driverbooktaxi.activity.RideOngoingLocal;
import com.hjsoft.driverbooktaxi.activity.RideOngoingOutstation;
import com.hjsoft.driverbooktaxi.activity.RideOutstation;
import com.hjsoft.driverbooktaxi.activity.RideStartActivity;
import com.hjsoft.driverbooktaxi.activity.SpecificOutStationRideOngoingActivity;
import com.hjsoft.driverbooktaxi.activity.SpecificRideOngoingActivity;
import com.hjsoft.driverbooktaxi.activity.TrackRideActivity;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.model.AllRidesPojo;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.LocationUpdates;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.model.ServiceLocationPojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 26/12/16.
 */
public class ShowRequestsFragment extends Fragment  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected boolean mRequestingLocationUpdates;
    final static int REQUEST_LOCATION = 199;
    protected Location mLastLocation;
    double latitude,longitude,current_lat,current_long;
    Geocoder geocoder;
    List<Address> addresses;
    LatLng lastLoc,curntloc;
    String complete_address;
    float[] results=new float[3];
    long res=0;
    boolean entered=false;
    TextView tvCloc;
    API REST_CLIENT;
    HashMap<String, String> user;
    SessionManager session;
    String stProfileId;
    String city;
    List<CabRequestsPojo> dataList;
    ArrayList<GuestData>  cabData=new ArrayList<>();
    Handler handler,h,hLoc;
    Runnable r,rr,rLoc;
    Marker cab;
    CabRequestsPojo data;
    BottomSheetDialogFragment myBottomSheet;
    ImageButton ibLogout;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    View dialogView;
    View rootView;
    int count=0;
    MediaPlayer mp;
    String companyId="CMP00001";
    LayoutInflater inflater;
    TextView tvCity;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    TextView tvLoc1,tvLoc2,tvLoc3,tvLoc4,tvLoc5;
    int j=0;
    boolean accepted=false;
    //boolean dataPresent=false;
    boolean first=true;
    int userVolume;
    AudioManager mAudioManager;
    ArrayList<AllRidesPojo> allRidesDataList;
    Date date1;
    DBAdapter dbAdapter;
    int position;
    LinearLayout llBottom;
    TextView btOk;
    AllRidesPojo d;
    boolean ongoingBooking=true;
    ArrayList<FormattedAllRidesData> rideDataList=new ArrayList<>();
    HomeActivity a;
    //NotificationManager notificationManager;
    SwitchCompat switchCompat;
    String version="1";
    //String version="4.2";
    //FormattedAllRidesData f;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_requests, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        tvCloc=(TextView)rootView.findViewById(R.id.ar_tv_cloc);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        ibLogout=(ImageButton)rootView.findViewById(R.id.ar_ib_logout);
        tvCity=(TextView)rootView.findViewById(R.id.ar_tv_city);

        llBottom=(LinearLayout)rootView.findViewById(R.id.ar_ll_below);
        btOk=(TextView)rootView.findViewById(R.id.ar_bt_ok);
        switchCompat=(SwitchCompat)rootView.findViewById(R.id.switchButton);

        REST_CLIENT= RestClient.get();
        session = new SessionManager(getActivity());
        user = session.getUserDetails();
        stProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        city=pref.getString("city",null);
        tvCity.setText(city);

        mRequestingLocationUpdates=false;

        dialogBuilder = new AlertDialog.Builder(getActivity());

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectServiceLocations();
            }
        });

        String currentStatus=pref.getString("status","online");

        if(currentStatus.equals("online"))
        {
            switchCompat.setText("Online");
            switchCompat.setChecked(true);
        }
        else {

            switchCompat.setText("Offline");
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String status=pref.getString("status","online");

                if(status.equals("online")) {

                    goingOffline();
                }
                else {

                    goingOnline();
                }
            }
        });

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.logoutUser();
                Intent i=new Intent(getActivity(),MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        if(Build.VERSION.SDK_INT<23)
        {
            establishConnection();
        }
        else
        {
            if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                establishConnection();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(getActivity(),"Location Permission is required for this app to run !",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*editor.putString("booking","in");
                editor.putBoolean("saved",false);
                editor.commit();*/


                //if(d.getTravelType().equals("local")||d.getTravelType().equals("Packages"))
                if(d.getTravelType().equals("local"))
                {

                    ArrayList<LocationUpdates> dList=dbAdapter.getAllLocUpdates(d.getRequestid());

                    if(dList.size()==0)
                    {
                        if(d.getOTPStatus().equals("1"))
                        {
                            // Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();



                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(d.getRequestid(), d.getGuestProfileId(), d.getGuestName(), d.getGuestMobile(), d.getPickupLatitude(), d.getPickupLongitude(),
                                    d.getDropLatitude(), d.getDropLongitude(), d.getFromlocation(), d.getTolocation(), d.getTravelType(), d.getTravelpackage(), d.getRidedate(), d.getRidedate().split(" ")[1], "", d.getBookingType(),d.getPaymentMode(),d.getOtherCharges()));
                            Intent j = new Intent(getActivity(), RideLocal.class);
                            j.putExtra("cabData", cabData);
                            startActivity(j);
                            getActivity().finish();
                        }
                        else {

                            //Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                           /* cabData.add(new GuestData(d.getRequestid(), d.getGuestProfileId(), d.getGuestName(), d.getGuestMobile(), d.getPickupLatitude(), d.getPickupLongitude(),
                                    d.getDropLatitude(), d.getDropLongitude(), d.getFromlocation(), d.getTolocation(), d.getTravelType(), d.getTravelpackage(), d.getRidedate(), d.getRidedate().split(" ")[1], "", d.getBookingType(),d.getPaymentMode()));
*/
                            cabData.add(new GuestData(d.getRequestid(), d.getGuestProfileId(), d.getGuestName(), d.getGuestMobile(), d.getPickupLatitude(), d.getPickupLongitude(),
                                    d.getDropLatitude(), d.getDropLongitude(), d.getFromlocation(), d.getTolocation(), d.getTravelType(), d.getTravelpackage(), d.getRidedate(), d.getRidedate().split(" ")[1], "", d.getBookingType(),d.getPaymentMode(),d.getOtherCharges()));
                            Intent j = new Intent(getActivity(), TrackRideActivity.class);
                            j.putExtra("cabData", cabData);
                            startActivity(j);
                            getActivity().finish();
                        }

                    }
                    else {

                        Intent j = new Intent(getActivity(), RideOngoingLocal.class);
                        j.putExtra("list", rideDataList);
                        j.putExtra("position", position);
                        startActivity(j);
                        getActivity().finish();
                    }

                }
                else {

                    ArrayList<LocationUpdates> dList=dbAdapter.getAllLocUpdates(d.getRequestid());

                    if(dList.size()==0)
                    {
                        if(d.getOTPStatus().equals("1"))
                        {
                            // Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(d.getRequestid(), d.getGuestProfileId(), d.getGuestName(), d.getGuestMobile(), d.getPickupLatitude(), d.getPickupLongitude(),
                                    d.getDropLatitude(), d.getDropLongitude(), d.getFromlocation(), d.getTolocation(), d.getTravelType(), d.getTravelpackage(), d.getRidedate(), d.getRidedate().split(" ")[1], "", d.getBookingType(),d.getPaymentMode(),d.getOtherCharges()));
                            Intent j = new Intent(getActivity(), RideOutstation.class);
                            j.putExtra("cabData", cabData);
                            startActivity(j);
                            getActivity().finish();
                        }
                        else {

                            //Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(d.getRequestid(), d.getGuestProfileId(), d.getGuestName(), d.getGuestMobile(), d.getPickupLatitude(), d.getPickupLongitude(),
                                    d.getDropLatitude(), d.getDropLongitude(), d.getFromlocation(), d.getTolocation(), d.getTravelType(), d.getTravelpackage(), d.getRidedate(), d.getRidedate().split(" ")[1], "", d.getBookingType(),d.getPaymentMode(),d.getOtherCharges()));
                            Intent j = new Intent(getActivity(), OutStationTrackRideActivity.class);
                            j.putExtra("cabData", cabData);
                            startActivity(j);
                            getActivity().finish();
                        }

                    }
                    else {

                        Intent j = new Intent(getActivity(), RideOngoingOutstation.class);
                        j.putExtra("list", rideDataList);
                        j.putExtra("position", position);
                        startActivity(j);
                        getActivity().finish();
                    }


                }

            }
        });

        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        mp = MediaPlayer.create(getActivity(), R.raw.beep);
        //notificationManager =(NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Metres per second
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                establishConnection();

            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        //getDetails();
        showIfBookingIsOngoing();
        entered=true;
        //Toast.makeText(getActivity(),"OnDuty !",Toast.LENGTH_SHORT).show();
        //getCabs();
        //sendLocationUpdatesToServer();
        //showIfBookingIsOngoing();

    }

    @Override
    public void onStart() {

        super.onStart();

        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {

            }
            else
            {

            }
        }
        else
        {
            //mGoogleApiClient.connect();
            // super.onStart();
        }


    }

    @Override
    public void onStop() {

        super.onStop();

        if(mGoogleApiClient!=null) {
            //mGoogleApiClient.disconnect();
        }
        super.onStop();
        //gettingCabs=true;

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                //stopLocationUpdates();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        if(entered)
        {
            if(mGoogleApiClient.isConnecting()||mGoogleApiClient.isConnected())
            {
            }
            else {
                mGoogleApiClient.connect();
            }
        }

        if(mGoogleApiClient!=null) {

            if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

    }

    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            //System.out.println("in buildGoogleApiClient after 'if' ");
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    protected void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        //Location Settings Satisfied
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to resolve it
                        break;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(getActivity(), "Location enabled by user!!!", Toast.LENGTH_LONG).show();
                       /* if(sbMsg!=null) {
                            sbMsg.dismiss();
                        }*/
                        startLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // Toast.makeText(MapsActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if(mGoogleApiClient!=null)
        {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mapFragment.getMapAsync(this);
        if (mLastLocation == null) {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        if(mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(mLastLocation==null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lastLoc = new LatLng(latitude, longitude);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(addresses.size()!=0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String add1 = addresses.get(0).getAddressLine(1);
                    String add2 = addresses.get(0).getAddressLine(2);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    complete_address = address + " " + add1 + " " + add2;
                    tvCloc.setText(complete_address);
                }
                else {
                    tvCloc.setText("-");
                }
            }
            catch(IOException e)
            {e.printStackTrace();
                complete_address="Unable to get the location details";
                tvCloc.setText(complete_address);
            }
            //mMap.addMarker(new MarkerOptions().position(lastLoc)
            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab1)));
            cab.setPosition(lastLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 15));
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }




        if(mLastLocation!=null) {

            current_lat=location.getLatitude();
            current_long=location.getLongitude();
            if (current_lat != 0 && current_long != 0) {
                curntloc = new LatLng(current_lat, current_long);


                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);
                    if(addresses.size()!=0) {
                        int l = addresses.get(0).getMaxAddressLineIndex();
                        String add = "", add1 = "", add2 = "";

                        for (int k = 0; k < l; k++) {
                            add = add + addresses.get(0).getAddressLine(k);
                            add = add + " ";

                            if (k == 1) {
                                add1 = addresses.get(0).getAddressLine(k);
                            }
                            if (k == 2) {
                                add2 = addresses.get(0).getAddressLine(k);
                            }
                        }
                        String address = addresses.get(0).getAddressLine(0);
                        String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                        String add_2 = addresses.get(0).getAddressLine(2);
                        // city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();

                        if(add_1!=null||add_2!=null) {
                            complete_address = address + " " + add_1 + " " + add_2;
                        }
                        else {
                            complete_address=address;
                        }
                        //complete_address = add;
                        tvCloc.setText(complete_address);

                    }
                    else {
                        tvCloc.setText("-");
                    }
                }
                catch(IOException e) {
                    e.printStackTrace();
                    complete_address="Unable to get the location details";
                    tvCloc.setText(complete_address);
                }
                cab.setPosition(curntloc);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 15));
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }

            if(first)
            {
                sendLocationUpdatesToServer();
                first=false;
            }
        }

        mLastLocation=location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        lastLoc = new LatLng(latitude, longitude);
        current_lat=lastLoc.latitude;
        current_long=lastLoc.longitude;

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        if(cab!=null)
        {

        }
        else
        {
            cab=mMap.addMarker(new MarkerOptions().position(lastLoc)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(lastLoc)
                .zoom(14)
                //.bearing(30).tilt(45)
                .build()));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        try{
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            if(addresses.size()!=0) {
                int l = addresses.get(0).getMaxAddressLineIndex();
                String add = "", add1 = "", add2 = "";

                for (int k = 0; k < l; k++) {
                    add = add + addresses.get(0).getAddressLine(k);
                    add = add + " ";

                    if (k == 1) {
                        add1 = addresses.get(0).getAddressLine(k);
                    }
                    if (k == 2) {
                        add2 = addresses.get(0).getAddressLine(k);
                    }
                }
                String address = addresses.get(0).getAddressLine(0);
                String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                String add_2 = addresses.get(0).getAddressLine(2);
                // city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();

                if(add_1!=null||add_2!=null) {
                    complete_address = address + " " + add_1 + " " + add_2;
                }
                else {
                    complete_address=address;
                }

                tvCloc.setText(complete_address);
            }
            else {
                tvCloc.setText("-");
            }
        }
        catch (Exception e){e.printStackTrace();}

    }

    public void getDetails(){

        handler = new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(r,10000);

                // Getting details

                if (cabData.size() != 0 && (data.getTravelType().equals("local")||data.getTravelType().equals("Packages")) && data.getBookingtype().equals("AppBooking") ) {

                    //if (mp.isPlaying()) {
                    if(mp!=null) {

                        //mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);

                        mp.stop();
                        mp.release();
                        //mp = MediaPlayer.create(getActivity(), R.raw.beep);
                        mp=null;
                    }

                    //notificationManager.cancel(0);

                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", stProfileId);
                    v.addProperty("requestid", data.getRequestId());
                    v.addProperty("status", "6");//3 No Response
                    v.addProperty("companyid", companyId);

                    Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if (myBottomSheet.isAdded()) {
                                myBottomSheet.dismiss();
                            }

                            if (response.isSuccessful()) {
                                //alertDialog.dismiss();

                                /*

                                if(!dataPresent) {
                        cabData.clear();
                    }
                                 */
                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            /*

                            if(!dataPresent) {
                                cabData.clear();
                            }

                            */

                            //alertDialog.dismiss();
                            if (myBottomSheet.isAdded()) {
                                //return;
                            } else {
                                if (rootView.isShown()) {
                                    myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                                }
                            }
                        }
                    });

//                    if(!dataPresent) {
//                        cabData.clear();
//                    }

                    cabData.clear();

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
                else
                {
                    // cabData.clear();

                    //if(alertDialog!=null) {
                    //  alertDialog.dismiss();
                    // }

                    //handler.postDelayed(r,20000);

                    if(cabData.size()==0) {

                        Call<List<CabRequestsPojo>> call = REST_CLIENT.getCabRequests(stProfileId, companyId);
                        call.enqueue(new Callback<List<CabRequestsPojo>>() {
                            @Override
                            public void onResponse(Call<List<CabRequestsPojo>> call, Response<List<CabRequestsPojo>> response) {

                                if (response.isSuccessful()) {

                                    //dataPresent=true;

                                    handler.removeCallbacks(r);

                                    /*String msgText = "New Booking Request.\nClick to view the details.";
                                    ActivityManager am = (ActivityManager)getContext().getSystemService(getContext().ACTIVITY_SERVICE);
                                    List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                                    ActivityManager.RunningTaskInfo task = tasks.get(0); //
                                    ComponentName rootActivity = task.baseActivity;


                                    android.app.Notification.Builder builder = new Notification.Builder(getActivity());
                                    builder.setContentTitle("PCS Cabs")
                                            .setContentText("New booking request")
                                            .setSmallIcon(R.drawable.ic_alert).setAutoCancel(true)
                                            .setStyle(new Notification.BigTextStyle().bigText(msgText));*/
                                    Intent myIntent = new Intent(a,HomeActivity.class);
                                    //myIntent.setComponent(rootActivity);
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    getContext().startActivity(myIntent);
                                    /*PendingIntent pendingIntent = PendingIntent.getActivity(
                                            getActivity(),
                                            0,
                                            myIntent, 0);
                                    builder.setContentIntent(pendingIntent);
                                    //Notification notification = new Notification.BigTextStyle(builder).bigText(msgText).build();

                                    notificationManager.notify(0,builder.build());
*/
                                    //remove comment if needed
                                    //mp = MediaPlayer.create(getActivity(), R.raw.beep);

                                    if(mp==null)
                                    {
                                        mp = MediaPlayer.create(a, R.raw.beep);
                                    }

                                    mp.start();
                                    mp.setLooping(true);

                                    //mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);

                                    dataList = response.body();

                                    // for (int i = 0; i < dataList.size(); i++) {
                                    data = dataList.get(0);
                                    cabData.add(new GuestData(data.getRequestId(), data.getGuestProfileid(), data.getGuestName(), data.getGuestMobile(),
                                            data.getPickupLat(), data.getPickupLong(), data.getDropLat(), data.getDropLong(), data.getPickupLoc(),
                                            data.getDropLoc(), data.getTravelType(), data.getTravelPackage(),data.getScheduledDate(), data.getScheduledTime(), data.getOTPrequired(), data.getBookingtype(),data.getPaymentMode(),data.getOthercharges()));


                                    /*System.out.println("%%%%%%%%%%%%%%%%%%%%");
                                    System.out.println("Req Id"+data.getRequestId());
                                    System.out.println("Guest Profile Id "+data.getGuestProfileid());
                                    System.out.println("Guest Name "+data.getGuestName());
                                    System.out.println("Guest Mobile "+data.getGuestMobile());
                                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");*/

                                    inflater = getActivity().getLayoutInflater();
                                    dialogView = inflater.inflate(R.layout.alert_cab_request, null);
                                    dialogBuilder.setView(dialogView);
                                    alertDialog=dialogBuilder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    alertDialog.show();

                                    LinearLayout llLocal = (LinearLayout) dialogView.findViewById(R.id.acr_ll_local);
                                    LinearLayout llOutstation = (LinearLayout) dialogView.findViewById(R.id.acr_ll_outstation);
                                    TextView tvPickup = (TextView) dialogView.findViewById(R.id.acr_tv_pickup);
                                    TextView tvDrop = (TextView) dialogView.findViewById(R.id.acr_tv_drop);
                                    TextView tvTravelType = (TextView) dialogView.findViewById(R.id.acr_tv_travel_type);
                                    TextView tvTravelPackage = (TextView) dialogView.findViewById(R.id.acr_tv_travel_package);
                                    TextView tvReportingTime = (TextView) dialogView.findViewById(R.id.acr_tv_reporting_time);
                                    TextView tvReportingDate=(TextView)dialogView.findViewById(R.id.acr_tv_reporting_date);
                                    final Button btStartDuty = (Button) dialogView.findViewById(R.id.acr_bt_start_duty);
                                    LinearLayout llOtherCharges=(LinearLayout)dialogView.findViewById(R.id.acr_ll_other_charges);
                                    TextView tvOtherCharges=(TextView)dialogView.findViewById(R.id.acr_tv_other_charges);
                                    llOtherCharges.setVisibility(View.GONE);

                                    if(data.getOthercharges().equals("0"))
                                    {

                                    }
                                    else {
                                        llOtherCharges.setVisibility(View.VISIBLE);
                                        tvOtherCharges.setText(getString(R.string.Rs)+" "+data.getOthercharges());
                                    }


                                    tvPickup.setText(data.getPickupLoc());
                                    tvDrop.setText(data.getDropLoc());
                                    tvTravelType.setText(data.getTravelType());

                                    if(data.getTravelPackage().equals(""))
                                    {
                                        tvTravelPackage.setText("-");
                                    }
                                    else {
                                        tvTravelPackage.setText(data.getTravelPackage());
                                    }
                                    tvReportingTime.setText(data.getScheduledTime());

                                    //tvReportingDate.setText(data.getScheduledDate().split(" ")[0]);

                                    SimpleDateFormat  format1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                    SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                                    try {
                                        tvReportingDate.setText(format.format(format1.parse(data.getScheduledDate())).split(" ")[0]);
                                    }
                                    catch (ParseException e)
                                    {
                                        e.printStackTrace();
                                    }


                                    if (data.getBookingtype().equals("TeleBook")) {
                                        btStartDuty.setVisibility(View.VISIBLE);
                                        llLocal.setVisibility(View.GONE);
                                        llOutstation.setVisibility(View.VISIBLE);

                                        if (data.getTravelType().equals("local")) {

                                            // tvTravelPackage.setVisibility(View.GONE);

                                        } else {


                                        }
                                    } else {

                                        if (data.getTravelType().equals("local")||data.getTravelType().equals("Packages")) {

                                            h.post(rr);

                                        } else {

                                            llLocal.setVisibility(View.GONE);
                                            llOutstation.setVisibility(View.VISIBLE);
                                            btStartDuty.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    btStartDuty.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            //Accept API have to be called by default
                                            //Depends whether notif api has to be called or no....

                                            // if (mp.isPlaying()) {
                                            if(mp!=null) {

                                                //mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);

                                                mp.stop();
                                                mp.release();
                                                //mp = MediaPlayer.create(getActivity(), R.raw.beep);
                                                mp=null;
                                            }

                                            btStartDuty.setBackgroundColor(Color.parseColor("#e0e0e0"));

                                            JsonObject v = new JsonObject();
                                            v.addProperty("profileid", stProfileId);
                                            v.addProperty("requestid", data.getRequestId());
                                            v.addProperty("status", "1");
                                            v.addProperty("companyid", companyId);

                                            Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                                            call.enqueue(new Callback<Pojo>() {
                                                @Override
                                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                    if(response.isSuccessful())
                                                    {
                                                        //notificationManager.cancel(0);

                                                        editor.putString("booking","in");
                                                        editor.putBoolean("saved",false);
                                                        editor.commit();

                                                        //if (data.getTravelType().equals("local")||data.getTravelType().equals("Packages")) {
                                                        if (data.getTravelType().equals("local")) {

                                                            if (data.getOTPrequired().equals("Yes")) {
                                                                h.removeCallbacks(rr);
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(getActivity(), TrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            } else if (data.getOTPrequired().equals("No")) {

                                                                    /*
                                                                    ///////////////////////////
                                                                    JsonObject v=new JsonObject();
                                                                    v.addProperty("profileid",stProfileId);
                                                                    v.addProperty("requestid",data.getRequestId());
                                                                    v.addProperty("companyid",companyId);

                                                                    Call<Pojo> call1=REST_CLIENT.sendOtpNotify(v);
                                                                    call1.enqueue(new Callback<Pojo>() {
                                                                        @Override
                                                                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                                            if(response.isSuccessful())
                                                                            {
                                                                                h.removeCallbacks(rr);
                                                                                alertDialog.dismiss();
                                                                                count = 0;
                                                                                Intent i = new Intent(getActivity(), RideStartActivity.class);
                                                                                i.putExtra("cabData", cabData);
                                                                                startActivity(i);
                                                                                getActivity().finish();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<Pojo> call, Throwable t) {

                                                                            Toast.makeText(getActivity(),"Booking Status Not Changed!!",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                    */

                                                                h.removeCallbacks(rr);
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(getActivity(), RideStartActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();

                                                            } else {
                                                                h.removeCallbacks(rr);
                                                                if(hLoc!=null) {

                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(getActivity(), TrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            }
                                                        } else {

                                                            if (data.getOTPrequired().equals("Yes")) {
                                                                h.removeCallbacks(rr);
                                                                hLoc.removeCallbacks(rLoc);
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(getActivity(), OutStationTrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            } else if (data.getOTPrequired().equals("No")) {

                                                                    /*

                                                                    JsonObject v=new JsonObject();
                                                                    v.addProperty("profileid",stProfileId);
                                                                    v.addProperty("requestid",data.getRequestId());
                                                                    v.addProperty("companyid",companyId);

                                                                    Call<Pojo> call1=REST_CLIENT.sendOtpNotify(v);
                                                                    call1.enqueue(new Callback<Pojo>() {
                                                                        @Override
                                                                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                                            if(response.isSuccessful())
                                                                            {
                                                                                h.removeCallbacks(rr);
                                                                                alertDialog.dismiss();
                                                                                count = 0;
                                                                                Intent i = new Intent(getActivity(), OutStationRideStartActivity.class);
                                                                                i.putExtra("cabData", cabData);
                                                                                startActivity(i);
                                                                                getActivity().finish();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<Pojo> call, Throwable t) {

                                                                            Toast.makeText(getActivity(),"Booking Status Not Changed!!",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                    */

                                                                h.removeCallbacks(rr);
                                                                hLoc.removeCallbacks(rLoc);
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(getActivity(), OutStationRideStartActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();


                                                            } else {
                                                                h.removeCallbacks(rr);
                                                                hLoc.removeCallbacks(rLoc);
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(getActivity(), OutStationTrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            }

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Pojo> call, Throwable t) {

                                                    Toast.makeText(getActivity(),"Please Check Internet Connection!",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });


                                    final Button btAccept = (Button) dialogView.findViewById(R.id.acr_bt_accept);
                                    final Button btDecline = (Button) dialogView.findViewById(R.id.acr_bt_decline);

                                    //btAccept.setBackgroundResource(R.drawable.rect_blue_stroke_nc_bg);
                                    //btDecline.setBackgroundResource(R.drawable.rect_blue_stroke_nc_bg);

                                    // h.post(rr);

                                    btAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            btAccept.setEnabled(false);
                                            btAccept.setClickable(false);

                                            //notificationManager.cancel(0);

                                            // if (mp.isPlaying()) {
                                            if(mp!=null) {

                                                // mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);

                                                mp.stop();
                                                mp.release();
                                                //mp = MediaPlayer.create(getActivity(), R.raw.beep);
                                                mp=null;
                                            }

                                            btAccept.setBackgroundColor(Color.parseColor("#e0e0e0"));

                                            accepted=true;

                                            JsonObject v = new JsonObject();
                                            v.addProperty("profileid", stProfileId);
                                            v.addProperty("requestid", data.getRequestId());
                                            v.addProperty("status", "1"); //accept
                                            v.addProperty("companyid", companyId);

                                            //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                            Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                                            call.enqueue(new Callback<Pojo>() {
                                                @Override
                                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                    if (myBottomSheet.isAdded()) {
                                                        myBottomSheet.dismiss();
                                                    }

                                                    if (response.isSuccessful()) {

                                                        editor.putString("booking","in");
                                                        editor.putBoolean("saved",false);
                                                        editor.commit();

                                                        h.removeCallbacks(rr);
                                                        if(hLoc!=null) {
                                                            hLoc.removeCallbacks(rLoc);
                                                        }
                                                        // handler.post(r);
                                                        alertDialog.dismiss();
                                                        count = 0;
                                                        //declineOtherRequests();
                                                        //alertDialog.dismiss();
                                                        // handler.removeCallbacks(r);
                                                        if (data.getTravelType().equals("local")) {

                                                            Intent i = new Intent(getActivity(), TrackRideActivity.class);
                                                            i.putExtra("cabData", cabData);
                                                            startActivity(i);
                                                            getActivity().finish();
                                                        }
                                                        else {
                                                            Intent i = new Intent(getActivity(), OutStationTrackRideActivity.class);
                                                            i.putExtra("cabData", cabData);
                                                            startActivity(i);
                                                            getActivity().finish();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Pojo> call, Throwable t) {

                                                    alertDialog.dismiss();

                                                    if (myBottomSheet.isAdded()) {
                                                        //return;

                                                    } else {
                                                        if (rootView.isShown()) {
                                                            myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                                                        }
                                                    }

                                                    btAccept.setEnabled(true);
                                                    btAccept.setClickable(true);
                                                }
                                            });
                                        }
                                    });

                                    btDecline.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            //  if (mp.isPlaying()) {
                                            if(mp!=null) {

                                                //mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);

                                                mp.stop();
                                                mp.release();
                                                mp = null;
                                            }

                                            //notificationManager.cancel(0);

                                            btDecline.setBackgroundColor(Color.parseColor("#e0e0e0"));

                                            accepted=false;

                                            JsonObject v = new JsonObject();
                                            v.addProperty("profileid", stProfileId);
                                            v.addProperty("requestid", data.getRequestId());
                                            v.addProperty("status", "5"); //2 Decline
                                            v.addProperty("companyid", companyId);

                                            //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                            Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                                            call.enqueue(new Callback<Pojo>() {
                                                @Override
                                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                    if (myBottomSheet.isAdded()) {
                                                        myBottomSheet.dismiss();
                                                    }

                                                    if (response.isSuccessful()) {

                                                        //dataPresent=false;

                                                        h.removeCallbacks(rr);
                                                        handler.post(r);
                                                        alertDialog.dismiss();
                                                        count = 0;
                                                        cabData.clear();
                                                        //alertDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Pojo> call, Throwable t) {

                                                    alertDialog.dismiss();
                                                    if (myBottomSheet.isAdded()) {
                                                        //return;
                                                    } else {
                                                        if (rootView.isShown()) {
                                                            myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    //  }
                                } else {

                                    //  Toast.makeText(getActivity(),"Error in getting data!!",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

                            }
                        });


                    }
                    else {

                    }
                }
            }
        };

        handler.post(r);

        h=new Handler();
        rr=new Runnable() {
            @Override
            public void run() {

                count=count+2;
                //h.postDelayed(rr,20000);

                if(count==4)
                {
                    if(!accepted) {

                        //dataPresent=false;

                        h.removeCallbacks(rr);
                        handler.post(r);
                        alertDialog.dismiss();
                        count = 0;
                    }
                }
                else
                {
                    //changing the interval from 18 sec to 10 sec
                    h.postDelayed(rr,10000);
                    //20000
                }

            }
        };

    }


    public void declineOtherRequests()
    {
        Call<List<CabRequestsPojo>> call = REST_CLIENT.getCabRequests(stProfileId,companyId);
        call.enqueue(new Callback<List<CabRequestsPojo>>() {

            List<CabRequestsPojo> lt;
            CabRequestsPojo dt;
            @Override
            public void onResponse(Call<List<CabRequestsPojo>> call, Response<List<CabRequestsPojo>> response) {

                if(response.isSuccessful())
                {
                    lt=response.body();

                    for(int i=0;i<lt.size();i++)
                    {
                        dt=lt.get(i);
                        JsonObject v = new JsonObject();
                        v.addProperty("profileid", stProfileId);
                        v.addProperty("requestid", dt.getRequestId());
                        v.addProperty("status", "2");
                        v.addProperty("companyid",companyId);

                        //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                        Call<Pojo> call1 = REST_CLIENT.sendCabAcceptanceStatus(v);
                        call1.enqueue(new Callback<Pojo>() {
                            @Override
                            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                if (response.isSuccessful()) {
                                    //alertDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<Pojo> call, Throwable t) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        stopLocationUpdates();

        if(handler!=null) {
            handler.removeCallbacks(r);
        }

        if(hLoc!=null)
        {
            hLoc.removeCallbacks(rLoc);
        }

        if (mp != null) {

            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }



    }

    public void selectServiceLocations()
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_locations, null);

        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        tvLoc1=(TextView)dialogView.findViewById(R.id.ale_tv_loc1);
        tvLoc2=(TextView)dialogView.findViewById(R.id.ale_tv_loc2);
        tvLoc3=(TextView)dialogView.findViewById(R.id.ale_tv_loc3);
        tvLoc4=(TextView)dialogView.findViewById(R.id.ale_tv_loc4);
        tvLoc5=(TextView)dialogView.findViewById(R.id.ale_tv_loc5);

        tvLoc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc1.setTextColor(Color.parseColor("#000000"));
                city=tvLoc1.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city",city);
                editor.commit();
                alertDialog.dismiss();
            }
        });

        tvLoc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc2.setTextColor(Color.parseColor("#000000"));

                city=tvLoc2.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();
            }
        });

        tvLoc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc3.setTextColor(Color.parseColor("#000000"));


                city=tvLoc3.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();
            }
        });

        tvLoc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc4.setTextColor(Color.parseColor("#000000"));


                city=tvLoc4.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();
            }
        });

        tvLoc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc5.setTextColor(Color.parseColor("#000000"));


                city=tvLoc5.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();
            }
        });



        Call<List<ServiceLocationPojo>> call=REST_CLIENT.getServiceLocations("CMP00001");
        call.enqueue(new Callback<List<ServiceLocationPojo>>() {
            @Override
            public void onResponse(Call<List<ServiceLocationPojo>> call, Response<List<ServiceLocationPojo>> response) {

                ServiceLocationPojo data;
                List<ServiceLocationPojo> dataList;

                if(response.isSuccessful())
                {
                    dataList=response.body();
                    j=0;

                    for(int i=0;i<dataList.size();i++)
                    {
                        data=dataList.get(i);
                        switch (j)
                        {
                            case 0:tvLoc1.setText(data.getLocation());
                                tvLoc1.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 1:tvLoc2.setText(data.getLocation());
                                tvLoc2.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 2:tvLoc3.setText(data.getLocation());
                                tvLoc3.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 3:tvLoc4.setText(data.getLocation());
                                tvLoc4.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 4:tvLoc5.setText(data.getLocation());
                                tvLoc5.setVisibility(View.VISIBLE);
                                j++;
                                break;
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<List<ServiceLocationPojo>> call, Throwable t) {

                Toast.makeText(getActivity(),"Check Internet Connection",Toast.LENGTH_LONG).show();

            }
        });

    }

    public void sendLocationUpdatesToServer()
    {
        hLoc=new Handler();
        rLoc=new Runnable() {
            @Override
            public void run() {

                hLoc.postDelayed(rLoc,20000);

                JsonObject v=new JsonObject();
                v.addProperty("profileid",stProfileId);
                v.addProperty("location",city);
                String c_lat=String.valueOf(current_lat);
                String c_long=String.valueOf(current_long);
                v.addProperty("latittude",c_lat);
                v.addProperty("longitude",c_long);
                v.addProperty("companyid",companyId);
                v.addProperty("ReqId","");

                System.out.println("*****"+stProfileId+"**"+city+"**"+c_lat+"**"+c_long+"******");

                Call<Pojo> call=REST_CLIENT.sendStatus(v);
                call.enqueue(new Callback<Pojo>() {
                    @Override
                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                        Pojo p;

                        if(myBottomSheet.isAdded())
                        {
                            myBottomSheet.dismiss();
                        }

                        if(response.isSuccessful())
                        {
                            p=response.body();

                            //System.out.println("------------***-------------updated"+p.getMessage()+"--------------***---------------------");

                            String[] newbooking = response.body().getMessage().split("-");

                            if (newbooking.length == 1) {

                                if(p.getMessage().equals("cancelled"))
                                {
                                    Toast.makeText(a,"Booking cancelled!",Toast.LENGTH_LONG).show();

                                    if(alertDialog!=null) {
                                        alertDialog.dismiss();
                                        //notificationManager.cancel(0);
                                    }
                                    if(mp!=null) {

                                        mp.stop();
                                        mp.release();
                                        //mp = MediaPlayer.create(getActivity(), R.raw.beep);
                                        mp=null;
                                    }
                                    handler.post(r);
                                    cabData.clear();
                                    count = 0;
                                }

                            } else {

                                if(newbooking[0].equals("cancelled"))
                                {
                                    Toast.makeText(a,"Booking cancelled!",Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                    if(mp!=null) {

                                        mp.stop();
                                        mp.release();
                                        //mp = MediaPlayer.create(getActivity(), R.raw.beep);
                                        mp=null;
                                    }
                                    handler.post(r);
                                    cabData.clear();
                                    count = 0;
                                }
                            }

                            if(p.getMessage().equals("Logout"))
                            {
                                Toast.makeText(a," Logout Done! ",Toast.LENGTH_LONG).show();
                                session.logoutUser();

                                Intent i=new Intent(a,MainActivity.class);
                                startActivity(i);
                                a.finish();
                            }

                        }
                        else
                        {

                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                        Toast.makeText(a,"Check Internet connection",Toast.LENGTH_SHORT).show();
                    }
                });


            }
        };

        hLoc.post(rLoc);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof HomeActivity){
            a=(HomeActivity) context;
        }

    }



    @Override
    public void onDetach() {

        super.onDetach();
    }


    public void showIfBookingIsOngoing()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
        String driverProfileId;
        user=session.getUserDetails();
        driverProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getCurrentBooking(driverProfileId,companyId);
        call.enqueue(new Callback<ArrayList<AllRidesPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<AllRidesPojo>> call, Response<ArrayList<AllRidesPojo>> response) {

                allRidesDataList=response.body();

                if(response.isSuccessful())
                {
                    d = allRidesDataList.get(0);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
                    // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                    try {
                        date1 = dateFormat.parse(d.getRidedate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    rideDataList.add(new FormattedAllRidesData(0,date1, d.getRequestid(), d.getFromlocation(), d.getTolocation(), d.getVehicleCategory(),
                            d.getVehicleType(), d.getDistancetravelled(), d.getStatusofride(), d.getRidestarttime(), d.getRidestoptime(),
                            d.getTotalamount(), d.getDrivername(),d.getDriverpic(),d.getTravelType(),d.getBookingType(),d.getTravelpackage(),d.getDrivermobile(),d.getGuestProfileId(),d.getGuestName(),d.getGuestMobile(),
                            d.getPickupLatitude(),d.getPickupLongitude(),d.getDropLatitude(),d.getDropLongitude(),d.getOTPStatus(),d.getDriverBattaAmt(),d.getPaymentMode(),d.getOtherCharges()));


                    llBottom.setVisibility(View.VISIBLE);
                    switchCompat.setVisibility(View.GONE);

                    position=0;

                    progressDialog.dismiss();

                    /*getDetails();
                    Toast.makeText(getActivity(),"OnDuty !",Toast.LENGTH_SHORT).show();*/
                }

                else {
                    progressDialog.dismiss();
                    getDetails();
                    Toast.makeText(getActivity(),"OnDuty !",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllRidesPojo>> call, Throwable t) {

            }
        });



        /*Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getUserRides(driverProfileId,"driver",companyId);
        call.enqueue(new Callback<ArrayList<AllRidesPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<AllRidesPojo>> call, Response<ArrayList<AllRidesPojo>> response) {


                allRidesDataList=response.body();

                if(response.isSuccessful())
                {


                    for(int i=0;i<allRidesDataList.size();i++)
                    {
                        d = allRidesDataList.get(i);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
                        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                        try {
                            date1 = dateFormat.parse(d.getRidedate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        rideDataList.add(new FormattedAllRidesData(i,date1, d.getRequestid(), d.getFromlocation(), d.getTolocation(), d.getVehicleCategory(),
                                d.getVehicleType(), d.getDistancetravelled(), d.getStatusofride(), d.getRidestarttime(), d.getRidestoptime(),
                                d.getTotalamount(), d.getDrivername(),d.getDriverpic(),d.getTravelType(),d.getBookingType(),d.getTravelpackage(),d.getDrivermobile(),d.getGuestProfileId(),d.getGuestName(),d.getGuestMobile(),
                                d.getPickupLatitude(),d.getPickupLongitude(),d.getDropLatitude(),d.getDropLongitude(),d.getOTPStatus(),d.getDriverBattaAmt(),d.getPaymentMode(),d.getOtherCharges()));


                        *//*if(d.getStatusofride().equals("ONGOING")) {

                            llBottom.setVisibility(View.VISIBLE);
                            switchCompat.setVisibility(View.GONE);

                            position=i;

                            break;
                        }*//*


                    }

                    if(rideDataList.size()!=0) {
                        Collections.sort(rideDataList, new Comparator<FormattedAllRidesData>() {
                            public int compare(FormattedAllRidesData o1, FormattedAllRidesData o2) {
                                if (o1.getRideDate() == null || o2.getRideDate() == null)
                                    return 0;
                                return o1.getRideDate().compareTo(o2.getRideDate());
                            }
                        });

                        Collections.reverse(rideDataList);
                    }

                    for(int i=0;i<rideDataList.size();i++)
                    {

                        f = rideDataList.get(i);

                        if(f.getRideStatus().equals("ONGOING")) {

                            llBottom.setVisibility(View.VISIBLE);
                            switchCompat.setVisibility(View.GONE);

                            position=i;

                            break;
                        }
                    }


                    progressDialog.dismiss();
                    getDetails();
                    Toast.makeText(getActivity(),"OnDuty !",Toast.LENGTH_SHORT).show();
                }

                else {
                    progressDialog.dismiss();
                    getDetails();
                    Toast.makeText(getActivity(),"OnDuty !",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ArrayList<AllRidesPojo>> call, Throwable t) {

                progressDialog.dismiss();

                Toast.makeText(getActivity(),"Check Internet connection!",Toast.LENGTH_SHORT).show();
                getActivity().finish();



            }
        });*/

    }


    public void goingOnline()
    {

        JsonObject v=new JsonObject();
        v.addProperty("login","-");
        v.addProperty("pwd","-");
        v.addProperty("companyid",companyId);
        v.addProperty("version",version);
        v.addProperty("profileid",stProfileId);

        Call<Pojo> call=REST_CLIENT.validateLogin(v);
        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(response.isSuccessful())
                {
                    switchCompat.setChecked(true);
                    switchCompat.setText("Online");
                    switchCompat.setBackgroundResource(R.drawable.rect_online);
                    editor.putString("status","online");
                    editor.commit();

                    ((HomeActivity) a).enableDisableDrawer(DrawerLayout.LOCK_MODE_UNLOCKED);

                    handler.post(r);
                    hLoc.post(rLoc);
                }
                else {

                    switchCompat.setChecked(false);

                    if(response.message().equals("Version mismatched")) {

                        // Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.alert_update, null);

                        dialogBuilder.setView(dialogView);

                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);

                        TextView tvOk = (TextView) dialogView.findViewById(R.id.au_bt_ok);
                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                alertDialog.dismiss();
                                getActivity().finish();
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

            }
        });
    }


    public void goingOffline()
    {

        JsonObject v=new JsonObject();
        v.addProperty("profileid",stProfileId);
        v.addProperty("companyid",companyId);

        Call<Pojo> call=REST_CLIENT.toOffline(v);
        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(response.isSuccessful())
                {

                    switchCompat.setChecked(false);
                    switchCompat.setText("Offline");
                    switchCompat.setBackgroundResource(R.drawable.rect_offline);
                    editor.putString("status","offline");
                    editor.commit();

                    if(handler!=null) {
                        handler.removeCallbacks(r);
                    }

                    if(hLoc!=null) {
                        hLoc.removeCallbacks(rLoc);
                    }

                    ((HomeActivity) a).enableDisableDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                }
                else {

                    Toast.makeText(getActivity(),"Unknown error! Please try again!",Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                Toast.makeText(getActivity(),"No Internet Connection!\nPlease try again.",Toast.LENGTH_LONG).show();
            }
        });
    }



}


