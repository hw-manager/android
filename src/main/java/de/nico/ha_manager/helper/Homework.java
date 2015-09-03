package de.nico.ha_manager.helper;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Helper;
import de.nico.ha_manager.database.Source;

public final class Homework {

    /**
     * Deletes one homework.
     *
     * @param c  Needed by {@link de.nico.ha_manager.database.Source}.
     * @param ID ID of homework to get deleted. If set to null, all homework will get deleted.
     */
    public static void delete(final Context c, final String ID) {
        final Source s = new Source(c);
        s.delete_item(ID);
    }

    /**
     * Deletes multiple homework.
     *
     * @param c  Needed by {@link de.nico.ha_manager.database.Source}.
     * @param IDs IDs of homework to get deleted. If set to null, all homework will get deleted.
     */
    public static void delete(final String[] IDs, final Context c) {
        for (String ID : IDs) {
            delete(c, ID);
        }
    }

    /**
     * Adds a homework.
     *
     * @param c         Needed by {@link de.nico.ha_manager.database.Source}.
     * @param ID        The ID used in the database.
     * @param title     The title of the homework.
     * @param subject   The subject of the homework.
     * @param time      The time until the homework has to be done.
     * @param info      Additional information to the homework.
     * @param urgent    Is it urgent?
     * @param completed Is it completed?
     */
    public static void add(final Context c, final String ID, final String title,
                           final String subject, final long time, final String info,
                           final String urgent, final String completed) {
        try {
            final Source s = new Source(c);
            s.open();
            s.createEntry(c, ID, title, subject, time, info, urgent, completed);
            s.close();
        } catch (final Exception ex) {
            Log.e("Database", ex.toString());
        }
    }

    /**
     * Imports a homework database.
     *
     * @param c        Needed by {@link de.nico.ha_manager.helper.Utils}.
     * @param filename The database to import.
     */
    public static void importIt(final Context c, final String filename) {
        // Check if directory exists
        final File dir = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name));
        if (!(dir.exists())) {
            CustomToast.showLong(
                    c,
                    c.getString(R.string.toast_nobackup)
                            + c.getString(R.string.app_name));
            return;
        }

        // Path for Database
        final File srcDB = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name) + "/" + filename + ".db");
        final File dstDB = new File(c.getApplicationInfo().dataDir
                + "/databases/" + Helper.DATABASE_NAME);

        // Check if Database exists
        if (!(srcDB.exists())) {
            CustomToast.showLong(
                    c,
                    c.getString(R.string.toast_nobackup)
                            + c.getString(R.string.app_name));
            return;
        }

        if (Utils.transfer(srcDB, dstDB))
            CustomToast.showShort(c, c.getString(R.string.toast_import_success));
        else
            CustomToast.showShort(c, c.getString(R.string.toast_import_fail));
    }

    /**
     * Exports the homework database.
     *
     * @param c    Needed by {@link de.nico.ha_manager.helper.Utils}.
     * @param auto Indicates if it's an automatic backup.
     */
    public static void exportIt(final Context c, final boolean auto) {
        // Check if directory exists
        final File dir = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name));
        if (!(dir.exists()))
            dir.mkdir();

        String stamp = new SimpleDateFormat("yyyy-MM-dd-hh-mm", Locale.US).format(new Date());

        if (auto)
            stamp = "auto-backup";

        // Path for Database
        final File srcDB = new File(c.getApplicationInfo().dataDir
                + "/databases/" + Helper.DATABASE_NAME);
        final File dstDB = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name) + "/Homework-" + stamp + ".db");

        if (dstDB.exists())
            dstDB.delete();

        if (Utils.transfer(srcDB, dstDB) && !auto)
            CustomToast.showShort(c, c.getString(R.string.toast_export_success));
        else if (!auto)
            CustomToast.showShort(c, c.getString(R.string.toast_export_fail));
    }
}
