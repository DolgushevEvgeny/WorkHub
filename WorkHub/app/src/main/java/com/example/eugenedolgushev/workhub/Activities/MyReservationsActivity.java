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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Reservation;
import com.example.eugenedolgushev.workhub.ReservationList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.eugenedolgushev.workhub.Activities.AuthorizationActivity.SHARED_PREFERENCES_NAME;

public class MyReservationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context m_context = null;
    private FloatingActionButton addReservationBtn = null;

    private static final String cityName = "Йошкар-Ола";
    private static final String URL = "http://192.168.0.32:3000/MyReservations";
    private ListView lvReservations = null;

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

        lvReservations = (ListView) findViewById(R.id.reservations_list_view);
        lvReservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new LoadReservations().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadReservations().execute();
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    class LoadReservations extends AsyncTask<Context, Void, String> {

        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            SharedPreferences sPref = getApplicationContext()
                    .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

            String savedUserID = sPref.getString("userID", "");
            String param = "userID=" + savedUserID;
            try {
                java.net.URL requestUrl = new URL(URL + "/?" + param);

                HttpURLConnection connection = null;
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader readerBuf = null;
                    readerBuf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line = "";
                    StringBuffer buffer = new StringBuffer();
                    while((line = readerBuf.readLine()) != null) {
                        buffer.append(line);
                    }

                    resultJson = buffer.toString();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            ArrayList<Reservation> reservations = new ArrayList<>();
            ReservationList reservationList = new ReservationList(m_context);

            JSONObject dataJsonObj;
            JSONArray list;

            try {
                dataJsonObj = new JSONObject(strJson);
                if (dataJsonObj.has("records")) {
                    list = dataJsonObj.getJSONArray("records");
                } else {
                    return;
                }

                for (int i = 0; i < list.length(); ++i){
                    Reservation reservation = new Reservation();
                    JSONObject recordJsonObj = list.getJSONObject(i);
                    if (recordJsonObj.has("city")) {
                        reservation.setOfficeCity(recordJsonObj.getString("city"));
                    }
                    if (recordJsonObj.has("plan")) {
                        reservation.setReservationPlanName(recordJsonObj.getString("plan"));
                    }
                    if (recordJsonObj.has("office")) {
                        reservation.setOfficeName(recordJsonObj.getString("office"));
                    }
                    if (recordJsonObj.has("date")) {
                        reservation.setReservationDate(recordJsonObj.getString("date"));
                    }
                    if (recordJsonObj.has("startTime")) {
                        reservation.setStartTime(recordJsonObj.getInt("startTime"));
                    }
                    if (recordJsonObj.has("duration")) {
                        reservation.setDuration(recordJsonObj.getInt("duration"));
                    }
                    if (recordJsonObj.has("planPrice")) {
                        reservation.setReservationSum(recordJsonObj.getInt("planPrice") *
                                reservation.getDuration());
                    }

                    reservations.add(reservation);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            reservationList.setList(reservations);
            if (lvReservations != null) {
                lvReservations.setAdapter(reservationList);
            }
        }
    }
}
