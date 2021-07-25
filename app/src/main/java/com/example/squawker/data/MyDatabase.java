package com.example.squawker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "squawk.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context){
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + MyContract.SquawkEntry.TABLE_NAME + " ("
                + MyContract.SquawkEntry._ID + " INTEGER PRIMARY KEY, "
                + MyContract.SquawkEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + MyContract.SquawkEntry.COLUMN_AUTHOR_KEY + " TEXT NOT NULL, "
                + MyContract.SquawkEntry.COLUMN_MESSAGE + " TEXT NOT NULL, "
                + MyContract.SquawkEntry.COLUMN_DATE + " INTEGER NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MyContract.SquawkEntry.TABLE_NAME);
        onCreate(db);
    }
}
