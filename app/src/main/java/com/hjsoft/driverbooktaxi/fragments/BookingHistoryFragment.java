package com.hjsoft.driverbooktaxi.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.Constants;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;

import com.hjsoft.driverbooktaxi.activity.BookingHistoryActivity;
import com.hjsoft.driverbooktaxi.activity.OutStationRideStartActivity;
import com.hjsoft.driverbooktaxi.activity.OutStationTrackRideActivity;
import com.hjsoft.driverbooktaxi.activity.RideStartActivity;
import com.hjsoft.driverbooktaxi.activity.TrackRideActivity;
import com.hjsoft.driverbooktaxi.adapter.BookingHistoryRecyclerAdapter;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;

import com.hjsoft.driverbooktaxi.model.BookingHistoryPojo;
import com.hjsoft.driverbooktaxi.model.BookingHistoryData;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.service.CabRequestService;
import com.hjsoft.driverbooktaxi.service.CabRequestServiceNew;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;
import com.inrista.loggliest.Loggly;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 17/2/18.
 */
public class BookingHistoryFragment extends Fragment {

    //View rootView;
    RecyclerView rView;
    BookingHistoryRecyclerAdapter mAdapter;
    DBAdapter dbAdapter;
    TextView tvNoRides;
    ArrayList<BookingHistoryPojo> allRidesDataList;
    BookingHistoryPojo allRidesData;
    API REST_CLIENT;
    //BottomSheetDialogFragment myBottomSheet;
    ArrayList<BookingHistoryData> dataList=new ArrayList<>();
    Date date1;
    String driverProfileId;
    HashMap<String, String> user;
    SessionManager session;
    ImageView ivRetry;
    String companyId="CMP00001";
    //TextView tvSearch;
    int day,mnth,yr;
    Button btOk;
    //TextView tvClear;
    String stDate;
    LayoutInflater inflater;
    DatePicker dp;
    ArrayList<BookingHistoryData> newDataList=new ArrayList<>();
    BookingHistoryData data;

    TextView tvFromDate,tvToDate,tvOk;
    ImageView ivFrom,ivTo;
    String fromdate,todate;
    DatePickerDialog datePickerDialog;
    String stProfileId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    BookingHistoryActivity a;
    boolean accepted=false;

