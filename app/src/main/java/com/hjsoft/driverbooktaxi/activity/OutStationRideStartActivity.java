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
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.Distance;
import com.hjsoft.driverbooktaxi.model.DistancePojo;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.Leg;
import com.hjsoft.driverbooktaxi.model.LocationUpdates;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.model.Route;
import com.hjsoft.driverbooktaxi.service.OutStationRideStartOverlayService;
import com.hjsoft.driverbooktaxi.service.RideStartOverlayService;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.text.DecimalFormat;
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

/**
 * Created by hjsoft on 2/3/17.
 */
public class OutStationRideStartActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected boolean mRequestingLocationUpdates;
    final static int REQUEST_LOCATION = 199;
    protected Location mLastLocation;
    double latitude,longitude,current_lat=0.0,current_long=0.0;
    Geocoder geocoder;
    List<Address> addresses;
    LatLng lastLoc,curntloc;
    String complete_address;
    float[] results=new float[3];
    long res=0;
    boolean entered=false;
    Button btDrop;
    String rideStartingTime,rideStoppingTime,rideCurrentTime;
    TextView tvCurrentLoc;
    String movingTimeFormat;
    ArrayList<GuestData> cabData;
    GuestData data;
    TextView tvGname,tvGmobile,tvGpickup,tvGdrop;
    ImageButton ibClose,ibDots;
    View vwBottomSheet;
    API REST_CLIENT;
    String stProfileId;
    HashMap<String, String> user;
    SessionManager session;
    String city;
    DBAdapter dbAdapter;
    SimpleDateFormat dateFormat;
    String timeUpdated;
    Marker cab,gPickup,gDrop;
    int waitingTime=0;
    String c_lat,c_long;
    Handler h,hC;
    Runnable r,rC;
    float[] dist=new float[3];
    long resDist=0;
    LatLng lastLocDist;
    ImageButton btGetDirections,btStop,ibCall;
    boolean gettingDirections=false;
    BottomSheetDialogFragment myBottomSheet;
    String requestId;
    boolean isMarkerRotating=false;
    LatLng startPosition,finalPosition,currentPosition;
    double cpLat,cpLng;
    String companyId="CMP00001";
    String guestName,guestMobile;
    TextView tvNewBooking,tvDateTime,tvRideTitle;
    String pickupLat,pickupLong,dropLat,dropLong;
    float finalDistance,distance=0;
    String rideData=" ";
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    long diff = 0,pauseDiff=0;
    String pauseTime, moveTime;
    boolean first=true,start=true;
    String billing="-";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstation_ride_start);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btDrop=(Button)findViewById(R.id.aors_bt_drop);
        tvCurrentLoc=(TextView)findViewById(R.id.aors_tv_cloc);
        tvGname=(TextView)findViewById(R.id.aors_tv_gname);
        tvGmobile=(TextView)findViewById(R.id.aors_tv_gmobile);
        tvGpickup=(TextView)findViewById(R.id.aors_tv_ploc);
        tvGdrop=(TextView)findViewById(R.id.aors_tv_dloc);
        ibClose=(ImageButton)findViewById(R.id.aors_ib_close);
        vwBottomSheet=(RelativeLayout)findViewById(R.id.aors_rl_bottom_sheet);
        ibCall=(ImageButton)findViewById(R.id.aors_ib_call);
        ibDots=(ImageButton)findViewById(R.id.aors_ib_dots);
        btGetDirections=(ImageButton)findViewById(R.id.aors_bt_get_directions);
        btStop=(ImageButton)findViewById(R.id.aors_bt_stop);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvDateTime=(TextView)findViewById(R.id.aors_tv_date_time);
        tvRideTitle=(TextView)findViewById(R.id.aors_tv_creq_title);

        tvNewBooking=(TextView)findViewById(R.id.aors_tv_new_booking);
        tvNewBooking.setVisibility(View.GONE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());

        //rideStartingTime=java.text.DateFormat.getTimeInstance().format(new Date());
        rideStartingTime=getCurrentTime();

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        city=pref.getString("city",null);

        cabData= (ArrayList<GuestData>) getIntent().getSerializableExtra("cabData");
        data=cabData.get(0);

        tvGname.setText(data.getgName());
        tvGmobile.setText(data.getgMobile());
        tvGpickup.setText(data.getgPickup());
        tvGdrop.setText(data.getgDrop());
        tvRideTitle.setText(data.getTravelType()+" - "+data.getTravelPackage());

        SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        try {
            tvDateTime.setText(format.format(format.parse(data.getScheduledDate())).split(" ")[0] + " " + data.getScheduledTime());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        requestId=data.getgRequestId();
/*
        pickupLat=data.getpLat();
        pickupLong=data.getpLng();

        editor.putString("pickup_lat",pickupLat);
        editor.putString("pickup_long",pickupLong);
        */
        editor.putString("ride_starting_time",rideStartingTime);
        editor.putString("request_id",requestId);
        editor.putString("company_id",companyId);
        editor.commit();

        guestName=data.getgName();
        guestMobile=data.getgMobile();

        REST_CLIENT=RestClient.get();

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        stProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        dbAdapter=new DBAdapter(this);
        dbAdapter=dbAdapter.open();
        dbAdapter.insertRideStatus(requestId,"ongoing");

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mRequestingLocationUpdates=false;

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
                    Toast.makeText(OutStationRideStartActivity.this,"Location Permission is required for this app to run!",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(vwBottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
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
                Log.i("BottomSheetCallback", "slideOffset" + slideOffset);

            }
        });

        tvNewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<List<CabRequestsPojo>> call = REST_CLIENT.getCabRequests(stProfileId, companyId);
                call.enqueue(new Callback<List<CabRequestsPojo>>() {
                    @Override
                    public void onResponse(Call<List<CabRequestsPojo>> call, Response<List<CabRequestsPojo>> response) {

                        List<CabRequestsPojo> dataList;
                        CabRequestsPojo data;

                        if(response.isSuccessful())
                        {
                            tvNewBooking.setVisibility(View.GONE);

                            dataList=response.body();
                            data=dataList.get(0);
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OutStationRideStartActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_new_booking, null);
                            dialogBuilder.setView(dialogView);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();

                            TextView tvPickupLoc=(TextView)dialogView.findViewById(R.id.anb_tv_pickuploc);
                            TextView tvDropLoc=(TextView)dialogView.findViewById(R.id.anb_tv_droploc);
                            TextView tvReportingTime=(TextView)dialogView.findViewById(R.id.anb_tv_reportingTime);
                            TextView tvTravelType=(TextView)dialogView.findViewById(R.id.anb_tv_travelType);
                            TextView tvTravelPackage=(TextView)dialogView.findViewById(R.id.anb_tv_travelPackage);
                            TextView tvBookingType=(TextView)dialogView.findViewById(R.id.anb_tv_bookingType);

                            tvPickupLoc.setText(data.getPickupLoc());
                            tvDropLoc.setText(data.getDropLoc());
                            tvReportingTime.setText(data.getScheduledTime());
                            tvTravelType.setText(data.getTravelType());
                            tvTravelPackage.setText(data.getTravelPackage());
                            tvBookingType.setText(data.getBookingtype());
                        }
                        else {

                            tvNewBooking.setVisibility(View.GONE);

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OutStationRideStartActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_new_booking, null);
                            dialogBuilder.setView(dialogView);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();

                            TextView tvPickupLoc=(TextView)dialogView.findViewById(R.id.anb_tv_pickuploc);
                            TextView tvDropLoc=(TextView)dialogView.findViewById(R.id.anb_tv_droploc);
                            TextView tvReportingTime=(TextView)dialogView.findViewById(R.id.anb_tv_reportingTime);
                            TextView tvTravelType=(TextView)dialogView.findViewById(R.id.anb_tv_travelType);
                            TextView tvTravelPackage=(TextView)dialogView.findViewById(R.id.anb_tv_travelPackage);
                            TextView tvBookingType=(TextView)dialogView.findViewById(R.id.anb_tv_bookingType);

                            tvPickupLoc.setText("-");
                            tvDropLoc.setText("-");
                            tvReportingTime.setText("-");
                            tvTravelType.setText("-");
                            tvTravelPackage.setText("-");
                            tvBookingType.setText("No New Booking!");
                        }

                    }

                    @Override
                    public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

                        Toast.makeText(OutStationRideStartActivity.this,"Connectivity Error!",Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(OutStationRideStartActivity.this,"Ride Paused !",Toast.LENGTH_SHORT).show();
                h.removeCallbacks(r);
                hC.removeCallbacks(rC);

                // pauseTime = java.text.DateFormat.getTimeInstance().format(new Date());
                pauseTime=getCurrentTime();
                stopLocationUpdates();

                Date date = new Date();
                timeUpdated=dateFormat.format(date);

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude!=0.0 && lastLocDist.longitude!=0.0) {

                    dbAdapter.insertEntry(requestId,current_lat,current_long, complete_address, resDist, timeUpdated);

                } else {

                    //dbAdapter.insertEntry(0.0, 0.0, complete_address, resDist, timeUpdated);
                }

                if(dbAdapter.findRequestId(requestId)) {

                    ArrayList<LocationUpdates> list=dbAdapter.getAllLocUpdates(requestId);
                    LocationUpdates l;

                    for(int i=0;i<list.size();i++)
                    {
                        l=list.get(0);
                    }
                    dbAdapter.updateLocEntry(requestId, rideCurrentTime, resDist, current_lat, current_long,pauseDiff);

                    ArrayList<LocationUpdates> list1=dbAdapter.getAllLocUpdates(requestId);
                    LocationUpdates l1;

                    for(int i=0;i<list.size();i++)
                    {
                        l1=list1.get(0);
                    }
                }
                else
                {
                    dbAdapter.insertLocEntry(requestId,rideStartingTime,rideCurrentTime,resDist,current_lat,current_long,
                            Double.parseDouble(data.getpLat()),Double.parseDouble(data.getpLng()),Double.parseDouble(data.getdLat()),Double.parseDouble(data.getdLng()),guestName,guestMobile,pauseDiff);
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OutStationRideStartActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_ride_paused, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);

                Button ok=(Button)dialogView.findViewById(R.id.arp_bt_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //moveTime = java.text.DateFormat.getTimeInstance().format(new Date());
                        moveTime=getCurrentTime();

                        h.post(r);
                        hC.post(rC);
                        alertDialog.dismiss();
                        startLocationUpdates();

                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        try {
                            Date date1 = timeFormat.parse(pauseTime);
                            Date date2 = timeFormat.parse(moveTime);

                            pauseDiff = pauseDiff + (date2.getTime() - date1.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                /*
                Here a provision is provided so that
                 */
            }
        });

        btGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT>=23) {

                    if (isSystemAlertPermissionGranted(OutStationRideStartActivity.this)) {

                        // Toast.makeText(OutStationRideStartActivity.this, "permission granted", Toast.LENGTH_LONG).show();
                        stopService(new Intent(getApplicationContext(), OutStationRideStartOverlayService.class));
                        startService(new Intent(getApplicationContext(), OutStationRideStartOverlayService.class));
                        // startService(new Intent(getApplicationContext(), HUD.class));

                        gettingDirections = true;

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + data.getdLat() + "," + data.getdLng());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        //mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");


                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }

                    } else {
                        requestSystemAlertPermission(OutStationRideStartActivity.this, 1);
                    }
                }
                else {

                    // Toast.makeText(OutStationRideStartActivity.this, "permission granted", Toast.LENGTH_LONG).show();
                    stopService(new Intent(getApplicationContext(), OutStationRideStartOverlayService.class));
                    startService(new Intent(getApplicationContext(), OutStationRideStartOverlayService.class));
                    // startService(new Intent(getApplicationContext(), HUD.class));

                    gettingDirections = true;

                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + data.getdLat() + "," + data.getdLng());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    //mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");


                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
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

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vwBottomSheet.setVisibility(View.GONE);

            }
        });

        ibDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vwBottomSheet.setVisibility(View.VISIBLE);
            }
        });

        btDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OutStationRideStartActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_drop, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                Button btOk=(Button)dialogView.findViewById(R.id.ad_bt_ok);
                Button btCancel=(Button)dialogView.findViewById(R.id.ad_bt_cancel);

                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean bookingValue=pref.getString("booking","").equals("out");


                        if(bookingValue)
                        {
                            Toast.makeText(OutStationRideStartActivity.this,"Booking already finished!",Toast.LENGTH_LONG).show();
                            dbAdapter.deleteLocUpdates(requestId);
                            dbAdapter.deleteRideDetails(requestId);
                            Intent i=new Intent(OutStationRideStartActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {

                            alertDialog.dismiss();
                            sendFinishDetailsToServer();
                        }

                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }
                });
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
                Toast.makeText(OutStationRideStartActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();
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
        storeCoordinatesIntoDB();
        //testCoordinates();
    }

    public void sendLocationUpdatesToServer()
    {

        h=new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

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
                            //city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();

                            if(add_1!=null&&add_2!=null) {
                                complete_address = address + " " + add_1 + " " + add_2;
                            }
                            else {
                                complete_address=address;
                            }

                            tvCurrentLoc.setText(complete_address);
                        } else {
                            tvCurrentLoc.setText("-");
                            complete_address = "-";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        complete_address = "Unable to get the location details";
                        tvCurrentLoc.setText(complete_address);
                    }

                    if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude!=0.0 && lastLocDist.longitude!=0.0) {

                        curntloc=new LatLng(current_lat,current_long);
                        startPosition = cab.getPosition();
                        finalPosition = new LatLng(current_lat, current_long);

                        double toRotation = bearingBetweenLocations(startPosition, finalPosition);
                        rotateMarker(cab, (float) toRotation);

                        accelerateDecelerate();

//                        CameraPosition oldPos = mMap.getCameraPosition();
//
//                        CameraPosition pos = CameraPosition.builder(oldPos).bearing((float)toRotation).build();
//                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 16));
                        mMap.getUiSettings().setMapToolbarEnabled(false);
                    } else {

                        // dbAdapter.insertEntry(0.0, 0.0, complete_address, resDist, timeUpdated);
                    }

                    /*

                    if (dbAdapter.findRequestId(requestId)) {

                        ArrayList<LocationUpdates> list = dbAdapter.getAllLocUpdates();
                        LocationUpdates l;

                        for (int i = 0; i < list.size(); i++) {
                            l = list.get(0);
                            System.out.println(l.getRequestId() + "::" + l.getStartingTime() + "::" + l.getStoppingTime() + "::" + l.getDistance() + "::" + l.getLatitude() + "::" + l.getLongitude());
                        }
                        dbAdapter.updateLocEntry(requestId, rideCurrentTime, resDist, current_lat, current_long);
                        // System.out.println("-------------------------------------------------------------------------------");
                        ArrayList<LocationUpdates> list1 = dbAdapter.getAllLocUpdates();
                        LocationUpdates l1;

                        for (int i = 0; i < list.size(); i++) {
                            l1 = list1.get(0);
                            System.out.println(l1.getRequestId() + "::" + l1.getStartingTime() + "::" + l1.getStoppingTime() + "::" + l1.getDistance() + "::" + l1.getLatitude() + "::" + l1.getLongitude());
                        }
                    } else {
                        dbAdapter.insertLocEntry(requestId, rideStartingTime, rideCurrentTime, resDist, current_lat, current_long,
                                Double.parseDouble(data.getpLat()), Double.parseDouble(data.getpLng()), Double.parseDouble(data.getdLat()), Double.parseDouble(data.getdLng()), guestName, guestMobile);
                        //  System.out.println("________________________________________________________________________________");
                    }
                    */

                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", stProfileId);
                    v.addProperty("location", city);
                    c_lat = String.valueOf(current_lat);
                    c_long = String.valueOf(current_long);
                    v.addProperty("latittude", c_lat);
                    v.addProperty("longitude", c_long);
                    v.addProperty("companyid", companyId);
                    v.addProperty("ReqId",requestId);

                    Call<Pojo> call = REST_CLIENT.sendStatus(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if (response.isSuccessful()) {

                                String[] newbooking = response.body().getMessage().split("-");

                                if (newbooking.length == 1) {

                                } else {
                                    tvNewBooking.setVisibility(View.VISIBLE);
                                }

                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            Toast.makeText(OutStationRideStartActivity.this, "Check Internet connection!", Toast.LENGTH_LONG).show();


                        }
                    });

                    lastLocDist = new LatLng(current_lat, current_long);
