package de.nico.ha_manager.database;

/* 
 * @author Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Utils;

public class Source {

    public static final String[] allColumns = {"ID", "HOMEWORK", "SUBJECT", "INFO", "URGENT", "TIME"};
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
                            String subject, String homework, long time, String info) {
        ContentValues values = new ContentValues();
        values.put(allColumns[1], homework);
        values.put(allColumns[2], subject);
        values.put(allColumns[3], info);
        values.put(allColumns[4], urgent);
        values.put(allColumns[5], String.valueOf(time));

        String insertId = "ID = " + database.insert("HOMEWORK", null, values);
        if (ID != null) {
            Homework.deleteOne(c, ID);
            insertId = ID;
        }

        Cursor cursor = database.query("HOMEWORK", allColumns, insertId, null,
                null, null, null);
        cursor.moveToFirst();
    }

    public void delete_item(String where) {
        open();
        database.delete("HOMEWORK", where, null);
        close();
    }

    public ArrayList<HashMap<String, String>> get(Context c) {
        ArrayList<HashMap<String, String>> entriesList = new ArrayList<>();

        Cursor cursor = database.query("HOMEWORK", allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0)
            return entriesList;

        while (!cursor.isAfterLast()) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(allColumns[0], String.valueOf(cursor.getLong(0)));
            for (int i = 1; i < allColumns.length; i++)
                temp.put(allColumns[i], cursor.getString(i));

            entriesList.add(temp);
            cursor.moveToNext();
        }

        cursor.close();

        // Sort by time
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        if (prefs.getBoolean("pref_sortbytime", false))
            Collections.sort(entriesList, new Comparator<HashMap<String, String>>() {

                @Override
                public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                    long a = Long.valueOf(lhs.get(allColumns[5])).longValue();
                    long b = Long.valueOf(rhs.get(allColumns[5])).longValue();
                    return Long.valueOf(a).compareTo(Long.valueOf(b));
                }
            });

        // Sort by importance
        if (prefs.getBoolean("pref_sortbyimportance", false))
            Collections.sort(entriesList, new Comparator<HashMap<String, String>>() {

                @Override
                public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                    String a = rhs.get(allColumns[4]);
                    String b = lhs.get(allColumns[4]);
                    return a.compareTo(b);
                }
            });

        // Add date, based on time in milliseconds
        for (int i = 0; i < entriesList.size(); i++) {
            HashMap<String, String> temp = entriesList.get(i);
            long time = Long.valueOf(temp.get(allColumns[5])).longValue();
            String date = Utils.convertToDate(time);
            temp.put("UNTIL", date);
        }

        return entriesList;
    }

}