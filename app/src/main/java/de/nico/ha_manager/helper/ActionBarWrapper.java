package de.nico.ha_manager.helper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.preference.PreferenceActivity;

/**
 * From @link{http://bit.ly/1AFDKgt}
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
	 * A static function that can be called to force the static
     * initialization of this class
	 */
    public static void isAvailable() {}

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
			actionBar = ((PreferenceActivity)context).getActionBar();
		}
		else {
			// FragmentActivity
        	actionBar = ((FragmentActivity)context).getActionBar();
		}
    }

    /**
	 * Basic core ActionBar functions
	 */

    public void setBackgroundDrawable(Drawable background) {
        actionBar.setBackgroundDrawable(background);
    }

    public void setDisplayShowTitleEnabled(boolean showTitle) {
        actionBar.setDisplayShowTitleEnabled(showTitle);
    }

    public void setDisplayUseLogoEnabled(boolean useLogo) {
        actionBar.setDisplayUseLogoEnabled(useLogo);
    }

    public void setDisplayHomeAsUpEnabled(boolean homeAsUpEnabled) {
        actionBar.setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }
}
