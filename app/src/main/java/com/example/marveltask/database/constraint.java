package com.example.marveltask.database;


public class constraint {


    public static final String DB_NAME="CALL_DATABASE";
    public static final int DB_VERSION=1;
    public static final String TBL_NAME="TBL_Detail";

    public static final String COL_ID="id";
    public static final String COL_NAME="name";
    public static final String COL_DETAIL="detail";
    public static final String COL_IMAGE="image";



    public static final String TBL_QUERY="create table "+TBL_NAME+"("+COL_ID+"" +" integer primary key autoincrement,"+COL_NAME+" text,"+COL_DETAIL+" text,"+COL_IMAGE+" blob)";



}
