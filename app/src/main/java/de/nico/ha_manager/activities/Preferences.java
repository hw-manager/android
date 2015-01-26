package de.nico.ha_manager.activities;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import de.nico.ha_manager.R;
import de.nico.ha_manager.helper.FilenameUtils;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;
import de.nico.ha_manager.helper.Utils;

public class Preferences extends PreferenceActivity {

    private static Context c;
    private String[] list;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        c = this;
        ArrayList<String> mArray = getFiles(Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name));
        if (mArray != null)
            list = mArray.toArray(new String[mArray.size()]);


        setBuildInfo();
        setLanguage();
        checkPreferences();
        Utils.setupActionBar(this, true);
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

    @SuppressWarnings("deprecation")
    private void setBuildInfo() {
        // Get Build Info
        String buildInfo = Utils.getBuildInfo(this);

        // Set Build Info
        PreferenceScreen prefscreen = ((PreferenceScreen) findPreference("pref_about_current_version"));
        prefscreen.setSummary(buildInfo);
        onContentChanged();
    }

    @SuppressWarnings("deprecation")
    private void setLanguage() {
        Preference language = findPreference("pref_app_language");

        // Locale of HW-Manager
        Locale appLoc = getResources().getConfiguration().locale;

        // Locale of device
        Locale devLoc = Locale.getDefault();

        if (devLoc.equals(appLoc)) {
            language.setSummary(getString(R.string.pref_language_default));
        } else {
            language.setSummary(appLoc.getDisplayLanguage(appLoc));
        }
    }

    @SuppressWarnings("deprecation")
    private void checkPreferences() {
        Preference language = findPreference("pref_app_language");
        language.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.langSpinner(c);
                return true;
            }
        });

        Preference subjects_add = findPreference("subjects_add");
        subjects_add
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final EditText input = new EditText(c);
                        input.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                c);
                        alertDialog
                                .setTitle(getString(R.string.dialog_addSubject))
                                .setMessage(
                                        getString(R.string.dialog_addSubject_message))
                                .setView(input)
                                .setPositiveButton(
                                        getString(android.R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface d, int i) {
                                                Subject.add(c, input.getText()
                                                        .toString());
                                            }
                                        })
                                .setNegativeButton(
                                        (getString(android.R.string.no)), null)
                                .show();
                        return true;
                    }
                });

        Preference subjects_reset = findPreference("subjects_reset");
        subjects_reset
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                c);
                        alertDialog
                                .setTitle(getString(R.string.dialog_delete))
                                .setMessage(
                                        getString(R.string.dialog_really_delete_subs))
                                .setPositiveButton(
                                        (getString(android.R.string.yes)),
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface d, int i) {
                                                Subject.setDefault(c);
                                            }

                                        })
                                .setNegativeButton(
                                        (getString(android.R.string.no)), null)
                                .show();
                        return true;
                    }
                });

        Preference feedback_share = findPreference("feedback_share");
        feedback_share
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        return Utils.shareApp(c);
                    }
                });

        Preference importexport_import = findPreference("pref_importexport_import");
        importexport_import.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                alertDialog.setTitle(getString(R.string.pref_homework_import))
                        .setNegativeButton(getString(android.R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface d, int i) {
                                        d.dismiss();
                                    }
                                })
                        .setItems(list, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Homework.importIt(c, list[item]);
                            }
                        }).show();
                return true;
            }
        });

        Preference importexport_export = findPreference("pref_importexport_export");
        importexport_export
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                c);
                        alertDialog
                                .setTitle(
                                        getString(R.string.pref_homework_export))
                                .setMessage(
                                        getString(R.string.dialog_export_message))
                                .setPositiveButton(
                                        (getString(android.R.string.yes)),
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface d, int i) {
                                                Homework.exportIt(c);
                                            }

                                        })
                                .setNegativeButton(
                                        (getString(android.R.string.no)), null)
                                .show();
                        return true;

                    }

                });
    }

    public ArrayList<String> getFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (File file : files) {
                if (FilenameUtils.getExtension(file.getName()).equals("db")) {
                    String mTrimmedFile = FilenameUtils.removeExtension(file.getName());
                    MyFiles.add(mTrimmedFile);
                }
            }
        }

        Collections.sort(MyFiles);
        return MyFiles;
    }
}
