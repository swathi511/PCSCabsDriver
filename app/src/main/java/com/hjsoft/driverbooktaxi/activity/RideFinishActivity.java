package com.hjsoft.driverbooktaxi.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.DrawerItemCustomAdapter;
import com.hjsoft.driverbooktaxi.fragments.RideInvoiceFragment;
import com.hjsoft.driverbooktaxi.fragments.ShowRequestsFragment;
import com.hjsoft.driverbooktaxi.model.NavigationData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 24/1/17.
 */
public class RideFinishActivity extends AppCompatActivity{

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter adapter;
    SessionManager session;
    String companyId="CMP00001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        session=new SessionManager(getApplicationContext());

        setupToolbar();

        NavigationData[] drawerItem = new NavigationData[3];
        drawerItem[0] = new NavigationData(R.drawable.car, "On Duty");
        drawerItem[1] = new NavigationData(R.drawable.history, "All Rides");
        drawerItem[2] = new NavigationData(R.drawable.logout,"Logout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setupDrawerToggle();

        Fragment fragment=new RideInvoiceFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment,"ride_in_progress").commit();
        setTitle("Ride Invoice");

        adapter.setSelectedItem(-1);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {


                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    mDrawerToggle.setDrawerIndicatorEnabled(false);//showing back button
                    setTitle("Confirm Ride");

                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // onBackPressed();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag("confirm_ride"));
                            fragmentManager.popBackStackImmediate();
                        }
                    });
                }
                else
                {
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    setTitle("Book a Ride");
                }
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;
        adapter.setSelectedItem(position);

        switch (position) {
            case 0:
                Intent k=new Intent(RideFinishActivity.this,HomeActivity.class);
                startActivity(k);
                finish();
                break;
            case 1:
                Intent i=new Intent(RideFinishActivity.this,AllRidesActivity.class);
                startActivity(i);
                finish();
                break;
            case 2:
                //  fragment = new ProfileFragment();
                mDrawerLayout.closeDrawer(mDrawerList);
                sendLogoutStatus();
                break;

            default:
                break;
        }

        if (fragment != null) {

            openFragment(fragment,position);

        } else {
           //Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void openFragment(Fragment fragment,int position){

        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (containerFragment.getClass().getName().equalsIgnoreCase(fragment.getClass().getName())) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }

        else{

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
       // session.logoutUser();
        sendLogoutStatus();
    }

    public void sendLogoutStatus()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RideFinishActivity.this);

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
}

