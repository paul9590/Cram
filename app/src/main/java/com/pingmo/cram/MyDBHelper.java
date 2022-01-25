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
        db.execSQL("CREATE TABLE userTB (userID varchar(40) NOT NULL," +
                    "userName varchar(20) PRIMARY KEY , " +
                    "cash Integer, " +
                    "rank Integer, " +
                    "state Integer);");

        db.execSQL("CREATE TABLE shopTB(itemCode Integer PRIMARY KEY DEFAULT 0, " +
                    "itemName varchar(30), " +
                    "itemType Integer, " +
                    "itemValue Integer);");

        db.execSQL("CREATE TABLE friendTB (friend varchar(20));");

        db.execSQL("CREATE TABLE inventoryTB (itemCode Integer," +
                    "itemType Integer );");

        db.execSQL("CREATE TABLE equipmentTB (eqip1 Integer, " +
                    "eqip2 Integer, " +
                    "eqip3 Integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS userTB");
        db.execSQL("DROP TABLE IF EXISTS shopTB");
        db.execSQL("DROP TABLE IF EXISTS friendTB");
        db.execSQL("DROP TABLE IF EXISTS inventoryTB");
        db.execSQL("DROP TABLE IF EXISTS equipmentTB");
        onCreate(db);
    }

    public void truncateTB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS userTB");
        db.execSQL("DROP TABLE IF EXISTS shopTB");
        db.execSQL("DROP TABLE IF EXISTS friendTB");
        db.execSQL("DROP TABLE IF EXISTS inventoryTB");
        db.execSQL("DROP TABLE IF EXISTS equipmentTB");
        onCreate(db);
    }
}