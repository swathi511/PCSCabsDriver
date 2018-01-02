package com.hjsoft.driverbooktaxi.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.hjsoft.driverbooktaxi.DatabaseHandler;
import com.hjsoft.driverbooktaxi.model.LocationUpdates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hjsoft on 2/12/16.
 */
public class DBAdapter {

    static final String DATABASE_NAME = "user.db";
    static final int DATABASE_VERSION = 50;
    public static final int NAME_COLUMN = 1;

    public static final String DB_CREATE_LATLNG = "create table if not exists "+"RIDE_LATLNG"+
            "( " +"REQUEST_ID text,LATITUDE  double,LONGITUDE double,PLACE text,CUM_DISTANCE integer,TIME_UPDATED text); ";

    public static final String DB_CREATE_LOC_UPDATES="create table if not exists "+"LOC_UPDATES"+
            "( " +"REQUEST_ID  text,STARTING_TIME text,STOPPING_TIME text,DISTANCE integer,LATITUDE double,LONGITUDE double" +
            ",P_LAT double,P_LNG double,D_LAT double,D_LNG double,GUEST_NAME text,GUEST_MOBILE text,IDLE_TIME integer);";

    public static final String DB_ONGOING_RIDE="create table if not exists ONGOING_RIDE( REQUEST_ID text,STATUS text);";

    public static final String DB_NETWORK_ISSUE="create table if not exists NETWORK_ISSUE(REQUEST_ID text,TIME_STAMP text);";




    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DatabaseHandler dbHelper;

    public  DBAdapter(Context _context)
    {
        context = _context;
        dbHelper = new DatabaseHandler(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public  DBAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public void insertEntry(String requestId,double latitude,double longitude,String place,long cum_distance,String time_updated)
    {
        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+latitude+longitude);
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("REQUEST_ID",requestId);
        newValues.put("LATITUDE",latitude);
        newValues.put("LONGITUDE",longitude);
        newValues.put("PLACE",place);
        newValues.put("CUM_DISTANCE",cum_distance);
        newValues.put("TIME_UPDATED",time_updated);
        // Insert the row into your table
        db.insert("RIDE_LATLNG", null, newValues);
        //  close();

        //  System.out.println("Value inserted");

    }

    public String getWaypoints(String requestId)
    {
        String waypoints="";

        String st = "";
        db = dbHelper.getReadableDatabase();
        float[] dist=new float[3];
        long distance=0;

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor c=db.rawQuery(sql,null);

        int n;

        //System.out.println("count is "+c.getCount());

        /*if(c.getCount()<5)
        {
            n=1;
        }
        else {
            n=c.getCount()/5;

            //System.out.println("n is "+n);
        }*/

        if(c.getCount()<10)
        {
            n=1;
        }
        else if(c.getCount()>10&&c.getCount()<20)
        {
            n=2;
        }
        else {
            n=c.getCount()/10;

            //System.out.println("n is "+n);
        }

        /*
        for(int i=0;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

            if(i==0)
            {
                waypoints=c.getString(0)+","+c.getString(1);
            }
            else {
                waypoints=waypoints+"|"+c.getString(0)+","+c.getString(1);
            }
        }
        */

        /*for(int i=0;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

            if(i==0)
            {

            }
            else if(i==1){

                waypoints=c.getString(1)+","+c.getString(2);
            }
            else {

                if(c.isLast())
                {

                }
                else {
                    waypoints = waypoints + "|" + c.getString(1) + "," + c.getString(2);
                }
            }
        }*/

        //System.out.println("data is "+c.getCount()+":"+n);

        for(int i=0;(n*i)<c.getCount();i++)
        {
            //System.out.println("*********** "+(n*i));
            c.moveToPosition(n*i);

            if(i==0)
            {
                waypoints=c.getString(1)+","+c.getString(2);
            }
            else {

                waypoints = waypoints + "|" + c.getString(1) + "," + c.getString(2);
            }
        }

        return waypoints;
    }

    public String getWaypointsForOutstation(String requestId)
    {
        //System.out.println("Waypoints for outstationnnnnnnnnnnnnnnn");
        String waypoints="";

        String st = "";
        db = dbHelper.getReadableDatabase();
        float[] dist=new float[3];
        long distance=0;

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor c=db.rawQuery(sql,null);

        int n;

        //System.out.println("count is "+c.getCount());

        /*if(c.getCount()<5)
        {
            n=1;
        }
        else {
            n=c.getCount()/5;

            //System.out.println("n is "+n);
        }*/

        if(c.getCount()<15)
        {
            n=1;
        }
        else if(c.getCount()>15&&c.getCount()<30)
        {
            n=2;
        }
        else {
            n=c.getCount()/15;
            n=n+1;

            //System.out.println("n is "+n);
        }

        /*
        for(int i=0;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

            if(i==0)
            {
                waypoints=c.getString(0)+","+c.getString(1);
            }
            else {
                waypoints=waypoints+"|"+c.getString(0)+","+c.getString(1);
            }
        }
        */

        /*for(int i=0;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

            if(i==0)
            {

            }
            else if(i==1){

                waypoints=c.getString(1)+","+c.getString(2);
            }
            else {

                if(c.isLast())
                {

                }
                else {
                    waypoints = waypoints + "|" + c.getString(1) + "," + c.getString(2);
                }
            }
        }*/

        // System.out.println("data is "+c.getCount()+":"+n);
        for(int i=0;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

           //System.out.println("****** "+c.getDouble(1)+","+c.getDouble(2));

            if(i==0)
            {

            }
            else if(i==1)
            {
                if(c.isLast())
                {

                }
                else {

                    waypoints = c.getDouble(1) + "," + c.getDouble(2);
                }
            }
            else {

                if(c.isLast())
                {

                }
                else {

                    waypoints = waypoints + "|" + c.getDouble(1) + "," + c.getDouble(2);
                }
            }
        }

        c.close();

        return waypoints;
    }


    public String getRideDetails(String requestId)
    {
        String st="";
        db=dbHelper.getReadableDatabase();

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor cursor=db.rawQuery(sql,null);
        JSONObject jobj ;
        JSONArray arr = new JSONArray();


        String data="";
        while (cursor.moveToNext())
        {
            if(cursor.isFirst())
            {
                data=data+cursor.getDouble(1) + "," + cursor.getDouble(2);
            }
            else {
                data=data+"*"+cursor.getDouble(1) + "," + cursor.getDouble(2);
            }
        }

        // System.out.println("**** data is "+data);
        /*


         */


        /*while(cursor.moveToNext()) {
            try {
                jobj = new JSONObject();
                jobj.put("Latitude", cursor.getString(0));
                jobj.put("Longitude", cursor.getString(1));
                jobj.put("Place",cursor.getString(2));
                jobj.put("Cumulative_Distance (meters)",cursor.getString(3));
                jobj.put("Time_Updated", cursor.getString(4));
                //System.out.println("lat "+cursor.getString(0)+" long "+cursor.ge  tString(1)+" place "+cursor.getString(2)+" distance "+cursor.getString(3)+" time"+cursor.getString(4));
                arr.put(jobj);
            }
            catch (JSONException e){e.printStackTrace();}
        }
        try{
            jobj = new JSONObject();
            jobj.put("data", arr);
            st=jobj.toString();
        }catch(JSONException e){e.printStackTrace();}*/

        cursor.close();

        return  data;
    }


    public long getDistance(String requestId)
    {
        String st = "";
        db = dbHelper.getReadableDatabase();
        float[] dist=new float[3];
        long distance=0;

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor c1=db.rawQuery(sql,null);
        Cursor c2=db.rawQuery(sql,null);
        JSONObject jobj ;
        JSONArray arr = new JSONArray();

        int count=c1.getCount();

        int k=1;

        //System.out.println("the count is "+count);

        for(int i=0;k<count;i++)
        {
            c1.moveToPosition(i);
            c2.moveToPosition(i+1);

            Location.distanceBetween(Double.parseDouble(c1.getString(1)),Double.parseDouble(c1.getString(2))
                    ,Double.parseDouble(c2.getString(1)),Double.parseDouble(c2.getString(2)),dist);

            //System.out.println(c1.getString(0)+":"+c1.getString(1)+":"+c2.getString(0)+":"+c2.getString(1)+":"+dist[0]);

            distance=distance+(long)dist[0];

            // System.out.println("distance "+distance);

            k++;
        }

        return distance;
    }

    public void deleteRideDetails(String requestId)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'" );
        db.close();
    }

