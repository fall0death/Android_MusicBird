package com.example.wdgk.musicbird.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "music.db";
    private static final String TABLE_NAME = "song";
    private static final int DB_VERSION = 1;
    private Context myContext;
    public myDB(Context context) {
        super(context, "music.db", null, 3);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table " + TABLE_NAME
                + "(id integer primary key,"
                +"name text,"   //歌名
                +"final_res text,"
                +"url text)";  //解码结果
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }

    public void insert(String name,String final_res,String url){
        SQLiteDatabase db =getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("final_res",final_res);
        values.put("url",url);
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public void update(String name,String final_res,String url){
        SQLiteDatabase db =getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = {name};
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("final_res",final_res);
        values.put("url",url);
        db.update(TABLE_NAME,values,whereClause,whereArgs);
        db.close();
    }

    public void delete(String name){
        SQLiteDatabase db =getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = {name};
        db.delete(TABLE_NAME,whereClause,whereArgs);
        db.close();
    }

    public Cursor query(){
        SQLiteDatabase db =getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }
}
