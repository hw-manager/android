package de.nico.ha_manager.activities;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import de.nico.ha_manager.R;
import de.nico.ha_manager.helper.Constants;
import de.nico.ha_manager.helper.Theme;
import de.nico.ha_manager.helper.Utils;

/**
 * Shows an About page
 */
public final class About extends FragmentActivity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.set(this, false);
        setContentView(R.layout.activity_about);
        update();
        Utils.setupActionBar(this, false);
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the content of the about page.
     */
    private void update() {
        // Get Build Info
        final String appName = getString(R.string.app_name);
        final String buildInfo = Utils.getBuildInfo(this);

        final TextView tv = (TextView) findViewById(R.id.about_title);
        tv.setText(appName + " " + buildInfo);

        final TextView contentView = (TextView) findViewById(R.id.about_content);
        contentView
                .setText(Html.fromHtml(Constants.about_us_content));
    }
}
