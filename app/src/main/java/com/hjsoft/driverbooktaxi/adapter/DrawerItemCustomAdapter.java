package com.hjsoft.driverbooktaxi.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.model.NavigationData;

/**
 * Created by hjsoft on 26/12/16.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<NavigationData> {

    Context mContext;
    int layoutResourceId;
    NavigationData data[] = null;
    private int mSelectedItem;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, NavigationData[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        NavigationData folder = data[position];

        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);

        if(position==mSelectedItem)
        {
            textViewName.setTextColor(Color.parseColor("#0067de"));
            textViewName.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            textViewName.setTextColor(Color.parseColor("#414040"));
        }

        return listItem;
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        mSelectedItem = selectedItem;
    }

}