//here was address retrieval done...

                }
                else {

                    Toast.makeText(OutStationRideStartActivity.this,"GPS is not enabled..Please Check!",Toast.LENGTH_LONG).show();

                  /*
                    if (dbAdapter.findRequestId(requestId)) {

                        ArrayList<LocationUpdates> list = dbAdapter.getAllLocUpdates();
                        LocationUpdates l;

                        for (int i = 0; i < list.size(); i++) {
                            l = list.get(0);
                            System.out.println(l.getRequestId() + "::" + l.getStartingTime() + "::" + l.getStoppingTime() + "::" + l.getDistance() + "::" + l.getLatitude() + "::" + l.getLongitude());
                        }
                        dbAdapter.updateLocEntry(requestId, rideCurrentTime, resDist,0.0, 0.0);
                        // System.out.println("-------------------------------------------------------------------------------");
                        ArrayList<LocationUpdates> list1 = dbAdapter.getAllLocUpdates();
                        LocationUpdates l1;

                        for (int i = 0; i < list.size(); i++) {
                            l1 = list1.get(0);
                            System.out.println(l1.getRequestId() + "::" + l1.getStartingTime() + "::" + l1.getStoppingTime() + "::" + l1.getDistance() + "::" + l1.getLatitude() + "::" + l1.getLongitude());
                        }
                    } else {
                        dbAdapter.insertLocEntry(requestId, rideStartingTime, rideCurrentTime, resDist,0.0,0.0,
                                Double.parseDouble(data.getpLat()), Double.parseDouble(data.getpLng()), Double.parseDouble(data.getdLat()), Double.parseDouble(data.getdLng()), guestName, guestMobile);
                        //  System.out.println("________________________________________________________________________________");
                    }

                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", stProfileId);
                    v.addProperty("location", city);
                    v.addProperty("latittude", "-");
                    v.addProperty("longitude","-");
                    v.addProperty("companyid", companyId);

                    //System.out.println("*****"+stProfileId+"**"+city+"**"+c_lat+"**"+c_long+"******");

                    Call<Pojo> call = REST_CLIENT.sendStatus(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if (response.isSuccessful()) {
                                //  System.out.println("-----------------------------** updated **-----------------------------------");
                                String[] newbooking = response.body().getMessage().split("-");
                                if (newbooking.length == 1) {

                                } else {
                                    tvNewBooking.setVisibility(View.VISIBLE);
                                }

                            } else {
                                System.out.println(response.errorBody() + "**" + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            Toast.makeText(OutStationRideStartActivity.this, "Error in network connection", Toast.LENGTH_LONG).show();


                        }
                    });
*/

                }
            }
        };
        h.post(r);
    }

    public void testCoordinates()
    {
        String f="17.7399916,83.2456419*17.7400248,83.2456158*17.7401643,83.2454088*17.7408212,83.2439189*17.7409049,83.243029*17.7281524,83.2134972*17.7049292,83.2058162*17.6858011,83.2039015*17.6836352,83.1875725*17.6848354,83.1502404*17.688552,83.1347015*17.6879325,83.0968249*17.6920447,83.0521262*17.6750759,83.0063395*17.678325,82.977981*17.6579916,82.9333676*17.6322663,82.8923463*17.5813717,82.8664218*17.5510832,82.8419095*17.5187337,82.8129727*17.4707284,82.7743836*17.4310613,82.7448737*17.4462728,82.7266306*17.4670326,82.7196807*17.4665484,82.7201957*17.4665997,82.7201811*17.4666455,82.7205867*17.4666292,82.7205744*17.4666003,82.7205861*17.4665793,82.7207093*17.466474,82.7206627*17.4666203,82.7205929*17.4666177,82.7205932*17.4666161,82.7205947*17.4666136,82.7205944*17.4666075,82.7205953*17.4666047,82.720594*17.4666041,82.7205929*17.466601,82.7205921*17.4666175,82.7205894*17.4665839,82.7202608";
        String f1[]=f.split("\\*");

        System.out.println("data calculating....");

        for(int i=0;i<f1.length;i++)
        {
            String d[]=f1[i].split(",");
            dbAdapter.insertEntry(requestId,Double.parseDouble(d[0]),Double.parseDouble(d[1]), complete_address, resDist, timeUpdated);

        }

        System.out.println("data finished ......"+f1.length);
    }



    public void storeCoordinatesIntoDB()
    {
        hC=new Handler();
        rC=new Runnable() {
            @Override
            public void run() {

                hC.postDelayed(rC,300000);// 5 min ~ 300 sec
                rideCurrentTime = java.text.DateFormat.getTimeInstance().format(new Date());

                Date date = new Date();
                timeUpdated=dateFormat.format(date);

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude!=0.0 && lastLocDist.longitude!=0.0) {

                    dbAdapter.insertEntry(requestId,current_lat,current_long, complete_address, resDist, timeUpdated);

                } else {

                    //dbAdapter.insertEntry(0.0, 0.0, complete_address, resDist, timeUpdated);

                }


                if (dbAdapter.findRequestId(requestId)) {

                    ArrayList<LocationUpdates> list = dbAdapter.getAllLocUpdates(requestId);
                    LocationUpdates l;

                    dbAdapter.updateLocEntry(requestId, rideCurrentTime, resDist, current_lat, current_long,pauseDiff);
                } else {
                    dbAdapter.insertLocEntry(requestId, rideStartingTime, rideCurrentTime, resDist, current_lat, current_long,
                            Double.parseDouble(data.getpLat()), Double.parseDouble(data.getpLng()), Double.parseDouble(data.getdLat()), Double.parseDouble(data.getdLng()), guestName, guestMobile,pauseDiff);
                }
            }
        };

        hC.post(rC);
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

                }

            }
        }
        else
        {
            if(gettingDirections) {

            }
            else
            {

            }

        }
    }

    @Override
    protected void onStop() {


        if(mGoogleApiClient!=null) {

            if(gettingDirections)
            {

            }
            else {

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
        stopService(new Intent(getApplicationContext(), OutStationRideStartOverlayService.class));

        if(h!=null)
        {
            h.removeCallbacks(r);
        }

        if(hC!=null)
        {
            hC.removeCallbacks(rC);
        }
    }

    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
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
                            status.startResolutionForResult(OutStationRideStartActivity.this, REQUEST_LOCATION);
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
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,OutStationRideStartActivity.this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, OutStationRideStartActivity.this);
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

        /*

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
                city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                complete_address=address+" "+add1+" "+add2;
              tvCurrentLoc.setText(complete_address);
            }
            catch(IOException e)
            {e.printStackTrace();
                complete_address="No response from server";
                tvCurrentLoc.setText(complete_address);
            }
            mMap.addMarker(new MarkerOptions().position(lastLoc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 15));
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }*/


        if(mLastLocation!=null) {

            if(location!=null&&location.hasAccuracy()) {

                if (location.getAccuracy() <= 10) {

                    if(location.getLatitude()!=0.0 && location.getLongitude()!=0.0)
                    {

                        current_lat = location.getLatitude();
                        current_long = location.getLongitude();

                        if(first)
                        {
                            Date date = new Date();
                            timeUpdated=dateFormat.format(date);

                            pickupLat=String.valueOf(current_lat);
                            pickupLong=String.valueOf(current_long);

                            editor.putString("pickup_lat",pickupLat);
                            editor.putString("pickup_long",pickupLong);
                            editor.commit();

                            //edited
                            dbAdapter.insertEntry(requestId,current_lat, current_long, complete_address, resDist, timeUpdated);
                            first=false;
                        }

                        if (location.getSpeed() < 3.0) {
                            waitingTime = waitingTime + 1;
                        }
                        //  res = res + (long) results[0];

                        //cab.setPosition(curntloc);
                    }
                }
            }
        }

        /*

        JsonObject v=new JsonObject();
        v.addProperty("profileid",stProfileId);
        v.addProperty("location",city);
        c_lat=String.valueOf(current_lat);
        c_long=String.valueOf(current_long);
        System.out.println("chaging lat and long.....");
        v.addProperty("latittude",c_lat);
        v.addProperty("longitude",c_long);

        System.out.println("*****"+stProfileId+"**"+city+"**"+c_lat+"**"+c_long+"******");

        Call<Pojo> call=REST_CLIENT.sendStatus(v);
        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(response.isSuccessful())
                {
                    System.out.println("---------------------------updated-----------------------------------");
                }
                else
                {
                    System.out.println(response.errorBody()+"**"+response.message());
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                Toast.makeText(OutStationRideStartActivity.this,"Error in network connection",Toast.LENGTH_LONG).show();


            }
        });*/

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        if(gPickup!=null)
        {

        }
        else
        {
            LatLng pickLatLng=new LatLng(Double.parseDouble(data.getpLat()),Double.parseDouble(data.getpLng()));
            gPickup= mMap.addMarker(new MarkerOptions().position(pickLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
        }

        if(gDrop!=null)
        {

        }
        else
        {
            LatLng dropLatLng=new LatLng(Double.parseDouble(data.getdLat()),Double.parseDouble(data.getdLng()));
            gPickup= mMap.addMarker(new MarkerOptions().position(dropLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pink)));
        }

        if(cab!=null){

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
                //city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                complete_address = address + " " + add_1 + " " + add_2;

                tvCurrentLoc.setText(complete_address);
            }
            else {
                tvCurrentLoc.setText("-");
            }
        }
        catch (Exception e){e.printStackTrace();}

        if(start)
        {
            pickupLat=String.valueOf(lastLoc.latitude);
            pickupLong=String.valueOf(lastLoc.longitude);

            editor.putString("pickup_lat",pickupLat);
            editor.putString("pickup_long",pickupLong);
            editor.commit();

            sendLocationUpdatesToServer();
            // storeCoordinatesIntoDB();

            start=false;
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public static String getCurrentTime() {
        //date output format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public void getFirstHalfDistance(final String waypoints, String coordinate)
    {

        distance=0;
//        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
//                "origin=" + pickupLat + "," + pickupLong + "&destination=" + coordinate + "&waypoints=" + waypoints + "&key=AIzaSyC2yrJneuttgbzN-l2zD_EDaKLfFfq5c5g";

        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + "17.7399916,83.2456419" + "&destination=" + coordinate + "&waypoints=" + waypoints + "&key=AIzaSyC2yrJneuttgbzN-l2zD_EDaKLfFfq5c5g";


        System.out.println(urlString);

        Call<DistancePojo> call1 = REST_CLIENT.getOSDistanceDetails(urlString);
        call1.enqueue(new Callback<DistancePojo>() {
            @Override
            public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                DistancePojo distData;
                Route rData;
                Leg lData;

                if (response.isSuccessful()) {
                    distData = response.body();

                    List<Route> rDataList = distData.getRoutes();

                    if (rDataList != null) {

                        for (int i = 0; i < rDataList.size(); i++) {
                            rData = rDataList.get(i);

                            List<Leg> lDataList = rData.getLegs();

                            for (int j = 0; j < lDataList.size(); j++) {
                                lData = lDataList.get(j);

                                Distance d = lData.getDistance();


                                distance = distance + d.getValue();

                                System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                            }

                            getNextHalfDistance(dbAdapter.getNextHalfWaypoints(requestId),dbAdapter.getMidCoordinate(requestId));


                            System.out.println("distance is "+distance);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DistancePojo> call, Throwable t) {

                Toast.makeText(OutStationRideStartActivity.this, "Connectivity Error..Please Try Again!", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void  getNextHalfDistance(String waypoints,String coordinate)
    {

//        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
//                "origin=" + coordinate + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + waypoints + "&key=AIzaSyC2yrJneuttgbzN-l2zD_EDaKLfFfq5c5g";
//

        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + coordinate + "&destination=" + "17.4665839,82.7202608" + "&waypoints=" + waypoints + "&key=AIzaSyC2yrJneuttgbzN-l2zD_EDaKLfFfq5c5g";

        System.out.println(urlString);

        Call<DistancePojo> call1 = REST_CLIENT.getOSDistanceDetails(urlString);
        call1.enqueue(new Callback<DistancePojo>() {
            @Override
            public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                DistancePojo distData;
                Route rData;
                Leg lData;

                if (response.isSuccessful()) {
                    distData = response.body();

                    List<Route> rDataList = distData.getRoutes();

                    if (rDataList != null) {

                        for (int i = 0; i < rDataList.size(); i++) {
                            rData = rDataList.get(i);

                            List<Leg> lDataList = rData.getLegs();

                            for (int j = 0; j < lDataList.size(); j++) {
                                lData = lDataList.get(j);

                                Distance d = lData.getDistance();


                                distance = distance + d.getValue();

                                System.out.println("distANCE and value is " + d.getValue() + ":::" + distance);
                            }

                            sendFinishDetailsToServerOld();

                            System.out.println("distance is "+distance);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DistancePojo> call, Throwable t) {

                Toast.makeText(OutStationRideStartActivity.this, "Connectivity Error..Please Try Again!", Toast.LENGTH_LONG).show();

            }
        });



    }


    public void sendFinishDetailsToServerOld()
    {
        finalDistance=distance / 1000;

        System.out.println("distanceeee is " + finalDistance + ":::" + distance);
        System.out.println("ride data issss "+rideData);

        ////
        JsonObject v = new JsonObject();
        v.addProperty("profileid", stProfileId);
        v.addProperty("requestid", pref.getString("request_id", requestId));
        v.addProperty("distancetravelled", finalDistance);
        v.addProperty("movingtime", movingTimeFormat);
        v.addProperty("idletime", 1);
        v.addProperty("ridedata", rideData);
        v.addProperty("ridestarttime", rideStartingTime);
        v.addProperty("ridestoptime", rideStoppingTime);
        v.addProperty("companyid", pref.getString("company_id", companyId));
        v.addProperty("billing","-");

        Call<Pojo> call2 = REST_CLIENT.sendRideDetails(v);
        call2.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                Pojo msg;

                if (response.isSuccessful()) {
                    dbAdapter.deleteLocUpdates(requestId);
                    dbAdapter.deleteRideDetails(requestId);
                    //progressDialog.dismiss();
                    msg = response.body();

                    if (myBottomSheet.isAdded()) {
                        myBottomSheet.dismiss();
                    }

                    h.removeCallbacks(r);
                    //hC.removeCallbacks(rC);
                    stopLocationUpdates();
                    mGoogleApiClient.disconnect();

                    Intent i = new Intent(OutStationRideStartActivity.this, RideFinishActivity.class);
                    i.putExtra("distance", finalDistance);
                    i.putExtra("time", movingTimeFormat);
                    i.putExtra("fare", msg.getMessage());
                    i.putExtra("cabData", cabData);
                    i.putExtra("rideStart", rideStartingTime);
                    i.putExtra("rideStop", rideStoppingTime);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                //progressDialog.dismiss();

                if (myBottomSheet.isAdded()) {
                    //return;
                } else {
                    myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                }
            }
        });

    }


    public void sendFinishDetailsToServer()
    {
        if (current_lat != 0.0 && current_long != 0.0) {

            Date date = new Date();
            timeUpdated = dateFormat.format(date);

            //edited
            dbAdapter.insertEntry(requestId, current_lat, current_long, complete_address, resDist, timeUpdated);

            dropLat = String.valueOf(current_lat);
            dropLong = String.valueOf(current_long);

            final ProgressDialog progressDialog = new ProgressDialog(OutStationRideStartActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait..!");
            progressDialog.show();

            int Hours = (int) (pauseDiff / (1000 * 60 * 60));
            int Mins = (int) (pauseDiff / (1000 * 60)) % 60;
            long Secs = (int) (pauseDiff / 1000) % 60;

            DecimalFormat formatter = new DecimalFormat("00");
            String hFormatted = formatter.format(Hours);
            String mFormatted = formatter.format(Mins);
            String sFormatted = formatter.format(Secs);
            final String date3 = hFormatted + ":" + mFormatted + ":" + sFormatted;

            dbAdapter.deleteRideStatus(requestId);

            //h.removeCallbacks(r);
            //alertDialog.dismiss();

            // rideStoppingTime=java.text.DateFormat.getTimeInstance().format(new Date());
            rideStoppingTime = getCurrentTime();

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                //System.out.println(pref.getString("ride_starting_time", rideStartingTime) + "::" + rideStartingTime);
                rideStartingTime = pref.getString("ride_starting_time", rideStartingTime);
                Date date1 = timeFormat.parse(rideStartingTime);
                Date date2 = timeFormat.parse(rideStoppingTime);
                diff = (date2.getTime() - date1.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // movingTimeFormat= timeFormat.format(new Date(diff));

            Hours = (int) (diff / (1000 * 60 * 60));
            Mins = (int) (diff / (1000 * 60)) % 60;
            Secs = (int) (diff / 1000) % 60;


            hFormatted = formatter.format(Hours);
            mFormatted = formatter.format(Mins);
            sFormatted = formatter.format(Secs);
            //movingTimeFormat = hFormatted + "." + mFormatted;
            String totTime = hFormatted + ":" + mFormatted + ":" + sFormatted;
            String mov_time = "";

            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date1 = (Date) format.parse(date3);
                Date date2 = (Date) format.parse(totTime);
                //time difference in milliseconds
                long timeDiff = date2.getTime() - date1.getTime();
                //new date object with time difference
                Hours = (int) (timeDiff / (1000 * 60 * 60));
                Mins = (int) (timeDiff / (1000 * 60)) % 60;
                Secs = (int) (timeDiff / 1000) % 60;


                String hFormatted1 = formatter.format(Hours);
                String mFormatted1 = formatter.format(Mins);
                String sFormatted1 = formatter.format(Secs);
                mov_time = hFormatted1 + ":" + mFormatted1 + ":" + sFormatted1;
                movingTimeFormat = hFormatted1 + "." + mFormatted1;

                // mov_time=hFormatted+"."+mFormatted;
                //tvTimeRoads.setText(mov_time);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            rideData = dbAdapter.getRideDetails(requestId);
            double distanceDB = dbAdapter.getDistance(requestId);
            distanceDB = distanceDB / 1000;
            final double dist = resDist / 1000;

            pickupLat = pref.getString("pickup_lat", null);
            pickupLong = pref.getString("pickup_long", null);

            String stWaypoints = dbAdapter.getWaypointsForOutstation(requestId);

            String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + pickupLat + "," + pickupLong + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + stWaypoints + "&key=AIzaSyC2yrJneuttgbzN-l2zD_EDaKLfFfq5c5g";

            rideData=rideData+"*"+urlString;

            System.out.println("ridedata isssssss "+rideData);

            Call<DistancePojo> call1 = REST_CLIENT.getOSDistanceDetails(urlString);
            call1.enqueue(new Callback<DistancePojo>() {
                @Override
                public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                    DistancePojo distData;
                    Route rData;
                    Leg lData;

                    if (response.isSuccessful()) {
                        distData = response.body();

                        List<Route> rDataList = distData.getRoutes();

                        if (rDataList != null) {

                            for (int i = 0; i < rDataList.size(); i++) {
                                rData = rDataList.get(i);

                                List<Leg> lDataList = rData.getLegs();

                                for (int j = 0; j < lDataList.size(); j++) {
                                    lData = lDataList.get(j);

                                    Distance d = lData.getDistance();


                                    distance = distance + d.getValue();

                                }

                            }

                            progressDialog.dismiss();
                            //getFirstHalfDistance(dbAdapter.getFirstHalfWaypoints(requestId),dbAdapter.getMidCoordinate(requestId));


                                /*float d1=getFirstHalfDistance(dbAdapter.getFirstHalfWaypoints(requestId),dbAdapter.getMidCoordinate(requestId));
                                float d2=getNextHalfDistance(dbAdapter.getNextHalfWaypoints(requestId),dbAdapter.getMidCoordinate(requestId));
                                finalDistance=d1+d2;*/

                            distance = distance / 1000;
                            finalDistance = distance;

                            //finalDistance=finalDistance / 1000;

                            JsonObject v = new JsonObject();
                            v.addProperty("profileid", stProfileId);
                            v.addProperty("requestid", pref.getString("request_id", requestId));
                            v.addProperty("distancetravelled", finalDistance);
                            v.addProperty("movingtime", movingTimeFormat);
                            v.addProperty("idletime", 1);
                            v.addProperty("ridedata", rideData);
                            v.addProperty("ridestarttime", rideStartingTime);
                            v.addProperty("ridestoptime", rideStoppingTime);
                            v.addProperty("companyid", pref.getString("company_id", companyId));
                            v.addProperty("billing",billing);
                            //System.out.println("*****************!!!*********************");
                            //System.out.println(stProfileId);
                            //System.out.println(data.getgRequestId());
                            //System.out.println(finalDistance + ":::" + resDist);
                            // System.out.println(movingTimeFormat);
                            //System.out.println(rideData);
                            //System.out.println("******************!!!**********************");

                            Call<Pojo> call2 = REST_CLIENT.sendRideDetails(v);
                            call2.enqueue(new Callback<Pojo>() {
                                @Override
                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                    Pojo msg;

                                    if (response.isSuccessful()) {
                                        dbAdapter.deleteLocUpdates(requestId);
                                        dbAdapter.deleteRideDetails(requestId);
                                        progressDialog.dismiss();
                                        msg = response.body();

                                        if (myBottomSheet.isAdded()) {
                                            myBottomSheet.dismiss();
                                        }

                                        h.removeCallbacks(r);
                                        hC.removeCallbacks(rC);
                                        stopLocationUpdates();
                                        mGoogleApiClient.disconnect();

                                        Intent i = new Intent(OutStationRideStartActivity.this, RideFinishActivity.class);
                                        i.putExtra("distance", finalDistance);
                                        i.putExtra("time", movingTimeFormat);
                                        i.putExtra("fare", msg.getMessage());
                                        i.putExtra("cabData", cabData);
                                        i.putExtra("rideStart", rideStartingTime);
                                        i.putExtra("rideStop", rideStoppingTime);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Pojo> call, Throwable t) {

                                    progressDialog.dismiss();

                                    if (myBottomSheet.isAdded()) {
                                        //return;
                                    } else {
                                        myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                                    }
                                }
                            });

                            // Distance calculation logic 'else' part
                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(OutStationRideStartActivity.this, distData.getStatus(), Toast.LENGTH_LONG).show();
                        }
                    }


                    else {

                        progressDialog.dismiss();
                        Toast.makeText(OutStationRideStartActivity.this, response.message() + "&" + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DistancePojo> call, Throwable t) {

                    progressDialog.dismiss();

                    Toast.makeText(OutStationRideStartActivity.this, "Connectivity Error..Please Try Again!", Toast.LENGTH_LONG).show();

                }
            });
        } else {

            Toast.makeText(OutStationRideStartActivity.this, "No Internet Connection..Try again!", Toast.LENGTH_SHORT).show();
        }

    }
}
