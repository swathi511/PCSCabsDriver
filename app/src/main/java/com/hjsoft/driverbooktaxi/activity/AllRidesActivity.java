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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.adapter.BookingHistoryRecyclerAdapter;
import com.hjsoft.driverbooktaxi.adapter.DBAdapter;
import com.hjsoft.driverbooktaxi.adapter.DrawerItemCustomAdapter;
import com.hjsoft.driverbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.driverbooktaxi.fragments.AllRidesFragment;
import com.hjsoft.driverbooktaxi.fragments.BookingHistoryFragment;
import com.hjsoft.driverbooktaxi.fragments.ShowRequestsFragment;
import com.hjsoft.driverbooktaxi.fragments.SpecificRideFragment;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.driverbooktaxi.model.GuestData;
import com.hjsoft.driverbooktaxi.model.LocationUpdates;
import com.hjsoft.driverbooktaxi.model.NavigationData;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 21/1/17.
 */
public class AllRidesActivity extends AppCompatActivity implements RecyclerAdapter.AdapterCallback {

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

        Fragment fragment=new AllRidesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment,"all_rides").commit();
        Bundle b=getIntent().getExtras();
        String s=b.getString("dateValue","Booking History");

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String format = new SimpleDateFormat("dd MMM, yyyy",Locale.ENGLISH).format(dateFormat.parse(s));

            setTitle(format);
        }
        catch (ParseException e)
        {
            e.printStackTrace();

            setTitle(s);
        }


        adapter.setSelectedItem(-1);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {


                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    mDrawerToggle.setDrawerIndicatorEnabled(false);//showing back button

                    setTitle("Ride Details");

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
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    Bundle b=getIntent().getExtras();
                    String s=b.getString("dateValue","Booking History");
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String format = new SimpleDateFormat("dd MMM, yyyy",Locale.ENGLISH).format(dateFormat.parse(s));

                        setTitle(format);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();

                        setTitle(s);
                    }

                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                            Intent i=new Intent(AllRidesActivity.this,BookingHistoryActivity.class);
//                            startActivity(i);
                            finish();
                        }
                    });
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
                Intent i=new Intent(AllRidesActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
                break;
            case 1:
                Intent j=new Intent(AllRidesActivity.this,AllRidesActivity.class);
                startActivity(j);
                finish();
                break;
            case 2:
                System.out.println("checkig");
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
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(AllRidesActivity.this,BookingHistoryActivity.class);
//                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        sendLogoutStatus();
    }


    @Override
    public void onMethodCallback(int position, ArrayList<FormattedAllRidesData> data) {

        System.out.println("ALLRIDESACTIVITY CALLED"+data.size());

        //position=data.size()-position-1;
        //this.position=position;

        System.out.println("possssssss "+position);

        ArrayList<FormattedAllRidesData> list=data;
        FormattedAllRidesData item;
        DBAdapter dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        item=list.get(position);

        String status=item.getRideStatus();

        if(status.equals("CANCELLED"))
        {

        }
        else {

            if(status.equals("COMPLETED")) {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putSerializable("list", data);

                Fragment frag = new SpecificRideFragment();
                frag.setArguments(args);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, frag, "specific_ride");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else
            {
                //if(item.getTravelType().equals("local")||item.getTravelType().equals("Packages")) {
                if(item.getTravelType().equals("local")) {

                    ArrayList<LocationUpdates> dataList=dbAdapter.getAllLocUpdates(item.getRequestId());

                    if(dataList.size()==0)
                    {
                        if(item.getOtpStatus().equals("1")||item.getOtpStatus().equals("True"))
                        {
                            Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(item.getRequestId(), item.getGuestProfileId(), item.getGuestName(), item.getGuestMobile(), item.getPickupLat(), item.getPickupLong(),
                                    item.getDropLat(), item.getDropLong(), item.getFromLocation(), item.getToLocation(), item.getTravelType(), item.getTravelPackage(), "", "", "", item.getBookingType(),item.getPaymentMode(),item.getOtherCharges(),"","",""));

                            Intent i = new Intent(AllRidesActivity.this, RideLocal.class);
                            i.putExtra("cabData", cabData);
                            startActivity(i);
                            finish();
                        }
                        else {

                            Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(item.getRequestId(), item.getGuestProfileId(), item.getGuestName(), item.getGuestMobile(), item.getPickupLat(), item.getPickupLong(),
                                    item.getDropLat(), item.getDropLong(), item.getFromLocation(), item.getToLocation(), item.getTravelType(), item.getTravelPackage(),"", "", "", item.getBookingType(),item.getPaymentMode(),item.getOtherCharges(),"","",""));

                            Intent i = new Intent(AllRidesActivity.this, TrackRideActivity.class);
                            i.putExtra("cabData", cabData);
                            startActivity(i);
                            finish();
                        }

                    }
                    else {

                        Intent i = new Intent(AllRidesActivity.this, RideOngoingLocal.class);
                        i.putExtra("list", data);
                        i.putExtra("position", position);
                        startActivity(i);
                        finish();
                    }
                }
                else
                {
                    ArrayList<LocationUpdates> dataList=dbAdapter.getAllLocUpdates(item.getRequestId());

                    if(dataList.size()==0)
                    {
                        if(item.getOtpStatus().equals("1")||item.getOtpStatus().equals("True"))
                        {
                            Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(item.getRequestId(), item.getGuestProfileId(), item.getGuestName(), item.getGuestMobile(), item.getPickupLat(), item.getPickupLong(),
                                    item.getDropLat(), item.getDropLong(), item.getFromLocation(), item.getToLocation(), item.getTravelType(), item.getTravelPackage(), "", "", "", item.getBookingType(),item.getPaymentMode(),item.getOtherCharges(),"","",""));

                            Intent i = new Intent(AllRidesActivity.this, RideOutstation.class);
                            i.putExtra("cabData", cabData);
                            startActivity(i);
                            finish();
                        }
                        else {

                            Toast.makeText(AllRidesActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                            ArrayList<GuestData> cabData = new ArrayList<>();
                            cabData.add(new GuestData(item.getRequestId(), item.getGuestProfileId(), item.getGuestName(), item.getGuestMobile(), item.getPickupLat(), item.getPickupLong(),
                                    item.getDropLat(), item.getDropLong(), item.getFromLocation(), item.getToLocation(), item.getTravelType(), item.getTravelPackage(),"", "", "", item.getBookingType(),item.getPaymentMode(),item.getOtherCharges(),"","",""));

                            Intent i = new Intent(AllRidesActivity.this, OutStationTrackRideActivity.class);
                            i.putExtra("cabData", cabData);
                            startActivity(i);
                            finish();
                        }

                    }
                    else {

                        Intent i = new Intent(AllRidesActivity.this,RideOngoingOutstation.class);
                        i.putExtra("list", data);
                        i.putExtra("position", position);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }


    }

    public void sendLogoutStatus()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AllRidesActivity.this);

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
            }
        });
    }

}
