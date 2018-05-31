package com.hjsoft.driverbooktaxi.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.activity.HomeActivity;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 26/12/17.
 */
public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");//if(intent!=null)

        if(intent!=null) {

            Bundle bundle = intent.getExtras();

            API REST_CLIENT;
            REST_CLIENT = RestClient.get();
            //Code here

            if (bundle != null) {

                final String logout = bundle.getString("logout");
                final String driverLoginId = bundle.getString("driverLoginId");

                if (logout != null && driverLoginId != null) {


                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", driverLoginId);
                    v.addProperty("companyid", "CMP00001");

                    //System.out.println("2");


                    Call<Pojo> call = REST_CLIENT.toOffline(v);

                    //System.out.println("2.5");

                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            //System.out.println("3");

                            if (response.isSuccessful()) {
                                //System.out.println("reponse successsssssssss");
                                OnClearFromRecentService.this.stopSelf();
                            } else {
                                //System.out.println("reponse not successsssssssss");
                                OnClearFromRecentService.this.stopSelf();

                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            //System.out.println("4");

                            //System.out.println("reponse failure");

                            OnClearFromRecentService.this.stopSelf();


                        }
                    });
                }
            } else {
                //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
                Log.e("****@@@***********", "Service Start");
            }
        }

        return super.onStartCommand(intent,flags,startId);


        //return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.e("ClearFromRecentService", "END");
        //System.out.println("*******************************************************");

        HashMap<String, String> user;
        SessionManager session=new SessionManager(getApplicationContext());
        //session.logoutUser();
        user = session.getUserDetails();

        //System.out.println("1");

        String stProfileId=user.get(SessionManager.KEY_PROFILE_ID);

//        UserLocalStore userLocalStore=new UserLocalStore(OnClearFromRecentService.this);
//        Log.e("USER DATA :",userLocalStore.fetchUserData().toString());
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        restartServiceTask.putExtra("logout","true");
        restartServiceTask.putExtra("driverLoginId",stProfileId);
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);
        super.onTaskRemoved(rootIntent);





    }
}