//group5 package
package com.group5.atoms;

//imports for the calendar fragment
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

//calendar fragment
public class CalendarFragment extends Fragment {

    //members needed
    private ArrayList<Long> calendarIds;
    private FirebaseUser account;
    private ArrayList<Event> events;
    final int MY_CAL_REQ = 20;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;


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

        //initialize recycler view
        this.recyclerView = view.findViewById(R.id.recycler);
        this.recyclerAdapter = new RecyclerAdapter(this.events, container.getContext());
        this.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //We set our adapter.
        this.recyclerView.setAdapter(recyclerAdapter);

        //This method notifies RecyclerView whenever data is changed.
        this.recyclerAdapter.notifyDataSetChanged();

        //return the view
        return view;
    }

    //method to set the calendar ids based on the owners email
    private void setCalendarIds(String email) {

        //get the content resolver
        ContentResolver cr = getActivity().getContentResolver();

        //declare mProjection
        String[] mProjection =
                {
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.OWNER_ACCOUNT
                };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;

        //set the uri and the selection args using the user's email
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = CalendarContract.Calendars.OWNER_ACCOUNT + " = ?";
        String[] selectionArgs = {email};

        //run the query
        Cursor cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        //get the data from the cursor
        while (cur.moveToNext()) {
            this.calendarIds.add(cur.getLong(PROJECTION_ID_INDEX));
        }

        //update the calendar id in the main activity
        MainActivity.setCalendarId(this.calendarIds.get(0));

        //get the event data
        readEvents(0, null);
    }


    //method to reset the events
    public void resetEvents(){
        this.events.clear();
    }

    public void readEvents(int timeFrame, Date chosenDate) {

        //reset events
        resetEvents();

        // then get the string array for the projection indexes
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,       // 0
                CalendarContract.Instances.BEGIN,          // 1
                CalendarContract.Instances.END,            // 2
                CalendarContract.Instances.TITLE,          // 3
                CalendarContract.Instances.ORGANIZER,
                CalendarContract.Instances.CALENDAR_ID
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_BEGIN_INDEX = 1;
        final int PROJECTION_END_INDEX = 2;
        final int PROJECTION_TITLE_INDEX = 3;
        final int PROJECTION_ORGANIZER_INDEX = 4;
        final int PROJECTION_CALENDAR_ID = 5;

        // Specify the date range you want to search for recurring event instances
        // We use the date formatter to get our date and time formats
        Calendar calendar = Calendar.getInstance();

        if (chosenDate != null) {
            calendar.setTime(chosenDate);
        }

        long startMillis;
        long endMillis;

        Toast.makeText(getContext(), MainActivity.dateSwitchPref, Toast.LENGTH_SHORT).show();

        DateFormat dateFormat = new SimpleDateFormat(MainActivity.dateSwitchPref);
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        switch (timeFrame) {
            case 1:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                startMillis = calendar.getTimeInMillis();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                calendar.set(Calendar.HOUR_OF_DAY,12);
                calendar.set(Calendar.MINUTE,59);
                calendar.set(Calendar.SECOND,59);
                endMillis = calendar.getTimeInMillis();
                break;
            case 2:
                calendar.set(currentYear, currentMonth, calendar.getActualMinimum(Calendar.DATE), 0, 0);
                startMillis = calendar.getTimeInMillis();
                int endMonth = calendar.getActualMaximum(Calendar.DATE);
                calendar.set(currentYear, currentMonth, endMonth, 23, 59, 59);
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
        cur = getActivity().getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, "DTSTART ASC");

        //while the cursor is able to iterate...
        while (cur.moveToNext()) {

            // Get the field values
            long eventID = cur.getLong(PROJECTION_ID_INDEX);
            long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
            long endVal = cur.getLong(PROJECTION_END_INDEX);
            long calendarID = cur.getLong(PROJECTION_CALENDAR_ID);
            String title = cur.getString(PROJECTION_TITLE_INDEX);
            String organizer = cur.getString(PROJECTION_ORGANIZER_INDEX);

            // Do something with the values.

            //Log the calendar events to the information log
            Log.i("Calendar", "Event:  " + title);

            //set the begin time to the calendar variable
            calendar.setTimeInMillis(beginVal);
            Date startTime = calendar.getTime();
            calendar.setTimeInMillis(endVal);
            Date endTime = calendar.getTime();

            Log.i("Calendar", "Date: " + dateFormat.format(calendar.getTime()));

            Event evt = new Event( calendarID,  eventID,
                    dateFormat.format(startTime.getTime()), organizer,  title,
                    timeFormat.format(startTime.getTime()), timeFormat.format(endTime.getTime()));
            this.events.add(evt);
        }

        //update the UI
        updateCalendarUI();
    }

    //updateCalendarUI, to be updated later
    // currently notifying that the data has changed to the recycler adapter
    private void updateCalendarUI() {
        if (this.recyclerAdapter != null) {
            this.recyclerAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<Event> getEvents(){
        return this.events;
    }

}
