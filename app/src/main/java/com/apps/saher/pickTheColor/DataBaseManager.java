package com.apps.saher.pickTheColor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by saher on 12/9/2015.
 */
public class DataBaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String Database_name = "color_DB";
    public static final String table_name = "color_table";
    public static final String col_1 = "id";
    public static final String col_2 = "HEX_color";
    public static final String col_3 = "RGB_color";
//    public static final String col_4="int_color";
    ArrayList<Colors> colors;

    public DataBaseManager(Context context) {
        super(context, Database_name,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String ss = ( " create table " + table_name + " ( "
                + col_1 + " integer primary key autoincrement , "
                + col_2 + " text not null, "
                + col_3 + " text not null ) ; " ) ;
        try {
            db.execSQL(ss);
        } catch (Exception sd) {}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("drop table if exists " + table_name);
//        onCreate(db);
    }
    public Boolean Add_Color(String HEX_color, String RGB_color){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2,HEX_color);
        contentValues.put(col_3,RGB_color);
//        contentValues.put(col_4,int_color);
        long raw = db.insert(table_name, null, contentValues);
        if (raw != -1) return true;
        else return false;

    }
    public ArrayList<Colors> viewdata() {
        SQLiteDatabase db = getWritableDatabase();
        colors = new ArrayList<>();
//        String[] string = new String[]{col_2,col_3};
        Cursor cursor=db.query(table_name,null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int ID = cursor.getInt(0);
            String hexcolor = cursor.getString(1);
            String rgbcolor = cursor.getString(2);

            Colors colors1 = new Colors();
            colors1.HEXcolor = hexcolor;
            colors1.RGBcolor = rgbcolor;
            colors1.ID=ID;

            colors.add(colors1);
        }
        cursor.close();
        Log.e("the list size ", " from db =" + colors.size());
        return colors;
    }

    public boolean delete(int ID){
        SQLiteDatabase db = getWritableDatabase();
//        db.delete(table_name, col_1 +"=?" , new String[]{String.valueOf((ID))});
//        db.delete(table_name,col_1+"="+ID,null);
//        db.close();
//        db.execSQL("delete from "+ table_name + "where "+col_1+ "="+ ID);
        return db.delete(table_name, col_1 + "=" + ID , null) > 0;
    }

    public boolean update(String id,String HEX_color, String RGB_color){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,id);
        contentValues.put(col_2,HEX_color);
        contentValues.put(col_3, RGB_color);
//        contentValues.put(col_4,mobile);
        db.update(table_name, contentValues, "ID = ?", new String[]{id});
        return true;
    }
}
