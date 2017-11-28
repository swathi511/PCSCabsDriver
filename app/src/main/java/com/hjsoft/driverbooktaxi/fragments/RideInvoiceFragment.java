package com.hjsoft.driverbooktaxi.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjsoft.driverbooktaxi.MyBottomSheetDialogFragment;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.activity.HomeActivity;
import com.hjsoft.driverbooktaxi.activity.MainActivity;
import com.hjsoft.driverbooktaxi.activity.RequestsActivity;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.RideStopPojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 24/1/17.
 */
public class RideInvoiceFragment extends Fragment {

    Button btNextDuty;
    String stFare,stTime,stRideStart,stRideStop;
    ArrayList<GuestData> cabData;
    double stDistance;
    GuestData data;
    TextView tvTripId,tvPickup,tvDrop,tvDistance,tvTime,tvFare,tvRideStart,tvRideStop,tvTotalFare,tvTaxes,tvTotalBill,tvOutstationBatta;
    TextView tvWalletAmnt,tvCash;
    LinearLayout llWallet,llCash,llOSBatta,llCashInfo,llWalletInfo;
    ImageButton ibLogout;
    SessionManager session;
    API REST_CLIENT;
    BottomSheetDialogFragment myBottomSheet;
    View rootView;
    long diff=0;
    String companyId="CMP00001";
    DBAdapter dbAdapter;
    double totalBill;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String osbatta;
    ImageView ivPayU;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_ride_fare_details, container, false);

        tvTripId=(TextView)rootView.findViewById(R.id.arfd_tv_trip_id);
        tvPickup=(TextView)rootView.findViewById(R.id.arfd_tv_pickup_loc);
        tvDrop=(TextView)rootView.findViewById(R.id.arfd_tv_drop_loc);
        tvDistance=(TextView)rootView.findViewById(R.id.arfd_tv_distance);
        tvTime=(TextView)rootView.findViewById(R.id.arfd_tv_time);
        tvFare=(TextView)rootView.findViewById(R.id.arfd_tv_fare);
        tvRideStart=(TextView)rootView.findViewById(R.id.arfd_tv_ride_start_time);
        tvRideStop=(TextView)rootView.findViewById(R.id.arfd_tv_ride_stop_time);
        ibLogout=(ImageButton)rootView.findViewById(R.id.arfd_ib_logout);
        tvTotalFare=(TextView)rootView.findViewById(R.id.arfd_tv_total_fare);
        tvTaxes=(TextView)rootView.findViewById(R.id.arfd_tv_taxes);
        tvTotalBill=(TextView)rootView.findViewById(R.id.arfd_tv_total_bill);
        tvWalletAmnt=(TextView)rootView.findViewById(R.id.arfd_tv_wallet_amnt);
        tvCash=(TextView)rootView.findViewById(R.id.arfd_tv_cash);
        llWallet=(LinearLayout)rootView.findViewById(R.id.arfd_ll_wallet);
        llCash=(LinearLayout)rootView.findViewById(R.id.arfd_ll_cash);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvOutstationBatta=(TextView)rootView.findViewById(R.id.arfd_tv_os_batta);
        llOSBatta=(LinearLayout)rootView.findViewById(R.id.arfd_ll_os_batta);
        ivPayU=(ImageView)rootView.findViewById(R.id.arfd_iv_payu);
        llCashInfo=(LinearLayout)rootView.findViewById(R.id.arfd_ll_cash_info);
        llWalletInfo=(LinearLayout)rootView.findViewById(R.id.arfd_ll_wallet_info);

        REST_CLIENT= RestClient.get();
        tvOutstationBatta.setVisibility(View.GONE);
        llOSBatta.setVisibility(View.GONE);
        ivPayU.setVisibility(View.GONE);

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        session=new SessionManager(getActivity());

        Bundle b=getActivity().getIntent().getExtras();
        stFare=b.getString("fare");
        stDistance=b.getFloat("distance");
        //System.out.println("distance is "+stDistance);
        //stDistance=stDistance/1000;
        stTime=b.getString("time");
        stRideStart=b.getString("rideStart");
        stRideStop=b.getString("rideStop");
        cabData= (ArrayList<GuestData>) getActivity().getIntent().getSerializableExtra("cabData");
        data=cabData.get(0);

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        /*editor.putString("pickup_lat","-");
        editor.putString("pickup_long","-");*/
        editor.putString("booking","out");
        editor.putBoolean("saved",false);
        editor.commit();

        //System.out.println("ride invoice.... ");

        //System.out.println("data.reqID "+data.getgRequestId());

        Call<List<RideStopPojo>> call=REST_CLIENT.getRideStopData(data.getgRequestId(),companyId,"driver");
        call.enqueue(new Callback<List<RideStopPojo>>() {
            @Override
            public void onResponse(Call<List<RideStopPojo>> call, Response<List<RideStopPojo>> response) {

                List<RideStopPojo> dataList;
                RideStopPojo data1;

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    data1=dataList.get(0);

                    tvRideStart.setText(stRideStart);
                    tvRideStop.setText(stRideStop);
                    tvTripId.setText(data.getgRequestId());
                    tvPickup.setText(data.getgPickup());
                    tvDrop.setText(data.getgDrop());
                    tvDistance.setText(String.valueOf((float)stDistance));
                    tvTime.setText(stTime);
                   /* tvFare.setText("Rs. "+data.getTotalfare());
                    tvTotalFare.setText("Rs. "+data.getTotalfare());
                    tvTotalBill.setText("Rs. "+data.getTotalfare());
                    tvTaxes.setText("Rs. 0");*/

                    //System.out.println("Total fare is "+data1.getTotalfare());

                    if(data1.getTravelType().equals("outstation"))
                    {
                        llOSBatta.setVisibility(View.VISIBLE);
                        tvOutstationBatta.setVisibility(View.VISIBLE);
                        tvOutstationBatta.setText(getString(R.string.Rs)+" "+data1.getDriverBattaAmt());
                        osbatta=data1.getDriverBattaAmt();
                    }


                    if(data1.getTotalfare().equals("")) {

                        tvFare.setText(getString(R.string.Rs)+" "+"-");
                        tvTotalFare.setText(getString(R.string.Rs)+" "+"-");
                        tvTotalBill.setText(getString(R.string.Rs)+" "+"-");
                        tvTaxes.setText(getString(R.string.Rs)+" "+"0");
                    }
                    else
                    {
                        String[] amount = data1.getTotalfare().split("-");


                        if (data1.getTotalfare().equals("-")) {
                            tvFare.setText(getString(R.string.Rs)+" "+"0");
                            tvTotalFare.setText(getString(R.string.Rs)+" "+"0");
                            tvTotalBill.setText(getString(R.string.Rs)+" "+"0");
                        } else {
                            String fare = amount[0];
                            String tax = amount[1];

                            if(data1.getTravelType().equals("outstation"))
                            {
                                Double finalFare=Double.parseDouble(fare)-Double.parseDouble(osbatta);
                                tvFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                                tvTotalFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                            }
                            else {
                                tvFare.setText(getString(R.string.Rs)+" "+ fare);
                                tvTotalFare.setText(getString(R.string.Rs)+" "+ fare);
                            }


                            tvTaxes.setText(getString(R.string.Rs)+" "+ tax);
                            totalBill = Double.parseDouble(fare)+Double.parseDouble(tax);
                            tvTotalBill.setText(getString(R.string.Rs)+" "+ String.valueOf(totalBill));
                        }

            /*
            tvFare.setText("Rs. "+data.getTotalAmount());
            tvTotalFare.setText("Rs. "+data.getTotalAmount());
            tvTotalBill.setText("Rs. "+data.getTotalAmount());*/
                    }

                    tvDistance.setText(data1.getDistancetravelled());

                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    try {
                        Date date1 = timeFormat.parse(data1.getRidestarttime());
                        Date date2 = timeFormat.parse(data1.getRidestoptime());

                        diff = (date2.getTime() - date1.getTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int Hours = (int) (diff/(1000 * 60 * 60));
                    int Mins = (int) (diff/(1000*60)) % 60;
                    long Secs = (int) (diff / 1000) % 60;

                    DecimalFormat formatter = new DecimalFormat("00");
                    String hFormatted = formatter.format(Hours);
                    String mFormatted = formatter.format(Mins);
                    String sFormatted = formatter.format(Secs);
                    String time=hFormatted+":"+mFormatted+":"+sFormatted;

                    tvTime.setText(time);

                    //System.out.println(data1.getPaymentMode()+":"+data1.getPaymentCash()+":"+data1.getPaymentWallet()+":"+data1.getWalletBalance());

                    if(data1.getPaymentMode().equals("cash"))
                    {
                        llWallet.setVisibility(View.GONE);
                        tvCash.setText(getString(R.string.Rs)+" "+data1.getPaymentCash());
                        llCashInfo.setVisibility(View.VISIBLE);

                    }else if(data1.getPaymentMode().equals("wallet"))
                    {
                        // llCash.setVisibility(View.GONE);
                        tvWalletAmnt.setText(getString(R.string.Rs)+" "+data1.getPaymentWallet());
                        if(data1.getPaymentCash().equals("0"))
                        {
                            llCash.setVisibility(View.GONE);
                        }
                        else {
                            tvCash.setText(getString(R.string.Rs)+" "+data1.getPaymentCash());
                        }
                        ivPayU.setVisibility(View.VISIBLE);
                        llWalletInfo.setVisibility(View.VISIBLE);
                    }
                    else {
                        llWallet.setVisibility(View.GONE);
                        tvCash.setText(getString(R.string.Rs)+" "+totalBill);
                        llCashInfo.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    //System.out.println(response.message()+"::"+response.code()+"::"+response.errorBody()+":");
                }




            }

            @Override
            public void onFailure(Call<List<RideStopPojo>> call, Throwable t) {

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

        /*

        tvRideStart.setText(stRideStart);
        tvRideStop.setText(stRideStop);
        tvTripId.setText(data.getgRequestId());
        tvPickup.setText(data.getgPickup());
        tvDrop.setText(data.getgDrop());
        tvDistance.setText(String.valueOf(stDistance));
        tvTime.setText(stTime);
        tvFare.setText("Rs. "+stFare);
        tvTotalFare.setText("Rs. "+stFare);
        tvTaxes.setText("Rs. 0");
        tvTotalBill.setText("Rs. "+stFare);*/

        /*
//******************************************
        String[] timeArray =stTime.split(".");
        String hh=timeArray[0];
        String mm=timeArray[1];

        if(hh.equals("00"))
        {
            if(mm.equals("00"))
            {
                tvTime.setText("- hr - min");
            }
            else {
                tvTime.setText(mm + " min");
            }
        }
        else {
            tvTime.setText(hh + " hr " + mm + " min ");
        }*/

        //   System.out.println("time is "+stTime);

      /* String[] timeArray=stTime.split(".");
        String hh=timeArray[0];
        String mm=timeArray[1];

        tvTime.setText(hh+" hr " +mm+" min ");
*/
        btNextDuty=(Button)rootView.findViewById(R.id.arfd_bt_next_duty);

        btNextDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
                getActivity().finish();
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

        return  rootView;
    }
}
