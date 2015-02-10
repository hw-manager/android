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

import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Helper;
import de.nico.ha_manager.database.Source;

public class Homework {

    /**
     * Deletes all homework.
     *
     * @param c Needed by {@link de.nico.ha_manager.database.Source}.
     */
    public static void deleteAll(final Context c) {
        Source s = new Source(c);
        s.delete_item(null);
    }

    /**
     * Deletes one homework.
     *
     * @param c Needed by {@link de.nico.ha_manager.database.Source}.
     */
    public static void deleteOne(Context c, String ID) {
        Source s = new Source(c);
        s.delete_item(ID);
    }

    /**
     * Adds a homework.
     *
     * @param c       Needed by {@link de.nico.ha_manager.database.Source}.
     * @param ID      The ID used in the database.
     * @param title   The title of the homework.
     * @param subject The subject of the homework.
     * @param time    The time until the homework has to be done.
     * @param info    Additional information to the homework.
     * @param urgent  Is it urgent?
     */
    public static void add(Context c, String ID, String title, String subject, long time, String info, String urgent) {
        try {
            Source s = new Source(c);
            s.open();
            s.createEntry(c, ID, title, subject, time, info, urgent);
            s.close();
        } catch (Exception ex) {
            Log.e("Database", ex.toString());
        }
    }

    /**
     * Imports a homework database.
     *
     * @param c        Needed by {@link de.nico.ha_manager.helper.Utils}.
     * @param filename The database to import.
     */
    public static void importIt(Context c, String filename) {
        // Check if directory exists
        File dir = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name));
        if (!(dir.exists())) {
            Utils.makeLongToast(
                    c,
                    c.getString(R.string.toast_nobackup)
                            + c.getString(R.string.app_name));
            return;
        }

        // Path for Database
        File srcDB = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name) + "/" + filename + ".db");
        File dstDB = new File(c.getApplicationInfo().dataDir
                + "/databases/" + Helper.DATABASE_NAME);

        // Check if Database exists
        if (!(srcDB.exists())) {
            Utils.makeLongToast(
                    c,
                    c.getString(R.string.toast_nobackup)
                            + c.getString(R.string.app_name));
            return;
        }

        if (Utils.transfer(srcDB, dstDB))
            Utils.makeShortToast(c, c.getString(R.string.toast_import_success));
        else
            Utils.makeShortToast(c, c.getString(R.string.toast_import_fail));
    }

    /**
     * Exports the homework database.
     *
     * @param c    Needed by {@link de.nico.ha_manager.helper.Utils}.
     * @param auto Indicates if it's an automatic backup.
     */
    public static void exportIt(Context c, boolean auto) {
        // Check if directory exists
        File dir = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name));
        if (!(dir.exists()))
            dir.mkdir();

        String stamp = new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date());

        if (auto)
            stamp = "auto-backup";

        // Path for Database
        File srcDB = new File(c.getApplicationInfo().dataDir
                + "/databases/" + Helper.DATABASE_NAME);
        File dstDB = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name) + "/Homework-" + stamp + ".db");

        if (dstDB.exists())
            dstDB.delete();

        if (Utils.transfer(srcDB, dstDB) && !auto)
            Utils.makeShortToast(c, c.getString(R.string.toast_export_success));
        else if (!auto)
            Utils.makeShortToast(c, c.getString(R.string.toast_export_fail));
    }
}
