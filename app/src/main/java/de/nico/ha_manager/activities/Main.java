package de.nico.ha_manager.activities;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;
import de.nico.ha_manager.helper.Utils;

public class Main extends Activity {

    private static ArrayList<HashMap<String, String>> hwArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle(getString(R.string.title_homework));
        update();

        if (!(Subject.get(this).length > 0))
            Subject.setDefault(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        update();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Preferences.class));
                return true;

            case R.id.action_delete:
                deleteAll();
                return true;

            case R.id.action_add:
                startActivity(new Intent(this, AddHomework.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getString(R.string.dialog_edit));
        menu.add(0, v.getId(), 1, getString(R.string.dialog_delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getTitle() == getString(R.string.dialog_edit)) {
            editOne(hwArray, info.position);
            return true;
        }
        if (item.getTitle() == getString(R.string.dialog_delete)) {
            deleteOne(hwArray, info.position);
            update();
            return true;
        }
        return false;

    }

    private void update() {
        // Remove old content
        hwArray.clear();
        Source s = new Source(this);

        // Get content from SQLite Database
        try {
            s.open();
            hwArray = s.get();
            s.close();
        } catch (Exception ex) {
            Log.e("Update Homework List:", ex.toString());
        }

        ListView hwList = (ListView) findViewById(R.id.listView_main);
        hwList.setAdapter(Utils.entryAdapter(this, hwArray));
        registerForContextMenu(hwList);

    }

    private void editOne(ArrayList<HashMap<String, String>> ArHa, int pos) {
        final String currentID = "ID = " + ArHa.get(pos).get("ID");
        Intent intent = new Intent(this, AddHomework.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Source.allColumns[0], currentID);
        for (int i = 1; i < 5; i++)
            mBundle.putString(Source.allColumns[i],
                    ArHa.get(pos).get(Source.allColumns[i]));
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    private void deleteOne(ArrayList<HashMap<String, String>> ArHa, int pos) {
        ArrayList<HashMap<String, String>> tempArray = Utils.tempArray(ArHa,
                pos);
        final String currentID = "ID = " + ArHa.get(pos).get("ID");
        SimpleAdapter alertAdapter = Utils.entryAdapter(this, tempArray);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog
                .setTitle(getString(R.string.dialog_delete))
                .setAdapter(alertAdapter, null)
                .setPositiveButton((getString(android.R.string.yes)),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface d, int i) {
                                Homework.deleteOne(Main.this, currentID);
                                update();

                            }

                        })
                .setNegativeButton((getString(android.R.string.no)), null)
                .show();
    }

    private void deleteAll() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog
                .setTitle(getString(R.string.dialog_delete))
                .setMessage(getString(R.string.dialog_really_delete_hw))
                .setPositiveButton((getString(android.R.string.yes)),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface d, int i) {
                                Homework.deleteAll(Main.this);
                                update();
                            }
                        })
                .setNegativeButton((getString(android.R.string.no)), null)
                .show();
    }

}