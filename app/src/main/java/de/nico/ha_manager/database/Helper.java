package de.nico.ha_manager.database;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class Helper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Homework.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CREATE_HOMEWORK = ""
            + "create table HOMEWORK("
            + "  ID integer primary key autoincrement, " + "URGENT text,"
            + "SUBJECT text," + "HOMEWORK text," + "UNTIL text)";

    public Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE_HOMEWORK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Helper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS SCANITEM");
        onCreate(db);
    }
}
