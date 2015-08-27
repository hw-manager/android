package de.nico.ha_manager.helper;

/*
 * @author Nico Alt
 * See the file "LICENSE" for the full license governing this code.
 */

import android.content.Context;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nico.ha_manager.R;

public final class CustomAdapter {

    /**
     * Returns a SimpleAdapter that uses the layout "listview_entry".
     *
     * @param c Needed for {@link android.widget.SimpleAdapter}.
     * @param a ArrayList with HashMaps to show with the adapter.
     */
    public static SimpleAdapter entry(final Context c, final ArrayList<HashMap<String, String>> a) {
        // All TextViews in Layout "listview_entry"
        final int[] i = {R.id.textView_urgent, R.id.textView_subject,
                R.id.textView_homework, R.id.textView_until};
        final String[] columns = {"URGENT", "SUBJECT", "HOMEWORK", "UNTIL"};

        // Make a SimpleAdapter which is like a row in the homework list
        return new SimpleAdapter(c, a, R.layout.listview_entry, columns, i);
    }

    /**
     * Returns a SimpleExpandableListAdapter that uses the layout "listview_expanded_entry1".
     *
     * @param c Needed for {@link android.widget.SimpleExpandableListAdapter}.
     * @param a ArrayList with HashMaps to show with the adapter.
     */
    public static SimpleExpandableListAdapter expandableEntry(final Context c, final ArrayList<HashMap<String, String>> a) {
        // All TextViews in Layout "listview_expanded_entry1"
        final int[] groupTexts = {R.id.textView_urgent, R.id.textView_subject,
                R.id.textView_homework, R.id.textView_until};
        final String[] groupColumns = {"URGENT", "SUBJECT", "HOMEWORK", "UNTIL"};

        // All TextViews in Layout "listview_expanded_entry2"
        final int[] childTexts = {R.id.textView_info};
        final String[] childColumns = {"INFO"};
        final List<List<Map<String, String>>> childData = covertToListListMap(a, childColumns[0]);

        // Make a SimpleAdapter which is like a row in the homework list
        return new SimpleExpandableListAdapter(c, a, R.layout.listview_expanded_entry1, groupColumns, groupTexts, childData, R.layout.listview_expanded_entry2, childColumns, childTexts);
    }

    /**
     * Converts an ArrayList containing HashMaps to a List containing a List Containing a Map.
     *
     * @param a   ArrayList with HashMaps to convert.
     * @param row Row to add to the Map.
     */
    private static List<List<Map<String, String>>> covertToListListMap(final ArrayList<HashMap<String, String>> a, final String row) {
        final List<List<Map<String, String>>> ll = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            final Map<String, String> tmpL = new HashMap<>();
            tmpL.put(row, a.get(i).get(row));

            final List<Map<String, String>> l = new ArrayList<>();
            l.add(tmpL);

            ll.add(l);
        }
        return ll;
    }
}
