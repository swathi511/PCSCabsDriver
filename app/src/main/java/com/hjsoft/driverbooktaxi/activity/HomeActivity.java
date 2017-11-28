package com.hjsoft.driverbooktaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.adapter.DrawerItemCustomAdapter;
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
 * Created by hjsoft on 26/12/16.
 */
public class HomeActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter adapter;
    SessionManager session;
    DBAdapter dbAdapter;
    String companyId="CMP00001";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



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

        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

       /* if(dbAdapter.getRideStaus())
        {
           Intent i=new Intent(HomeActivity.this,AllRidesActivity.class);
            startActivity(i);
            finish();
        }*/

        // else {

        Fragment fragment = new ShowRequestsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment, "on_duty").commit();
        setTitle("On Duty");
        //}
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
                break;
            case 1:
                Intent i=new Intent(HomeActivity.this,AllRidesActivity.class);
                startActivity(i);
                finish();
                break;
            case 2:
                mDrawerLayout.closeDrawer(mDrawerList);

                sendLogoutStatus();
                break;

            default:
                break;
        }

        if (fragment != null) {

            openFragment(fragment,position);

        } else {
            // Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void openFragment(Fragment fragment,int position){

        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (containerFragment.getClass().getName().equalsIgnoreCase(fragment.getClass().getName())) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }

        else
        {
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

        //session.logoutUser();
        sendLogoutStatus();

        // super.onBackPressed();
    }

    public void sendLogoutStatus()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);

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
                        else {

                            Toast.makeText(HomeActivity.this,"Unknown error! Please try again!",Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                       Toast.makeText(HomeActivity.this,"No Internet Connection!\nPlease try again.",Toast.LENGTH_LONG).show();
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


    public void enableDisableDrawer(int mode) {
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(mode);
        }
    }


    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            System.out.println("*********************** KEYCODE_HOME");
            Toast.makeText(HomeActivity.this,"HOme clicked !!!",Toast.LENGTH_SHORT).show();


            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("KEYCODE_BACK");

            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_MENU)) {
            System.out.println("KEYCODE_MENU");

            return true;
        }
        return false;
    }*/

//    public void onUserLeaveHint() { // this only executes when Home is selected.
//        // do stuff
//        super.onUserLeaveHint();
//        System.out.println("HOMEEE");
//        sendLogoutStatus();
//
//    }

}
