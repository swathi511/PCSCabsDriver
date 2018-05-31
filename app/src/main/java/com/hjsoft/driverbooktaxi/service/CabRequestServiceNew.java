package com.hjsoft.driverbooktaxi.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonElement;
import com.hjsoft.driverbooktaxi.Constants;
import com.hjsoft.driverbooktaxi.SessionManager;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.webservices.API;
import com.hjsoft.driverbooktaxi.webservices.RestClient;
import com.inrista.loggliest.Loggly;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 10/4/18.
 */
public class CabRequestServiceNew extends Service {

    public static final String ACTION = "com.hjsoft.driverbooktaxi.service.cabrequestservicenew";
    API REST_CLIENT;
    Handler handler;
    Runnable r;

    HashMap<String, String> user;
    SessionManager session;
    String stProfileId;
    PubNub pubnub;

    @Override
    public void onCreate() {
        super.onCreate();

        //System.out.println("New Srevice onCreate called!!!!");

        REST_CLIENT= RestClient.get();
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        stProfileId=user.get(SessionManager.KEY_PROFILE_ID);
        //getDetails();
        /*try {
            initAbly(stProfileId);
        }
        catch (AblyException e)
        {
            e.printStackTrace();
        }*/

        //System.out.println("profileid in srevice new is "+stProfileId);

        initPubNub(stProfileId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //public CabRequestService() {
    // super("test-service");
    //}

    /*@Override
    public void onHandleIntent(Intent intent) {
        // Fetch data passed into the intent on start
        //String val = intent.getStringExtra("foo");
        // Construct an Intent tying it to the ACTION (arbitrary event namespace)

        System.out.println("on handling intent is being called");

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(r,10000);

                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");


            *//*    Call<List<CabRequestsPojo>> call = REST_CLIENT.getCabRequests("CMP00001/PID0076", "CMP00001");
                call.enqueue(new Callback<List<CabRequestsPojo>>() {
                    @Override
                    public void onResponse(Call<List<CabRequestsPojo>> call, Response<List<CabRequestsPojo>> response) {

                        List<CabRequestsPojo> cList = new ArrayList<CabRequestsPojo>();
                        CabRequestsPojo c;

                        if (response.isSuccessful()) {
                            cList = response.body();
                            c = cList.get(0);

                            Intent in = new Intent(ACTION);
                            // Put extras into the intent as usual
                            in.putExtra("resultCode", Activity.RESULT_OK);
                            in.putExtra("resultValue", c.getBookingtype());

                            System.out.println("Booking type is "+c.getBookingtype());
                            // Fire the broadcast with intent packaged
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
                        }


                    }

                    @Override
                    public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

                    }
                });*//*

                // or sendBroadcast(in) for a normal broadcast;

            }
        };

        handler.post(r);
    }*/

    public void getDetails()
    {
        //System.out.println("on handling intent is being called");

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                //handler.postDelayed(r,10000);

                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@new");


                Call<List<CabRequestsPojo>> call = REST_CLIENT.getCabRequests(stProfileId,"CMP00001");
                //System.out.println("calling API here............");
                call.enqueue(new Callback<List<CabRequestsPojo>>() {
                    @Override
                    public void onResponse(Call<List<CabRequestsPojo>> call, Response<List<CabRequestsPojo>> response) {

                        List<CabRequestsPojo> cList = new ArrayList<CabRequestsPojo>();
                        CabRequestsPojo c;

                        //System.out.println("call is done here");

                        if (response.isSuccessful()) {
                            cList = response.body();
                            c = cList.get(0);

                            Intent in = new Intent(ACTION);
                            // Put extras into the intent as usual
                            in.putExtra("resultCode", Activity.RESULT_OK);
                            in.putExtra("resultValue", c.getBookingtype());

                            System.out.println("Booking type is "+c.getBookingtype());
                            // Fire the broadcast with intent packaged
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
                        }


                    }

                    @Override
                    public void onFailure(Call<List<CabRequestsPojo>> call, Throwable t) {

                        t.printStackTrace();

                        // System.out.println("on failure is being called");
                    }
                });

                // or sendBroadcast(in) for a normal broadcast;

            }
        };

        handler.post(r);

    }

