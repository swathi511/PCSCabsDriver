package com.hjsoft.driverbooktaxi.fragments;

import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.driverbooktaxi.adapter.SearchRideRecyclerAdapter;
import com.hjsoft.driverbooktaxi.model.AllRidesPojo;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

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

/**
 * Created by hjsoft on 30/11/17.
 */
public class SearchRideDateFragment extends Fragment {

    RecyclerView rView;
    SearchRideRecyclerAdapter mAdapter;
    DBAdapter dbAdapter;
    ArrayList<AllRidesPojo> allRidesDataList;
    FormattedAllRidesData data;
    API REST_CLIENT;
    View rootView;
    BottomSheetDialogFragment myBottomSheet;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    ArrayList<FormattedAllRidesData> newDataList=new ArrayList<>();
    Date date1;
    String driverProfileId;
    HashMap<String, String> user;
    SessionManager session;
    String companyId="CMP00001";
    DatePicker dp;
    int day,mnth,yr;
    Button btOk;
    TextView tvDate,tvMsg;
    ImageView ivSearch;
    String stDate;
    LayoutInflater inflater;


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

        rootView = inflater.inflate(R.layout.fragment_specific_date_ride, container, false);
        rView = (RecyclerView) rootView.findViewById(R.id.fsdr_rview);

        tvMsg = (TextView) rootView.findViewById(R.id.fsdr_tv_no_rides);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
       // ivRetry=(ImageView)rootView.findViewById(R.id.fsdr_iv_retry);
        tvDate=(TextView)rootView.findViewById(R.id.fsdr_tv_date);
        ivSearch=(ImageView)rootView.findViewById(R.id.fsdr_iv_date);

        tvMsg.setVisibility(View.GONE);

        getAllRides();
       // ivRetry.setVisibility(View.GONE);

        //mAdapter.notifyDataSetChanged();
        //  if (rideData.size() == 0) {
        //  tvNoRides.setVisibility(View.VISIBLE);
        //  }

        //Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getAllUserRides(guestProfileId);

        //getAllRides();

        //dataList=(ArrayList<FormattedAllRidesData>)  getActivity().getIntent().getSerializableExtra("list");





        return rootView;
    }


    public void getAllData() {

        newDataList.clear();
        tvMsg.setVisibility(View.GONE);

        for (int i = 0; i < dataList.size(); i++) {

            data = dataList.get(i);

            try {

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = formatter.parse(stDate);
                String date2 = formatter.format(data.getRideDate());

                System.out.println("***** "+stDate+":"+date2);

                if (stDate.equals(date2)) {

                    newDataList.add(new FormattedAllRidesData(i,date1, data.getRequestId(), data.getFromLocation(), data.getToLocation(), data.getVehicleCategory(),
                            data.getVehicleType(), data.getDistanceTravelled(), data.getRideStatus(), data.getRideStartTime(), data.getRideStopTime(),
                            data.getTotalAmount(), data.getDriverName(), data.getDriverPic(), data.getTravelType(), data.getBookingType(), data.getTravelPackage(), data.getDriverMobile(), data.getGuestProfileId(), data.getGuestName(), data.getGuestMobile(),
                            data.getPickupLat(), data.getPickupLong(), data.getDropLat(), data.getDropLong(), data.getOtpStatus(), data.getOsBatta(), data.getPaymentMode(), data.getOtherCharges(),"",""));

                }

                if (newDataList.size() != 0) {
                    mAdapter = new SearchRideRecyclerAdapter(getActivity(), newDataList, rView);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rView.setLayoutManager(mLayoutManager);
                    rView.setItemAnimator(new DefaultItemAnimator());
                    rView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {

                    tvMsg.setVisibility(View.VISIBLE);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

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
                    progressDialog.dismiss();
                    allRidesDataList=response.body();

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

                            dataList.add(new FormattedAllRidesData(i,date1, data.getRequestid(), data.getFromlocation(), data.getTolocation(), data.getVehicleCategory(),
                                    data.getVehicleType(), data.getDistancetravelled(), data.getStatusofride(), data.getRidestarttime(), data.getRidestoptime(),
                                    data.getTotalamount(), data.getDrivername(),data.getDriverpic(),data.getTravelType(),data.getBookingType(),data.getTravelpackage(),data.getDrivermobile(),data.getGuestProfileId(),data.getGuestName(),data.getGuestMobile(),
                                    data.getPickupLatitude(),data.getPickupLongitude(),data.getDropLatitude(),data.getDropLongitude(),data.getOTPStatus(),data.getDriverBattaAmt(),data.getPaymentMode(),data.getOtherCharges(),"",""));

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
                    showAlert();

                }else
                {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllRidesPojo>> call, Throwable t) {



                progressDialog.dismiss();
                if(myBottomSheet.isAdded())
                {
                    //return;
                }
                else
                {
                    if(rootView.isShown()) {

                        myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                    }
                }

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

        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                day=dayOfMonth;
                mnth=month+1;
                yr=year;

                System.out.println("date is "+day+":::"+mnth+":::"+yr);
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stDate=day+"/"+mnth+"/"+yr;
                tvDate.setText(stDate);
                getAllData();
                alertDialog.dismiss();

            }
        });

    }
}
