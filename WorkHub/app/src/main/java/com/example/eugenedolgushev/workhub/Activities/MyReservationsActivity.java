package com.example.eugenedolgushev.workhub.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.eugenedolgushev.workhub.AsyncTasks.GetMyReservations;
import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Reservation;
import com.example.eugenedolgushev.workhub.ReservationsAdapter;
import com.example.eugenedolgushev.workhub.TimeNotification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.eugenedolgushev.workhub.Strings.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Strings.MY_RESERVATIONS_URL;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.removeSharedPreferences;

public class MyReservationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context m_context = null;
    private FloatingActionButton addReservationBtn = null;

    private String cityName;
    //private static final String URL = "http://192.168.0.32:3000/MyReservations";
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

        cityName = getStringFromSharedPreferences("cities", m_context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addReservationBtn = (FloatingActionButton) findViewById(R.id.fab);
        addReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyReservationsActivity.this, OfficesActivity.class);
                intent.putExtra("cityName", getStringFromSharedPreferences("cities", m_context));
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
        getMyReservationsTask.execute(MAIN_URL + MY_RESERVATIONS_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.Status status = getMyReservationsTask.getStatus();
        if (status.name().equals("FINISHED")) {
            getMyReservationsTask = getNewTask();
            getMyReservationsTask.execute(MAIN_URL + MY_RESERVATIONS_URL);
        }
    }

    private GetMyReservations getNewTask() {
        GetMyReservations getMyReservationsTask = new GetMyReservations(new GetMyReservations.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<Reservation> reservations) {
                reservationsAdapter.setList(reservations);
                lvReservations.setAdapter(reservationsAdapter);
                //createNotificationForContact(reservations.get(0));
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
        removeSharedPreferences("userID", m_context);

        Intent intent = new Intent(MyReservationsActivity.this, AuthorizationActivity.class);
        startActivity(intent);
    }

    public void createNotificationForContact(Reservation reservation) {
        GregorianCalendar todayDate = getLocaleDate(reservation);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, todayDate.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, todayDate.get(Calendar.MONTH));
        Log.d("MONTH_CONTACT", String.valueOf(todayDate.get(Calendar.MONTH)));
        calendar.set(Calendar.DAY_OF_MONTH, todayDate.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        intent.putExtra("name", "Hello world");
        intent.putExtra("day", "Today");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public GregorianCalendar getLocaleDate(Reservation reservation) {
        GregorianCalendar gCal = new GregorianCalendar(reservation.getReservationYear(),
                reservation.getReservationMonth(), reservation.getReservationDay());
        return  gCal;
    }
}
