package com.hjsoft.driverbooktaxi.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.service.RideOverlayService;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

/**
 * Created by hjsoft on 16/11/16.
 */
public class TrackRideActivity extends FragmentActivity implements OnMapReadyCallback,
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
    String pickupTime,dropTime;
    TextView tvCloc;
    ImageButton ibClose,ibDots;
    RelativeLayout rlBottomSheet;
    Button btPickup,btArrived;
    ArrayList<GuestData> cabData;
    TextView tvGname,tvGmobile,tvPickup,tvDrop;
    GuestData data;
    API REST_CLIENT;
    String stProfileId;
    HashMap<String, String> user;
    SessionManager session;
    ImageButton ibCall;
    String city;
    Marker cab,gPickup,gDrop;
    ImageButton btGetDirections;
    double directionLat,directionLong;
    String c_lat,c_long;
    Handler h;
    Runnable r;
    boolean gettingDirections=false;
    BottomSheetDialogFragment myBottomSheet;
    MediaPlayer mp;
    String companyId="CMP00001";
    TextView tvNewBooking;
    boolean isMarkerRotating=false;
    LatLng startPosition,finalPosition,currentPosition;
    double cpLat,cpLng;
    LatLng lastLocDist;
    TextView tvRid,tvDateTime;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String requestId;
    boolean first=true;
    TextView tvPaymentMode,tvCancelRide;
    String pickupLat,pickupLong,rideStartingTime;
    DBAdapter dbAdapter;
    String cancelOption="";
    boolean checked=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_ride);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        tvCloc=(TextView)findViewById(R.id.atr_tv_cloc);
        ibClose=(ImageButton)findViewById(R.id.atr_ib_close);
        rlBottomSheet=(RelativeLayout)findViewById(R.id.atr_rl_bottom_sheet);
        ibDots=(ImageButton)findViewById(R.id.atr_ib_dots);
        btPickup=(Button)findViewById(R.id.atr_bt_pick);
        btArrived=(Button)findViewById(R.id.atr_bt_arrived);

        btPickup.setVisibility(View.GONE);
        tvGname=(TextView)findViewById(R.id.atr_tv_gname);
        tvGmobile=(TextView)findViewById(R.id.atr_tv_gmobile);
        tvPickup=(TextView)findViewById(R.id.atr_tv_ploc);
        tvDrop=(TextView)findViewById(R.id.atr_tv_dloc);
        ibCall=(ImageButton)findViewById(R.id.atr_ib_call);
        btGetDirections=(ImageButton)findViewById(R.id.atr_bt_get_directions);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvRid=(TextView)findViewById(R.id.atr_tv_creq_title);
        tvDateTime=(TextView)findViewById(R.id.atr_tv_date_time);
        tvNewBooking=(TextView)findViewById(R.id.atr_tv_new_booking);
        tvNewBooking.setVisibility(View.GONE);

        tvPaymentMode=(TextView)findViewById(R.id.atr_tv_payment);
        tvCancelRide=(TextView)findViewById(R.id.atr_tv_cancel_ride);

        REST_CLIENT= RestClient.get();

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        stProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        city=pref.getString("city",null);

        cabData= (ArrayList<GuestData>) getIntent().getSerializableExtra("cabData");

        if(cabData!=null) {
            data = cabData.get(0);

            if(data!=null)
            {
               // continue
                tvGname.setText(data.getgName());
                tvGmobile.setText(data.getgMobile());
                tvPickup.setText(data.getgPickup());
                tvDrop.setText(data.getgDrop());

                if(data.getPaymentMode()!=null) {
                    String upperString = data.getPaymentMode().substring(0, 1).toUpperCase() + data.getPaymentMode().substring(1);
                    tvPaymentMode.setText(upperString + " Payment");
                }else {
                    tvPaymentMode.setText("Cash Payment");

                }

                SimpleDateFormat  format1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",Locale.ENGLISH);
                SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a",Locale.ENGLISH);
                try {
                    tvDateTime.setText(format.format(format1.parse(data.getScheduledDate())).split(" ")[0] + " " + data.getScheduledTime());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                //tvDateTime.setText(data.getScheduledDate().split(" ")[0]+" "+data.getScheduledTime());
                requestId=data.getgRequestId();

                if(data.getTravelPackage().equals(""))
                {

                }
                else
                {
                    tvRid.setText("Ride Details - "+data.getTravelPackage());
                }
            }
            else {

                Toast.makeText(TrackRideActivity.this,"Unknown error!Please reopen the booking.",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        }
        else {

            Toast.makeText(TrackRideActivity.this,"Unknown error!Please reopen the booking.",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }

        mRequestingLocationUpdates=false;

        dropTime=java.text.DateFormat.getTimeInstance().format(new Date());

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(rlBottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING....");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback..", "slideOffset: " + slideOffset);

            }
        });

        if(Build.VERSION.SDK_INT<23)
        {
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
                    Toast.makeText(TrackRideActivity.this,"Location Permission is required for this app to run!",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

        tvCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checked=false;

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_cancel_options, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final RadioGroup rg=(RadioGroup)dialogView.findViewById(R.id.aco_rb);
                TextView ok=(TextView)dialogView.findViewById(R.id.aco_bt_ok);

                ArrayList<String> cancelData=dbAdapter.getCancelOptions();

                for (int i = 0; i < cancelData.size(); i++) {
                    RadioButton radioButton = new RadioButton(TrackRideActivity.this);
                    radioButton.setText(cancelData.get(i));
                    radioButton.setId(i);
                    rg.addView(radioButton);
                }

                //set listener to radio button group
               rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        System.out.println(rg.getCheckedRadioButtonId()+"::"+checkedId);
                        int checkedRadioButtonId =rg.getCheckedRadioButtonId();
                        RadioButton radioBtn = (RadioButton)dialogView.findViewById(checkedId);
                        //System.out.println("++++"+radioBtn.getText());
                        //System.out.println("++++"+radioBtn.getText().toString());

                        cancelOption=radioBtn.getText().toString();
                        checked=true;
                        //radioBtn.getText()
                        //Toast.makeText(ConfigurationActivity.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(checked) {

                            alertDialog.dismiss();
                            final ProgressDialog progressDialog = new ProgressDialog(TrackRideActivity.this);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Please Wait..!");
                            progressDialog.show();

                            //System.out.println("+++++++++++++ 0" + dbAdapter.getCancelId(cancelOption));
                            String id=dbAdapter.getCancelId(cancelOption);
                            System.out.println("id "+id);

                            //Toast.makeText(TrackRideActivity.this,cancelOption,Toast.LENGTH_SHORT).show();

                            JsonObject j = new JsonObject();
                            j.addProperty("requestid", requestId);
                            j.addProperty("companyid", companyId);
                            j.addProperty("reasonid",id);
                            j.addProperty("source", "driver");
                            j.addProperty("profileid", stProfileId);
                            j.addProperty("reason", cancelOption);

                            Call<Pojo> call = REST_CLIENT.sendCancelStatus(j);
                            call.enqueue(new Callback<Pojo>() {
                                @Override
                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                    progressDialog.dismiss();
                                    if (response.isSuccessful()) {


                                        if (h != null) {
                                            h.removeCallbacks(r);
                                        }
                                        stopLocationUpdates();
                                        //mGoogleApiClient.disconnect();

                                        editor.putString("booking", "out");
                                        editor.commit();

                                        Intent i = new Intent(TrackRideActivity.this, HomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Pojo> call, Throwable t) {

                                    progressDialog.dismiss();
                                    Toast.makeText(TrackRideActivity.this, "Check Internet connection..Please retry!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        else {

                            Toast.makeText(TrackRideActivity.this,"Please select a reason!",Toast.LENGTH_SHORT).show();
                        }

                        //API Call
                    }
                });



            }
        });

        btGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(data.getpLat().equals(""))&&!(data.getpLng().equals(""))) {

                    directionLat = Double.parseDouble(data.getpLat());
                    directionLong = Double.parseDouble(data.getpLng());

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (isSystemAlertPermissionGranted(TrackRideActivity.this)) {

                            //Toast.makeText(TrackRideActivity.this, "permission granted", Toast.LENGTH_LONG).show();
                            stopService(new Intent(getApplicationContext(), RideOverlayService.class));
                            startService(new Intent(getApplicationContext(), RideOverlayService.class));
                            // startService(new Intent(getApplicationContext(), HUD.class));

                            gettingDirections = true;

                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + directionLat + "," + directionLong);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");


                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }

                        } else {
                            requestSystemAlertPermission(TrackRideActivity.this, 1);
                        }
                    } else {
                        //Toast.makeText(TrackRideActivity.this, "permission granted..", Toast.LENGTH_LONG).show();
                        stopService(new Intent(getApplicationContext(), RideOverlayService.class));
                        startService(new Intent(getApplicationContext(), RideOverlayService.class));
                        // startService(new Intent(getApplicationContext(), HUD.class));

                        gettingDirections = true;

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + directionLat + "," + directionLong);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");


                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                }
            }
        });

        ibCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+data.getgMobile()));//GUEST NUMBER HERE...
                startActivity(intent);
            }
        });

        btArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean bookingValue=pref.getString("booking","").equals("out");

                if(bookingValue)
                {
                    Toast.makeText(TrackRideActivity.this,"Booking already finished!",Toast.LENGTH_LONG).show();
                    Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();
                }
                else {

                    final ProgressDialog progressDialog = new ProgressDialog(TrackRideActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please Wait..!");
                    progressDialog.show();

                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", stProfileId);
                    v.addProperty("location", city);
                    c_lat = String.valueOf(current_lat);
                    c_long = String.valueOf(current_long);
                    v.addProperty("latittude", c_lat);
                    v.addProperty("longitude", c_long);
                    v.addProperty("companyid", companyId);
                    v.addProperty("ReqId", requestId);

                    Call<Pojo> call = REST_CLIENT.sendStatus(v);
                    call.enqueue(new Callback<Pojo>() {

                        Pojo msg;

                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if (response.isSuccessful()) {

                                /*if (myBottomSheet.isAdded()) {
                                    myBottomSheet.dismiss();

                                }*/

                                msg = response.body();

                                if (msg.getMessage().equals("cancelled")) {
                                    //mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                    mp.start();
                                    mp.setLooping(true);
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_ride_cancelled, null);
                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    Button btOk = (Button) dialogView.findViewById(R.id.arc_bt_ok);
                                    // Toast.makeText(TrackRideActivity.this,"Ride Cancelled !",Toast.LENGTH_LONG).show();
                                    if(h!=null) {
                                        h.removeCallbacks(r);
                                    }
                                    stopLocationUpdates();
                                    //mGoogleApiClient.disconnect();

                                    editor.putString("booking","out");
                                    editor.commit();

                                    btOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            if (mp.isPlaying()) {
                                                mp.stop();
                                                mp.release();
                                                // mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                                mp = null;
                                            }

                                            alertDialog.dismiss();
                                            progressDialog.dismiss();
                                            Intent i = new Intent(TrackRideActivity.this, HomeActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    });
                                } else {
                                    JsonObject v = new JsonObject();
                                    v.addProperty("profileid", stProfileId);
                                    v.addProperty("requestid", data.getgRequestId());
                                    v.addProperty("companyid", companyId);

                                    Call<Pojo> call1 = REST_CLIENT.sendOtpNotify(v);
                                    call1.enqueue(new Callback<Pojo>() {
                                        @Override
                                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                            /*if (myBottomSheet.isAdded()) {
                                                myBottomSheet.dismiss();

                                            }*/

                                            if (response.isSuccessful()) {

                                                if(response.message().equals("cancelled"))
                                                {
                                                    mp.start();
                                                    mp.setLooping(true);

                                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                                                    LayoutInflater inflater = getLayoutInflater();
                                                    final View dialogView = inflater.inflate(R.layout.alert_ride_cancelled, null);
                                                    dialogBuilder.setView(dialogView);

                                                    final AlertDialog alertDialog = dialogBuilder.create();
                                                    alertDialog.show();
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setCanceledOnTouchOutside(false);

                                                    Button btOk=(Button)dialogView.findViewById(R.id.arc_bt_ok);
                                                    // Toast.makeText(TrackRideActivity.this,"Ride Cancelled !",Toast.LENGTH_LONG).show();
                                                    if(h!=null) {
                                                        h.removeCallbacks(r);
                                                    }
                                                    stopLocationUpdates();
                                                    //mGoogleApiClient.disconnect();

                                                    editor.putString("booking","out");
                                                    editor.commit();

                                                    btOk.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            if (mp.isPlaying()) {
                                                                mp.stop();
                                                                mp.release();
                                                                //mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                                                mp=null;
                                                            }

                                                            Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
                                                            startActivity(i);

                                                            alertDialog.dismiss();
                                                            finish();
                                                        }
                                                    });
                                                }
                                                else {

                                                    btArrived.setVisibility(View.GONE);
                                                    btPickup.setVisibility(View.VISIBLE);
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Pojo> call, Throwable t) {

                                            Toast.makeText(TrackRideActivity.this,"Check Internet connection!",Toast.LENGTH_LONG).show();


                                           /* if (myBottomSheet.isAdded()) {
                                                //return;
                                            } else {
                                                myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                                            }*/
                                            progressDialog.dismiss();

                                        }
                                    });
                                }
                            } else {
                                //System.out.println("error msg is " + response.errorBody() + ":" + response.message() + ":" + response.code());

                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            progressDialog.dismiss();

                            Toast.makeText(TrackRideActivity.this,"Check Internet connection!",Toast.LENGTH_LONG).show();



                           /* if (myBottomSheet.isAdded()) {
                                //return;
                            } else {
                                myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                            }*/
                        }
                    });
                }
            }
        });

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rlBottomSheet.setVisibility(View.GONE);
            }
        });

        ibDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rlBottomSheet.setVisibility(View.VISIBLE);
            }
        });

        btPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean bookingValue=pref.getString("booking","").equals("out");

                if(bookingValue)
                {
                    Toast.makeText(TrackRideActivity.this,"Booking already finished!",Toast.LENGTH_LONG).show();
                    Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();
                }
                else {

                    final ProgressDialog progressDialog = new ProgressDialog(TrackRideActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please Wait..!");
                    progressDialog.show();

                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", stProfileId);
                    v.addProperty("location", city);
                    c_lat = String.valueOf(current_lat);
                    c_long = String.valueOf(current_long);
                    v.addProperty("latittude", c_lat);
                    v.addProperty("longitude", c_long);
                    v.addProperty("companyid", companyId);
                    v.addProperty("ReqId", requestId);

                    Call<Pojo> call = REST_CLIENT.sendStatus(v);
                    call.enqueue(new Callback<Pojo>() {

                        Pojo msg;

                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if (response.isSuccessful()) {

                                /*if (myBottomSheet.isAdded()) {
                                    myBottomSheet.dismiss();

                                }*/

                                msg = response.body();

                                if (msg.getMessage().equals("cancelled")) {
                                    //mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                    mp.start();
                                    mp.setLooping(true);
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_ride_cancelled, null);
                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    Button btOk = (Button) dialogView.findViewById(R.id.arc_bt_ok);
                                    // Toast.makeText(TrackRideActivity.this,"Ride Cancelled !",Toast.LENGTH_LONG).show();
                                    if(h!=null) {
                                        h.removeCallbacks(r);
                                    }
                                    stopLocationUpdates();
                                    //mGoogleApiClient.disconnect();

                                    editor.putString("booking", "out");
                                    editor.commit();

                                    btOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            if (mp.isPlaying()) {
                                                mp.stop();
                                                mp.release();
                                                // mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                                mp = null;
                                            }

                                            alertDialog.dismiss();
                                            progressDialog.dismiss();
                                            Intent i = new Intent(TrackRideActivity.this, HomeActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    });
                                } else {

                                    progressDialog.dismiss();

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_otp, null);
                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();

                                    final EditText etOtp = (EditText) dialogView.findViewById(R.id.ao_et_otp);
                                    Button btOk = (Button) dialogView.findViewById(R.id.ao_bt_ok);
                                    Button btCancel = (Button) dialogView.findViewById(R.id.ao_bt_cancel);

                                    btOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            System.out.println("getCurrentDatTime**************88 "+getCurrentDateTime());

                                            pickupTime = java.text.DateFormat.getTimeInstance().format(new Date());

                                            String stOtp = etOtp.getText().toString().trim();

                                            JsonObject v = new JsonObject();
                                            v.addProperty("requestid", data.getgRequestId());
                                            v.addProperty("otp", stOtp);
                                            v.addProperty("companyid", companyId);
                                            v.addProperty("rideStartTime", getCurrentDateTime());
                                            v.addProperty("pickup_lat",String.valueOf(current_lat));
                                            v.addProperty("pickup_long",String.valueOf(current_long));

                                            Call<Pojo> call = REST_CLIENT.checkOTP(v);

                                            call.enqueue(new Callback<Pojo>() {
                                                @Override
                                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                    if (response.isSuccessful()) {

                                                        pickupLat = String.valueOf(current_lat);
                                                        pickupLong = String.valueOf(current_long);

                                                        //rideStartingTime = getCurrentTime();
                                                        rideStartingTime=getCurrentDateTime();

                                                        editor.putString("pickup_lat", pickupLat);
                                                        editor.putString("pickup_long", pickupLong);
                                                        editor.putString("rideStartingTime", rideStartingTime);
                                                        editor.commit();

                                                        //System.out.println("pickup data "+pickupLat+":"+pickupLong);
                                                        //System.out.println("rideStartingTime "+rideStartingTime);


                                                        alertDialog.dismiss();
                                                        //btPickup.setVisibility(View.GONE);
                                                        // btDrop.setVisibility(View.VISIBLE);

                                                       /* if (myBottomSheet.isAdded()) {
                                                            myBottomSheet.dismiss();
                                                        }*/

                                                        if(h!=null) {
                                                            h.removeCallbacks(r);
                                                        }

                                                        stopLocationUpdates();
                                                        //mGoogleApiClient.disconnect();
                                                        Intent i = new Intent(TrackRideActivity.this, RideLocal.class);
                                                        i.putExtra("cabData", cabData);
                                                        i.putExtra("current_lat", current_lat);
                                                        i.putExtra("current_long", current_long);
                                                        startActivity(i);
                                                        finish();

                                                    } else {
                                                        alertDialog.dismiss();

                                                        Toast.makeText(TrackRideActivity.this, "OTP Authentication Failed", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Pojo> call, Throwable t) {

                                                    alertDialog.dismiss();

                                                    Toast.makeText(TrackRideActivity.this,"Check Internet connection!",Toast.LENGTH_LONG).show();

                                                   /* if (myBottomSheet.isAdded()) {
                                                        //return;
                                                    } else {
                                                        myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                                                    }*/
                                                }
                                            });
                                        }
                                    });

                                    btCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            }
                            else {
                                //System.out.println("error msg is " + response.errorBody() + ":" + response.message() + ":" + response.code());

                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            progressDialog.dismiss();

                            Toast.makeText(TrackRideActivity.this,"Check Internet connection!",Toast.LENGTH_LONG).show();


                            /*if (myBottomSheet.isAdded()) {
                                //return;
                            } else {
                                myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                            }*/
                        }
                    });
                }
            }
        });



        tvNewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvNewBooking.setVisibility(View.GONE);

            }
        });
    }

    public static void requestSystemAlertPermission(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        final String packageName = context == null ? context.getPackageName() : context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        if (context != null)
            context.startActivityForResult(intent, requestCode);
        else
            context.startActivityForResult(intent, requestCode);
    }

    @TargetApi(23)
    public static boolean isSystemAlertPermissionGranted(Context context) {

        final boolean result = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || Settings.canDrawOverlays(context);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                establishConnection();

            } else {
                Toast.makeText(TrackRideActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
        //sendLocationUpdatesToServer();
    }

    public void sendLocationUpdatesToServer()
    {
        h=new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                //changed from 15 sec to 30 sec
                h.postDelayed(r,20000);

                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);
                    if (addresses.size() != 0) {
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

                        if(add_1!=null&&add_2!=null) {
                            complete_address = address + " " + add_1 + " " + add_2;
                        }
                        else {
                            complete_address=address;
                        }
                        //complete_address = add;
                        tvCloc.setText(complete_address);

                    } else {
                        tvCloc.setText("-");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    complete_address = "Unable to get the location details";
                    tvCloc.setText(complete_address);
                }

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude!=0.0 && lastLocDist.longitude!=0.0) {

                    curntloc=new LatLng(current_lat,current_long);

                    startPosition = cab.getPosition();
                    finalPosition = new LatLng(current_lat, current_long);


                    double toRotation = bearingBetweenLocations(startPosition, finalPosition);
                    rotateMarker(cab, (float) toRotation);

                    accelerateDecelerate();

//                    CameraPosition oldPos = mMap.getCameraPosition();
//
//                    CameraPosition pos = CameraPosition.builder(oldPos).bearing((float)toRotation).build();
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 16));
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                }

                JsonObject v=new JsonObject();
                v.addProperty("profileid",stProfileId);
                v.addProperty("location",city);
                c_lat=String.valueOf(current_lat);
                c_long=String.valueOf(current_long);
                v.addProperty("latittude",c_lat);
                v.addProperty("longitude",c_long);
                v.addProperty("companyid",companyId);
                v.addProperty("ReqId",requestId);

               System.out.println("*****"+stProfileId+"**"+city+"****"+c_lat+"**"+c_long+"******");
                Call<Pojo> call=REST_CLIENT.sendStatus(v);
                call.enqueue(new Callback<Pojo>() {

                    Pojo msg;

                    @Override
                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                        if(response.isSuccessful())
                        {
                            msg=response.body();
                            String newbooking[]=msg.getMessage().split("-");

                            //System.out.println("msg.getMessage() "+msg.getMessage());

                            if(newbooking.length==1)
                            {

                            }
                            else
                            {
                                tvNewBooking.setVisibility(View.VISIBLE);

                                if(newbooking[0].equals("cancelled "))
                                {
                                    mp.start();
                                    mp.setLooping(true);

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_ride_cancelled, null);
                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    Button btOk=(Button)dialogView.findViewById(R.id.arc_bt_ok);
                                    // Toast.makeText(TrackRideActivity.this,"Ride Cancelled !",Toast.LENGTH_LONG).show();
                                    h.removeCallbacks(r);
                                    stopLocationUpdates();
                                    //mGoogleApiClient.disconnect();

                                    editor.putString("booking","out");
                                    editor.commit();

                                    btOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (mp.isPlaying()) {
                                                mp.stop();
                                                mp.release();
                                                //mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                                mp=null;
                                            }

                                            Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
                                            startActivity(i);

                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            }

                            if(msg.getMessage().equals("cancelled"))
                            {

                                mp.start();
                                mp.setLooping(true);

                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TrackRideActivity.this);

                                LayoutInflater inflater = getLayoutInflater();
                                final View dialogView = inflater.inflate(R.layout.alert_ride_cancelled, null);
                                dialogBuilder.setView(dialogView);

                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();
                                alertDialog.setCancelable(false);
                                alertDialog.setCanceledOnTouchOutside(false);

                                Button btOk=(Button)dialogView.findViewById(R.id.arc_bt_ok);
                                // Toast.makeText(TrackRideActivity.this,"Ride Cancelled !",Toast.LENGTH_LONG).show();
                                h.removeCallbacks(r);
                                stopLocationUpdates();
                                //mGoogleApiClient.disconnect();

                                editor.putString("booking","out");
                                editor.commit();

                                btOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (mp.isPlaying()) {
                                            mp.stop();
                                            mp.release();
                                            //mp = MediaPlayer.create(TrackRideActivity.this, R.raw.beep);
                                            mp=null;
                                        }

                                        Intent i=new Intent(TrackRideActivity.this,HomeActivity.class);
                                        startActivity(i);

                                        alertDialog.dismiss();
                                        finish();
                                    }
                                });
                            }
                        }
                        else
                        {

                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                        Toast.makeText(TrackRideActivity.this,"Check Internet connection!",Toast.LENGTH_LONG).show();
                    }
                });

                lastLocDist = new LatLng(current_lat, current_long);
            }
        };
        h.post(r);
    }


    @Override
    protected void onStart() {

        super.onStart();
        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {

            }
            else
            {
                if(gettingDirections) {

                }
                else
                {
                    //mGoogleApiClient.connect();
                }
                //super.onStart();
            }
        }
        else
        {
            if(gettingDirections) {

            }
            else
            {
                // mGoogleApiClient.connect();
            }
            // super.onStart();
        }
    }

    @Override
    protected void onStop() {

        if(mGoogleApiClient!=null) {

            if(gettingDirections)
            {

            }
            else {
                //mGoogleApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {

                if(gettingDirections)
                {

                }
                else {
                    //stopLocationUpdates();
                }
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
            }
            else {
                mGoogleApiClient.connect();
            }
        }

        if(mGoogleApiClient!=null) {

            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {

                if(gettingDirections)
                {
                    gettingDirections=false;
                }
                else {
                    // startLocationUpdates();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        stopService(new Intent(getApplicationContext(), RideOverlayService.class));

        if(h!=null)
        {
            h.removeCallbacks(r);
        }

        if (mp != null) {

            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }

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
                            status.startResolutionForResult(TrackRideActivity.this, REQUEST_LOCATION);
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
        mLocationRequest.setInterval(2000);//10 sec
        mLocationRequest.setFastestInterval(2000);//5 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,TrackRideActivity.this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){

        if (mGoogleApiClient != null) {

            if(mGoogleApiClient.isConnected()) {

                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, TrackRideActivity.this);
                mGoogleApiClient.disconnect();
            }
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
                    city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    complete_address = address + " " + add1 + " " + add2;
                    tvCloc.setText(complete_address);
                }
                else
                {
                    tvCloc.setText("-");
                }
            }
            catch(IOException e)
            {e.printStackTrace();
                complete_address="Unable to get the location details";
                tvCloc.setText(complete_address);
            }
            // mMap.addMarker(new MarkerOptions().position(lastLoc)
            //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
            //cab.setPosition(lastLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 16));
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }


        if(mLastLocation!=null) {

            if(location!=null&&location.hasAccuracy()) {

                if (location.getAccuracy() <= 10) {

                    //System.out.println(location.getAccuracy()+":"+current_lat+":"+current_long);

                    current_lat = location.getLatitude();
                    current_long = location.getLongitude();

                    if (current_lat != 0.0 && current_long != 0.0) {
                        //curntloc = new LatLng(current_lat, current_long);
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), current_lat, current_long, results);
                        location.getAccuracy();
                        res = res + (long) results[0];
                        //cab.setPosition(curntloc);
                    }
                }
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
        lastLocDist=lastLoc;
        current_lat=lastLoc.latitude;
        current_long=lastLoc.longitude;

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        if(gPickup!=null)
        {

        }
        else
        {
            if(!(data.getpLat().equals(""))&&!(data.getpLng()).equals("")) {
                LatLng pickLatLng = new LatLng(Double.parseDouble(data.getpLat()), Double.parseDouble(data.getpLng()));
                gPickup = mMap.addMarker(new MarkerOptions().position(pickLatLng)
                        .title(data.getgPickup())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
            }
        }

        if(gDrop!=null)
        {

        }
        else
        {
            if((!(data.getdLat().equals("-"))&&!(data.getdLng().equals("-")))&&(!(data.getdLat().equals(""))&&!(data.getdLng().equals("")))) {

                LatLng dropLatLng = new LatLng(Double.parseDouble(data.getdLat()), Double.parseDouble(data.getdLng()));
                gDrop = mMap.addMarker(new MarkerOptions().position(dropLatLng)
                        .title(data.getgDrop())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pink)));
            }
        }

        if(cab!=null)
        {

        }
        else {
            cab = mMap.addMarker(new MarkerOptions().position(lastLoc)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(lastLoc)
                .zoom(16)
                //.bearing(30).tilt(45)
                .build()));

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
                complete_address=address+" "+add_1+" "+add_2;

                tvCloc.setText(complete_address);
            }
            else {
                tvCloc.setText("-");
            }
        }
        catch (Exception e){e.printStackTrace();}

        if(first)
        {
            sendLocationUpdatesToServer();
            first=false;
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
    }

    private void accelerateDecelerate()
    {
        final Handler handler = new Handler();

        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 5000;
        final boolean hideMarker = false;



        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;

                cpLat=startPosition.latitude * (1 - t) + finalPosition.latitude * t;
                cpLng= startPosition.longitude * (1 - t) + finalPosition.longitude * t;

                currentPosition = new LatLng(cpLat,cpLng);

                cab.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        cab.setVisible(false);
                    } else {
                        cab.setVisible(true);
                    }
                }
            }
        });
    }

    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    public static String getCurrentTime() {
        //date output format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getCurrentDateTime() {
        //date output format
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
