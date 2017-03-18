package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.eugenedolgushev.workhub.AsyncTasks.GetMyReservations;
import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Reservation;
import com.example.eugenedolgushev.workhub.ReservationsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.example.eugenedolgushev.workhub.Activities.AuthorizationActivity.SHARED_PREFERENCES_NAME;

public class MyReservationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context m_context = null;
    private FloatingActionButton addReservationBtn = null;

    private static final String cityName = "Йошкар-Ола";
    private static final String URL = "http://192.168.0.32:3000/MyReservations";
    private RecyclerView lvReservations = null;
    private ReservationsAdapter reservationsAdapter;
    private List<Reservation> reservations = new ArrayList<Reservation>();
    private GetMyReservations getMyReservationsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations2);
        setTitle("Ваши бронирования");

        m_context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addReservationBtn = (FloatingActionButton) findViewById(R.id.fab);
        addReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyReservationsActivity.this, OfficesActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });

        lvReservations = (RecyclerView) findViewById(R.id.reservations_list_view);
        lvReservations.setLayoutManager(new LinearLayoutManager(this));
        reservationsAdapter = new ReservationsAdapter((ArrayList) reservations);
        lvReservations.setAdapter(reservationsAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getMyReservationsTask = getNewTask();
        getMyReservationsTask.execute(URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.Status status = getMyReservationsTask.getStatus();
        if (status.name().equals("FINISHED")) {
            getMyReservationsTask = getNewTask();
            getMyReservationsTask.execute(URL);
        }
    }

    private GetMyReservations getNewTask() {
        GetMyReservations getMyReservationsTask = new GetMyReservations(new GetMyReservations.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<Reservation> reservations) {
                reservationsAdapter.setList(reservations);
                lvReservations.setAdapter(reservationsAdapter);
            }
        }, m_context);

        return getMyReservationsTask;
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
        getMenuInflater().inflate(R.menu.my_reservations_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logOut();
        } else {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.naw_settings) {
            goToSettings();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToSettings() {
        Intent intent = new Intent(MyReservationsActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        removeSharedPreferences("userID");

        Intent intent = new Intent(MyReservationsActivity.this, AuthorizationActivity.class);
        startActivity(intent);
    }

    private void removeSharedPreferences(String key) {
        SharedPreferences sPref = getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = sPref.edit();
        editor.remove(key);
        editor.commit();
    }
}