    Handler handler,h,hLoc,e;
    Runnable r,rr,rLoc,dr;
    ArrayList<GuestData>  cabData=new ArrayList<>();
    MediaPlayer mp;
    CabRequestsPojo cdata;
    List<CabRequestsPojo> cdataList;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    View dialogView;
    View rootView;
    int count=0;
    int k=15;
    //private final static String API_KEY = "3PzQvg.MchECw:Brb2D4FEUuEXMuKs";
    private final static String API_KEY = "kcfhRA.H13JVA:pX7G9-lrgVftOHBZ";
    PubNub pubnub;
    boolean debugLogs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_booking_history, container, false);
        rView = (RecyclerView) rootView.findViewById(R.id.fbh_rview);

        tvNoRides = (TextView) rootView.findViewById(R.id.fbh_tv_no_rides);
        //myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        ivRetry=(ImageView)rootView.findViewById(R.id.fbh_iv_retry);
        //tvSearch=(TextView)rootView.findViewById(R.id.fbh_tv_search);
        //llDate=(LinearLayout)rootView.findViewById(R.id.fbh_ll);
        //tvDate=(TextView)rootView.findViewById(R.id.fbh_tv_date);
        //tvClear=(TextView)rootView.findViewById(R.id.fbh_tv_clear);
        //ivDate=(ImageView)rootView.findViewById(R.id.fbh_iv_date);

        tvFromDate=(TextView)rootView.findViewById(R.id.fbh_tv_from);
        tvToDate=(TextView)rootView.findViewById(R.id.fbh_tv_to);
        ivFrom=(ImageView)rootView.findViewById(R.id.fbh_iv_from);
        ivTo=(ImageView)rootView.findViewById(R.id.fbh_iv_to);
        tvOk=(TextView)rootView.findViewById(R.id.fbh_tv_ok);

        tvNoRides.setVisibility(View.GONE);
        ivRetry.setVisibility(View.GONE);
        //llDate.setVisibility(View.GONE);
        //tvClear.setVisibility(View.GONE);

        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        fromdate=mYear+"-"+(mMonth+1)+"-"+mDay;
        todate=mYear+"-"+(mMonth+1)+"-"+mDay;
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        tvFromDate.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
        tvToDate.setText(mDay+"/"+(mMonth+1)+"/"+mYear);

        session=new SessionManager(getActivity());

        user=session.getUserDetails();

        driverProfileId=user.get(SessionManager.KEY_PROFILE_ID);
        stProfileId=driverProfileId;

        //mAdapter.notifyDataSetChanged();
        //  if (rideData.size() == 0) {
        //  tvNoRides.setVisibility(View.VISIBLE);
        //  }

        //Call<ArrayList<BookingHistoryPojo>> call=REST_CLIENT.getAllUserRides(guestProfileId);



        ivFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tvFromDate.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                                fromdate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }


        });

        ivTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tvToDate.setText(dayOfMonth + "/"
                                        +(monthOfYear + 1)+ "/" + year);

                                todate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAllRides();
            }
        });

        ivRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivRetry.setVisibility(View.GONE);
                getAllRides();
            }
        });

        dialogBuilder = new AlertDialog.Builder(getActivity());

        getAllRides();
        //getDetails();
        initPubNub(stProfileId);

        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        REST_CLIENT= RestClient.get();
        dbAdapter = new DBAdapter(getContext());
        dbAdapter = dbAdapter.open();
        //rideData = dbAdapter.getAllRideEntries();

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        mp = MediaPlayer.create(getActivity(), R.raw.beep);

        onStartService();

        IntentFilter filter = new IntentFilter(CabRequestServiceNew.ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(testReceiver, filter);
        //notificationManager =(NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Metres per second
//        final String LOGGLY_TOKEN = "b505c85d-71ae-4ad6-803b-78b2f8893cb4";
//        Timber.plant(new LogglyTree(LOGGLY_TOKEN));
    }

    public void onStartService() {

        Intent i = new Intent(getActivity(), CabRequestServiceNew.class);
        //i.putExtra("foo", "bar");
        getActivity().startService(i);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            if (resultCode == Activity.RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                //Toast.makeText(getActivity(), resultValue, Toast.LENGTH_SHORT).show();

                if(cabData.size()==0)
                {
                    getDetails();
                }
                else {

                    if (resultValue.equals("Telebooking")) {

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //getDetails();

                            }
                        }, 15000);
                    }
                }
            }
        }
    };


    public void getAllRides()
    {
        tvNoRides.setVisibility(View.GONE);
        dataList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        Call<ArrayList<BookingHistoryPojo>> call=REST_CLIENT.getBookingCountDetails(driverProfileId,"driver",companyId,fromdate,todate);
        call.enqueue(new Callback<ArrayList<BookingHistoryPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<BookingHistoryPojo>> call, Response<ArrayList<BookingHistoryPojo>> response) {

                BookingHistoryPojo data;

                if(response.isSuccessful())
                {

                    allRidesDataList=response.body();

                    for(int i=0;i<allRidesDataList.size();i++)
                    {
                        data=allRidesDataList.get(i);
                        dataList.add(new BookingHistoryData(data.getBookingdate(),data.getCompleterides(),data.getCancelrides(),data.getTotalamount()));
                    }

                }

                if(dataList.size()!=0)
                {
                    progressDialog.dismiss();
                    mAdapter = new BookingHistoryRecyclerAdapter(getActivity(), dataList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rView.setLayoutManager(mLayoutManager);
                    rView.setItemAnimator(new DefaultItemAnimator());
                    rView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    progressDialog.dismiss();
                    tvNoRides.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookingHistoryPojo>> call, Throwable t) {

                ivRetry.setVisibility(View.VISIBLE);
                //tvSearch.setVisibility(View.GONE);

                Toast.makeText(getActivity(),"Please check Internet connection!",Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

            }
        });

    }

    public void getDetails(){

        handler = new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                //handler.postDelayed(r,10000);

                // Getting details

                if (cabData.size() != 0 && (cdata.getTravelType().equals("local")||cdata.getTravelType().equals("Packages")) && cdata.getBookingtype().equals("AppBooking") ) {

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
                    v.addProperty("requestid", cdata.getRequestId());
                    v.addProperty("status", "6");//3 No Response
                    v.addProperty("companyid", companyId);

                    Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

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

                        //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

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
                                    Intent myIntent = new Intent(a,BookingHistoryActivity.class);
                                    //myIntent.setComponent(rootActivity);
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    //myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    a.startActivity(myIntent);
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

                                    cdataList = response.body();

                                    // for (int i = 0; i < dataList.size(); i++) {
                                    cdata = cdataList.get(0);
                                    cabData.add(new GuestData(cdata.getRequestId(), cdata.getGuestProfileid(), cdata.getGuestName(), cdata.getGuestMobile(),
                                            cdata.getPickupLat(), cdata.getPickupLong(), cdata.getDropLat(), cdata.getDropLong(), cdata.getPickupLoc(),
                                            cdata.getDropLoc(), cdata.getTravelType(), cdata.getTravelPackage(),cdata.getScheduledDate(), cdata.getScheduledTime(), cdata.getOTPrequired(), cdata.getBookingtype(),cdata.getPaymentMode(),cdata.getOthercharges(),cdata.getPickupLat(),cdata.getPickupLong(),""));


                                    /*System.out.println("%%%%%%%%%%%%%%%%%%%%");
                                    System.out.println("Req Id"+data.getRequestId());
                                    System.out.println("Guest Profile Id "+data.getGuestProfileid());
                                    System.out.println("Guest Name "+data.getGuestName());
                                    System.out.println("Guest Mobile "+data.getGuestMobile());
                                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");*/

                                    inflater = a.getLayoutInflater();
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
                                    final TextView tvTime=(TextView)dialogView.findViewById(R.id.acr_tv_time);
                                    tvTime.setVisibility(View.GONE);

                                    //System.out.println("data bkng type "+data.getBookingtype());


                                    if(cdata.getBookingtype().equals("AppBooking")&&(cdata.getTravelType().equals("local")||cdata.getTravelType().equals("Packages"))) {

                                        k=15;

                                        tvTime.setVisibility(View.VISIBLE);

                                        e = new Handler();
                                        dr = new Runnable() {
                                            @Override
                                            public void run() {

                                                //System.out.println("??????????????????????");

                                                e.postDelayed(dr, 1000);

                                                tvTime.setText("Time remaining " + k + " sec");

                                                k--;

                                                if (k == 0) {
                                                    e.removeCallbacks(dr);
                                                    //tvTime.setVisibility(View.GONE);
                                                }
                                            }
                                        };
                                        e.post(dr);
                                    }

                                    if(cdata.getOthercharges()!=null) {

                                        if (cdata.getOthercharges().equals("0")) {

                                        } else {
                                            llOtherCharges.setVisibility(View.VISIBLE);
                                            tvOtherCharges.setText(getString(R.string.Rs) + " " + cdata.getOthercharges());
                                        }
                                    }


                                    tvPickup.setText(cdata.getPickupLoc());
                                    tvDrop.setText(cdata.getDropLoc());
                                    tvTravelType.setText(cdata.getTravelType());

                                    if(cdata.getTravelPackage().equals(""))
                                    {
                                        tvTravelPackage.setText("-");
                                    }
                                    else {
                                        tvTravelPackage.setText(cdata.getTravelPackage());
                                    }
                                    tvReportingTime.setText(cdata.getScheduledTime());

                                    //tvReportingDate.setText(data.getScheduledDate().split(" ")[0]);

                                    SimpleDateFormat  format1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
                                    SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a",Locale.ENGLISH);
                                    try {
                                        tvReportingDate.setText(format.format(format1.parse(cdata.getScheduledDate())).split(" ")[0]);
                                    }
                                    catch (ParseException e)
                                    {
                                        e.printStackTrace();
                                    }


                                    if (cdata.getBookingtype().equals("TeleBook")) {
                                        btStartDuty.setVisibility(View.VISIBLE);
                                        llLocal.setVisibility(View.GONE);
                                        llOutstation.setVisibility(View.VISIBLE);

                                        if (cdata.getTravelType().equals("local")) {

                                            // tvTravelPackage.setVisibility(View.GONE);

                                        } else {


                                        }
                                    } else {

                                        if (cdata.getTravelType().equals("local")||cdata.getTravelType().equals("Packages")) {

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
                                            v.addProperty("requestid", cdata.getRequestId());
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
                                                        if (cdata.getTravelType().equals("local")) {

                                                            if (cdata.getOTPrequired().equals("Yes")) {
                                                                if(h!=null) {
                                                                    h.removeCallbacks(rr);
                                                                }
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, TrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            } else if (cdata.getOTPrequired().equals("No")) {

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

                                                                if(h!=null) {
                                                                    h.removeCallbacks(rr);
                                                                }
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, RideStartActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();

                                                            } else {
                                                                if(h!=null) {
                                                                    h.removeCallbacks(rr);
                                                                }
                                                                if(hLoc!=null) {

                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, TrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            }
                                                        } else {

                                                            if (cdata.getOTPrequired().equals("Yes")) {
                                                                if(h!=null) {
                                                                    h.removeCallbacks(rr);
                                                                }
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, OutStationTrackRideActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();
                                                            } else if (cdata.getOTPrequired().equals("No")) {

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

                                                                if(h!=null) {
                                                                    h.removeCallbacks(rr);
                                                                }
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, OutStationRideStartActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();


                                                            } else {
                                                                if(h!=null) {
                                                                    h.removeCallbacks(rr);
                                                                }
                                                                if(hLoc!=null) {
                                                                    hLoc.removeCallbacks(rLoc);
                                                                }
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, OutStationTrackRideActivity.class);
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

                                            publish(cdata.getRequestId()+"accept",stProfileId);


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
                                            v.addProperty("requestid", cdata.getRequestId());
                                            v.addProperty("status", "1"); //accept
                                            v.addProperty("companyid", companyId);

                                            //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                            Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                                            call.enqueue(new Callback<Pojo>() {
                                                @Override
                                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {


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
                                                        if (cdata.getTravelType().equals("local")) {

                                                            Intent i = new Intent(a, TrackRideActivity.class);
                                                            i.putExtra("cabData", cabData);
                                                            startActivity(i);
                                                            getActivity().finish();
                                                        }
                                                        else {
                                                            Intent i = new Intent(a, OutStationTrackRideActivity.class);
                                                            i.putExtra("cabData", cabData);
                                                            startActivity(i);
                                                            getActivity().finish();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Pojo> call, Throwable t1) {

                                                    alertDialog.dismiss();

                                                    String trace = t1.toString() + "\n";

                                                    for (StackTraceElement e1 : t1.getStackTrace()) {
                                                        trace += "\t at " + e1.toString() + "\n";
                                                    }
                                                    Loggly.i("BookingHistoryFragment",stProfileId+"[API failed,AcceptBooking]"+trace);

                                                    btAccept.setEnabled(true);
                                                    btAccept.setClickable(true);
                                                }
                                            });
                                        }
                                    });

                                    btDecline.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            publish(cdata.getRequestId()+"decline",stProfileId);


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
                                            v.addProperty("requestid", cdata.getRequestId());
                                            v.addProperty("status", "5"); //2 Decline
                                            v.addProperty("companyid", companyId);

                                            //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                            Call<Pojo> call = REST_CLIENT.sendCabAcceptanceStatus(v);
                                            call.enqueue(new Callback<Pojo>() {
                                                @Override
                                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                                    if (response.isSuccessful()) {

                                                        //dataPresent=false;

                                                        h.removeCallbacks(rr);
                                                        handler.post(r);

                                                        if(e!=null)
                                                        {
                                                            e.removeCallbacks(dr);
                                                        }
                                                        alertDialog.dismiss();
                                                        count = 0;
                                                        cabData.clear();
                                                        //alertDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Pojo> call, Throwable t1) {

                                                    alertDialog.dismiss();

                                                    String trace = t1.toString() + "\n";

                                                    for (StackTraceElement e1 : t1.getStackTrace()) {
                                                        trace += "\t at " + e1.toString() + "\n";
                                                    }
                                                    Loggly.i("BookingHistoryFragment",stProfileId+"[API failed,DeclineBooking]"+trace);

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
                            public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t1) {

                                String trace = t1.toString() + "\n";

                                for (StackTraceElement e1 : t1.getStackTrace()) {
                                    trace += "\t at " + e1.toString() + "\n";
                                }
                                Loggly.i("BookingHistoryFragment",stProfileId+"[API failed,UserDetailsToCab/getUserDetails]"+trace);
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
                    h.postDelayed(rr,15000);
                    //20000
                }

            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BookingHistoryActivity){
            a=(BookingHistoryActivity) context;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(handler!=null)
        {
            handler.removeCallbacks(r);
            handler=null;
        }


        if (mp != null) {

            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }


        if(e!=null)
        {
            e.removeCallbacks(dr);
        }
        if(pubnub!=null)
        {

            pubnub.removeListener(subscribeCallback);

            pubnub.unsubscribe();
            pubnub.destroy();
        }


        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(testReceiver);

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        Intent i = new Intent(getActivity(), CabRequestServiceNew.class);
        //i.putExtra("foo", "bar");
        getActivity().stopService(i);

    }

   /* private void initAbly(String driverId) throws AblyException {

        System.out.println("ABLY IS INITIALISED!!!");
        System.out.println("driverId is "+driverId);


        AblyRealtime realtime = new AblyRealtime(API_KEY);

        channel = realtime.channels.get(driverId);
        //Toast.makeText(getBaseContext(), "Message received: " + messages.data, Toast.LENGTH_SHORT).show();

        channel.subscribe(new Channel.MessageListener() {

            @Override
            public void onMessage(final Message messages) {

                System.out.println("****** msg received!!!"+messages.data.toString()+"!!!");

                if(messages.data.toString().equals("cab request"))
                {
                    //getDetails();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), "New Booking Request", Toast.LENGTH_SHORT).show();

                            System.out.println("cab size isssssssss "+cabData.size());

                            if(cabData.size()==0) {
                                getDetails();
                            }
                            else {


                            }
                        }
                    });
                }



                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getActivity(), "Msg "+messages.data, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
    }


    public void publishMessage(String msg) throws AblyException{

        channel.publish("update", msg, new CompletionListener() {
            @Override
            public void onSuccess() {

                System.out.println("***************** success");

                //Toast.makeText(getBaseContext(), "Message sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorInfo reason) {

                System.out.println("********************** error");

                // Toast.makeText(getBaseContext(), "Message not sent", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private final void initPubNub(String driverId) {

        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        config.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        // config.setUuid(this.mUsername);
        config.setSecure(true);

        pubnub=new PubNub(config);

        pubnub.addListener(subscribeCallback);

        pubnub.subscribe()
                .channels(Arrays.asList(driverId)) // subscribe to channels
                .execute();

        if(debugLogs)
        {
            Loggly.i("BookingHistoryFragment",stProfileId+" [Pubnub initialised]");
        }

    }

    public void publish(final String msg,String profileId)
    {

        pubnub.publish()
                .message(msg)
                .channel(profileId)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // handle publish result, status always present, result if successful
                        // status.isError() to see if error happened
                        if(!status.isError()) {
                            //System.out.println("pub timetoken: " + result.getTimetoken());
                            if(debugLogs)
                            {
                                Loggly.i("BookingHistoryFragment",stProfileId+" "+msg+" [published]");
                            }

                        }
                        else {
                            Loggly.i("BookingHistoryFragment",stProfileId+" "+msg+" [error,published] "+status.isError());
                        }

                        //System.out.println("pub status code: " + status.getStatusCode());
                    }
                });
    }

    SubscribeCallback subscribeCallback = new SubscribeCallback() {
        @Override
        public void status(PubNub pubnub, PNStatus status) {

           /* switch (status.getOperation()) {
                // let's combine unsubscribe and subscribe handling for ease of use
                case PNSubscribeOperation:
                case PNUnsubscribeOperation:
                    // note: subscribe statuses never have traditional
                    // errors, they just have categories to represent the
                    // different issues or successes that occur as part of subscribe

                    //Loggly.i("BookingHistoryFragment",stProfileId+" subscribe status "+status.getCategory());*/


                    switch (status.getCategory()) {
                        case PNConnectedCategory:
                            //Toast.makeText(MainActivity.this, "hey", Toast.LENGTH_SHORT).show();
                            // this is expected for a subscribe, this means there is no error or issue whatsoever
                            break;
                        case PNReconnectedCategory:
                            // this usually occurs if subscribe temporarily fails but reconnects. This means
                            // there was an error but there is no longer any issue
                            break;
                        case PNDisconnectedCategory:
                            // this is the expected category for an unsubscribe. This means there
                            // was no error in unsubscribing from everything
                            break;

                        case PNUnexpectedDisconnectCategory:

                            pubnub.reconnect();

                            break;
                        // this is usually an issue with the internet connection, this is an error, handle appropriately
                        case PNTimeoutCategory:

                            pubnub.reconnect();

                            break;
                        case PNAccessDeniedCategory:
                            // this means that PAM does allow this client to subscribe to this
                            // channel and channel group configuration. This is another explicit error
                            break;
                        default:
                            // More errors can be directly specified by creating explicit cases for other
                            // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                            break;
                    }


                /*case PNHeartbeatOperation:
                    // heartbeat operations can in fact have errors, so it is important to check first for an error.
                    // For more information on how to configure heartbeat notifications through the status
                    // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                    if (status.isError()) {
                        // There was an error with the heartbeat operation, handle here
                    } else {
                        // heartbeat operation was successful
                    }
                default: {
                    // Encountered unknown status type
                }
            }*/
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {

            System.out.println(message.toString());

            JsonElement msg = message.getMessage();
            //System.out.println(msg+"&&&&&&&&&&"+msg.toString()+"&&&&&&&&&&"+msg.getAsString());
            String s=message.toString();

            if(msg.getAsString().equals("Hello"))
            {
                //mainUIThread("Hurray");
            }


            //getHistory();

        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {



        }

    };
}
