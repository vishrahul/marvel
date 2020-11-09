package com.example.marveltask.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class firstmanager {

    Context context;
    firsthelper firsthelpe;
    SQLiteDatabase sb;


    public firstmanager(Context context)
    {
        this.context=context;
        firsthelpe =new firsthelper(context,constraint.DB_NAME,null,constraint.DB_VERSION);
    }


    public SQLiteDatabase openDB()
    {
        sb = firsthelpe.getWritableDatabase();
        return  sb;
    }

    public SQLiteDatabase closeDB()
    {
        firsthelpe.close();
        return  sb;
    }

}
