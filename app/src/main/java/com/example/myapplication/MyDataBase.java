package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyDataBase extends SQLiteOpenHelper {
    private Context context;
    private static String DATABASE_NAME = "Andoroid_db";
    private static int DATABASE_VERSION = 1;

    private static String TABLE_ACTIVITY = "activity";
    private static String COLUMN_USER = "user";
    private static String COLUMN_ACTIVITY = "type";
    private static String COLUMN_DATE = "date";

    public MyDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + TABLE_ACTIVITY + "("
                + COLUMN_USER + " TEXT," + COLUMN_ACTIVITY + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")";
        // Execute script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        // Recreate
        onCreate(db);
    }

    // Ajouter un user
    public Long addActivity(Activity activity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER, activity.getUser());
        cv.put(COLUMN_ACTIVITY, activity.getTypeActivity());
        cv.put(COLUMN_DATE, activity.getDate());
        Long res = db.insert(TABLE_ACTIVITY, null, cv);

        return res;
    }
    public List<Activity> getActivities(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ACTIVITY, null);
        List<Activity> activities = new ArrayList<>();
        if(c != null){
            c.moveToFirst();
            Activity activity = new Activity(c.getString(0), c.getString(1), c.getString(2));
            activities.add(activity);
        } else {
            return null;
        }
        return activities;
    }
}
