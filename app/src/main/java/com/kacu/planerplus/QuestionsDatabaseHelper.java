package com.kacu.planerplus;


import static java.lang.Double.parseDouble;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;


public class QuestionsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Accounts";
    private static final int DB_VERSION = 1;



    public QuestionsDatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        updateMyDatabase(db,oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "TYPE INTEGER,"
                    + "EMAIL TEXT,"
                    + "PASSWORD TEXT,"
                    + "TOKEN TEXT,"
                    + "UID TEXT);");
    }
}


