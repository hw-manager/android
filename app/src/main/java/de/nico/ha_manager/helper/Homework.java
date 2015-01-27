package de.nico.ha_manager.helper;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;

public class Homework {

    public static void deleteAll(final Context c) {
        Source s = new Source(c);
        s.delete_item(null);
    }

    public static void deleteOne(Context c, String ID) {
        Source s = new Source(c);
        s.delete_item(ID);
    }

    public static void add(Context c, String ID, String urgent, String subject,
                           String homework, String until) {
        try {
            Source s = new Source(c);
            s.open();
            s.createEntry(c, ID, urgent, subject, homework, until);
            s.close();
        } catch (Exception ex) {
            Log.e("Database:", ex.toString());
        }

    }

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
                + "/databases/Homework.db");

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
                + "/databases/Homework.db");
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
