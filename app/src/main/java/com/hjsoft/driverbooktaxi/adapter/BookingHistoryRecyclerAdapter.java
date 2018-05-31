package com.hjsoft.driverbooktaxi.adapter;

import android.content.Context;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.model.BookingHistoryData;
import com.hjsoft.driverbooktaxi.model.FormattedAllRidesData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hjsoft on 17/2/18.
 */
public class BookingHistoryRecyclerAdapter extends RecyclerView.Adapter<BookingHistoryRecyclerAdapter.MyViewHolder> {

    Context context;
    LayoutInflater inflater;
    Geocoder geocoder;
    RecyclerView rview;
    ArrayList<BookingHistoryData> mResultList;
    BookingHistoryData data;
    private AdapterCallback mAdapterCallback;
    int pos;


    public BookingHistoryRecyclerAdapter(Context context,ArrayList<BookingHistoryData> mResultList)
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_booking_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override

    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        data=mResultList.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        //String date = dateFormat.format(data.getAmount());
        try {
            String format = new SimpleDateFormat("dd MMM",Locale.ENGLISH).format(dateFormat.parse(data.getDate()));

            holder.tvDate.setText(format);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        holder.tvCompletedRides.setText(data.getCompletedRides());
        holder.tvCancelledRides.setText(data.getCancelledRides());
        holder.tvAmount.setText(context.getResources().getString(R.string.Rs)+" "+data.getAmount());

        holder.rLayout.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate,tvCompletedRides,tvCancelledRides,tvAmount;
        RelativeLayout rLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tvDate=(TextView)itemView.findViewById(R.id.rbh_tv_date);
            tvCompletedRides=(TextView)itemView.findViewById(R.id.rbh_tv_completed_rides);
            tvCancelledRides=(TextView)itemView.findViewById(R.id.rbh_tv_cancelled_rides);
            tvAmount=(TextView)itemView.findViewById(R.id.rbh_tv_amount);
            rLayout=(RelativeLayout)itemView.findViewById(R.id.rbh_rl);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //System.out.println("pos is "+data.getPosition());

                    try {
                        pos= (int) view.getTag();

                        System.out.println("pos is "+pos);
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
        void onMethodCallback(int position,ArrayList<BookingHistoryData> data);
    }
}
