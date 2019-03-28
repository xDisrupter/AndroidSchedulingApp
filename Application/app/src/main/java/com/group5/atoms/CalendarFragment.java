package com.group5.atoms;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    //members needed
    ArrayList<Long> calendarIds = new ArrayList<>();
    String email;

    //TODO: text view for debugging, need to fill out actual recycler/card views
    TextView debugTextView;

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int MY_CAL_REQ = 4;

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


        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (email != null) {
            //create the calendarId's
            setCalendarIds(email);
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
                            CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                            CalendarContract.Calendars.ACCOUNT_NAME,
                            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                            CalendarContract.Calendars.CALENDAR_LOCATION,
                            CalendarContract.Calendars.CALENDAR_TIME_ZONE
                    };

            //set the uri and the selection args using the user's email
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
            String[] selectionArgs = new String[]{email, email};

            //run the query
            cur = cr.query(uri, mProjection, selection, selectionArgs, null);

            //get the data from the cursor
            while (cur.moveToNext()) {
                calendarIds.add(cur.getLong(PROJECTION_ID_INDEX));
            }

            //update the UI
            updateCalendarUI();

    }


    //TODO: this needs to be updated to update Recycler view when implemented
    //updateCalendarUI, to be updated later
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
