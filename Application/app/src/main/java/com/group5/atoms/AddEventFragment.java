package com.group5.atoms;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;


public class AddEventFragment extends Fragment {

    //members
    private LinearLayout linearLayout;

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
                    //showDynamicLayout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //return the view
        return view;
    }

    private void showStaticLayout() {
        //
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
