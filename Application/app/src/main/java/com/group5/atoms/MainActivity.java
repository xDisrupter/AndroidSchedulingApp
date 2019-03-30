package com.group5.atoms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //members
    CalendarFragment calendarFragment;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set the firebase user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //change the nav header contents
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderDN = headerView.findViewById(R.id.nav_header_display_name);
        TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_email);
        ImageView navHeaderImage = headerView.findViewById(R.id.nav_header_image);

        if (navHeaderDN != null) {
            navHeaderDN.setText(currentUser.getDisplayName());
        }

        if (navHeaderEmail != null) {
            navHeaderEmail.setText(currentUser.getEmail());
        }

        if (navHeaderImage != null) {
            // show The Image in a ImageView
            new DownloadImageTask(navHeaderImage).execute(currentUser.getPhotoUrl().toString());
        }

        //swap the fragment layout with the calendar fragment
        calendarFragment = new CalendarFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, calendarFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO: add setting menu action here, switch to settings fragment
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_logout) {
            Intent intentToLogin = new Intent(this, LoginActivity.class);
            //sign out
            FirebaseAuth.getInstance().signOut();

            this.startActivity(intentToLogin);
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: update navigation menu items and actions
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_view) {
            calendarFragment.readEvents(0);
        }
        else if (id == R.id.nav_monthly_view) {
            calendarFragment.readEvents(1);
        }
        else if (id == R.id.nav_yearly_view) {
            calendarFragment.readEvents(2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