    /*private void initAbly(String driverId) throws AblyException {

        System.out.println("ABLY IS INITIALISED!!!");
        System.out.println("driverId is "+driverId);


        AblyRealtime realtime = new AblyRealtime(API_KEY);

        channel = realtime.channels.get(driverId);
        //Toast.makeText(getBaseContext(), "Message received: " + messages.data, Toast.LENGTH_SHORT).show();
        PresenceMessage[] members = channel.presence.get();

        System.out.println("There are " + members.length + " members on this channel");

        for(int i=0;i<members.length;i++)
        {
            System.out.println("The first member has client ID: " + members[i].clientId);
        }


        channel.subscribe(new Channel.MessageListener() {

            @Override
            public void onMessage(final Message messages) {

                System.out.println("MESSAGE HERE ISSSSSSS "+messages.data.toString());

                if(messages.data.toString().equals("cab request"))
                {
                    //getDetails();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            getDetails();
                        }
                    });
                }
            }
        });
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(handler!=null) {
            handler.removeCallbacks(r);
        }

        if(pubnub!=null)
        {
            pubnub.removeListener(subscribeCallback);

            pubnub.unsubscribe();

            pubnub.destroy();
        }
    }

    private final void initPubNub(String driverId) {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        config.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        // config.setUuid(this.mUsername);
        config.setSecure(true);

        pubnub=new PubNub(config);

        pubnub.subscribe()
                .channels(Arrays.asList(driverId)) // subscribe to channels
                .execute();

        pubnub.addListener(subscribeCallback);

    }

    SubscribeCallback subscribeCallback=new SubscribeCallback()  {
        @Override
        public void status(PubNub pubnub, PNStatus status) {
           /* switch (status.getOperation()) {
                // let's combine unsubscribe and subscribe handling for ease of use
                case PNSubscribeOperation:
                case PNUnsubscribeOperation:
                    // note: subscribe statuses never have traditional
                    // errors, they just have categories to represent the
                    // different issues or successes that occur as part of subscribe*/

                    System.out.println("*******"+status.getCategory());

                    //Loggly.i("CabRequestServiceNew",stProfileId+" subscribe status "+status.getCategory());


                    switch (status.getCategory()) {
                        case PNConnectedCategory:
                            //Toast.makeText(MainActivity.this, "hey", Toast.LENGTH_SHORT).show();
                            // this is expected for a subscribe, this means there is no error or issue whatsoever
                            break;
                        case PNReconnectedCategory:
                            // this usually occurs if subscribe temporarily fails but reconnects. This means
                            // there was an error but there is no longer any issue
                            break;
                        case PNDisconnectedCategory:
                            // this is the expected category for an unsubscribe. This means there
                            // was no error in unsubscribing from everything
                            break;

                        case PNUnexpectedDisconnectCategory:

                            pubnub.reconnect();

                            break;
                        // this is usually an issue with the internet connection, this is an error, handle appropriately
                        case PNTimeoutCategory:

                            pubnub.reconnect();

                            break;
                        case PNAccessDeniedCategory:
                            // this means that PAM does allow this client to subscribe to this
                            // channel and channel group configuration. This is another explicit error
                            break;
                        default:
                            // More errors can be directly specified by creating explicit cases for other
                            // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                            break;
                    }

                /*case PNHeartbeatOperation:
                    // heartbeat operations can in fact have errors, so it is important to check first for an error.
                    // For more information on how to configure heartbeat notifications through the status
                    // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                    if (status.isError()) {
                        // There was an error with the heartbeat operation, handle here
                    } else {
                        // heartbeat operation was successful
                    }
                default: {
                    // Encountered unknown status type
                }
            }*/
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {

            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22");

            System.out.println(message.toString());

            JsonElement msg = message.getMessage();
            //System.out.println(msg+"&&&&&&&&&&"+msg.toString()+"&&&&&&&&&&"+msg.getAsString());
            String s=message.toString();

            Loggly.i("CabRequestServiceNew",stProfileId+" "+msg);


            if(msg.getAsString().equals("cab request"))
            {
                //mainUIThread("Hurray");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        getDetails();
                    }
                });
            }


            //getHistory();

        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }

    };

   /* public void publish(String msg)
    {
       *//* JsonObject position = new JsonObject();
        position.addProperty("lat", 32L);
        position.addProperty("lng", 32L);

        String p="Hello";

        System.out.println("before pub: " + position);*//*
        pubnub.publish()
                .message(msg)
                .channel("my_channel")
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // handle publish result, status always present, result if successful
                        // status.isError() to see if error happened
                        if(!status.isError()) {
                            System.out.println("pub timetoken: " + result.getTimetoken());
                        }
                        System.out.println("pub status code: " + status.getStatusCode());
                    }
                });
    }*/
}
