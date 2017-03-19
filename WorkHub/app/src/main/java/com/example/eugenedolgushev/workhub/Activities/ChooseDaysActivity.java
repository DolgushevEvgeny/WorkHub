package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.MyAdapter;
import com.example.eugenedolgushev.workhub.MyOnClick;
import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Reservation;
import com.example.eugenedolgushev.workhub.Utils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.eugenedolgushev.workhub.Strings.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Strings.REMOVE_RESERVATION_URL;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;

public class ChooseDaysActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView = null;
    private Button continueBtn = null, reservationListBtn = null, closeListBtn = null;
    private TextView daysCountView = null, totalSumView = null;
    private LinearLayout reservationList = null;
    private RecyclerView listView = null;
    private MyAdapter myAdapter;

    private String officeName = "", cityName = "", planName = "", officeAddress = "";
    private Integer planPrice = 0;
    private List<Reservation> reservations = new ArrayList<Reservation>();
    private int day = 0, month = 0, year = 0, dayOfWeek = 0;
    private Context m_context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_days);

        officeName = getIntent().getExtras().getString("officeName");
        cityName = getIntent().getExtras().getString("cityName");
        planName = getIntent().getExtras().getString("planName");
        planPrice = getIntent().getExtras().getInt("planPrice");
        officeAddress = getIntent().getExtras().getString("officeAddress");

        m_context = this;

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setSelectionColor(R.color.green);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                long time = System.currentTimeMillis();

                if (time > date.getDate().getTime()) {
                    Utils.showAlertDialog("Нельзя занять", m_context);
                    calendarView.setDateSelected(CalendarDay.from(year, month - 1, day), false);
                } else if (time < date.getDate().getTime()) {
                    day = date.getDay();
                    month = date.getMonth() + 1;
                    year = date.getYear();
                    dayOfWeek = dayOfWeek(day, month, year);

                    Intent intent = new Intent(ChooseDaysActivity.this, ChooseTimeActivity.class);
                    intent.putExtra("cityName", cityName);
                    intent.putExtra("officeName", officeName);
                    intent.putExtra("dayOfWeek", dayOfWeek);
                    intent.putExtra("planName", planName);
                    intent.putExtra("date", "" + day + "." + month + "." + year);
                    intent.putExtra("planPrice", planPrice);
                    intent.putExtra("officeAddress", officeAddress);
                    startActivityForResult(intent, 1);
                }
            }
        });

        continueBtn = (Button) findViewById(R.id.next);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReservationsOnCollision()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseDaysActivity.this);
                    builder.setTitle("Важное сообщение!")
                        .setMessage("Совпадений нет")
                        .setCancelable(false)
                        .setPositiveButton("Продолжить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ChooseDaysActivity.this, OrderInfoActivity.class);
                                    intent.putExtra("planName", planName);
                                    intent.putExtra("planPrice", planPrice);
                                    intent.putExtra("duration", reservations.size());
                                    intent.putExtra("totalSum", calculateTotalSum());
                                    intent.putStringArrayListExtra("reservations", makeJson());
                                    dialog.cancel();
                                    startActivityForResult(intent, 1);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (reservations.size() == 0) {
                        Utils.showAlertDialog("Нужно сделать хотя бы 1 бронирование", m_context);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseDaysActivity.this);
                        builder.setTitle("Важное сообщение!")
                                .setMessage("Есть совпадения в бронировании")
                                .setCancelable(false)
                                .setPositiveButton("Буду исправлять",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

        reservationListBtn = (Button) findViewById(R.id.list_button);
        reservationListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setElementsEnable(false);
                myAdapter.setList((ArrayList) reservations);
                reservationList.setVisibility(View.VISIBLE);
            }
        });

        closeListBtn = (Button) findViewById(R.id.close_list_button);
        closeListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setElementsEnable(true);
                reservationList.setVisibility(View.GONE);
            }
        });

        listView = (RecyclerView) findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(new MyOnClick() {
            @Override
            public void onClick(Reservation reservation) {
                reservations.remove(reservation);
                myAdapter.setList((ArrayList) reservations);
                updateViews();
            }
        }, (ArrayList) reservations);
        listView.setAdapter(myAdapter);

        reservationList = (LinearLayout) findViewById(R.id.reservation_list);
        daysCountView = (TextView) findViewById(R.id.days_count);
        totalSumView = (TextView) findViewById(R.id.result_sum);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {
                int startTime = data.getExtras().getInt("startTime");
                int duration = data.getExtras().getInt("duration");
                Reservation reservation = new Reservation();
                reservation.setOfficeCity(cityName);
                reservation.setOfficeName(officeName);
                reservation.setStartTime(startTime);
                reservation.setDuration(duration);
                reservation.setReservationDay(day);
                reservation.setReservationMonth(month);
                reservation.setReservationYear(year);
                reservation.setReservationPlanName(planName);
                reservation.setReservationSum(planPrice * duration);
                reservation.setOfficeAddress(officeAddress);
                reservations.add(reservation);

                updateViews();
                calendarView.setDateSelected(CalendarDay.from(year, month - 1, day), true);
            }
        }
    }

    private void updateViews() {
        totalSumView.setText("Сумма :  " + String.valueOf(calculateTotalSum()) + " руб.");
        daysCountView.setText("Вы выбрали :  " + String.valueOf(reservations.size()) + " посещений ");
    }

    private void setElementsEnable(Boolean enable) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_choose_days);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            switch(child.getId()) {
                case R.id.list_button:
                    child.setEnabled(enable);
                    break;
                case R.id.calendarView:
                    child.setEnabled(enable);
                    break;
                case R.id.next:
                    child.setEnabled(enable);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        for (int i = 0; i < reservations.size(); ++i) {
            Reservation reservation = reservations.get(i);
            String date = reservation.getReservationDay() + "."
                    + reservation.getReservationMonth() + "."
                    + reservation.getReservationYear();

            String startTime = reservation.getStartTime().toString();
            String duration = reservation.getDuration().toString();

            new removeReservation().execute(date, startTime, duration);
        }
    }

    class removeReservation extends AsyncTask<String, Void, String> {

        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String city = "", plan = "", date = params[0], startTime = params[1], duration = params[2];
            try {
                city = URLEncoder.encode(cityName, "UTF-8");
                plan = URLEncoder.encode(planName, "UTF-8");
            } catch(UnsupportedEncodingException e) {

            }

            String savedUserID = getStringFromSharedPreferences("userID", m_context);
            String requestParams = "city=" + city + "&office=" + officeName
                    + "&plan=" + plan + "&date=" + date + "&startTime=" + startTime
                    + "&duration=" + duration + "&userID=" + savedUserID;
            try {
                URL requestUrl = new URL(MAIN_URL + REMOVE_RESERVATION_URL + "/?" + requestParams);

                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader readerBuf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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
        }
    }

    private Integer calculateTotalSum() {
        Integer totalSum = 0;
        for (int i = 0; i < reservations.size(); ++i) {
            totalSum += reservations.get(i).getReservationSum();
        }
        return totalSum;
    }

    private void sortReservations() {
        if (reservations.size() > 0) {
            Collections.sort(reservations, new Comparator<Reservation>() {
                @Override
                public int compare(final Reservation object1, final Reservation object2) {
                    return object1.getStartTime().compareTo(object2.getStartTime());
                }
            });
        }
    }

    private Boolean checkReservationsOnCollision() {
        sortReservations();
        if (reservations.size() > 1) {
            for (int i = 0; i < reservations.size() - 1; ++i) {
                Reservation first = reservations.get(i);
                for (int j = i + 1; j < reservations.size(); ++j) {
                    Reservation second = reservations.get(j);
                    if (first.getReservationYear() == second.getReservationYear() &&
                            first.getReservationMonth() == second.getReservationMonth() &&
                            first.getReservationDay() == second.getReservationDay()) {
                        if (second.getStartTime() >= first.getStartTime() &&
                                second.getStartTime() < (first.getStartTime() + first.getDuration())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private void onDatePress() {
        FragmentTransaction fT = getSupportFragmentManager().beginTransaction();
        //fT.replace(R.id.fragment_container, new CalendarFragment());
        fT.addToBackStack(null);
        fT.commitAllowingStateLoss();
    }

    public static Integer dayOfWeek(int day, int month, int year){
        String days[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int a = (14 - month) / 12, y = year - a, m = month + 12 * a - 2;
        return ((7000 + (day + y + y / 4 - y / 100 + y / 400 + (31 * m) / 12)) % 7) - 1;
    }

    private ArrayList<String> makeJson() {
        String savedUserID = getStringFromSharedPreferences("userID", m_context);
        ArrayList<String> resultReservations = new ArrayList<>();
        for (int i = 0; i < reservations.size(); ++i) {
            Reservation reservation = reservations.get(i);
            String object = "{ ";
            object += "\"office\" : \"" + reservation.getOfficeName() + "\", " +
                    "\"city\" : \"" + reservation.getOfficeCity() + "\", " +
                    "\"officeAddress\" : \"" + reservation.getOfficeAddress() + "\", " +
                    "\"plan\" : \"" + reservation.getReservationPlanName() + "\", " +
                    "\"date\" : \"" + reservation.getReservationDate() + "\", " +
                    "\"startTime\" : " + reservation.getStartTime() + ", " +
                    "\"duration\" : " + reservation.getDuration() + ", " +
                    "\"planPrice\" : " + reservation.getReservationSum() / reservation.getDuration() + ", " +
                    "\"userID\" : \"" + savedUserID + "\" }";
            resultReservations.add(object);
        }

        return resultReservations;
    }
}
