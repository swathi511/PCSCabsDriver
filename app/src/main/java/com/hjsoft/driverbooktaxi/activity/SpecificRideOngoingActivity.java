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
import com.hjsoft.driverbooktaxi.KalmanLatLong;
import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.Distance;
import com.hjsoft.driverbooktaxi.model.DistancePojo;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.Leg;
import com.hjsoft.driverbooktaxi.model.LocationUpdates;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.model.RideStopPojo;
import com.hjsoft.driverbooktaxi.model.Route;
import com.hjsoft.driverbooktaxi.service.RideOngoingOverlayService;
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
 * Created by hjsoft on 2/2/17.
 */
public class SpecificRideOngoingActivity extends AppCompatActivity implements OnMapReadyCallback,
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
    long diff=0;
    String movingTimeFormat;
    ArrayList<FormattedAllRidesData> cabData;
    FormattedAllRidesData data;
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
    ImageButton btGetDirections,ibPhone;
    boolean gettingDirections=false;
    BottomSheetDialogFragment myBottomSheet;
    String requestId;
    ArrayList<LocationUpdates> dataList;
    LocationUpdates locationData;
    LatLng startPosition,finalPosition,currentPosition;
    double cpLat,cpLng;
    boolean isMarkerRotating=false;
    String companyId="CMP00001";
    TextView tvNewBooking,tvDateTime,tvRid;
    String pickupLat,pickupLong,dropLat,dropLong;
    float finalDistance,distance=0;
    String rideData=" ";
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    SharedPreferences pref;
    long idleTime=0;
    String billing="-";
    public static final int ACCURACY_DECAYS_TIME = 10;
    private KalmanLatLong kalmanLatLong;
    boolean first=true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_ride_ongoing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        kalmanLatLong = new KalmanLatLong(ACCURACY_DECAYS_TIME);

        btDrop=(Button)findViewById(R.id.asro_bt_drop);
        tvCurrentLoc=(TextView)findViewById(R.id.asro_tv_cloc);
        tvGname=(TextView)findViewById(R.id.asro_tv_gname);
        tvGmobile=(TextView)findViewById(R.id.asro_tv_gmobile);
        tvGpickup=(TextView)findViewById(R.id.asro_tv_ploc);
        tvGdrop=(TextView)findViewById(R.id.asro_tv_dloc);
        ibClose=(ImageButton)findViewById(R.id.asro_ib_close);
        vwBottomSheet=(RelativeLayout)findViewById(R.id.asro_rl_bottom_sheet);
        ibPhone=(ImageButton)findViewById(R.id.asro_ib_call);
        ibDots=(ImageButton)findViewById(R.id.asro_ib_dots);
        btGetDirections=(ImageButton)findViewById(R.id.asro_bt_get_directions);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvDateTime=(TextView)findViewById(R.id.asro_tv_date_time);
        tvRid=(TextView)findViewById(R.id.asro_tv_creq_title);
        tvNewBooking=(TextView)findViewById(R.id.asro_tv_new_booking);
        tvNewBooking.setVisibility(View.GONE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        city=pref.getString("city",null);

        //rideStartingTime=java.text.DateFormat.getTimeInstance().format(new Date());

        cabData= (ArrayList<FormattedAllRidesData>) getIntent().getSerializableExtra("list");
        Bundle b=getIntent().getExtras();
        int pos=b.getInt("position");
        data=cabData.get(pos);

        requestId=data.getRequestId();
        SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        tvDateTime.setText(format.format(data.getRideDate()));

        dataList=dbAdapter.getAllLocUpdates(requestId);

        locationData=dataList.get(0);

        latitude=locationData.getLatitude();
        longitude=locationData.getLongitude();
        rideStartingTime=locationData.getStartingTime();
        rideCurrentTime=locationData.getStoppingTime();
        resDist=Long.parseLong(locationData.getDistance());

        lastLocDist=new LatLng(latitude,longitude);

        tvGname.setText(locationData.getGuestName());
        tvGmobile.setText(locationData.getGuestMobile());
        tvGpickup.setText(data.getFromLocation());
        tvGdrop.setText(data.getToLocation());

        REST_CLIENT=RestClient.get();

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        stProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mRequestingLocationUpdates=false;

        if(data.getTravelPackage().equals(""))
        {

        }
        else {

            tvRid.setText("Ride Details - "+data.getTravelPackage());
        }

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
                    Toast.makeText(SpecificRideOngoingActivity.this,"Location permission is required for this app to run!",Toast.LENGTH_LONG).show();
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
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);

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
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SpecificRideOngoingActivity.this);

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
                        }

                    }

                    @Override
                    public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

                        Toast.makeText(SpecificRideOngoingActivity.this,"Connectivity Error!",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });


        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+data.getGuestMobile()));//GUEST NUMBER HERE...
                startActivity(intent);
            }
        });

        btGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (!(String.valueOf(locationData.getdLat()).equals("-")) && (!(String.valueOf(locationData.getdLng()).equals("-")))) {

                if(locationData.getdLat()!=0.0&&locationData.getdLng()!=0.0)
                {
                    if (Build.VERSION.SDK_INT >= 23) {

                        if (isSystemAlertPermissionGranted(SpecificRideOngoingActivity.this)) {

                            // Toast.makeText(RideStartActivity.this, "permission granted", Toast.LENGTH_LONG).show();
                            stopService(new Intent(getApplicationContext(), RideOngoingOverlayService.class));
                            startService(new Intent(getApplicationContext(), RideOngoingOverlayService.class));
                            // startService(new Intent(getApplicationContext(), HUD.class));

                            gettingDirections = true;

                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + locationData.getdLat() + "," + locationData.getdLng());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }

                        } else {
                            requestSystemAlertPermission(SpecificRideOngoingActivity.this, 1);
                        }
                    } else {

                        stopService(new Intent(getApplicationContext(), RideOngoingOverlayService.class));
                        startService(new Intent(getApplicationContext(), RideOngoingOverlayService.class));
                        // startService(new Intent(getApplicationContext(), HUD.class));

                        gettingDirections = true;

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + locationData.getdLat() + "," + locationData.getdLng());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                }
                else
                {
                    btGetDirections.setEnabled(false);
                    btGetDirections.setClickable(false);
                    Toast.makeText(SpecificRideOngoingActivity.this,"Drop Location not known!",Toast.LENGTH_SHORT).show();
                }
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

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SpecificRideOngoingActivity.this);

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

                        //System.out.println("booking value issssssss "+bookingValue);

                        if(bookingValue)
                        {
                            Toast.makeText(SpecificRideOngoingActivity.this,"Booking already finished!",Toast.LENGTH_LONG).show();
                            dbAdapter.deleteLocUpdates(requestId);
                            dbAdapter.deleteRideDetails(requestId);
                            Intent i=new Intent(SpecificRideOngoingActivity.this,HomeActivity.class);
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
                Toast.makeText(SpecificRideOngoingActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();
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
    }

    public void sendLocationUpdatesToServer()
    {
        h=new Handler();
        r=new Runnable() {
            @Override
            public void run() {

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

                if(lastLocDist!=null&&current_lat!=0.0&&current_long!=0.0&&lastLocDist.latitude!=0.0 && lastLocDist.longitude!=0.0) {

                    curntloc=new LatLng(current_lat,current_long);

                    startPosition = cab.getPosition();
                    finalPosition = new LatLng(current_lat, current_long);

                    double toRotation=bearingBetweenLocations(startPosition,finalPosition);
                    rotateMarker(cab,(float)toRotation);

                    accelerateDecelerate();
//
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

                //System.out.println("*******"+stProfileId+"**"+city+"**"+c_lat+"**"+c_long+"******");

                Call<Pojo> call=REST_CLIENT.sendStatus(v);
                call.enqueue(new Callback<Pojo>() {


                    @Override
                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                        if(response.isSuccessful())
                        {
                            //System.out.println("-----------------------------** updated **-----------------------------------");

                            String[] newbooking=response.body().getMessage().split("-");

                            if(newbooking.length==1)
                            {

                            }
                            else
                            {
                                tvNewBooking.setVisibility(View.VISIBLE);
                            }

                        }
                        else
                        {
                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                        Toast.makeText(SpecificRideOngoingActivity.this,"Error in network connection",Toast.LENGTH_LONG).show();
                    }
                });

                lastLocDist = new LatLng(current_lat, current_long);

            }
        };
        h.post(r);
    }

    public void storeCoordinatesIntoDB()
    {
        hC=new Handler();
        rC=new Runnable() {
            @Override
            public void run() {

                hC.postDelayed(rC,300000);// 5 min ~ 300sec

                rideCurrentTime=java.text.DateFormat.getTimeInstance().format(new Date());

                Date date = new Date();
                timeUpdated=dateFormat.format(date);

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude!=0.0 && lastLocDist.longitude!=0.0) {

                    dbAdapter.insertEntry(requestId,current_lat, current_long, complete_address, resDist, timeUpdated);

                } else {

                    //dbAdapter.insertEntry(0.0, 0.0, complete_address, resDist, timeUpdated);

                }


                if (dbAdapter.findRequestId(requestId)) {

                    ArrayList<LocationUpdates> list = dbAdapter.getAllLocUpdates(requestId);
                    LocationUpdates l;

                   /* for (int i = 0; i < list.size(); i++) {
                        l = list.get(0);
                        System.out.println(l.getRequestId() + "::" + l.getStartingTime() + "::" + l.getStoppingTime() + "::" + l.getDistance() + "::" + l.getLatitude() + "::" + l.getLongitude());
                    }*/
                    dbAdapter.updateLocEntry(requestId, rideCurrentTime, resDist, current_lat, current_long,idleTime);
                } else {


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
                    // mGoogleApiClient.connect();
                }
            }
        }
        else
        {
            if(gettingDirections) {

            }
            else
            {
                //mGoogleApiClient.connect();
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
                // mGoogleApiClient.disconnect();
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
                    // stopLocationUpdates();
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
                            status.startResolutionForResult(SpecificRideOngoingActivity.this, REQUEST_LOCATION);
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
        mLocationRequest.setInterval(1000);//3 sec
        mLocationRequest.setFastestInterval(1000);//2 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        //System.out.println("Location Updates Started..");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,SpecificRideOngoingActivity.this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, SpecificRideOngoingActivity.this);

        if (mGoogleApiClient != null) {
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

                    if(location.getLatitude()!=0.0&&location.getLongitude()!=0.0)
                    {
                        current_lat = location.getLatitude();
                        current_long = location.getLongitude();

                        /*kalmanLatLong.Process(
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAccuracy(),
                                location.getTime());

                        if(kalmanLatLong.get_accuracy()<=10) {

                            current_lat = kalmanLatLong.get_lat();
                            current_long = kalmanLatLong.get_lng();
                        }*/
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
        current_lat=lastLoc.latitude;
        current_long=lastLoc.longitude;
        //lastLocDist=lastLoc;

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        if(gPickup!=null)
        {

        }
        else
        {
            LatLng pickLatLng=new LatLng(locationData.getpLat(),locationData.getpLng());
            gPickup= mMap.addMarker(new MarkerOptions().position(pickLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
        }

        if(gDrop!=null)
        {

        }
        else
        {
            LatLng dropLatLng=new LatLng(locationData.getdLat(),locationData.getdLng());
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

    public static String getCurrentTime() {
        //date output format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        stopService(new Intent(getApplicationContext(), RideOngoingOverlayService.class));

        if(h!=null)
        {
            h.removeCallbacks(r);
        }

        if(hC!=null)
        {
            hC.removeCallbacks(rC);
        }
    }

    public void sendFinishDetailsToServer()
    {
        if (current_lat != 0.0 && current_long != 0.0) {

            final Date date = new Date();
            timeUpdated = dateFormat.format(date);

            dbAdapter.insertEntry(requestId, current_lat, current_long, complete_address, resDist, timeUpdated);

            dropLat = String.valueOf(current_lat);
            dropLong = String.valueOf(current_long);

            dbAdapter.deleteRideStatus(requestId);

            h.removeCallbacks(r);
            hC.removeCallbacks(rC);
            stopLocationUpdates();
            //mGoogleApiClient.disconnect();
            //alertDialog.dismiss();

            // rideStoppingTime=java.text.DateFormat.getTimeInstance().format(new Date());
            rideStoppingTime = getCurrentTime();

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date1 = timeFormat.parse(rideStartingTime);
                Date date2 = timeFormat.parse(rideStoppingTime);
                diff = (date2.getTime() - date1.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // movingTimeFormat= timeFormat.format(new Date(diff));

            int Hours = (int) (diff / (1000 * 60 * 60));
            int Mins = (int) (diff / (1000 * 60)) % 60;
            long Secs = (int) (diff / 1000) % 60;

            DecimalFormat formatter = new DecimalFormat("00");
            String hFormatted = formatter.format(Hours);
            String mFormatted = formatter.format(Mins);
            String sFormatted = formatter.format(Secs);
            movingTimeFormat = hFormatted + "." + mFormatted;

            final ProgressDialog progressDialog = new ProgressDialog(SpecificRideOngoingActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait..!");
            progressDialog.show();


            rideData = dbAdapter.getRideDetails(requestId);

            final double dist = resDist / 1000;

            pickupLat = pref.getString("pickup_lat", null);
            pickupLong = pref.getString("pickup_long", null);

            String stWaypoints = dbAdapter.getWaypointsForOutstation(requestId);

            String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + pickupLat + "," + pickupLong + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + stWaypoints + "&key=AIzaSyC2yrJneuttgbzN-l2zD_EDaKLfFfq5c5g";

            rideData=rideData+"*"+urlString;

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

                            distance = distance / 1000;
                            finalDistance = distance;

                            JsonObject v = new JsonObject();
                            v.addProperty("profileid", stProfileId);
                            v.addProperty("requestid", data.getRequestId());
                            v.addProperty("distancetravelled", finalDistance);
                            v.addProperty("movingtime", movingTimeFormat);
                            v.addProperty("idletime", 1);
                            v.addProperty("ridedata", rideData);
                            v.addProperty("ridestarttime", rideStartingTime);
                            v.addProperty("ridestoptime", rideStoppingTime);
                            v.addProperty("companyid", companyId);
                            v.addProperty("billing",billing);

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

                                        ArrayList<GuestData> gd = new ArrayList<GuestData>();
                                        gd.add(new GuestData(requestId, stProfileId, "-", "-", String.valueOf(locationData.getpLat()), String.valueOf(locationData.getpLng()),
                                                String.valueOf(locationData.getdLat()), String.valueOf(locationData.getdLng()), data.getFromLocation(), data.getToLocation(),
                                                "travel_type", "travel_package", "scheduled_date", "scheduled_time", "otp_required", "booking_type",data.getPaymentMode(),data.getOtherCharges(),"","",""));

                                        Intent i = new Intent(SpecificRideOngoingActivity.this, RideFinishActivity.class);
                                        i.putExtra("distance", finalDistance);
                                        i.putExtra("time", movingTimeFormat);
                                        i.putExtra("fare", msg.getMessage());
                                        i.putExtra("cabData", gd);
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

                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(SpecificRideOngoingActivity.this, distData.getStatus(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SpecificRideOngoingActivity.this, response.message() + "&" + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DistancePojo> call, Throwable t) {

                    progressDialog.dismiss();

                    Toast.makeText(SpecificRideOngoingActivity.this, "Connectivity Error..Please Try Again!", Toast.LENGTH_LONG).show();

                }
            });

        } else {

            Toast.makeText(SpecificRideOngoingActivity.this, "No Internet Connection..Try again!", Toast.LENGTH_SHORT).show();
        }
    }
}

