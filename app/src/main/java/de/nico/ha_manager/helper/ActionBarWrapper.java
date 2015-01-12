package de.nico.ha_manager.helper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;

/**
 * From @link{http://bit.ly/1AFDKgt}
 */

@TargetApi(11)
public class ActionBarWrapper {
    private ActionBar actionBar;

    // Check if android.app.ActionBar exists and throw an error if not
    static {
        try {
            Class.forName("android.app.ActionBar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ActionBarWrapper(Context context) {
        actionBar = ((FragmentActivity) context).getActionBar();
    }

    // A static function that can be called to force the static
    // initialization of this class
    public static void isAvailable() {
    }

    // Wrapper functions
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
