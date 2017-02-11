package com.android_examples.slidingtab_android_examplescom;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import static com.android_examples.slidingtab_android_examplescom.GoalContract.Goal.COLUMN_NAME_PROGRESS;
import static com.android_examples.slidingtab_android_examplescom.GoalContract.Goal.TABLE_NAME;

/**
 * Created by liamdiamond on 31/01/2017.
 */

public final class GoalContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private GoalContract() {}

    /* Inner class that defines the table contents */
    public static class Goal implements BaseColumns {
        public static final String TABLE_NAME = "goal";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_UNITS = "units";
        public static final String COLUMN_NAME_PROGRESS = "progress";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    Goal._ID + " INTEGER PRIMARY KEY," +
                    Goal.COLUMN_NAME_TITLE + " TEXT," +
                    Goal.COLUMN_NAME_DISTANCE + " DOUBLE," +
                    Goal.COLUMN_NAME_UNITS + " TEXT," +
                    COLUMN_NAME_PROGRESS + " DOUBLE," +
                    Goal.COLUMN_NAME_DATE + " BIGINT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public static class GoalDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "goal.db";

        public GoalDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        public void increaseProgress (SQLiteDatabase db, int id, double increase){
            String rawQuery = "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME_PROGRESS +  " = " + COLUMN_NAME_PROGRESS +  " + " + increase + "  WHERE _id  = " + id;
            db.execSQL(rawQuery);
        }



        public String getTableAsString(SQLiteDatabase db, String tableName) {
            Log.d("", "getTableAsString called");
            String tableString = String.format("Table %s:\n", tableName);
            Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
            if (allRows.moveToFirst() ){
                String[] columnNames = allRows.getColumnNames();
                do {
                    for (String name: columnNames) {
                        tableString += String.format("%s: %s\n", name,
                                allRows.getString(allRows.getColumnIndex(name)));
                    }
                    tableString += "\n";

                } while (allRows.moveToNext());
            }

            return tableString;
        }

        public ArrayList<GoalsListDisplay> getAllGoals(SQLiteDatabase db, String tableName) {
            ArrayList<GoalsListDisplay> results = new ArrayList<GoalsListDisplay>();
            try {
                String rawQuery = "Select _id, title, distance, units, progress, date from goal";

                Cursor c = db.rawQuery(rawQuery, null);

                try {

                    if (!c.moveToFirst())
                        return null;
                    do {
//                        System.out.println("CURSOR  "  +c.getInt(5));
                        results.add(new GoalsListDisplay(c.getInt(0), c.getString(1),c.getString(2),c.getString(3), c.getInt(4), c.getLong(5)));
                    } while (c.moveToNext());
                } finally {
                    c.close();
                }
            } finally {
                db.close();
            }
            return results;
        }

    }


}
