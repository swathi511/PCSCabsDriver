package com.hjsoft.driverbooktaxi.fragments;

/**
 * Created by hjsoft on 21/1/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.activity.HomeActivity;
import com.hjsoft.driverbooktaxi.activity.MainActivity;
import com.hjsoft.driverbooktaxi.activity.SearchRideDateActivity;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.driverbooktaxi.adapter.SearchRideRecyclerAdapter;
import com.hjsoft.driverbooktaxi.model.AllRidesPojo;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;
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
    View rootView;
    //BottomSheetDialogFragment myBottomSheet;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    Date date1;
    String driverProfileId;
    HashMap<String, String> user;
    SessionManager session;
    ImageView ivRetry;
    String companyId="CMP00001";
    TextView tvSearch;
    int day,mnth,yr;
    Button btOk;
    TextView tvDate,tvClear;
    String stDate;
    LayoutInflater inflater;
    DatePicker dp;
    ArrayList<FormattedAllRidesData> newDataList=new ArrayList<>();
    FormattedAllRidesData data;
    LinearLayout llDate;
    ImageView ivDate;



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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_rides, container, false);
        rView = (RecyclerView) rootView.findViewById(R.id.fmr_rview);

        tvNoRides = (TextView) rootView.findViewById(R.id.fmr_tv_no_rides);
        //myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        ivRetry=(ImageView)rootView.findViewById(R.id.fmr_iv_retry);
        tvSearch=(TextView)rootView.findViewById(R.id.fmr_tv_search);
        llDate=(LinearLayout)rootView.findViewById(R.id.fmr_ll);
        tvDate=(TextView)rootView.findViewById(R.id.fmr_tv_date);
        tvClear=(TextView)rootView.findViewById(R.id.fmr_tv_clear);
        ivDate=(ImageView)rootView.findViewById(R.id.fmr_iv_date);

        tvNoRides.setVisibility(View.GONE);
        ivRetry.setVisibility(View.GONE);
        llDate.setVisibility(View.GONE);
        tvClear.setVisibility(View.GONE);

        //mAdapter.notifyDataSetChanged();
        //  if (rideData.size() == 0) {
        //  tvNoRides.setVisibility(View.VISIBLE);
        //  }

        //Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getAllUserRides(guestProfileId);

        getAllRides();

        ivRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivRetry.setVisibility(View.GONE);
                getAllRides();
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
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
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getUserRides(driverProfileId,"driver",companyId);
        call.enqueue(new Callback<ArrayList<AllRidesPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<AllRidesPojo>> call, Response<ArrayList<AllRidesPojo>> response) {

                AllRidesPojo data;

                if(response.isSuccessful())
                {
                    tvSearch.setVisibility(View.VISIBLE);
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

                            System.out.println("j isssssssssss "+j);


                            dataList.add(new FormattedAllRidesData(j,date1, data.getRequestid(), data.getFromlocation(), data.getTolocation(), data.getVehicleCategory(),
                                    data.getVehicleType(), data.getDistancetravelled(), data.getStatusofride(), data.getRidestarttime(), data.getRidestoptime(),
                                    data.getTotalamount(), data.getDrivername(),data.getDriverpic(),data.getTravelType(),data.getBookingType(),data.getTravelpackage(),data.getDrivermobile(),data.getGuestProfileId(),data.getGuestName(),data.getGuestMobile(),
                                    data.getPickupLatitude(),data.getPickupLongitude(),data.getDropLatitude(),data.getDropLongitude(),data.getOTPStatus(),data.getDriverBattaAmt(),data.getPaymentMode(),data.getOtherCharges()));

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
                    tvSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllRidesPojo>> call, Throwable t) {

                ivRetry.setVisibility(View.VISIBLE);
                tvSearch.setVisibility(View.GONE);

                Toast.makeText(getActivity(),"Please check Internet connection!",Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
                /*if(myBottomSheet.isAdded())
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

                System.out.println("ssssssss is  "+s);
                mFormat.format(month+1);

                day=dayOfMonth;
                mnth=month+1;
                yr=year;

                System.out.println("date is "+day+":::"+mnth+":::"+yr);
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvSearch.setVisibility(View.GONE);
                llDate.setVisibility(View.VISIBLE);
                tvClear.setVisibility(View.VISIBLE);
                DecimalFormat mFormat= new DecimalFormat("00");
                //mFormat.format(Double.valueOf(year));
                String s1=String.valueOf(mFormat.format(day));
                String s2=String.valueOf(mFormat.format(mnth));
                String s3=String.valueOf(yr);


                stDate=s1+"/"+s2+"/"+s3;
                tvDate.setText(stDate);
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
                            data.getPickupLat(), data.getPickupLong(), data.getDropLat(), data.getDropLong(), data.getOtpStatus(), data.getOsBatta(), data.getPaymentMode(), data.getOtherCharges()));

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


}
