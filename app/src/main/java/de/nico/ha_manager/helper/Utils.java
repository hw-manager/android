package de.nico.ha_manager.helper;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Paint;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.nico.ha_manager.HWManager;
import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;

public class Utils {

    private static boolean isActionBarAvailable = false;

    /**
     * Shows a short Toast.
     *
     * @param c   Needed for {@link android.widget.Toast}.
     * @param msg Message to show.
     */
    public static void makeShortToast(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a long Toast.
     *
     * @param c   Needed for {@link android.widget.Toast}.
     * @param msg Message to show.
     */
    public static void makeLongToast(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Converts an ArrayList with multiples HashMaps to an ArrayList with just one HashMap.
     *
     * @param ArHa An ArrayList with multiples HashMaps.
     * @param pos  Indicates which HashMap has to be used.
     */
    public static ArrayList<HashMap<String, String>> tempArray(
            ArrayList<HashMap<String, String>> ArHa, int pos) {

        // Temporary ArrayList containing a HashMap
        ArrayList<HashMap<String, String>> tempArHa = new ArrayList<>();

        // Temporary HashMap
        HashMap<String, String> tempHashMap = new HashMap<>();

        // Fill temporary HashMap with one row of original HashMap
        for (int i = 0; i < Source.allColumns.length; i++)
            tempHashMap.put(Source.allColumns[i],
                    ArHa.get(pos).get(Source.allColumns[i]));

        String date = Utils.convertToDate(Long.valueOf(ArHa.get(pos).get(Source.allColumns[5])).longValue());
        tempHashMap.put("UNTIL", date);

        // Add temporary HashMap to temporary ArrayList containing a HashMap
        tempArHa.add(tempHashMap);
        return tempArHa;
    }

    /**
     * Sets the theme.
     *
     * @param c          {@link android.app.Activity} - It would be Context, but we need to
     *                   set the window background of the Activity, so Activity extends Context, so it
     *                   works out.
     * @param isAddTheme Since we use the DialogWhenLarge theme on tablets in the add
     *                   activity, we need to incorporate that.
     */
    public static void setTheme(Activity c, boolean isAddTheme) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(c);
        final boolean dark = prefs.getBoolean("theme", false);
        final boolean black = prefs.getBoolean("black", false);

        if (dark) {
            if (isAddTheme)
                c.setTheme(R.style.DarkAddTheme);
            else
                c.setTheme(R.style.DarkAppTheme);
            if (black)
                c.getWindow().setBackgroundDrawableResource(android.R.color.black);
        } else {
            if (isAddTheme)
                c.setTheme(R.style.AddTheme);
            else
                c.setTheme(R.style.AppTheme);
        }
    }

    /**
     * A fix for a VerifyError crash on old versions
     * of Android
     */
    static {
        try {
            ActionBarWrapper.isAvailable();
            isActionBarAvailable = true;
        } catch (Throwable t) {
            isActionBarAvailable = false;
        }
    }

    public static void setupActionBar(Context context, boolean isPreferenceActivity) {
        if (Build.VERSION.SDK_INT >= 11 && isActionBarAvailable) {
            ActionBarWrapper actionBarWrapper = new ActionBarWrapper(context, isPreferenceActivity);
            actionBarWrapper.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Returns a SimpleAdapter that uses the layout "listview_entry".
     *
     * @param c Needed for {@link android.widget.SimpleAdapter}.
     * @param a ArrayList with HashMaps to show with the adapter.
     */
    public static SimpleAdapter entryAdapter(Context c,
                                             ArrayList<HashMap<String, String>> a) {
        // All TextViews in Layout "listview_entry"
        int[] i = {R.id.textView_urgent, R.id.textView_subject,
                R.id.textView_homework, R.id.textView_until};
        String[] columns = {"URGENT", "SUBJECT", "HOMEWORK", "UNTIL"};

        // Make a SimpleAdapter which is like a row in the homework list
        return new SimpleAdapter(c, a, R.layout.listview_entry, columns, i);
    }

    /**
     * Returns a SimpleExpandableListAdapter that uses the layout "listview_expanded_entry1".
     *
     * @param c Needed for {@link android.widget.SimpleExpandableListAdapter}.
     * @param a ArrayList with HashMaps to show with the adapter.
     */
    public static SimpleExpandableListAdapter expandableEntryAdapter(Context c,
                                                                     ArrayList<HashMap<String, String>> a) {
        // All TextViews in Layout "listview_expanded_entry1"
        int[] groupTexts = {R.id.textView_urgent, R.id.textView_subject,
                R.id.textView_homework, R.id.textView_until};
        String[] groupColumns = {"URGENT", "SUBJECT", "HOMEWORK", "UNTIL"};

        // All TextViews in Layout "listview_expanded_entry2"
        int[] childTexts = {R.id.textView_info};
        String[] childColumns = {"INFO"};
        List<List<Map<String, String>>> childData = covertToListListMap(a, childColumns[0]);

        // Make a SimpleAdapter which is like a row in the homework list
        return new SimpleExpandableListAdapter(c, a, R.layout.listview_expanded_entry1, groupColumns, groupTexts, childData, R.layout.listview_expanded_entry2, childColumns, childTexts);
    }

    /**
     * Converts an ArrayList containing HashMaps to a List containing a List Containing a Map.
     *
     * @param a   ArrayList with HashMaps to convert.
     * @param row Row to add to the Map.
     */
    public static List<List<Map<String, String>>> covertToListListMap(ArrayList<HashMap<String, String>> a, String row) {
        List<List<Map<String, String>>> ll = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            Map<String, String> tmpL = new HashMap<>();
            tmpL.put(row, a.get(i).get(row));

            List<Map<String, String>> l = new ArrayList<>();
            l.add(tmpL);

            ll.add(l);
        }
        return ll;
    }

    /**
     * Sends an Intent with a text to share the app.
     *
     * @param c Needed for {@link android.content.Intent}.
     */
    @SuppressWarnings("deprecation")
    public static boolean shareApp(Context c) {
        String share_title = c.getString(R.string.intent_share_title);
        String app_name = c.getString(R.string.app_name);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT,
                c.getString(R.string.intent_share_text));
        try {
            c.startActivity(Intent.createChooser(intent, share_title + " "
                    + app_name));
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e("ActivityNotFoundExcept", e.toString());
            return false;
        }
    }

    /**
     * Returns some information to the build of the app.
     *
     * @param c Needed for {@link android.content.pm.PackageInfo} and
     *          {@link android.content.pm.ApplicationInfo}.
     */
    public static String getBuildInfo(Context c) {
        String buildInfo = "Built with love.";
        try {
            // Get Version Name
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(
                    c.getPackageName(), 0);
            String versionName = pInfo.versionName;

            // Get build time
            ApplicationInfo aInfo = c.getPackageManager().getApplicationInfo(
                    c.getPackageName(), 0);
            ZipFile zf = new ZipFile(aInfo.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            zf.close();
            long time = ze.getTime();
            DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                    Locale.getDefault());
            String buildDate = f.format(time);

            buildInfo = versionName + " (" + buildDate + ")";

        } catch (Exception e) {
            Log.e("Get Build Info", e.toString());
        }

        return buildInfo;
    }

    /**
     * Shows a spinner with all available languages of HW-Manager.
     *
     * @param c Needed for {@link de.nico.ha_manager.HWManager} and
     *          {@link android.content.SharedPreferences}.
     */
    public static void langSpinner(final Context c) {
        AlertDialog.Builder b = new Builder(c);
        // Current translations of HW-Manager
        final String[] languages = {"cs", "de", "en", "es", "fr", "hu", "ar", "fa"};
        // Items with translation's language
        String[] items = new String[languages.length + 1];
        items[0] = c.getString(R.string.pref_language_default);
        for (int i = 1; i < languages.length + 1; i++) {
            Locale appLoc = new Locale(languages[i - 1]);
            items[i] = appLoc.getDisplayLanguage(appLoc);
        }
        b.setTitle(c.getString(R.string.pref_language));
        b.setItems(items, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(c);
                SharedPreferences.Editor editor = prefs.edit();

                if (which == 0) {
                    editor.putString("locale_override", "");
                    editor.commit();
                } else {
                    editor.putString("locale_override", languages[which - 1]);
                    editor.commit();
                }
                HWManager.updateLanguage(c);
            }

        });

