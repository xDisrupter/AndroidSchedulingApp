package com.group5.atoms;
import android.content.Context;

import android.content.Intent;

import android.net.Uri;

import android.support.annotation.NonNull;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;



/*import com.example.acm_demo.R;

import com.example.acm_demo.models.mlh.Event;

import com.squareup.picasso.Picasso;*/


import java.util.ArrayList;
import java.util.List;


// RecyclerAdapter class
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{


    // class members: An Array List holding type of Event to store the events and our context
    private ArrayList<Event> event;

    private Context mContext;



    // constructor that takes in the Event arrayList and the context (not in use currently)

    public RecyclerAdapter(ArrayList<Event> evt, Context context) {

        this.event = evt;

        this.mContext = context;

    }



    //Since we extend the Adapter, we must implement some methods.

    //The RecyclerViewHolder is one. This tells the adapter what item will we use

    //For our RecyclerView

    @NonNull

    @Override

    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //Here, we inflate the Layout with our item.

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view,viewGroup,false);

        //Then we return the ViewHolder so we can use it in our Resources class.

        return new RecyclerViewHolder(view);

    }

    //Whenever you scroll down, the RecyclerView recycles an old view to be replace

    // with new information that is supplied. This is where that happens.

    @Override

    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {

        recyclerViewHolder.bind(i);

    }



    //Returns the size of the array.

    @Override

    public int getItemCount() {

        return event.size();

    }



    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //These are the variables for each view.

        ImageView mEventImage;

        TextView mEvenTitle;

        TextView mEventStart;

        TextView mEventLocation;

        TextView mStartTime;

        TextView mEndTime;



        //Here in the RecyclerViewHolder, we instantiate each View by finding their id.

        // Since findViewById returns a view, we must type-cast it (this is redundant)

        public RecyclerViewHolder(View itemView){

            super(itemView);

            mEventImage = itemView.findViewById(R.id.image_url);

            mEvenTitle =  itemView.findViewById(R.id.title);

            mEventStart = itemView.findViewById(R.id.start_date);

            mEventLocation = itemView.findViewById(R.id.location);

            mStartTime = itemView.findViewById(R.id.start_time);

            mEndTime = itemView.findViewById(R.id.end_time);

        }



        //Here, we bind the information with the view itself.
        // these set texts should be set to string resources

        void bind(final int position){


            mEvenTitle.setText(event.get(position).getEvent());

            mEventStart.setText(event.get(position).getDate());

            String startTime = event.get(position).getStartTime();
            String endTime = event.get(position).getEndTime();

            mStartTime.setText(startTime);

            if (!startTime.equals(endTime)) {
                mEndTime.setText(endTime);
            }
            else {
                mEndTime.setText("");
            }

            itemView.setOnClickListener(this);

        }



        //onClick for the recycler adapter

        @Override

        public void onClick(View view) {
            //fill this is to redirect to edit event
        }



    }

}