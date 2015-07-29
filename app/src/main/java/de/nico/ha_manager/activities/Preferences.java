package de.nico.ha_manager.activities;

/* 
 * @author Nico Alt
 * @author Devin
 * See the file "LICENSE" for the full license governing this code.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
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
import de.nico.ha_manager.helper.Theme;
import de.nico.ha_manager.helper.Utils;

public final class Preferences extends PreferenceActivity {

    /**
     * {@link android.content.Context} of this class.
     */
    private static Context c;

    /**
     * List with all homework databases.
     */
    private String[] list;

    @SuppressWarnings("deprecation")
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        Theme.set(this, false);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        c = this;

        setImportDialog();
        setBuildInfo();
        setLanguage();
        checkPreferences();
        Utils.setupActionBar(this, true);
    }

    @Override
    public final void onBackPressed() {
        startActivity(new Intent(Preferences.this, Main.class));
        finish();
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the content of the import {@link android.app.AlertDialog}.
     */
    @SuppressWarnings("deprecation")
    private void setImportDialog() {
        final ArrayList<String> mArray = getFiles(Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name));
        if (mArray != null)
            list = mArray.toArray(new String[mArray.size()]);
        final Preference importexport_import = findPreference("pref_importexport_import");
        importexport_import.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public final boolean onPreferenceClick(final Preference preference) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                alertDialog.setTitle(getString(R.string.pref_homework_import))
                        .setNegativeButton(getString(android.R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public final void onClick(
                                            final DialogInterface d, final int i) {
                                        d.dismiss();
                                    }
                                })
                        .setItems(list, new DialogInterface.OnClickListener() {
                            public final void onClick(final DialogInterface dialog, final int item) {
                                Homework.importIt(c, list[item]);
                            }
                        }).show();
                return true;
            }
        });
    }

    /**
     * Sets the information when the application was built.
     */
    @SuppressWarnings("deprecation")
    private void setBuildInfo() {
        // Get Build Info
        final String buildInfo = Utils.getBuildInfo(this);

        // Set Build Info
        final PreferenceScreen prefscreen = ((PreferenceScreen) findPreference("pref_about_current_version"));
        prefscreen.setSummary(buildInfo);
        onContentChanged();
    }

    /**
     * Sets the list with all available languages of the app.
     */
    @SuppressWarnings("deprecation")
    private void setLanguage() {
        final Preference language = findPreference("pref_app_language");

        // Locale of HW-Manager
        final Locale appLoc = getResources().getConfiguration().locale;

        // Locale of device
        final Locale devLoc = Locale.getDefault();

        if (devLoc.equals(appLoc))
            language.setSummary(getString(R.string.pref_language_default));
        else
            language.setSummary(appLoc.getDisplayLanguage(appLoc));
    }

    /**
     * Checks if a preference was clicked.
     */
    @SuppressWarnings("deprecation")
    private void checkPreferences() {
        final Preference language = findPreference("pref_app_language");
        language.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public final boolean onPreferenceClick(final Preference preference) {
                Utils.langSpinner(c);
                return true;
            }
        });

        final CheckBoxPreference pref_theme = (CheckBoxPreference) findPreference("theme");
        pref_theme.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public final boolean onPreferenceClick(final Preference preference) {
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    recreate();
                return true;
            }
        });

        final CheckBoxPreference pref_black = (CheckBoxPreference) findPreference("black");
        pref_black.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public final boolean onPreferenceClick(final Preference preference) {
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    recreate();
                return true;
            }
        });

        final Preference subjects_add = findPreference("subjects_add");
        subjects_add
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public final boolean onPreferenceClick(final Preference preference) {
                        final EditText input = new EditText(c);
                        input.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
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
                                            public final void onClick(
                                                    final DialogInterface d, final int i) {
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

        final Preference subjects_reset = findPreference("subjects_reset");
        subjects_reset
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public final boolean onPreferenceClick(final Preference preference) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                c);
                        alertDialog
                                .setTitle(getString(R.string.dialog_delete))
                                .setMessage(
                                        getString(R.string.dialog_really_delete_subs))
                                .setPositiveButton(
                                        (getString(android.R.string.yes)),
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public final void onClick(
                                                    final DialogInterface d, final int i) {
                                                Subject.setDefault(c);
                                            }

                                        })
                                .setNegativeButton(
                                        (getString(android.R.string.no)), null)
                                .show();
                        return true;
                    }
                });

        final Preference feedback_share = findPreference("feedback_share");
        feedback_share
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public final boolean onPreferenceClick(final Preference preference) {
                        return Utils.shareApp(c);
                    }
                });

        final Preference importexport_export = findPreference("pref_importexport_export");
        importexport_export
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public final boolean onPreferenceClick(final Preference preference) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
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
                                            public final void onClick(
                                                    final DialogInterface d, final int i) {
                                                Homework.exportIt(c, false);
                                                // Reload the Import dialog.
                                                setImportDialog();
                                            }

                                        })
                                .setNegativeButton(
                                        (getString(android.R.string.no)), null)
                                .show();
                        return true;

                    }

                });
    }

    /**
     * Gets a list of the *.db files in /sdcard/HW-Manager/
     * for the import dialog.
     */
    private ArrayList<String> getFiles(final String DirectoryPath) {
        final ArrayList<String> myFiles = new ArrayList<>();
        final File f = new File(DirectoryPath);

        f.mkdirs();
        final File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (final File file : files) {
                // We only want .db files here.
                if (FilenameUtils.getExtension(file.getName()).equals("db")) {
                    // Since the file extensions are all the same, we can just remove them.
                    final String mTrimmedFile = FilenameUtils.removeExtension(file.getName());
                    myFiles.add(mTrimmedFile);
                }
            }
        }
        // Make sure the list is in alphabetical order. It already will.
        Collections.sort(myFiles);
        // Reverse the order so newest is on top.
        Collections.reverse(myFiles);
        return myFiles;
    }
}
