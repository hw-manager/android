package de.nico.ha_manager.helper;

/*
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 *
 * Original code by Shane Tully located at:
 * https://shanetully.com/2011/10/android-3-0-actionbar-class-maintaining-compatibility-with-pre-android-3-0-apps/
 */

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;

/**
 * Fixes a nasty VerifyError crash with getActionBar()
 * on Android versions lower than 3.0.
 */

@TargetApi(11)
@SuppressWarnings("unused")
public final class ActionBarWrapper {
    /**
     * Check if android.app.ActionBar exists and throw an error if not
     */
    static {
        try {
            Class.forName("android.app.ActionBar");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Indicates if ActionBar is available.
     */
    private ActionBar actionBar;

    /**
     * This method gets an instance on the Action Bar working.
     *
     * @param context      Context from the Activity
     * @param prefActivity Because of the app needing the cast, we need
     *                     to change it to cast to PreferenceActivity instead
     *                     of FragmentActivity
     */
    public ActionBarWrapper(final Context context, final boolean prefActivity) {
        if (prefActivity) {
            // PreferenceActivity
            actionBar = ((PreferenceActivity) context).getActionBar();
        } else {
            // FragmentActivity
            actionBar = ((FragmentActivity) context).getActionBar();
        }
    }

    /**
     * A static function that can be called to force the static
     * initialization of this class
     */
    public static void isAvailable() {
    }

    /**
     * Basic core ActionBar functions
     */

    public final void setBackgroundDrawable(final Drawable background) {
        if (actionBar != null)
            actionBar.setBackgroundDrawable(background);
    }

    public final void setDisplayShowTitleEnabled(final boolean showTitle) {
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(showTitle);
    }

    public final void setDisplayUseLogoEnabled(final boolean useLogo) {
        if (actionBar != null)
            actionBar.setDisplayUseLogoEnabled(useLogo);
    }

    public final void setDisplayHomeAsUpEnabled(final boolean homeAsUpEnabled) {
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }
}
