package com.group5.atoms;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.*;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    //members needed
    ArrayList<Long> calendarIds = new ArrayList<>();

    //TODO: text view for debugging, need to fill out actual recycler/card views
    TextView debugTextView;

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the user's email from the bundle
        Bundle bundleData = this.getArguments();

        //get the text view for debugging
        debugTextView = this.getView().findViewById(R.id.debuggingTextView);

        //if the bundle data is not null, get the email
        if (bundleData != null) {
            String userEmail = (String)bundleData.get("email");

            //create the calendarId's
            setCalendarIds(userEmail);

            //updateUI
            updateCalendarUI();
        }
        else {
            //change the layout accordingly here
            updateCalendarUI("Error getting email to pull calendar");
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }


    //TODO: this needs to be implemented with a loader to prevent tying up the main thread
    private void setCalendarIds(String email) {
        // Run query
        Cursor cur = null;
        ContentResolver cr = getActivity().getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"hera@example.com", "com.example",
                "hera@example.com"};

        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);

            // add to calendar IDs array list
            this.calendarIds.add(calID);
        }

    }

    /*
    TODO: this needs to be updated to update Recycler view when implemented
    String version may not be needed
     */
    private void updateCalendarUI(String inputData) {

        //update the textview to contain the data
        debugTextView.setText(inputData);
    }

    //overloaded updateCalendarUI, to be updated later
    private void updateCalendarUI() {

        //create a string builder
        StringBuilder sb = new StringBuilder();

        //fill the string builder with the calendar IDs
        for (Long id: this.calendarIds) {
            sb.append(id + "\n");
        }

        //update the textView
        debugTextView.setText(sb.toString());
    }

}
