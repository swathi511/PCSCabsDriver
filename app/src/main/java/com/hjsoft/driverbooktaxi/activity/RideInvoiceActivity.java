package com.hjsoft.driverbooktaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 1/12/16.
 */
public class RideInvoiceActivity extends AppCompatActivity {

    Button btNextDuty;
    String stFare,stTime,stRideStart,stRideStop;
    ArrayList<GuestData> cabData;
    double stDistance;
    GuestData data;
    TextView tvTripId,tvPickup,tvDrop,tvDistance,tvTime,tvFare,tvRideStart,tvRideStop,tvTotalFare,tvTaxes,tvTotalBill;
    ImageButton ibLogout;
    SessionManager session;
    String companyId="CMP00001";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_fare_details);
        tvTripId=(TextView)findViewById(R.id.arfd_tv_trip_id);
        tvPickup=(TextView)findViewById(R.id.arfd_tv_pickup_loc);
        tvDrop=(TextView)findViewById(R.id.arfd_tv_drop_loc);
        tvDistance=(TextView)findViewById(R.id.arfd_tv_distance);
        tvTime=(TextView)findViewById(R.id.arfd_tv_time);
        tvFare=(TextView)findViewById(R.id.arfd_tv_fare);
        tvRideStart=(TextView)findViewById(R.id.arfd_tv_ride_start_time);
        tvRideStop=(TextView)findViewById(R.id.arfd_tv_ride_stop_time);
        ibLogout=(ImageButton)findViewById(R.id.arfd_ib_logout);
        tvTotalFare=(TextView)findViewById(R.id.arfd_tv_total_fare);
        tvTaxes=(TextView)findViewById(R.id.arfd_tv_taxes);
        tvTotalBill=(TextView)findViewById(R.id.arfd_tv_total_bill);

        session=new SessionManager(getApplicationContext());

        Bundle b=getIntent().getExtras();
        stFare=b.getString("fare");
        stDistance=b.getDouble("distance");
        //stDistance=stDistance/1000;
        stTime=b.getString("time");
        stRideStart=b.getString("rideStart");
        stRideStop=b.getString("rideStop");
        cabData= (ArrayList<GuestData>) getIntent().getSerializableExtra("cabData");
        data=cabData.get(0);

        tvRideStart.setText(stRideStart);
        tvRideStop.setText(stRideStop);
        tvTripId.setText(data.getgRequestId());
        tvPickup.setText(data.getgPickup());
        tvDrop.setText(data.getgDrop());
        tvDistance.setText(String.valueOf(stDistance)+" km");
        tvTime.setText(stTime);
        tvFare.setText("Rs. "+stFare);
        tvTotalFare.setText("Rs. "+stFare);
        tvTaxes.setText("Rs. 0");
        tvTotalBill.setText("Rs. "+stFare);

        /*

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

      /* String[] timeArray=stTime.split(".");
        String hh=timeArray[0];
        String mm=timeArray[1];

        tvTime.setText(hh+" hr " +mm+" min ");
*/
        btNextDuty=(Button)findViewById(R.id.arfd_bt_next_duty);

        btNextDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(RideInvoiceActivity.this,RequestsActivity.class);
                startActivity(i);
                finish();
            }
        });

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.logoutUser();
                Intent i=new Intent(RideInvoiceActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //session.logoutUser();
        sendLogoutStatus();
    }

    public void sendLogoutStatus()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RideInvoiceActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_logout, null);

        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        Button btOk=(Button)dialogView.findViewById(R.id.alog_bt_ok);
        Button btCancel=(Button)dialogView.findViewById(R.id.alog_bt_cancel);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.logoutUser();

                HashMap<String, String> user;
                SessionManager session=new SessionManager(getApplicationContext());
                user = session.getUserDetails();
                String stProfileId=user.get(SessionManager.KEY_PROFILE_ID);
                API REST_CLIENT;
                REST_CLIENT= RestClient.get();

                JsonObject v=new JsonObject();
                v.addProperty("profileid",stProfileId);
                v.addProperty("companyid",companyId);

                Call<Pojo> call=REST_CLIENT.toOffline(v);
                call.enqueue(new Callback<Pojo>() {
                    @Override
                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                        if(response.isSuccessful())
                        {
                            alertDialog.dismiss();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                        alertDialog.dismiss();
                        finish();
                    }
                });

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                //System.out.println("cancel done");
            }
        });
    }

//    public void onUserLeaveHint() { // this only executes when Home is selected.
//        // do stuff
//        super.onUserLeaveHint();
//        System.out.println("HOMEEEEEEEEEEEEEEEEEEEEEEE");
//        sendLogoutStatus();
//
//    }
}
