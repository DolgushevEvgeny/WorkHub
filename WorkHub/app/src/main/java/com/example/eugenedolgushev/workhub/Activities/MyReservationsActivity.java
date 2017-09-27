package com.example.eugenedolgushev.workhub.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.Api.ReservationApi.ReservationApi;
import com.example.eugenedolgushev.workhub.Api.ReservationApi.ReservationApiListener;
import com.example.eugenedolgushev.workhub.DBManager;
import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Reservation;
import com.example.eugenedolgushev.workhub.ReservationsAdapter;
import com.example.eugenedolgushev.workhub.TimeNotification;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.eugenedolgushev.workhub.DefaultValues.NOTIFICATION_DELAY;
import static com.example.eugenedolgushev.workhub.DefaultValues.SHARED_PREFERENCES_NAME;
import static com.example.eugenedolgushev.workhub.Utils.compareDates;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.removeSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class MyReservationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Идентификатор уведомления
    private static final int NOTIFY_ID = 101;

    private Context context = null;
    private FloatingActionButton addReservationBtn = null;
    private TextView userName, userSurname;

    private String cityName;
    private RecyclerView lvReservations = null;
    private ReservationsAdapter reservationsAdapter;
    private List<Reservation> reservations = new ArrayList<Reservation>();

    private DBManager dbManager;
    private SQLiteDatabase database;
    private ReservationApi reservationApi;
    private Boolean isRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations2);
        setTitle("Ваши бронирования");

        context = this;

        reservationApi = new ReservationApi(context);
        cityName = getStringFromSharedPreferences("cities", context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addReservationBtn = (FloatingActionButton) findViewById(R.id.fab);
        addReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasConnection(context)) {
                    Intent intent = new Intent(MyReservationsActivity.this, OfficesActivity.class);
                    intent.putExtra("cityName", getStringFromSharedPreferences("cities", context));
                    startActivity(intent);
                } else {
                    showAlertDialog("Нет подключения к интернету", context);
                }
            }
        });

        lvReservations = (RecyclerView) findViewById(R.id.reservations_list_view);
        lvReservations.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy >= 0) {
                    // Scroll Down
                    if (addReservationBtn.isShown()) {
                        addReservationBtn.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!addReservationBtn.isShown()) {
                        addReservationBtn.show();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    addReservationBtn.hide();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
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

        View headerView = navigationView.getHeaderView(0);

        userName = (TextView) headerView.findViewById(R.id.user_name_view);
        userName.setText(getStringFromSharedPreferences("userName", context));

        userSurname = (TextView) headerView.findViewById(R.id.user_surname_view);
        userSurname.setText(getStringFromSharedPreferences("userSurname", context));

        dbManager = new DBManager(this);
        database = null;

        if (hasConnection(context)) {
            getReservations();
        } else {
            loadReservationFromDB();
        }
    }

    private void loadReservations(final RequestParams params) {
        isRequest = true;
        reservationApi.getReservations(params, new ReservationApiListener() {
            @Override
            public void onReservationsLoad(ArrayList<Reservation> reservations) {
                reservationsAdapter.setList(reservations);
                lvReservations.swapAdapter(reservationsAdapter, false);
                checkForUpdates(reservations);
                for (int i = 0; i < reservations.size(); ++i) {
                    dbManager.setToDB(database, dbManager, reservations.get(i), context);
                }
                isRequest = false;
            }
        });
    }

    private void getReservations() {
        RequestParams params = new RequestParams();
        params.put("userID", getStringFromSharedPreferences("userID", context));
        loadReservations(params);
    }

    private void loadReservationFromDB() {
        showAlertDialog("Нет подключения к интернету", context);
        reservationsAdapter.setList(dbManager.getFromDB(database, dbManager));
        lvReservations.swapAdapter(reservationsAdapter, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasConnection(context)) {
            if (!isRequest) {
                getReservations();
            }
        } else {
            loadReservationFromDB();
        }
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
        removeSharedPreferences("userID", context);

        Intent intent = new Intent(MyReservationsActivity.this, AuthorizationActivity.class);
        startActivity(intent);
    }

    private void checkForUpdates(ArrayList<Reservation> reservations) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        for (int i = 0; i < reservations.size(); ++i) {
            if (compareDates(currentDay, currentMonth, currentYear, reservations.get(i).getReservationDate())) {
                createNotificationForContact(reservations.get(i));
            }
        }
    }

    public void createNotificationForContact(final Reservation reservation) {
        GregorianCalendar todayDate = getLocaleDate(reservation);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, todayDate.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, todayDate.get(Calendar.MONTH) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, todayDate.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, reservation.getStartTime() - NOTIFICATION_DELAY);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeNotification.class);
        intent.putExtra("date", reservation.getReservationDate());
        intent.putExtra("officeName", reservation.getOfficeName());
        intent.putExtra("officeAddress", reservation.getOfficeAddress());
        intent.putExtra("startTime", reservation.getStartTime());
        intent.putExtra("duration", reservation.getDuration());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static GregorianCalendar getLocaleDate(final Reservation reservation) {
        return new GregorianCalendar(reservation.getReservationYear(),
                reservation.getReservationMonth(), reservation.getReservationDay());
    }
}
