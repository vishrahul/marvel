package com.example.marveltask.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class firsthelper  extends SQLiteOpenHelper  {

    Context context;

    public firsthelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(constraint.TBL_QUERY);

        System.out.println("Sqlite Table Created...");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // System.out.println("update...");


    }
}