    public void insertLocEntry(String requestId,String startingTime,String stoppingTime,long distance,double latitude,double longitude,double pLat,double pLng,double dLat,double dLng,String guestName,String guestMobile,long idleTime)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("REQUEST_ID",requestId);
        newValues.put("STARTING_TIME",startingTime);
        newValues.put("STOPPING_TIME",stoppingTime);
        newValues.put("DISTANCE",distance);
        newValues.put("LATITUDE",latitude);
        newValues.put("LONGITUDE",longitude);
        newValues.put("P_LAT",pLat);
        newValues.put("P_LNG",pLng);
        newValues.put("D_LAT",dLat);
        newValues.put("D_LNG",dLng);
        newValues.put("GUEST_NAME",guestName);
        newValues.put("GUEST_MOBILE",guestMobile);
        newValues.put("IDLE_TIME",idleTime);
        // Assign values for each row.

        // Insert the row into your table
        db.insert("LOC_UPDATES", null, newValues);
        //  close();
        // System.out.println("Value inserted");
    }

    public void updateLocEntry(String requestId,String stoppingTime,long distance,double latitude,double longitude,long idleTime)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("STOPPING_TIME",stoppingTime);
        newValues.put("DISTANCE",distance);
        newValues.put("LATITUDE",latitude);
        newValues.put("LONGITUDE",longitude);
        newValues.put("IDLE_TIME",idleTime);
        // Assign values for each row.

        // Insert the row into your table
        db.update("LOC_UPDATES",newValues,"REQUEST_ID="+" '"+requestId+"' ", null);
        //  close();
        //  System.out.println("Value updated");
    }

    public boolean findRequestId(String requestId){

        boolean flag=false;

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM LOC_UPDATES WHERE REQUEST_ID ="+" '"+requestId+"' ",null);
        String status="";

        if(c.getCount()>0) {

            flag=true;
        }
        c.close();
        // close();
        return flag;
    }


    public ArrayList<LocationUpdates> getAllLocUpdates(String requestId){

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM LOC_UPDATES WHERE REQUEST_ID ="+" '"+requestId+"' ",null);
        ArrayList<LocationUpdates> dataList=new ArrayList<LocationUpdates>();
        LocationUpdates data;

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();
                data=new LocationUpdates(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getDouble(4),c.getDouble(5),
                        Double.parseDouble(c.getString(6)),Double.parseDouble(c.getString(7)),Double.parseDouble(c.getString(8)),Double.parseDouble(c.getString(9)),c.getString(10),c.getString(11),c.getString(12));
                dataList.add(data);
            }
        }
        c.close();
        // close();
        return dataList;
    }

    public void deleteLocUpdates(String requestId)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from LOC_UPDATES where REQUEST_ID="+" '"+requestId+"'" );
    }

    public void insertRideStatus(String requestId,String status)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("REQUEST_ID",requestId);
        newValues.put("STATUS",status);
        // Assign values for each row.

        // Insert the row into your table
        db.insert("ONGOING_RIDE", null, newValues);
        //  close();
        //  System.out.println("Value inserted");
    }

    public void deleteRideStatus(String requestId)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from ONGOING_RIDE where REQUEST_ID="+" '"+requestId+"'" );
        db.close();
    }

    public boolean getRideStaus()
    {
        boolean flag=false;

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM ONGOING_RIDE",null);

        if(c.getCount()>0) {

            flag=true;
        }
        c.close();

        return flag;
    }

    public String getFirstHalfWaypoints(String requestId)
    {
        String waypoints="";
        db = dbHelper.getReadableDatabase();

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor c=db.rawQuery(sql,null);
        int w=c.getCount()/2;

        int n;

        if(w<15)
        {
            n=1;
        }
        else if(w>15&&w<30)
        {
            n=2;
        }
        else {
            n=w/15;
            n=n+1;
        }

        for(int i=0;(n*i)<w;i++)
        {
            c.moveToPosition(n*i);

            if(i==0)
            {

            }
            else if(i==1)
            {
                if(c.isLast())
                {

                }
                else {

                    waypoints = c.getDouble(1) + "," + c.getDouble(2);
                }
            }
            else {

                if(c.isLast())
                {

                }
                else {

                    waypoints = waypoints + "|" + c.getDouble(1) + "," + c.getDouble(2);
                }
            }
        }

        c.close();

        return waypoints;
    }

    public String getNextHalfWaypoints(String requestId)
    {
        String waypoints="";
        db = dbHelper.getReadableDatabase();

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor c=db.rawQuery(sql,null);
        int w=c.getCount()/2;

        int n;

        if(w<15)
        {
            n=1;
        }
        else if(w>15&&w<30)
        {
            n=2;
        }
        else {
            n=w/15;
            n=n+1;
        }

        int v=w/n;

        for(int i=v;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

            if(i==v)
            {

            }
            else if(i==v+1)
            {
                if(c.isLast())
                {

                }
                else {

                    waypoints = c.getDouble(1) + "," + c.getDouble(2);
                }
            }
            else {

                if(c.isLast())
                {

                }
                else {

                    waypoints = waypoints + "|" + c.getDouble(1) + "," + c.getDouble(2);
                }
            }
        }

        c.close();

        return waypoints;
    }

    public String getMidCoordinate(String requestId)
    {
        String midpoint="";
        db = dbHelper.getReadableDatabase();

        String sql="SELECT * FROM RIDE_LATLNG where REQUEST_ID="+" '"+requestId+"'";
        Cursor c=db.rawQuery(sql,null);
        int w=c.getCount()/2;

        w=w-1;
        c.moveToPosition(w);
        midpoint=c.getDouble(1)+","+c.getDouble(2);

        return  midpoint;
    }

    public void insertNetworkIssueData(String requestId,String timeStamp)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("REQUEST_ID",requestId);
        newValues.put("TIME_STAMP",timeStamp);
        // Assign values for each row.

        // Insert the row into your table
        db.insert("NETWORK_ISSUE", null, newValues);
        //  close();
        //  System.out.println("Value inserted");
    }

    public String getNetworkIssueData(String requestId)
    {
        String stNetworkData="NA";

        db=dbHelper.getReadableDatabase();
        String sql="SELECT * FROM NETWORK_ISSUE where REQUEST_ID="+" '"+requestId+"'";
        Cursor c=db.rawQuery(sql,null);

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();

                if(i==0)
                {
                    stNetworkData=c.getString(1);
                }
                else {

                    stNetworkData = stNetworkData + " * " + c.getString(1);
                }
            }
        }

        c.close();


        return stNetworkData;
    }

    public void deleteNetworkIssueData(String requestId)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from NETWORK_ISSUE where REQUEST_ID="+" '"+requestId+"'" );
        db.close();
    }

}
