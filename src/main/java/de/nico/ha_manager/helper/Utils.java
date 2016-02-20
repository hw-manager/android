package de.nico.ha_manager.helper;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

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
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.nico.ha_manager.HWManager;
import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;

public final class Utils {

    private static boolean isActionBarAvailable = false;

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

    public static void setupActionBar(final Context context, final boolean isPreferenceActivity) {
        if (Build.VERSION.SDK_INT >= 11 && isActionBarAvailable) {
            final ActionBarWrapper actionBarWrapper = new ActionBarWrapper(context, isPreferenceActivity);
            actionBarWrapper.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Sends an Intent with a text to share the app.
     *
     * @param c Needed for {@link android.content.Intent}.
     */
    @SuppressWarnings("deprecation")
    public static boolean shareApp(final Context c) {
        final String share_title = c.getString(R.string.intent_share_title);
        final String app_name = c.getString(R.string.app_name);

        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT,
                c.getString(R.string.intent_share_text));
        try {
            c.startActivity(Intent.createChooser(intent, share_title + " "
                    + app_name));
            return true;
        } catch (final ActivityNotFoundException e) {
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
    public static String getBuildInfo(final Context c) {
        String buildInfo = "Built with love.";
        try {
            // Get Version Name
            final PackageInfo pInfo = c.getPackageManager().getPackageInfo(
                    c.getPackageName(), 0);
            final String versionName = pInfo.versionName;

            // Get build time
            final ApplicationInfo aInfo = c.getPackageManager().getApplicationInfo(
                    c.getPackageName(), 0);
            final ZipFile zf = new ZipFile(aInfo.sourceDir);
            final ZipEntry ze = zf.getEntry("classes.dex");
            zf.close();
            final long time = ze.getTime();
            final DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                    Locale.getDefault());
            final String buildDate = f.format(time);

            buildInfo = versionName + " (" + buildDate + ")";

        } catch (final Exception e) {
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
        final AlertDialog.Builder b = new Builder(c);
        // Current translations of HW-Manager
        final String[] languages = {"cs", "de", "en", "es", "fr", "hu", "tr", "ja", "ar", "fa"};
        // Items with translation's language
        final String[] items = new String[languages.length + 1];
        items[0] = c.getString(R.string.pref_language_default);
        for (int i = 1; i < languages.length + 1; i++) {
            final Locale appLoc = new Locale(languages[i - 1]);
            items[i] = appLoc.getDisplayLanguage(appLoc);
        }
        b.setTitle(c.getString(R.string.pref_language));
        b.setItems(items, new OnClickListener() {

            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
                final SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(c);
                final SharedPreferences.Editor editor = prefs.edit();

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
     * @param e       {@link android.widget.ExpandableListAdapter} which contains the homework.
     * @param hwArray {@link java.util.ArrayList} which contains the homework.
     */
    public static void crossOut(final ExpandableListAdapter e, final ArrayList<HashMap<String, String>> hwArray) {
        for (int i = 0; i < hwArray.size(); i++) {
            if (!hwArray.get(i).get(Source.allColumns[6]).equals("")) {
                final View v = e.getGroupView(i, false, null, null);

                final TextView tv1 = (TextView) v.findViewById(R.id.textView_subject);
                final TextView tv2 = (TextView) v.findViewById(R.id.textView_until);
                final TextView tv3 = (TextView) v.findViewById(R.id.textView_homework);
                final TextView tv4 = (TextView) v.findViewById(R.id.textView_urgent);
                tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv2.setPaintFlags(tv2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv3.setPaintFlags(tv3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv4.setPaintFlags(tv4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    /**
     * Cross out one solved homework.
     *
     * @param hwArray {@link java.util.ArrayList} which contains the homework.
     * @param pos     Position of solved homework.
     */
    public static boolean crossOneOut(final Context c, final ArrayList<HashMap<String, String>> hwArray, final int pos) {
        try {
            final String ID = "ID = " + hwArray.get(pos).get(Source.allColumns[0]);
            final String title = hwArray.get(pos).get(Source.allColumns[1]);
            final String subject = hwArray.get(pos).get(Source.allColumns[2]);
            final long time = Long.valueOf(hwArray.get(pos).get(Source.allColumns[5])).longValue();
            final String info = hwArray.get(pos).get(Source.allColumns[3]);
            final String urgent = hwArray.get(pos).get(Source.allColumns[4]);
            final String completed = "completed";
            Homework.add(c, ID, title, subject, time, info, urgent, completed);
            return true;
        } catch (final Exception e) {
            Log.e("Utils.crossOneOut", e.toString());
            return false;
        }
    }

    /**
     * Transfers a file.
     *
     * @param src Source from where the file has to be transferred.
     * @param dst Source to where the file has to be transferred.
     */
    public static boolean transfer(final File src, final File dst) {
        try {
            final FileInputStream inStream = new FileInputStream(src);
            final FileOutputStream outStream = new FileOutputStream(dst);
            final FileChannel inChannel = inStream.getChannel();
            final FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            return true;
        } catch (final FileNotFoundException e) {
            Log.e("FileNotFoundException", e.toString());
            return false;
        } catch (final IOException e) {
            Log.e("IOException", e.toString());
            return false;
        }
    }
}



