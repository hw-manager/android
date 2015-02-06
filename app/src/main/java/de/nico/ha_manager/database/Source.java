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

    public static final String[] allColumns = {"ID", "URGENT", "SUBJECT",
            "HOMEWORK", "UNTIL", "TIME", "INFO"};
    public static final String[] mostColumns = {"URGENT", "SUBJECT",
            "HOMEWORK", "UNTIL", "TIME", "INFO"};
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
        values.put(mostColumns[0], urgent);
        values.put(mostColumns[1], subject);
        values.put(mostColumns[2], homework);
        values.put(mostColumns[3], "");
        values.put(mostColumns[4], String.valueOf(time));
        values.put(mostColumns[5], info);

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
            for (int i = 1; i < 6; i++) {
                // Until/Time
                if (i == 4) {
                    if (cursor.getString(i).equals(""))
                        temp.put(allColumns[i], cursor.getString(i + 1));
                    else
                        temp.put(allColumns[i], "0");
                }
                // Info
                else if (i == 5)
                    temp.put(allColumns[i + 1], cursor.getString(i + 1));
                else
                    temp.put(allColumns[i], cursor.getString(i));
            }
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
                    long a = Long.valueOf(lhs.get(allColumns[4])).longValue();
                    long b = Long.valueOf(rhs.get(allColumns[4])).longValue();
                    return Long.valueOf(a).compareTo(Long.valueOf(b));
                }
            });

        // Sort by importance
        if (prefs.getBoolean("pref_sortbyimportance", false))
            Collections.sort(entriesList, new Comparator<HashMap<String, String>>() {

                @Override
                public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                    String a = rhs.get(allColumns[1]);
                    String b = lhs.get(allColumns[1]);
                    return a.compareTo(b);
                }
            });

        // Convert milliseconds to date
        for (int i = 0; i < entriesList.size(); i++) {
            HashMap<String, String> temp = entriesList.get(i);
            long time = Long.valueOf(temp.get(allColumns[4])).longValue();
            String date = Utils.convertToDate(time);
            temp.remove(allColumns[4]);
            temp.put(allColumns[4], date);
        }

        return entriesList;
    }

}