        b.show();
    }

    /**
     * Cross out solved homework.
     *
     * @param hwList  {@link android.widget.ExpandableListView} which contains the homework.
     * @param hwArray {@link java.util.ArrayList} which contains the homework.
     */
    public static void crossOut(ExpandableListView hwList, ArrayList<HashMap<String, String>> hwArray) {
        for (int i = 0; i < hwArray.size(); i++) {
            /* TODO: DO NOT WORK
            if (!hwArray.get(i).get(Source.allColumns[6]).equals("")) {
                TextView tv1 = (TextView) hwList.getChildAt(i).findViewById(R.id.textView_subject);
                TextView tv2 = (TextView) hwList.getChildAt(i).findViewById(R.id.textView_until);
                TextView tv3 = (TextView) hwList.getChildAt(i).findViewById(R.id.textView_homework);
                TextView tv4 = (TextView) hwList.getChildAt(i).findViewById(R.id.textView_urgent);
                tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv2.setPaintFlags(tv2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv3.setPaintFlags(tv3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv4.setPaintFlags(tv4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }*/
        }
        return;
    }

    /**
     * Cross out one solved homework.
     *
     * @param hwList  {@link android.widget.ExpandableListView} which contains the homework.
     * @param hwArray {@link java.util.ArrayList} which contains the homework.
     * @param pos     Position of solved homework.
     */
    public static boolean crossOneOut(Context c, ExpandableListView hwList, ArrayList<HashMap<String, String>> hwArray, int pos) {
        String ID = "ID = " + hwArray.get(pos).get(Source.allColumns[0]);
        String title = hwArray.get(pos).get(Source.allColumns[1]);
        String subject = hwArray.get(pos).get(Source.allColumns[2]);
        long time = Long.valueOf(hwArray.get(pos).get(Source.allColumns[5])).longValue();
        String info = hwArray.get(pos).get(Source.allColumns[3]);
        String urgent = hwArray.get(pos).get(Source.allColumns[4]);
        String completed = "completed";
        Homework.add(c, ID, title, subject, time, info, urgent, completed);
        return true;
    }

    /**
     * Transfers a file.
     *
     * @param src Source from where the file has to be transferred.
     * @param dst Source to where the file has to be transferred.
     */
    public static boolean transfer(final File src, final File dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.toString());
            return false;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return false;
        }
    }

    /**
     * Converts a time in milliseconds to a date.
     *
     * @param time Time in milliseconds,
     */
    public static String convertToDate(long time) {
        String until;
        // Format to 31.12.14 or local version of that
        DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault());
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(time);

        // Format to Week of Day, for example Mo. or local version of that
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE",
                Locale.getDefault());

        // Tab space because else the date is too far to the left
        until = (dateFormat.format(gc.getTime()) + ", " + f.format(gc.getTime()));
        return until;
    }

    /**
     * Converts a time in an int array to a date.
     *
     * @param time Time in an int array,
     */
    public static String convertToDate(int[] time) {
        String until;
        // Format to 31.12.14 or local version of that
        DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault());
        GregorianCalendar gc = new GregorianCalendar(time[0], time[1], time[2]);

        // Format to Week of Day, for example Mo. or local version of that
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE",
                Locale.getDefault());

        // Tab space because else the date is too far to the left
        until = (dateFormat.format(gc.getTime()) + ", " + f.format(gc.getTime()));
        return until;
    }

    /**
     * Converts a time in milliseconds to the time in milliseconds.
     *
     * @param time Time in an int array to a date.
     */
    public static long convertToMilliseconds(int[] time) {
        GregorianCalendar gc = new GregorianCalendar(time[0], time[1], time[2]);
        return gc.getTimeInMillis();
    }
}



