package com.hjsoft.driverbooktaxi.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 26/12/16.
 */
public class RequestsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

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
    ArrayList<GuestData> cabData;
    Handler handler;
    Runnable r;
    GuestData gData;
    ArrayList<GuestData> acceptedCabData;
    Marker cab;
    boolean accepted=false;
    int j=0;
    CabRequestsPojo data;
    BottomSheetDialogFragment myBottomSheet;
    ImageButton ibLogout;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    View dialogView;
    LayoutInflater inflater;
    String companyId="CMP00001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        tvCloc=(TextView)findViewById(R.id.ar_tv_cloc);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        ibLogout=(ImageButton)findViewById(R.id.ar_ib_logout);

        REST_CLIENT= RestClient.get();
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        stProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        mRequestingLocationUpdates=false;

        dialogBuilder = new AlertDialog.Builder(RequestsActivity.this);

        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.alert_cab_request, null);
        dialogBuilder.setView(dialogView);

        alertDialog=dialogBuilder.create();

        if(Build.VERSION.SDK_INT<23)
        {
            //System.out.println("Sdk_int is"+Build.VERSION.SDK_INT);
            //System.out.println("the enetred values is "+entered);
            establishConnection();
        }
        else
        {
            if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                establishConnection();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(RequestsActivity.this,"Location Permission is required for this app to run !",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

        cabData=new ArrayList<>();

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.logoutUser();
                Intent i=new Intent(RequestsActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                establishConnection();

            } else {
                Toast.makeText(RequestsActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getDetails(){

        handler = new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                if(cabData.size()!=0)
                {
                    JsonObject v=new JsonObject();
                    v.addProperty("profileid",stProfileId);
                    v.addProperty("requestid",data.getRequestId());
                    v.addProperty("status","2");
                    v.addProperty("companyid",companyId);

                    //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                    Call<Pojo> call=REST_CLIENT.sendCabAcceptanceStatus(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if(myBottomSheet.isAdded())
                            {
                                myBottomSheet.dismiss();
                            }

                            if(response.isSuccessful())
                            {
                                alertDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            alertDialog.dismiss();
                            if(myBottomSheet.isAdded())
                            {
                                //return;
                            }
                            else
                            {
                                myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                            }
                        }
                    });
                }


                cabData.clear();

                if(alertDialog!=null) {
                    alertDialog.dismiss();
                }

                handler.postDelayed(this, 20000);
                Call<List<CabRequestsPojo>> call=REST_CLIENT.getCabRequests(stProfileId,"CMP00001");
                call.enqueue(new Callback<List<CabRequestsPojo>>() {
                    @Override
                    public void onResponse(Call<List<CabRequestsPojo>> call, Response<List<CabRequestsPojo>> response) {

                        if(response.isSuccessful()){
                            dataList=response.body();

                            for(int i=0;i<dataList.size();i++)
                            {
                                data=dataList.get(0);
                                cabData.add(new GuestData(data.getRequestId(),data.getGuestProfileid(),data.getGuestName(),data.getGuestMobile(),
                                        data.getPickupLat(),data.getPickupLong(),data.getDropLat(),data.getDropLong(),data.getPickupLoc(),
                                        data.getDropLoc(),"traveltype","travelPackage","xxx","00","-","-",data.getPaymentMode()));


                                alertDialog.show();

                                TextView tvPickup=(TextView)dialogView.findViewById(R.id.acr_tv_pickup);
                                TextView tvDrop=(TextView)dialogView.findViewById(R.id.acr_tv_drop);

                                tvPickup.setText(data.getPickupLoc());
                                tvDrop.setText(data.getDropLoc());

                                Button btAccept=(Button) dialogView.findViewById(R.id.acr_bt_accept);
                                Button btDecline=(Button) dialogView.findViewById(R.id.acr_bt_decline);

                                btAccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        JsonObject v=new JsonObject();
                                        v.addProperty("profileid",stProfileId);
                                        v.addProperty("requestid",data.getRequestId());
                                        v.addProperty("status","1");
                                        v.addProperty("companyid",companyId);

                                        //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                        Call<Pojo> call=REST_CLIENT.sendCabAcceptanceStatus(v);
                                        call.enqueue(new Callback<Pojo>() {
                                            @Override
                                            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                if(myBottomSheet.isAdded())
                                                {
                                                    myBottomSheet.dismiss();
                                                }

                                                if(response.isSuccessful())
                                                {
                                                    alertDialog.dismiss();
                                                    handler.removeCallbacks(r);
                                                    Intent i = new Intent(RequestsActivity.this, TrackRideActivity.class);
                                                    i.putExtra("cabData", cabData);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Pojo> call, Throwable t) {

                                                alertDialog.dismiss();

                                                if(myBottomSheet.isAdded())
                                                {
                                                    //return;

                                                }
                                                else
                                                {
                                                    myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                                                }
                                            }
                                        });
                                    }
                                });

                                btDecline.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        JsonObject v=new JsonObject();
                                        v.addProperty("profileid",stProfileId);
                                        v.addProperty("requestid",data.getRequestId());
                                        v.addProperty("status","2");
                                        v.addProperty("companyid",companyId);


                                        //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                        Call<Pojo> call=REST_CLIENT.sendCabAcceptanceStatus(v);
                                        call.enqueue(new Callback<Pojo>() {
                                            @Override
                                            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                if(myBottomSheet.isAdded())
                                                {
                                                    myBottomSheet.dismiss();
                                                }

                                                if(response.isSuccessful())
                                                {
                                                    alertDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Pojo> call, Throwable t) {

                                                alertDialog.dismiss();
                                                if(myBottomSheet.isAdded())
                                                {
                                                    //return;
                                                }
                                                else
                                                {
                                                    myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                        else {

                            // Toast.makeText(RequestsActivity.this,"Error in getting data!!",Toast.LENGTH_LONG).show();
                            //System.out.println("not getting data...................");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

                        if(myBottomSheet.isAdded())
                        {
                            //return;
                        }
                        else
                        {
                            myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                        }
                    }
                });
            }
        };
        handler.post(r);
    }

    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        getDetails();
        entered=true;
    }


    @Override
    protected void onStart() {

        super.onStart();

        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {
                // System.out.println("value of entered in 'if' "+entered);
            }
            else
            {
                //System.out.println("value of entered in 'else' "+entered);
                mGoogleApiClient.connect();
                //super.onStart();
            }
        }
        else
        {
            mGoogleApiClient.connect();
            // super.onStart();
        }
    }

    @Override
    protected void onStop() {

        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if(entered)
        {
            if(mGoogleApiClient.isConnecting()||mGoogleApiClient.isConnected())
            {
                // System.out.println("Google Client connectng in 'if' "+mGoogleApiClient.isConnecting());
            }
            else {
                //System.out.println("Google Client connectng in 'else' "+mGoogleApiClient.isConnecting());
                mGoogleApiClient.connect();
            }
        }

        if(mGoogleApiClient!=null) {

            // System.out.println("connections "+mGoogleApiClient.isConnected()+" "+mGoogleApiClient.isConnecting());
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        lastLoc = new LatLng(latitude, longitude);

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
            int l=addresses.get(0).getMaxAddressLineIndex();
            String add="",add1="",add2="";

            for(int k=0;k<l;k++)
            {
                add=add+addresses.get(0).getAddressLine(k);
                add=add+" ";

                if(k==1)
                {
                    add1=addresses.get(0).getAddressLine(k);
                }
                if(k==2)
                {
                    add2=addresses.get(0).getAddressLine(k);
                }
            }
            tvCloc.setText(add);
        }
        catch (Exception e){e.printStackTrace();}
    }

    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            //System.out.println("in buildGoogleApiClient after 'if' ");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                            status.startResolutionForResult(RequestsActivity.this, REQUEST_LOCATION);
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
                        //Toast.makeText(MapsActivity.this, "Location enabled by user!!!", Toast.LENGTH_LONG).show();
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
        mLocationRequest.setInterval(45000);//45 sec
        mLocationRequest.setFastestInterval(30000);//5 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,RequestsActivity.this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, RequestsActivity.this);
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
                String address = addresses.get(0).getAddressLine(0);
                String add1=addresses.get(0).getAddressLine(1);
                String add2=addresses.get(0).getAddressLine(2);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                complete_address=address+" "+add1+" "+add2;
                tvCloc.setText(complete_address);
            }
            catch(IOException e)
            {e.printStackTrace();
                complete_address="No response from server";
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
                Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), current_lat, current_long, results);
                location.getAccuracy();
                res = res + (long) results[0];


                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);
                    int l=addresses.get(0).getMaxAddressLineIndex();
                    String add="",add1="",add2="";

                    for(int k=0;k<l;k++)
                    {
                        add=add+addresses.get(0).getAddressLine(k);
                        add=add+" ";

                        if(k==1)
                        {
                            add1=addresses.get(0).getAddressLine(k);
                        }
                        if(k==2)
                        {
                            add2=addresses.get(0).getAddressLine(k);
                        }
                    }
                    String address = addresses.get(0).getAddressLine(0);
                    String add_1=addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                    String add_2=addresses.get(0).getAddressLine(2);
                    city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    //complete_address=address+" "+add1+" "+add2;
                    tvCloc.setText(add);
                    complete_address=add;
                }
                catch(IOException e) {
                    e.printStackTrace();
                    complete_address="No response from server";
                    tvCloc.setText(complete_address);
                }
                cab.setPosition(curntloc);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 15));
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }
        }

        JsonObject v=new JsonObject();
        v.addProperty("profileid",stProfileId);
        v.addProperty("location",city);
        String c_lat=String.valueOf(current_lat);
        String c_long=String.valueOf(current_long);
        v.addProperty("latittude",c_lat);
        v.addProperty("longitude",c_long);

       // System.out.println("*****"+stProfileId+"**"+city+"**"+c_lat+"**"+c_long+"******");

        Call<Pojo> call=REST_CLIENT.sendStatus(v);
        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(myBottomSheet.isAdded())
                {
                    myBottomSheet.dismiss();

                }

                if(response.isSuccessful())
                {
                    //System.out.println("-------------------------updated-----------------------------------");
                }
                else
                {
                   // System.out.println(response.errorBody()+"**"+response.message());
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                if(myBottomSheet.isAdded())
                {
                    //return;

                }
                else
                {
                    myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                }
            }
        });

        mLastLocation=location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
