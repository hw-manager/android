package de.nico.ha_manager.activities;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;

import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;
import de.nico.ha_manager.helper.Converter;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;
import de.nico.ha_manager.helper.Theme;
import de.nico.ha_manager.helper.Utils;

@SuppressLint("SimpleDateFormat")
/**
 * Shows a page to add a homework.
 */
public class AddHomework extends FragmentActivity {

    /**
     * String array containing the subjects
     */
    private static String[] subjects;

    /**
     * 0 is year, 1 is month and 2 is day
     */
    private static int[] date;

    /**
     * Time in milliseconds
     */
    private static long time;

    /**
     * ID of the homework entry in the database
     */
    private static String ID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.set(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        subjects = Subject.get(this);
        date = getDate(0);

        setUntilTV(date);
        setSpinner();
        handleIntent(getIntent());
        Utils.setupActionBar(this, false);
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

    /**
     * @param time If it's 0, current time will be used.
     * @return Int array where 0 is year, 1 is month and 2 is day.
     */
    private int[] getDate(long time) {
        final Calendar c = Calendar.getInstance();
        if (time != 0)
            c.setTimeInMillis(time);

        int[] tmpDate = new int[3];

        // E.g "1970"
        tmpDate[0] = c.get(Calendar.YEAR);

        // E.g "01"
        tmpDate[1] = c.get(Calendar.MONTH);

        // Get current day, e.g. "01", plus one day > e.g. "02"
        tmpDate[2] = c.get(Calendar.DAY_OF_MONTH) + 1;

        if (time != 0)
            tmpDate[2] = c.get(Calendar.DAY_OF_MONTH);

        return tmpDate;
    }

    /**
     * Sets the button with the date until the homework has to be done.
     *
     * @param date If it's 0, current time will be used.
     */
    private void setUntilTV(int[] date) {
        String until = Converter.toDate(date);
        TextView untilTV = (TextView) findViewById(R.id.button_until);
        untilTV.setText(until);
        time = Converter.toMilliseconds(date);
    }

    /**
     * Sets spinner with subjects.
     */
    private void setSpinner() {
        Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subSpin.setAdapter(adapter);
    }

    /**
     * Handles information from an intent.
     *
     * @param intent Intent with information
     */
    private void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            // Set ID
            ID = extras.getString(Source.allColumns[0]);

            // Set Title
            EditText hwEdit = (EditText) findViewById(R.id.editText_homework);
            hwEdit.setText(extras.getString(Source.allColumns[1]));

            // Set Subject
            String subject = extras.getString(Source.allColumns[2]);
            Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, subjects);
            // Get position in subject list
            int spinnerPosition = adapter.getPosition(subject);
            // If subject is not in subject list
            if (spinnerPosition == -1) {
                int size = subjects.length;
                String[] tmp = new String[size + 1];
                System.arraycopy(subjects, 0, tmp, 0, size);
                tmp[size] = subject;
                Arrays.sort(tmp);

                subjects = tmp;
                setSpinner();
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, subjects);
                spinnerPosition = adapter.getPosition(subject);
            }
            subSpin.setSelection(spinnerPosition);

            // Set Info
            EditText infoEdit = (EditText) findViewById(R.id.editText_info);
            infoEdit.setText(extras.getString(Source.allColumns[3]));

            // Set Urgent
            if (!extras.getString(Source.allColumns[4]).equals("")) {
                CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_urgent);
                checkBox.setChecked(true);
            }

            // Set Until
            time = Long.valueOf(extras.getString(Source.allColumns[5])).longValue();
            date = getDate(time);
            setUntilTV(date);

            // Change the "Add" button to "Save"
            Button mAdd = (Button) findViewById(R.id.button_add);
            mAdd.setText(R.string.hw_save);
        }
    }

    /**
     * Sets button with {@link android.app.DatePickerDialog}.
     *
     * @param v Needed because method is called from layout
     */
    public void setUntil(View v) {
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date[0] = year;
                        date[1] = monthOfYear;
                        date[2] = dayOfMonth;
                        setUntilTV(date);

                    }

                }, date[0], date[1], date[2]);

        dpd.show();
    }

    /**
     * Adds the homework to the database and finishes the activity.
     *
     * @param v Needed because method is called from layout
     */
    public void addHomework(View v) {
        Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
        EditText hwEdit = (EditText) findViewById(R.id.editText_homework);
        EditText infoEdit = (EditText) findViewById(R.id.editText_info);

        // Close keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(hwEdit.getWindowToken(), 0);

        // If nothing filled in -> cancel
        if (hwEdit.getText().toString().length() == 0) {
            hwEdit.setError(getString(R.string.toast_have2enter));
            return;
        }

        // Urgent?
        String urgent;
        CheckBox urgentCheck = (CheckBox) findViewById(R.id.checkBox_urgent);
        if (urgentCheck.isChecked())
            urgent = getString(R.string.action_urgent);
        else
            urgent = "";

        // Get filled in data
        String subject = subSpin.getSelectedItem().toString();
        String homework = hwEdit.getText().toString();
        String info = infoEdit.getText().toString();

        // Entry in database
        Homework.add(this, ID, homework, subject, time, info, urgent, "");

        // Auto-export
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoExport = prefs.getBoolean("pref_autoexport", false);
        if (autoExport)
            Homework.exportIt(this, true);

        finish();
    }
}


