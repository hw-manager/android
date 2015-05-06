package de.nico.ha_manager.helper;

/*
 * @author Nico Alt
 * See the file "LICENSE" for the full license governing this code.
 */

import android.content.Context;
import android.widget.Toast;

class CustomToast {

    /**
     * Shows a short Toast.
     *
     * @param c   Needed for {@link android.widget.Toast}.
     * @param msg Message to show.
     */
    public static void showShort(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a long Toast.
     *
     * @param c   Needed for {@link android.widget.Toast}.
     * @param msg Message to show.
     */
    public static void showLong(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }
}
