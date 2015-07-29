package de.nico.ha_manager;

/* 
 * @author Nico Alt
 * See the file "LICENSE" for the full license governing this code.
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

public final class HWManager extends Application {

    /**
     * Update the language used in the app.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    public static void updateLanguage(final Context c) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(c);
        final String lang = prefs.getString("locale_override", "");
        updateLanguage(c, lang);
    }

    /**
     * Update the language used in the app.
     *
     * @param c    Needed to get the resources.
     * @param lang Language to be used in the app,
     */
    private static void updateLanguage(final Context c, final String lang) {
        final Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        c.getResources().updateConfiguration(cfg, null);
    }

    @Override
    public final void onCreate() {
        updateLanguage(this);
        super.onCreate();
    }
}