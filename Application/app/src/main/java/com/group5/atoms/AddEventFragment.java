package com.group5.atoms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
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
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;


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
                else {
                    createDynamicEvent();
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
        DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy:hh:mm a");

        try {
            DateTime startDate = DateTime.parse(startDateEditText.getText().toString(), simpleDateFormat);
            DateTime endDate = DateTime.parse(endDateEditText.getText().toString(), simpleDateFormat);
            String eventTitle = eventTitleEditText.getText().toString();
            String eventDisc = eventDiscEditText.getText().toString();

            addEvent(eventTitle, eventDisc, startDate, endDate);
        }
        catch (Exception e) {
            Toast.makeText(getContext(), "Error Adding Event", Toast.LENGTH_LONG);
        }
    }

    public void createDynamicEvent() {
        //create a simple date format for parsing the date
        DateTimeFormatter eventFormat = DateTimeFormat.forPattern(MainActivity.dateSwitchPref + "hh:mm a");
        DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy:hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            DateTime dueDate = DateTime.parse(dueDateEditText.getText().toString(), simpleDateFormat);
            int estHours = Integer.parseInt(estHoursEditText.getText().toString());
            String eventTitle = eventTitleEditText.getText().toString();
            String eventDisc = eventDiscEditText.getText().toString();
            DateTime today = DateTime.now();


            //get the events
            ArrayList<Event> events = MainActivity.calendarFragment.getEvents();

            Collections.sort(events);

            ArrayList<Interval> EventRanges = new ArrayList<>();

            //get events between now and due date
            for (Event event: events) {
                DateTime eventStartTime = DateTime.parse(event.getDate() + event.getStartTime(), eventFormat);
                DateTime eventEndTime = DateTime.parse(event.getDate() + event.getEndTime(), eventFormat);
                Interval range = new Interval(eventStartTime, eventEndTime);

                EventRanges.add(range);
            }

            //sort the list of intervals
            Collections.sort(EventRanges, new IntervalComparator());

            //check for overlapping events
            isOverlapping(EventRanges);


            //pick a random date and time between the specified time periods
            Random r = new Random();


            for (int i = 0; i < 5; i ++) {

                DateTime tryDate;

                if (dueDate.getDayOfMonth() != today.getDayOfMonth()) {
                    int randomDate = r.nextInt(dueDate.getDayOfMonth() - today.getDayOfMonth()) + today.getDayOfMonth();
                    int randomHour = r.nextInt(20 - 6) + 6;
                    tryDate = new DateTime(today.getYear(), today.getMonthOfYear(), randomDate, randomHour, 0);
                }
                else {
                    int randomHour = r.nextInt(20 - 6) + 6;
                    tryDate = new DateTime(today.getYear(), today.getMonthOfYear(), dueDate.getDayOfMonth(), randomHour, 0);
                }


                int counter = 0;

                for (Interval interval : EventRanges) {
                    if (interval.contains(tryDate)) {
                        break;
                    }
                    else {
                        counter++;
                        continue;
                    }
                }

                if (counter == EventRanges.size()) {
                    addEvent(eventTitle, eventDisc, tryDate, tryDate.plusHours(estHours));
                    break;
                }


            }


        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            Toast.makeText(getContext(), "Error Adding Event", Toast.LENGTH_LONG);
        }

    }

    public boolean isOverlapping(List<Interval> sortedIntervals) {
        for (int i = 0, n = sortedIntervals.size(); i < n - 1; i++) {
            if (sortedIntervals.get(i).overlaps(sortedIntervals.get(i + 1))) {
                return true; // your evaluation for overlap case
            }
        }

        return false;
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void addEvent(String eventTitle, String eventDescription, DateTime startDateTime, DateTime endDateTime) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, MY_CAL_WRITE_REQ);
        }

        //get the calendar id from the main activity
        long calID = MainActivity.getCalendarId();

        System.out.println("Calendar Id" + calID);

        ContentResolver cr = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startDateTime.getMillis());
        values.put(Events.DTEND, endDateTime.getMillis());
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
