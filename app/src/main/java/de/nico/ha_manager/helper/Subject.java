package de.nico.ha_manager.helper;

/* 
 * @author Nico Alt
 * See the file "LICENSE" for the full license governing this code.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;

import de.nico.ha_manager.R;

public class Subject {

    /**
     * Default {@link android.content.SharedPreferences} used in this class.
     */
    private static SharedPreferences prefs;

    /**
     * Initializes the default {@link android.content.SharedPreferences} used in this class.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    private static void initPrefs(Context c) {
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
    }

    /**
     * Returns a list with all subjects used by the user.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    public static String[] get(Context c) {
        initPrefs(c);

        // Set size of array to amount of Strings in SharedPreferences
        int size = prefs.getInt("subjects_size", 0);
        String[] subjects = new String[size];

        // Get parts of subject array from SharedPreferences Strings
        for (int i = 0; i < size; i++)
            subjects[i] = prefs.getString("subjects_" + i, null);

        return subjects;
    }

    /**
     * Adds a subject.
     *
     * @param c       Needed by {@link android.preference.PreferenceManager}.
     * @param subject The subject to add.
     */
    public static void add(Context c, String subject) {
        initPrefs(c);
        int size = prefs.getInt("subjects_size", 0);
        String[] subjects = new String[size + 1];

        for (int i = 0; i < size; i++)
            subjects[i] = prefs.getString("subjects_" + i, null);

        subjects[size] = subject;

        SharedPreferences.Editor editor = prefs.edit();
        Arrays.sort(subjects);

        for (int i = 0; i < subjects.length; i++)
            editor.putString("subjects_" + i, subjects[i]);

        editor.putInt("subjects_size", subjects.length);
        editor.commit();

        String sAdded = c.getString(R.string.added);
        CustomToast.showShort(c, subject + " " + sAdded);
    }

    /**
     * Resets the list of subjects.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    public static void setDefault(Context c) {
        // Get subjects from strings.xml
        String[] subjects = c.getResources().getStringArray(R.array.subjects);

        // Sort subjects array alphabetically
        Arrays.sort(subjects);

        // Add subjects to SharedPreferences
        initPrefs(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("subjects_size", subjects.length);
        for (int i = 0; i < subjects.length; i++)
            editor.putString("subjects_" + i, subjects[i]);

        editor.commit();
    }

    /**
     * Deletes a subject.
     *
     * @param c   Needed by {@link android.preference.PreferenceManager}.
     * @param pos The subject to delete.
     */
    public static void delete(Context c, int pos) {
        initPrefs(c);
        int size = prefs.getInt("subjects_size", 0);
        String[] subjects = new String[size - 1];

        for (int i = 0; i < size; i++) {
            if (i < pos)
                subjects[i] = prefs.getString("subjects_" + i, null);

            if (i > pos)
                subjects[i - 1] = prefs.getString("subjects_" + i, null);
        }

        SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < subjects.length; i++)
            editor.putString("subjects_" + i, subjects[i]);

        editor.putInt("subjects_size", subjects.length);
        editor.commit();
    }
}
