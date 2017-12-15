package com.hjsoft.driverbooktaxi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hjsoft.driverbooktaxi.adapter.DBAdapter;

/**
 * Created by hjsoft on 2/12/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DBAdapter.DB_CREATE_LATLNG);
        sqLiteDatabase.execSQL(DBAdapter.DB_CREATE_LOC_UPDATES);
        sqLiteDatabase.execSQL(DBAdapter.DB_ONGOING_RIDE);
        sqLiteDatabase.execSQL(DBAdapter.DB_NETWORK_ISSUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "RIDE_LATLNG");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "LOC_UPDATES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "ONGOING_RIDE");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "NETWORK_ISSUE");


        onCreate(sqLiteDatabase);

    }
}
