package de.nico.ha_manager.activities;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.nico.ha_manager.R;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;
import de.nico.ha_manager.helper.Utils;

public class Subjects extends FragmentActivity {

	private static boolean wasChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setTheme(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        update();
        Utils.setupActionBar(this, false);
    }
	
	@Override
	public void onBackPressed() {
		if (wasChanged) {
			// Auto-export
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			boolean autoExport = prefs.getBoolean("pref_autoexport", false);
			if (autoExport)
				Homework.exportIt(this, true);
		}
		super.onBackPressed();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        String[] subjects = Subject.get(this);

        // Make simple list containing subjects
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, subjects);

        ListView subList = (ListView) findViewById(R.id.listView_main);
        subList.setAdapter(subAdapter);

        subList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int pos, long id) {

                String item = ((TextView) v).getText().toString();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        Subjects.this);
                alertDialog
                        .setTitle(
                                getString(R.string.dialog_delete) + ": " + item)
                        .setPositiveButton((getString(android.R.string.yes)),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface d, int i) {
                                        Subject.delete(Subjects.this, pos);
										wasChanged = true;
                                        update();
                                    }
                                })
                        .setNegativeButton((getString(android.R.string.no)),
                                null).show();

            }

        });

    }

}
