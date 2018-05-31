package com.hjsoft.driverbooktaxi.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.model.CancelData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.model.ServiceLocationPojo;
//import com.hjsoft.driverbooktaxi.service.OnClearFromRecentService;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;
import com.inrista.loggliest.Loggly;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 23/11/16.
 */
public class MainActivity extends AppCompatActivity {

    EditText etUname,etPwd;
    Button btLogin;
    String stUname,stPwd;
    API REST_CLIENT;
    SessionManager session;
    HashMap<String, String> user;
    String uname;
    String companyId="CMP00001";
    String stName,stPd;
    SimpleDateFormat dateFormat;
    String timeUpdated;
    TextView tvLoc1,tvLoc2,tvLoc3,tvLoc4,tvLoc5;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    int j=0;
    String version="4.5";//4.5//5.7//change loggly token also while testing & pubnub also
    //String version="4.5";//20
    String city="Visakhapatnam";
    //protected MyApplicationNew app;
    DBAdapter dbAdapter;
    boolean debugLogs=true;
    final static int REQUEST_LOCATION = 199;
    String deviceId,profileid;
    final String TOKEN = "b505c85d-71ae-4ad6-803b-78b2f8893cb4"; //(swathipriya)



    private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Thread.UncaughtExceptionHandler handler1 = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {

            Log.e("TestApplication", "Uncaught exception is: ", ex);
            // log it & phone home.

            String trace = ex.toString() + "\n";

            for (StackTraceElement e1 : ex.getStackTrace()) {
                trace += "\t at " + e1.toString() + "\n";
            }

            Loggly.i("Uncaught Exception"," in Driver App : "+trace);
            Loggly.forceUpload();

            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler1);

        etUname=(EditText)findViewById(R.id.am_et_uname);
        etPwd=(EditText)findViewById(R.id.am_et_pwd);
        btLogin=(Button)findViewById(R.id.am_bt_login);
        session=new SessionManager(getApplicationContext());

        user = session.getUserDetails();
        uname=user.get(SessionManager.KEY_PROFILE_ID);
        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        timeUpdated=dateFormat.format(date);

