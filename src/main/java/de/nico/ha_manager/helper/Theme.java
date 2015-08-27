package de.nico.ha_manager.helper;

/*
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.nico.ha_manager.R;

public final class Theme {

    /**
     * Sets the theme.
     *
     * @param c          {@link android.app.Activity} - It would be Context, but we need to
     *                   set the window background of the Activity, so Activity extends Context, so it
     *                   works out.
     * @param isAddTheme Since we use the DialogWhenLarge theme on tablets in the add
     *                   activity, we need to incorporate that.
     */
    public static void set(final Activity c, final boolean isAddTheme) {
        final SharedPreferences prefs = PreferenceManager
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
}
