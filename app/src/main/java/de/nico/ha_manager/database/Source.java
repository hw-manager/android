package de.nico.ha_manager.database;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import de.nico.ha_manager.helper.Homework;

public class Source {

    public static final String[] allColumns = {"ID", "URGENT", "SUBJECT",
            "HOMEWORK", "UNTIL"};
    public static final String[] mostColumns = {"URGENT", "SUBJECT",
            "HOMEWORK", "UNTIL"};
    private final Helper dbHelper;
    private SQLiteDatabase database;

    public Source(Context context) {
        dbHelper = new Helper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createEntry(Context c, String ID, String urgent,
                            String subject, String homework, String until) {
        ContentValues values = new ContentValues();
        values.put("URGENT", urgent);
        values.put("SUBJECT", subject);
        values.put("HOMEWORK", homework);
        values.put("UNTIL", until);

        String insertId = "ID = " + database.insert("HOMEWORK", null, values);
        if (ID != null) {
            Homework.deleteOne(c, ID);
            insertId = ID;
        }

        Cursor cursor = database.query("HOMEWORK", allColumns, insertId, null,
                null, null, null);
        cursor.moveToFirst();
    }

    public void delete_item(String whereC) {
        open();
        database.delete("HOMEWORK", whereC, null);
        close();
    }

    public ArrayList<HashMap<String, String>> get() {
        ArrayList<HashMap<String, String>> entriesList = new ArrayList<>();

        Cursor cursor = database.query("HOMEWORK", allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0)
            return entriesList;

        while (!cursor.isAfterLast()) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(allColumns[0], String.valueOf(cursor.getLong(0)));
            for (int i = 1; i < 5; i++)
                temp.put(allColumns[i], cursor.getString(i));
            entriesList.add(temp);
            cursor.moveToNext();
        }

        cursor.close();

        return entriesList;
    }

}