        REST_CLIENT= RestClient.get();

        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("data", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        Loggly.with(MainActivity.this,TOKEN)
                .appendDefaultInfo(true)
                .uploadIntervalLogCount(500)
                .uploadIntervalSecs(300)//5 min
                .maxSizeOnDisk(500000)
                //.appendStickyInfo("language", currentLanguage)
                .init();

//        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        System.out.println("IMIE number is "+telephonyManager.getDeviceId());

        if(Build.VERSION.SDK_INT<23)
        {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            //System.out.println("IMIE number is "+telephonyManager.getDeviceId());
            deviceId=telephonyManager.getDeviceId();

            checkIfLoggedIn();
        }
        else
        {
            if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED)
            {
                TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                //System.out.println("IMIE number is "+telephonyManager.getDeviceId());
                deviceId=telephonyManager.getDeviceId();

                checkIfLoggedIn();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))
                {
                    Toast.makeText(MainActivity.this,"Permission is required for this app to run !",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_LOCATION);
            }
        }
        //deviceId="12345";




        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stUname=etUname.getText().toString().trim();
                stPwd=etPwd.getText().toString().trim();

                if(stUname.length()==0)
                {
                    Toast.makeText(MainActivity.this,"Enter Valid Username!",Toast.LENGTH_LONG).show();
                }
                else if(stPwd.length()==0)
                {
                    Toast.makeText(MainActivity.this,"Enter Valid Password!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();

                    JsonObject v=new JsonObject();
                    v.addProperty("login",stUname);
                    v.addProperty("pwd",stPwd);
                    v.addProperty("companyid",companyId);
                    v.addProperty("version",version);
                    v.addProperty("profileid","-");
                    //login // online

                    System.out.println(stUname+":"+stPwd+":"+companyId+":"+version);
                    Call<Pojo> call=REST_CLIENT.validateLogin(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            Pojo data;
                            String s;

                            //progressDialog.dismiss();

                            if(response.isSuccessful())
                            {
                                data=response.body();
                                s=data.getMessage();
                                profileid=s.split("-")[0];

                                Loggly.i("MainActivity",profileid+" Login-New");

                                if(s.split("-").length==2)
                                {
                                    JsonObject v=new JsonObject();
                                    v.addProperty("profileid",profileid);
                                    v.addProperty("companyid",companyId);
                                    v.addProperty("imei",deviceId);

                                    Call<Pojo> call2=REST_CLIENT.sendIMIEnumber(v);
                                    call2.enqueue(new Callback<Pojo>() {
                                        @Override
                                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                            if(response.isSuccessful())
                                            {
                                                session.createLoginSession(stUname, stPwd, profileid);
                                                progressDialog.dismiss();
                                                editor.putString("city", city);
                                                editor.putBoolean("debugLogs", debugLogs);
                                                editor.commit();

                                /*Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                                serviceIntent.setPackage(MainActivity.this.getPackageName());
                                startService(serviceIntent);*/

                                                //(new Intent(getBaseContext(), OnClearFromRecentService.class));

                                                getCancelData();

                                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Pojo> call, Throwable t) {

                                        }
                                    });
                                }
                                else {

                                    //System.out.println("deviceId & imei "+deviceId+":"+s.split("-")[2]);

                                    if (deviceId.equals(s.split("-")[2])) {

                                        session.createLoginSession(stUname, stPwd, profileid);
                                        progressDialog.dismiss();
                                        editor.putString("city", city);
                                        editor.putBoolean("debugLogs", debugLogs);
                                        editor.commit();

                                /*Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                                serviceIntent.setPackage(MainActivity.this.getPackageName());
                                startService(serviceIntent);*/

                                        //(new Intent(getBaseContext(), OnClearFromRecentService.class));

                                        getCancelData();

                                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {

                                        //Toast.makeText(MainActivity.this, "Login from wrong device!", Toast.LENGTH_SHORT).show();

                                        activateLogin();

                                    }
                                }
                            }
                            else
                            {
                                if(response.message().equals("Version mismatched"))
                                {

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_update, null);

                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    TextView tvOk=(TextView)dialogView.findViewById(R.id.au_bt_ok);
                                    tvOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter valid details ! "+response.message(), Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            Toast.makeText(MainActivity.this,"Check Network Connection !",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                //System.out.println("IMIE number is "+telephonyManager.getDeviceId()+"::"+session.isLoggedIn());
                deviceId=telephonyManager.getDeviceId();

                checkIfLoggedIn();

            } else {
                Toast.makeText(MainActivity.this, "Permission is required for this app to run !", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public void selectServiceLocations()
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_locations, null);

        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        tvLoc1=(TextView)dialogView.findViewById(R.id.ale_tv_loc1);
        tvLoc2=(TextView)dialogView.findViewById(R.id.ale_tv_loc2);
        tvLoc3=(TextView)dialogView.findViewById(R.id.ale_tv_loc3);
        tvLoc4=(TextView)dialogView.findViewById(R.id.ale_tv_loc4);
        tvLoc5=(TextView)dialogView.findViewById(R.id.ale_tv_loc5);

        tvLoc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc1.setTextColor(Color.parseColor("#000000"));


                city=tvLoc1.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();
                alertDialog.dismiss();
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        tvLoc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc2.setTextColor(Color.parseColor("#000000"));

                city=tvLoc2.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        tvLoc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc3.setTextColor(Color.parseColor("#000000"));


                city=tvLoc3.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        tvLoc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc4.setTextColor(Color.parseColor("#000000"));


                city=tvLoc4.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();


                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        tvLoc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc5.setTextColor(Color.parseColor("#000000"));


                city=tvLoc5.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });



        Call<List<ServiceLocationPojo>> call=REST_CLIENT.getServiceLocations(companyId);
        call.enqueue(new Callback<List<ServiceLocationPojo>>() {
            @Override
            public void onResponse(Call<List<ServiceLocationPojo>> call, Response<List<ServiceLocationPojo>> response) {

                ServiceLocationPojo data;
                List<ServiceLocationPojo> dataList;

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    for(int i=0;i<dataList.size();i++)
                    {
                        data=dataList.get(i);
                        switch (j)
                        {
                            case 0:tvLoc1.setText(data.getLocation());
                                tvLoc1.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 1:tvLoc2.setText(data.getLocation());
                                tvLoc2.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 2:tvLoc3.setText(data.getLocation());
                                tvLoc3.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 3:tvLoc4.setText(data.getLocation());
                                tvLoc4.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 4:tvLoc5.setText(data.getLocation());
                                tvLoc5.setVisibility(View.VISIBLE);
                                j++;
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ServiceLocationPojo>> call, Throwable t) {

                Toast.makeText(MainActivity.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    public void getCancelData()
    {
        //System.out.println("getting Canel data");

        Call<ArrayList<CancelData>> call=REST_CLIENT.getCancelList(companyId,"driver");
        call.enqueue(new Callback<ArrayList<CancelData>>() {
            @Override
            public void onResponse(Call<ArrayList<CancelData>> call, Response<ArrayList<CancelData>> response) {

                ArrayList<CancelData> cdList=new ArrayList<CancelData>();
                CancelData cd;
                if(response.isSuccessful())
                {
                    cdList=response.body();
                    editor.putBoolean("cancelOptions",false);
                    editor.commit();

                    dbAdapter.deleteCancelData();

                    for(int i=0;i<cdList.size();i++)
                    {
                        cd=cdList.get(i);

                        dbAdapter.insertCancelOptions(cd.getReason(),cd.getId());

                        //System.out.println("****** "+cd.getReason());

                    }
                    System.out.println("cancel done !!!!!!!!!!!");

                }
            }

            @Override
            public void onFailure(Call<ArrayList<CancelData>> call, Throwable t) {

            }
        });
    }

    public void activateLogin()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_profile_active, null);

        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        Button btOk=(Button)dialogView.findViewById(R.id.apa_bt_ok);
        Button btCancel=(Button)dialogView.findViewById(R.id.apa_bt_cancel);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsonObject v=new JsonObject();
                v.addProperty("profileid",profileid);
                v.addProperty("companyid",companyId);
                v.addProperty("imei",deviceId);

                Call<Pojo> call2=REST_CLIENT.sendIMIEnumber(v);
                call2.enqueue(new Callback<Pojo>() {
                    @Override
                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                        alertDialog.dismiss();

                        if(response.isSuccessful())
                        {
                            session.createLoginSession(stUname, stPwd, profileid);
                            editor.putString("city", city);
                            editor.putBoolean("debugLogs", debugLogs);
                            editor.commit();

                                /*Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                                serviceIntent.setPackage(MainActivity.this.getPackageName());
                                startService(serviceIntent);*/

                            //(new Intent(getBaseContext(), OnClearFromRecentService.class));

                            getCancelData();

                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                    }
                });
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                etUname.setText("");
                etPwd.setText("");
            }
        });
    }

    public void checkIfLoggedIn()
    {

        if(session.checkLogin())
        {
            Log.w("data", "Login done");

            stName=user.get(SessionManager.KEY_NAME);
            stPd=user.get(SessionManager.KEY_PWD);

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JsonObject v=new JsonObject();
            v.addProperty("login",stName);
            v.addProperty("pwd",stPd);
            v.addProperty("companyid",companyId);
            v.addProperty("version",version);
            v.addProperty("profileid","-");

            Call<Pojo> call=REST_CLIENT.validateLogin(v);
            call.enqueue(new Callback<Pojo>() {
                @Override
                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                    progressDialog.dismiss();

                    String s;
                    Pojo data;


                    if(response.isSuccessful())
                    {

                        data=response.body();
                        s=data.getMessage();

                        editor.putString("status","online");
                        editor.putBoolean("debugLogs",debugLogs);
                        editor.commit();

                        //System.out.println("!!!!!!!!!!!"+pref.getBoolean("cancelOptions",true));

                        /*if(pref.getBoolean("cancelOptions",true))
                        {
                            getCancelData();
                        }*/

                        //System.out.println("s is "+s+"::");
                        profileid=s.split("-")[0];

                        //System.out.println("imie number is "+s.split("-")[1]+"::"+s.split("-").length);

                        Loggly.i("MainActivity",profileid+" Login-Session stored.");

                        if(s.split("-").length==2)
                        {
                            JsonObject v=new JsonObject();
                            v.addProperty("profileid",profileid);
                            v.addProperty("companyid",companyId);
                            v.addProperty("imei",deviceId);
                            Call<Pojo> call2=REST_CLIENT.sendIMIEnumber(v);
                            call2.enqueue(new Callback<Pojo>() {
                                @Override
                                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                    if(response.isSuccessful())
                                    {
                                        //System.out.println("success .. imei");
                                        getCancelData();
                       /* else {
                            ArrayList<String> a=dbAdapter.getCancelOptions();

                            for(int i=0;i<a.size();i++)
                            {
                                System.out.println("&&&&&&&&&&& "+a.get(i));
                            }
                        }*/
                        /*Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                        serviceIntent.setPackage(MainActivity.this.getPackageName());
                        startService(serviceIntent);*/

                                        //startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

                                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Pojo> call, Throwable t) {

                                }
                            });
                        }
                        else {

                            if (deviceId.equals(s.split("-")[2])) {

                                getCancelData();
                       /* else {
                            ArrayList<String> a=dbAdapter.getCancelOptions();

                            for(int i=0;i<a.size();i++)
                            {
                                System.out.println("&&&&&&&&&&& "+a.get(i));
                            }
                        }*/
                        /*Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                        serviceIntent.setPackage(MainActivity.this.getPackageName());
                        startService(serviceIntent);*/

                                //startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {

                                Toast.makeText(MainActivity.this, "Profile is active in another device!\nLogin to deactivate it.", Toast.LENGTH_LONG).show();
                                session.logoutUser();
                            }
                        }
                    }
                    else {

                        if(response.message().equals("Version mismatched"))
                        {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_update, null);

                            dialogBuilder.setView(dialogView);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);

                            TextView tvOk=(TextView)dialogView.findViewById(R.id.au_bt_ok);
                            tvOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    alertDialog.dismiss();
                                    finish();
                                }
                            });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Enter valid details ! "+response.message(), Toast.LENGTH_LONG).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<Pojo> call, Throwable t) {

                    progressDialog.dismiss();

                    Toast.makeText(MainActivity.this,"Please check Internet connection!",Toast.LENGTH_LONG).show();
                    finish();

                }
            });
        }
    }


}
