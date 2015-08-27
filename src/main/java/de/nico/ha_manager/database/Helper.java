package de.nico.ha_manager.database;

/* 
 * @author Nico Alt
 * See the file "LICENSE" for the full license governing this code.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class Helper extends SQLiteOpenHelper {

    /**
     * The name of the database containing the homework.
     */
    public static final String DATABASE_NAME = "Homework.db";

    /**
     * The current version of the database containing the homework.
     */
    private static final int DATABASE_VERSION = 3;

    /**
     * The command when first creating the database.
     */
    private static final String TABLE_CREATE_HOMEWORK = "create table HOMEWORK(ID integer primary key autoincrement,HOMEWORK text,SUBJECT text,TIME text,INFO text,URGENT text,COMPLETED text)";

    public Helper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public final void onCreate(final SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE_HOMEWORK);
    }

    @Override
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // Upgrade from first to third version
        if (oldVersion == 1 && newVersion == 3) {
            db.execSQL("ALTER TABLE HOMEWORK ADD COLUMN INFO TEXT");
            db.execSQL("ALTER TABLE HOMEWORK ADD COLUMN TIME TEXT");
            db.execSQL("ALTER TABLE HOMEWORK ADD COLUMN COMPLETED TEXT");
            Log.w(Helper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ".");
        }
        // Upgrade from second to third version
        if (oldVersion == 2 && newVersion == 3) {
            db.execSQL("ALTER TABLE HOMEWORK ADD COLUMN COMPLETED TEXT");
            Log.w(Helper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ".");
        }
    }
}
