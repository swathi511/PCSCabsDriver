package com.hjsoft.driverbooktaxi.fragments;

/**
 * Created by hjsoft on 21/1/17.
 */

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.activity.AllRidesActivity;
import com.hjsoft.driverbooktaxi.activity.HomeActivity;
import com.hjsoft.driverbooktaxi.activity.MainActivity;
import com.hjsoft.driverbooktaxi.activity.OutStationRideStartActivity;
import com.hjsoft.driverbooktaxi.activity.OutStationTrackRideActivity;
import com.hjsoft.driverbooktaxi.activity.RideStartActivity;
import com.hjsoft.driverbooktaxi.activity.SearchRideDateActivity;
import com.hjsoft.driverbooktaxi.activity.TrackRideActivity;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.driverbooktaxi.adapter.SearchRideRecyclerAdapter;
import com.hjsoft.driverbooktaxi.model.AllRidesPojo;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllRidesFragment extends Fragment implements RecyclerAdapter.AdapterCallback{

    RecyclerView rView;
    RecyclerAdapter mAdapter;
    DBAdapter dbAdapter;
    TextView tvNoRides;
    ArrayList<AllRidesPojo> allRidesDataList;
    AllRidesPojo allRidesData;
    API REST_CLIENT;
    //BottomSheetDialogFragment myBottomSheet;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
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
    ArrayList<FormattedAllRidesData> newDataList=new ArrayList<>();
    FormattedAllRidesData data;
    //LinearLayout llDate;
    //ImageView ivDate;

    // ***************************
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
    String stProfileId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    AllRidesActivity a;
    boolean accepted=false;
    //***********************

    TextView tvFromDate,tvToDate,tvOk;
    ImageView ivFrom,ivTo;
    String fromdate,todate;
    DatePickerDialog datePickerDialog;
    Bundle b;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        REST_CLIENT= RestClient.get();
        dbAdapter = new DBAdapter(getContext());
        dbAdapter = dbAdapter.open();
        //rideData = dbAdapter.getAllRideEntries();
        session=new SessionManager(getActivity());

        user=session.getUserDetails();

        driverProfileId=user.get(SessionManager.KEY_PROFILE_ID);
        stProfileId=driverProfileId;
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        mp = MediaPlayer.create(getActivity(), R.raw.beep);

        dialogBuilder = new AlertDialog.Builder(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_all_rides, container, false);
        rView = (RecyclerView) rootView.findViewById(R.id.fmr_rview);

        tvNoRides = (TextView) rootView.findViewById(R.id.fmr_tv_no_rides);
        //myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        ivRetry=(ImageView)rootView.findViewById(R.id.fmr_iv_retry);
        //tvSearch=(TextView)rootView.findViewById(R.id.fmr_tv_search);
        //llDate=(LinearLayout)rootView.findViewById(R.id.fmr_ll);
        //tvDate=(TextView)rootView.findViewById(R.id.fmr_tv_date);
        //tvClear=(TextView)rootView.findViewById(R.id.fmr_tv_clear);
        //ivDate=(ImageView)rootView.findViewById(R.id.fmr_iv_date);

        tvFromDate=(TextView)rootView.findViewById(R.id.fmr_tv_from);
        tvToDate=(TextView)rootView.findViewById(R.id.fmr_tv_to);
        ivFrom=(ImageView)rootView.findViewById(R.id.fmr_iv_from);
        ivTo=(ImageView)rootView.findViewById(R.id.fmr_iv_to);
        tvOk=(TextView)rootView.findViewById(R.id.fmr_tv_ok);

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

        //mAdapter.notifyDataSetChanged();
        //  if (rideData.size() == 0) {
        //  tvNoRides.setVisibility(View.VISIBLE);
        //  }

        //Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getAllUserRides(guestProfileId);

        b=getActivity().getIntent().getExtras();
        fromdate=b.getString("dateValue",fromdate);
        todate=b.getString("dateValue",todate);

        getAllRides();
        //getDetails();

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

        /*tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlert();


                System.out.println("calling search ride date....");

            }
        });

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvNoRides.setVisibility(View.GONE);

                showAlert();
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvNoRides.setVisibility(View.GONE);

                tvClear.setVisibility(View.GONE);

                llDate.setVisibility(View.GONE);
                tvSearch.setVisibility(View.VISIBLE);

                mAdapter = new RecyclerAdapter(getActivity(), dataList, rView,dataList);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                rView.setLayoutManager(mLayoutManager);
                rView.setItemAnimator(new DefaultItemAnimator());
                rView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


            }
        });
*/


        return rootView;
    }

    /*@Override
    public void onMethodCallback(final int position,ArrayList<FormattedAllRidesData> data) {

        Fragment frag = new AllRidesFragment();
        Bundle args = new Bundle();
        args.putInt("position",position);
        args.putSerializable("list",data);
        frag.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, frag,"specific_ride");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }*/

    public void getAllRides()
    {
        tvNoRides.setVisibility(View.GONE);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getRideHistory(driverProfileId,"driver",companyId,fromdate,todate);
        call.enqueue(new Callback<ArrayList<AllRidesPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<AllRidesPojo>> call, Response<ArrayList<AllRidesPojo>> response) {

                AllRidesPojo data;

                if(response.isSuccessful())
                {
                   // tvSearch.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    allRidesDataList=response.body();

                    int j=0;

                    for(int i=0;i<allRidesDataList.size();i++)
                    {
                        data = allRidesDataList.get(i);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
                        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                        try {
                            date1 = dateFormat.parse(data.getRidedate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (data.getStatusofride().equals("BOOKED")||data.getStatusofride().equals("NORESPONSE")||data.getStatusofride().equals("DECLINED")) {

                        } else {

                            //System.out.println("j isssssssssss "+j);


                            dataList.add(new FormattedAllRidesData(j,date1, data.getRequestid(), data.getFromlocation(), data.getTolocation(), data.getVehicleCategory(),
                                    data.getVehicleType(), data.getDistancetravelled(), data.getStatusofride(), data.getRidestarttime(), data.getRidestoptime(),
                                    data.getTotalamount(), data.getDrivername(),data.getDriverpic(),data.getTravelType(),data.getBookingType(),data.getTravelpackage(),data.getDrivermobile(),data.getGuestProfileId(),data.getGuestName(),data.getGuestMobile(),
                                    data.getPickupLatitude(),data.getPickupLongitude(),data.getDropLatitude(),data.getDropLongitude(),data.getOTPStatus(),data.getDriverBattaAmt(),data.getPaymentMode(),data.getOtherCharges(),data.getPickupLat(),data.getPickupLong()));

                            j++;
                        }
                    }
                }

                else {

                    progressDialog.dismiss();
                }

                if(dataList.size()!=0) {
                    Collections.sort(dataList,new Comparator<FormattedAllRidesData>()
                    {
                        public int compare(FormattedAllRidesData o1, FormattedAllRidesData o2) {
                            if (o1.getRideDate()==null||o2.getRideDate()==null)
                                return 0;
                            return o1.getRideDate().compareTo(o2.getRideDate());
                        }
                    });

                    Collections.reverse(dataList);
                    mAdapter = new RecyclerAdapter(getActivity(), dataList, rView,dataList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rView.setLayoutManager(mLayoutManager);
                    rView.setItemAnimator(new DefaultItemAnimator());
                    rView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else
                {
                    tvNoRides.setVisibility(View.VISIBLE);
                    //tvSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllRidesPojo>> call, Throwable t) {

                ivRetry.setVisibility(View.VISIBLE);
                //tvSearch.setVisibility(View.GONE);

                Toast.makeText(getActivity(),"Please check Internet connection!",Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

               /* if(myBottomSheet.isAdded())
                {
                    //return;
                }
                else
                {
                    if(rootView.isShown()) {

                        myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                    }
                }*/

            }
        });
    }


    public void showAlert()
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_date, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        dp=(DatePicker)dialogView.findViewById(R.id.datePicker);
        btOk=(Button)dialogView.findViewById(R.id.ok);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        day = dp.getDayOfMonth();
        mnth = dp.getMonth() + 1;
        yr = dp.getYear();

        long now = System.currentTimeMillis() - 1000;

        dp.setMaxDate(now);

        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {


                DecimalFormat mFormat= new DecimalFormat("00");
                //mFormat.format(Double.valueOf(year));
                String s=String.valueOf(mFormat.format(dayOfMonth));

                //System.out.println("ssssssss is  "+s);
                mFormat.format(month+1);

                day=dayOfMonth;
                mnth=month+1;
                yr=year;

                //System.out.println("date is "+day+":::"+mnth+":::"+yr);
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*tvSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.VISIBLE);
                tvClear.setVisibility(View.VISIBLE);*/
                DecimalFormat mFormat= new DecimalFormat("00");
                //mFormat.format(Double.valueOf(year));
                String s1=String.valueOf(mFormat.format(day));
                String s2=String.valueOf(mFormat.format(mnth));
                String s3=String.valueOf(yr);


                stDate=s1+"/"+s2+"/"+s3;
                //tvDate.setText(stDate);
                getAllData();
                alertDialog.dismiss();

            }
        });

    }

    public void getAllData() {

        //tvNoRides.setVisibility(View.GONE);

        newDataList.clear();

        for (int i = 0; i < dataList.size(); i++) {

            data = dataList.get(i);

            try {

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = formatter.parse(stDate);
                String date2 = formatter.format(data.getRideDate());

                System.out.println("***** " + stDate + ":" + date2);

                if (stDate.equals(date2)) {

                    newDataList.add(new FormattedAllRidesData(data.getPosition(), date1, data.getRequestId(), data.getFromLocation(), data.getToLocation(), data.getVehicleCategory(),
                            data.getVehicleType(), data.getDistanceTravelled(), data.getRideStatus(), data.getRideStartTime(), data.getRideStopTime(),
                            data.getTotalAmount(), data.getDriverName(), data.getDriverPic(), data.getTravelType(), data.getBookingType(), data.getTravelPackage(), data.getDriverMobile(), data.getGuestProfileId(), data.getGuestName(), data.getGuestMobile(),
                            data.getPickupLat(), data.getPickupLong(), data.getDropLat(), data.getDropLong(), data.getOtpStatus(), data.getOsBatta(), data.getPaymentMode(), data.getOtherCharges(),data.getAppPickupLat(),data.getAppPickupLong()));

                }
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (newDataList.size() != 0) {
            mAdapter = new RecyclerAdapter(getActivity(), newDataList, rView,dataList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rView.setLayoutManager(mLayoutManager);
            rView.setItemAnimator(new DefaultItemAnimator());
            rView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {

            System.out.println("data issssssssssss "+newDataList.size());

            mAdapter = new RecyclerAdapter(getActivity(), newDataList, rView,dataList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rView.setLayoutManager(mLayoutManager);
            rView.setItemAnimator(new DefaultItemAnimator());
            rView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            tvNoRides.setVisibility(View.VISIBLE);

        }




    }

    @Override
    public void onMethodCallback(int position, ArrayList<FormattedAllRidesData> data) {

    }


    public void getDetails(){

        handler = new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(r,10000);

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
                                    Intent myIntent = new Intent(a,AllRidesActivity.class);
                                    //myIntent.setComponent(rootActivity);
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    //myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

                                    SimpleDateFormat  format1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",Locale.ENGLISH);
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
                                                                h.removeCallbacks(rr);
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

                                                                h.removeCallbacks(rr);
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
                                                                h.removeCallbacks(rr);
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
                                                                h.removeCallbacks(rr);
                                                                hLoc.removeCallbacks(rLoc);
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

                                                                h.removeCallbacks(rr);
                                                                hLoc.removeCallbacks(rLoc);
                                                                alertDialog.dismiss();
                                                                count = 0;
                                                                Intent i = new Intent(a, OutStationRideStartActivity.class);
                                                                i.putExtra("cabData", cabData);
                                                                startActivity(i);
                                                                getActivity().finish();


                                                            } else {
                                                                h.removeCallbacks(rr);
                                                                hLoc.removeCallbacks(rLoc);
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
                                                public void onFailure(Call<Pojo> call, Throwable t) {

                                                    alertDialog.dismiss();


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
                                                public void onFailure(Call<Pojo> call, Throwable t) {

                                                    alertDialog.dismiss();

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
                    h.postDelayed(rr,15000);
                    //20000
                }

            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AllRidesActivity){
            a=(AllRidesActivity) context;
        }

    }


}
