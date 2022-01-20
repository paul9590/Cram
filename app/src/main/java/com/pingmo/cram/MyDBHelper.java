package com.pingmo.cram;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context) {
        super(context, "CramDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE userTB (userID VARCHAR(10) PRIMARY KEY, userName VARCHAR(20), friend VARCHAR(20), rank Integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS userTB");
        onCreate(db);
    }

    public void truncateTB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS userTB");
        onCreate(db);
    }
}