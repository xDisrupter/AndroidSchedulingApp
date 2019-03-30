package com.group5.atoms;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    //members needed
    private ArrayList<Long> calendarIds;
    private ArrayList<String> events;
    private FirebaseUser account;
    final int MY_CAL_REQ = 20;

    //TODO: text view for debugging, need to fill out actual recycler/card views
    TextView debugTextView;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // check to make sure we have the permissions necessary
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //get the text view for debugging
        debugTextView = view.findViewById(R.id.debuggingTextView);

        //initialize array lists
        this.calendarIds = new ArrayList<>();
        this.events = new ArrayList<>();

        //set account
        account = FirebaseAuth.getInstance().getCurrentUser();

        //if the email is not equal to null
        if (account != null) {
            //create the calendarId's
            setCalendarIds(account.getEmail());
        }

        return view;
    }

    //TODO: this needs to be implemented with a loader to prevent tying up the main thread
    private void setCalendarIds(String email) {

        //get a cursor and the content resolver
        Cursor cur = null;
        ContentResolver cr = getActivity().getContentResolver();

        //declare mProjection
        String[] mProjection =
                {
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.OWNER_ACCOUNT
                };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 1;

        //set the uri and the selection args using the user's email
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = CalendarContract.Calendars.OWNER_ACCOUNT + " = ?";
        String[] selectionArgs = {email};

        //run the query
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        //get the data from the cursor
        while (cur.moveToNext()) {
            this.calendarIds.add(cur.getLong(PROJECTION_ID_INDEX));
        }

        //get the event data
        readEvents(0);
    }

    public void resetEvents() {
        this.events = new ArrayList<>();
    }

    public void readEvents(int timeFrame) {

        //reset events
        resetEvents();

        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE,          // 2
                CalendarContract.Instances.ORGANIZER,
                CalendarContract.Instances.CALENDAR_ID
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_BEGIN_INDEX = 1;
        final int PROJECTION_TITLE_INDEX = 2;
        final int PROJECTION_ORGANIZER_INDEX = 3;
        final int PROJECTION_CALENDAR_ID = 4;

        // Specify the date range you want to search for recurring event instances
        Calendar calendar = Calendar.getInstance();
        long startMillis;
        long endMillis;
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        switch (timeFrame) {
            case 1:
                calendar.set(currentYear, currentMonth, calendar.get(Calendar.DATE), 0, 0);
                startMillis = calendar.getTimeInMillis();
                int endMonth = calendar.getActualMaximum(Calendar.DATE);
                calendar.set(currentYear, currentMonth, endMonth, 23, 59, 59);
                endMillis = calendar.getTimeInMillis();
                break;
            case 2:
                calendar.set(currentYear, 1, 1, 0, 0);
                startMillis = calendar.getTimeInMillis();
                calendar.set(currentYear, 12, 31, 23, 59, 59);
                endMillis = calendar.getTimeInMillis();
                break;
            default:
                startMillis = calendar.getTimeInMillis();
                calendar.add(Calendar.DATE, 1);
                endMillis = calendar.getTimeInMillis();
                break;
        }


        // Submit the query
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);
        String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = {calendarIds.get(0) + ""};
        Cursor cur;

        //here until create calendar added
        cur = getActivity().getContentResolver().query(builder.build(), INSTANCE_PROJECTION, null, null, "DTSTART ASC");


        while (cur.moveToNext()) {

            // Get the field values
            long eventID = cur.getLong(PROJECTION_ID_INDEX);
            long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
            long calendarID = cur.getLong(PROJECTION_CALENDAR_ID);
            String title = cur.getString(PROJECTION_TITLE_INDEX);
            String organizer = cur.getString(PROJECTION_ORGANIZER_INDEX);

            // Do something with the values.
            Log.i("Calendar", "Event:  " + title);
            calendar.setTimeInMillis(beginVal);

            Log.i("Calendar", "Date: " + formatter.format(calendar.getTime()));

            this.events.add(String.format("\nEvent: %s\nID: %s\nOrganizer: %s\nDate: %s\nCalendar ID: %s\n", title, eventID + "", organizer, formatter.format(calendar.getTime()), calendarID + ""));
        }

        //update the UI
        updateCalendarUI();
    }


    //TODO: this needs to be updated to update Recycler view when implemented
    //updateCalendarUI, to be updated later
    private void updateCalendarUI() {

        //create a string builder
        StringBuilder sb = new StringBuilder();

        sb.append("Owner: " + account.getEmail() + "\n");

        //fill the string builder with the calendar IDs
        for (Long id: this.calendarIds) {
            sb.append("CalendarID: " + id + "\n");
        }

        //fill the text view with the event data
        for (String eventLine : this.events) {
            sb.append(eventLine + "\n");
        }

        //update the textView
        debugTextView.setText(sb.toString());
    }

}
