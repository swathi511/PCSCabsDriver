package com.hjsoft.driverbooktaxi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
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
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.model.ServiceLocationPojo;
import com.hjsoft.driverbooktaxi.service.OnClearFromRecentService;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.text.SimpleDateFormat;
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
    //String version="4.5";
    String version="1";//20
    String city="Visakhapatnam";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("data", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }


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

                    if(response.isSuccessful())
                    {
                        editor.putString("status","online");
                        editor.commit();

                        /*Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                        serviceIntent.setPackage(MainActivity.this.getPackageName());
                        startService(serviceIntent);*/

                       startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
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

                            if(response.isSuccessful())
                            {
                                data=response.body();
                                s=data.getMessage();
                                String profileid=s.split("-")[0];

                                session.createLoginSession(stUname,stPwd,profileid);
                                progressDialog.dismiss();
                                editor.putString("city",city);
                                editor.commit();

                                Intent serviceIntent = new Intent(MainActivity.this, OnClearFromRecentService.class);
                                serviceIntent.setPackage(MainActivity.this.getPackageName());
                                startService(serviceIntent);

                                //(new Intent(getBaseContext(), OnClearFromRecentService.class));

                                Intent i=new Intent(MainActivity.this,HomeActivity.class);
                                startActivity(i);
                                finish();
//
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

                Toast.makeText(MainActivity.this,"Check Internet COnnection",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
