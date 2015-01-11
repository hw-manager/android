package de.nico.ha_manager.activities;

/* 
 * Author: Nico Alt and Devin
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;

@SuppressLint("SimpleDateFormat")
public class AddHomework extends Activity {

    // String array containing the subjects
    private static String[] subjects;

    // Until when the homework has to be finished
    private static String until;

    // 0 is year, 1 is month and 2 is day
    private static int[] date;

    private static String ID = null;

    private static boolean getLarge(Context c) {
        int screenLayout = c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;

        // If Small or Normal
        if (screenLayout == 1 || screenLayout == 2)
            return false;

        // If Large or XLarge
        if (screenLayout == 3 || screenLayout == 4)
            return true;
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        subjects = Subject.get(this);
        date = getCurrentDate();

        setTextViewUntil(date);
        setSpinner();
        handleIntent(getIntent());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            if (!(getLarge(this)))
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

    private int[] getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        date = new int[3];

        // E.g "1970"
        date[0] = c.get(Calendar.YEAR);

        // E.g "01"
        date[1] = c.get(Calendar.MONTH);

        // Get current day, e.g. "01", plus one day > e.g. "02"
        date[2] = c.get(Calendar.DAY_OF_MONTH) + 1;

        return date;

    }

    private void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            // Set ID
            ID = extras.getString(Source.allColumns[0]);

            // Set Urgent
            if (!extras.getString(Source.allColumns[1]).equals("")) {
                CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_urgent);
                checkBox.setChecked(true);
            }

            // Set Subject
            String subject = extras.getString(Source.allColumns[2]);
            Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, subjects);
            // Get position in subject list
            int spinnerPostion = adapter.getPosition(subject);
            // If subject is not in subject list
            if (spinnerPostion == -1) {
                int size = subjects.length;
                String[] tmp = new String[size + 1];
                System.arraycopy(subjects, 0, tmp, 0, size);
                tmp[size] = subject;
                Arrays.sort(tmp);

                subjects = tmp;
                setSpinner();
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, subjects);
                spinnerPostion = adapter.getPosition(subject);
            }

            subSpin.setSelection(spinnerPostion);

            // Set Homework
            EditText hwEdit = (EditText) findViewById(R.id.editText_homework);
            hwEdit.setText(extras.getString(Source.allColumns[3]));

            // Set Until
            Button untilButton = (Button) findViewById(R.id.button_until);
            untilButton.setText(extras.getString(Source.allColumns[4]));
            until = extras.getString(Source.allColumns[4]);
        }
    }

    private void setTextViewUntil(int[] date) {
        // Format to 31.12.14 or local version of that
        DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault());
        GregorianCalendar gc = new GregorianCalendar(date[0], date[1], date[2]);
        until = f.format(gc.getTime());

        // Format to Week of Day, for example Mo. or local version of that
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE",
                Locale.getDefault());
        String asWeek = dateFormat.format(gc.getTime());

        // Tab space because else the date is too far to the left
        until = (asWeek + ", " + until);
        Button untilButton = (Button) findViewById(R.id.button_until);
        untilButton.setText(until);

    }

    private void setSpinner() {
        // Set spinner with subjects
        Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subSpin.setAdapter(adapter);
    }

    public void setUntil(View v) {
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date[0] = year;
                        date[1] = monthOfYear;
                        date[2] = dayOfMonth;
                        setTextViewUntil(date);

                    }

                }, date[0], date[1], date[2]);

        dpd.show();
    }

    public void addHomework(View v) {
        Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
        EditText hwEdit = (EditText) findViewById(R.id.editText_homework);

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

        // Entry in database
        Homework.add(this, ID, urgent, subject, homework, until);

        finish();
    }

}
