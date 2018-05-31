package com.hjsoft.driverbooktaxi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hjsoft on 30/11/17.
 */
public class SearchRideRecyclerAdapter extends RecyclerView.Adapter<SearchRideRecyclerAdapter.MyViewHolder> {

    Context context;
    LayoutInflater inflater;
    Geocoder geocoder;
    RecyclerView rview;
    ArrayList<FormattedAllRidesData> mResultList;
    FormattedAllRidesData data;
    private AdapterCallback mAdapterCallback;
    int pos;


    public SearchRideRecyclerAdapter(Context context,ArrayList<FormattedAllRidesData> mResultList,RecyclerView rview)
    {

        this.context=context;
        this.mResultList=mResultList;
        this.rview=rview;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        geocoder=new Geocoder(context, Locale.getDefault());
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_rides, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        data=mResultList.get(position);


        if(data.getTravelType().equals("outstation")) {
            holder.tvTravelType.setText(data.getTravelType()+" - "+data.getTravelPackage());
        }
        else if(data.getTravelType().equals("local"))
        {
            if(data.getTravelPackage().equals("")) {

                holder.tvTravelType.setText("Local");
            }
            else {
                holder.tvTravelType.setText("Local"+" - "+data.getTravelPackage());
            }
        }
        else {
            holder.tvTravelType.setText(data.getTravelType()+" - "+data.getTravelPackage());
        }
        holder.tvTripId.setText(data.getRequestId());
        holder.tvFrom.setText(data.getFromLocation());
        holder.tvTo.setText(data.getToLocation());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        String date = dateFormat.format(data.getRideDate());
        String format = new SimpleDateFormat("E, MMM d,yy   hh:mm a",Locale.ENGLISH).format(data.getRideDate());
        // String format = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());


/*
        try {
            SimpleDateFormat newformat = new SimpleDateFormat("dd-MM-yyyy");
            String datestring = date.split("T")[0];
            SimpleDateFormat oldformat = new SimpleDateFormat("MM/dd/yyyy");
            String reformattedStr = newformat.format(oldformat.parse(datestring));
            holder.tvDate.setText(reformattedStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        holder.tvDate.setText(format);

        // holder.tvDate.setText(date);
        // holder.tvStatus.setText(data.getRideStatus());
        if(data.getRideStatus().equals("CANCELLED"))
        {
            holder.tvStatus.setText(data.getRideStatus());
            holder.tvStatus.setTextColor(Color.parseColor("#f44336"));
        }
        else
        {
            holder.tvStatus.setText(data.getRideStatus());
            holder.tvStatus.setTextColor(Color.parseColor("#0067de"));
        }
        holder.rLayout.setTag(data.getPosition());

    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTripId,tvFrom,tvTo,tvStatus,tvDate,tvTravelType;
        RelativeLayout rLayout;


        public MyViewHolder(final View itemView) {
            super(itemView);
            rLayout=(RelativeLayout)itemView.findViewById(R.id.rmr_rlayout);
            tvTripId=(TextView)itemView.findViewById(R.id.rmr_tv_tripid);
            tvFrom=(TextView)itemView.findViewById(R.id.rmr_tv_from);
            tvTo=(TextView)itemView.findViewById(R.id.rmr_tv_to);
            tvStatus=(TextView)itemView.findViewById(R.id.rmr_tv_status);
            tvDate=(TextView)itemView.findViewById(R.id.rmr_tv_date);
            tvTravelType=(TextView)itemView.findViewById(R.id.rmr_tv_ttype);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        pos= (int) view.getTag();
                        mAdapterCallback.onMethodCallback(pos,mResultList);

                    }
                    catch (ClassCastException e)
                    {
                        e.printStackTrace();
                    }
                    //Toast.makeText(context,"clicked .."+pos,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static interface AdapterCallback {
        void onMethodCallback(int position,ArrayList<FormattedAllRidesData> data);
    }
}

