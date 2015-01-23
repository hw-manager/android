package de.nico.ha_manager.helper;

/*
 * Author: Nico Alt and Devin
 * See the file "LICENSE.txt" for the full license governing this code.
 *
 * Original code by Shane Tully located at: http://bit.ly/1AFDKgt
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
public class ActionBarWrapper {
    private ActionBar actionBar;

    /**
     * Check if android.app.ActionBar exists and throw an error if not
     */
    static {
        try {
            Class.forName("android.app.ActionBar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Since the method uses the FragmentActivity class in a cast, we need
     * to manually switch for our PreferenceActivity until we can get
     * a PreferenceFragment working.
     *
     * @param context
     * @param prefActivity
     */

    public ActionBarWrapper(Context context, boolean prefActivity) {
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

    public void setBackgroundDrawable(Drawable background) {
        if (actionBar != null)
            actionBar.setBackgroundDrawable(background);
    }

    public void setDisplayShowTitleEnabled(boolean showTitle) {
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(showTitle);
    }

    public void setDisplayUseLogoEnabled(boolean useLogo) {
        if (actionBar != null)
            actionBar.setDisplayUseLogoEnabled(useLogo);
    }

    public void setDisplayHomeAsUpEnabled(boolean homeAsUpEnabled) {
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }
}
