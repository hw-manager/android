package de.nico.ha_manager.activities;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import de.nico.ha_manager.R;
import de.nico.ha_manager.helper.Utils;

public class About extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        update();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        // Get Build Info
        String appName = getString(R.string.app_name);
        String buildInfo = Utils.getBuildInfo(this);

        TextView tv = (TextView) findViewById(R.id.about_title);
        tv.setText(appName + " " + buildInfo);

        TextView contentView = (TextView) findViewById(R.id.about_content);
        contentView
                .setText(Html.fromHtml(getString(R.string.about_us_content)));
    }

}