package com.hjsoft.driverbooktaxi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.model.AllRidesPojo;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hjsoft on 23/1/17.
 */
public class SpecificRideFragment extends Fragment {

    int position;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    FormattedAllRidesData data;
    TextView tvFrom,tvTo,tvDistance,tvTime,tvFare,tvRideStartTime,tvRideStopTime,tvTotalFare,tvTaxes,tvTotalBill,tvTripId,tvOSBatta;
    long diff=0;
    double dis=0;
    LinearLayout llOsBatta,llOtherCharges;
    String osbatta;
    TextView tvPayment,tvOtherCharges;
    double otherCharges=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_specific_ride, container, false);

        tvTripId=(TextView)rootView.findViewById(R.id.fsr_tv_trip_id);
        tvFrom=(TextView)rootView.findViewById(R.id.fsr_tv_from);
        tvTo=(TextView)rootView.findViewById(R.id.fsr_tv_to);
        tvDistance=(TextView)rootView.findViewById(R.id.fsr_tv_distance);
        tvTime=(TextView)rootView.findViewById(R.id.fsr_tv_time);
        tvFare=(TextView)rootView.findViewById(R.id.fsr_tv_fare);
        tvRideStartTime=(TextView)rootView.findViewById(R.id.fsr_tv_ride_start_time);
        tvRideStopTime=(TextView)rootView.findViewById(R.id.fsr_tv_ride_stop_time);
        tvTotalFare=(TextView)rootView.findViewById(R.id.fsr_tv_total_fare);
        tvTaxes=(TextView)rootView.findViewById(R.id.fsr_tv_taxes);
        tvTotalBill=(TextView)rootView.findViewById(R.id.fsr_tv_total_bill);
        tvOSBatta=(TextView)rootView.findViewById(R.id.fsr_tv_os_batta);
        llOsBatta=(LinearLayout)rootView.findViewById(R.id.fsr_ll_os_batta);
        llOtherCharges=(LinearLayout)rootView.findViewById(R.id.fsr_ll_other_charges);
        llOsBatta.setVisibility(View.GONE);
        llOtherCharges.setVisibility(View.GONE);
        tvOSBatta.setVisibility(View.GONE);

        tvPayment=(TextView)rootView.findViewById(R.id.fsr_tv_payment);
        tvOtherCharges=(TextView)rootView.findViewById(R.id.fsr_tv_charges);


        tvFrom.setText(data.getFromLocation());
        tvTo.setText(data.getToLocation());
        tvTaxes.setText("Rs. 0");
        tvTripId.setText(data.getRequestId());
        // tvDistance.setText(data.getDistanceTravelled());

        if(data.getTravelType().equals("outstation"))
        {
            llOsBatta.setVisibility(View.VISIBLE);
            tvOSBatta.setVisibility(View.VISIBLE);
            tvOSBatta.setText(getString(R.string.Rs)+" "+data.getOsBatta());
            osbatta=data.getOsBatta();
        }

        if(data.getOtherCharges()==null||data.getOtherCharges().equals("0"))
        {

        }
        else {
            llOtherCharges.setVisibility(View.VISIBLE);
            tvOtherCharges.setText(getString(R.string.Rs)+" "+data.getOtherCharges());
            otherCharges=Double.parseDouble(data.getOtherCharges());
        }

        if(data.getTotalAmount().equals("")) {

            tvFare.setText(getString(R.string.Rs)+" "+"0");
            tvTotalFare.setText(getString(R.string.Rs)+" "+"0");
            tvTotalBill.setText(getString(R.string.Rs)+" "+"0");
        }
        else
        {
            if(data.getTotalAmount().equals("-"))
            {
                tvFare.setText(getString(R.string.Rs)+" "+"0");
                tvTotalFare.setText(getString(R.string.Rs)+" "+"0");
                tvTotalBill.setText(getString(R.string.Rs)+" "+"0");
            }
            else {
                String[] amount = data.getTotalAmount().split("-");
                String fare = amount[0];
                String tax = amount[1];

                if(data.getTravelType().equals("outstation"))
                {
                    Double finalFare=Double.parseDouble(fare)-Double.parseDouble(osbatta)-otherCharges;
                    tvFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                    tvTotalFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                }
                else {
                    Double finalFare=Double.parseDouble(fare)-otherCharges;
                    tvFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                    tvTotalFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                }


                //tvFare.setText("Rs. " + fare);
//                tvFare.setText(getString(R.string.Rs)+" "+fare);
//                tvTotalFare.setText(getString(R.string.Rs)+" "+ fare);
                tvTaxes.setText(getString(R.string.Rs)+" "+ tax);
                int totalBill = (int)(Double.parseDouble(fare)+Double.parseDouble(tax));
                tvTotalBill.setText(getString(R.string.Rs)+" "+ String.valueOf(totalBill));
            }

        }



        if(data.getDistanceTravelled().equals("")) {
            tvDistance.setText("- km");
        }
        else{
            tvDistance.setText(data.getDistanceTravelled());
        }

        //System.out.println("checking......................."+data.getRideStartTime().length());
        //if(data.getRideStartTime().length()>=4) {
            if ((data.getRideStartTime().substring(data.getRideStartTime().length() - 4, data.getRideStartTime().length()).equals("a.m.")
                    || data.getRideStartTime().substring(data.getRideStartTime().length() - 4, data.getRideStartTime().length()).equals("p.m."))) {

                String time = data.getRideStartTime().substring(0, data.getRideStartTime().length() - 4);
                //System.out.println(time);
                String timePick = data.getRideStartTime().substring(data.getRideStartTime().length() - 4, data.getRideStartTime().length());
                //System.out.println(timePick);
                if (timePick.equals("p.m.")) {
                    time += "PM";
                } else if (timePick.equals("a.m.")) {
                    time += "AM";
                }

                data.setRideStartTime(time);
            } else {

            }
       // }

        //if(data.getRideStopTime().length()>=4) {
            if ((data.getRideStopTime().substring(data.getRideStopTime().length() - 4, data.getRideStopTime().length()).equals("a.m.")
                    || data.getRideStopTime().substring(data.getRideStopTime().length() - 4, data.getRideStopTime().length()).equals("p.m."))) {

                String time = data.getRideStopTime().substring(0, data.getRideStopTime().length() - 4);
                //System.out.println(time);
                String timePick = data.getRideStopTime().substring(data.getRideStopTime().length() - 4, data.getRideStopTime().length());
                //System.out.println(timePick);
                if (timePick.equals("p.m.")) {
                    time += "PM";
                } else if (timePick.equals("a.m.")) {
                    time += "AM";
                }

                data.setRideStopTime(time);
            } else {

            }
       // }

        tvRideStartTime.setText(data.getRideStartTime());
        tvRideStopTime.setText(data.getRideStopTime());

       // SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if(data.getTravelType().equals("local")||data.getTravelType().equals("Packages")) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                Date date1 = timeFormat.parse(data.getRideStartTime());
                Date date2 = timeFormat.parse(data.getRideStopTime());

                diff = (date2.getTime() - date1.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            int Hours = (int) (diff / (1000 * 60 * 60));
            int Mins = (int) (diff / (1000 * 60)) % 60;
            long Secs = (int) (diff / 1000) % 60;

            DecimalFormat formatter = new DecimalFormat("00");
            String hFormatted = formatter.format(Hours);
            String mFormatted = formatter.format(Mins);
            String sFormatted = formatter.format(Secs);
            String time = hFormatted + ":" + mFormatted + ":" + sFormatted;

            tvTime.setText(time);
        }
        else {

            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                Date date1 = timeFormat.parse(data.getRideStartTime());
                Date date2 = timeFormat.parse(data.getRideStopTime());

                diff = (date2.getTime() - date1.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            int Hours = (int) (diff / (1000 * 60 * 60));
            int Mins = (int) (diff / (1000 * 60)) % 60;
            long Secs = (int) (diff / 1000) % 60;

            DecimalFormat formatter = new DecimalFormat("00");
            String hFormatted = formatter.format(Hours);
            String mFormatted = formatter.format(Mins);
            String sFormatted = formatter.format(Secs);
            String time = hFormatted + ":" + mFormatted + ":" + sFormatted;

            tvTime.setText(time);

        }

        tvPayment.setText(data.getPaymentMode());


        return  rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position=getArguments().getInt("position");
        dataList=(ArrayList<FormattedAllRidesData>) getArguments().getSerializable("list");
        // Toast.makeText(getActivity(),"value "+position,Toast.LENGTH_LONG).show();

        data=dataList.get(position);

    }
}
