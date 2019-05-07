package com.group5.atoms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddEventFragment extends Fragment {

    //members
    private LinearLayout linearLayout;
    //instances of the linear layouts for fields
    LinearLayout startTimeLayout;
    LinearLayout endTimeLayout;
    LinearLayout dueDateLayout;
    LinearLayout estHoursLayout;

    private final int MY_CAL_WRITE_REQ = 42;

    //get a reference to the submit button and the fields
    Button submitButton;
    EditText startDateEditText;
    EditText endDateEditText;
    EditText dueDateEditText;
    EditText estHoursEditText;
    EditText eventTitleEditText;
    EditText eventDiscEditText;

    private int eventType = 0; // 0 for static 1 for dynamic

    private static final int STANDARD_EVENT = 0;
    private static final int AUTO_EVENT = 1;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        //get the linear layout
        this.linearLayout = view.findViewById(R.id.add_event_linear);

        //get the field linear layouts
        this.startTimeLayout = view.findViewById(R.id.start_date_linear_layout);
        this.endTimeLayout = view.findViewById(R.id.end_date_linear_layout);
        this.dueDateLayout = view.findViewById(R.id.due_date_linear_layout);
        this.estHoursLayout = view.findViewById(R.id.est_hours_linear_layout);

        //get a reference to the submit button and text fields
        this.submitButton = view.findViewById(R.id.submit_event);
        this.startDateEditText = view.findViewById(R.id.start_date_field);
        this.endDateEditText = view.findViewById(R.id.end_date_field);
        this.dueDateEditText = view.findViewById(R.id.due_date_field);
        this.estHoursEditText = view.findViewById(R.id.est_hours_field);
        this.eventTitleEditText = view.findViewById(R.id.event_title_field);
        this.eventDiscEditText = view.findViewById(R.id.event_disc_field);

        //get a reference to the spinner that holds the event type
        Spinner eventTypeSpinner = view.findViewById(R.id.event_type_spinner);

        //add items to the spinner
        String[] eventTypes = new String[]{"Standard Event", "Automatically Scheduled Event"};

        //build an array adapter for the event types
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, eventTypes);

        //set the adapter
        eventTypeSpinner.setAdapter(adapter);

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //get the string representation of the item selected
                String itemSelected = parent.getItemAtPosition(position).toString();

                if (itemSelected.equals("Standard Event")) {
                    showStaticLayout();
                }
                else {
                    showDynamicLayout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //set the onclick for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventType == STANDARD_EVENT) {
                    createStandardEvent();
                }
            }
        });

        //return the view
        return view;
    }

    private void showStaticLayout() {

        this.eventType = STANDARD_EVENT;

        //hide the due date and estimated hours field
        dueDateLayout.setVisibility(View.GONE);
        estHoursLayout.setVisibility(View.GONE);

        //show the start date and end date
        startTimeLayout.setVisibility(View.VISIBLE);
        endTimeLayout.setVisibility(View.VISIBLE);
    }

    private void showDynamicLayout() {
        this.eventType = AUTO_EVENT;

        //hide the due date and estimated hours field
        dueDateLayout.setVisibility(View.VISIBLE);
        estHoursLayout.setVisibility(View.VISIBLE);

        //show the start date and end date
        startTimeLayout.setVisibility(View.GONE);
        endTimeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void createStandardEvent() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy:hh:mm");

        try {
            Date startDate = simpleDateFormat.parse(startDateEditText.getText().toString());
            Date endDate = simpleDateFormat.parse(endDateEditText.getText().toString());
            String eventTitle = eventTitleEditText.getText().toString();
            String eventDisc = eventDiscEditText.getText().toString();

            addEvent(eventTitle, eventDisc, startDate, endDate);
        }
        catch (Exception e) {
            Toast.makeText(getContext(), "Error Adding Event", Toast.LENGTH_LONG);
        }
    }

    public void addEvent(String eventTitle, String eventDescription, Date startDateTime, Date endDateTime) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, MY_CAL_WRITE_REQ);
        }

        //get the calendar id from the main activity
        long calID = MainActivity.getCalendarId();

        System.out.println("Calendar Id" + calID);

        //get longs to represent the start time and end time in milliseconds
        long startMillis = 0;
        long endMillis = 0;

        //get a calendar object to represent the start time
        Calendar beginTime = Calendar.getInstance();

        //set the begin time
        beginTime.setTime(startDateTime);

        //set the start milliseconds
        startMillis = beginTime.getTimeInMillis();

        //get a calendar object to represent the end time
        Calendar endTime = Calendar.getInstance();

        //set the end time
        endTime.setTime(endDateTime);

        //set the end milliseconds
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, eventTitle);
        values.put(Events.DESCRIPTION, eventDescription);
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
        Uri uri = cr.insert(Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        //print the event id for debugging purposes
        System.out.println("Event Id:" + eventID);
    }

}
