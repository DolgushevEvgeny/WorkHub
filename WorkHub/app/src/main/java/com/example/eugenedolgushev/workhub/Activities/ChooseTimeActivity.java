package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Time;
import com.example.eugenedolgushev.workhub.TimeList;
import com.example.eugenedolgushev.workhub.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseTimeActivity extends AppCompatActivity {

    private Context m_context = null;
    private ListView timeListView = null;
    private Button takePlaceBtn = null;

    private static final String url = "http://192.168.0.32:3000/getDayWorkTime";
    private static final String url1 = "http://192.168.0.32:3000/canMakeReservation";
    private String officeName = "", cityName = "", planName = "", date = "";
    private Integer dayOfWeek = 0, planPrice = 0;
    private List times = new ArrayList<Integer>();
    private Boolean canReserve = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_time);
        setTitle("Выберите время");

        officeName = getIntent().getExtras().getString("officeName");
        cityName = getIntent().getExtras().getString("cityName");
        planName = getIntent().getExtras().getString("planName");
        date = getIntent().getExtras().getString("date");
        dayOfWeek = getIntent().getExtras().getInt("dayOfWeek");
        planPrice = getIntent().getExtras().getInt("planPrice");

        m_context = this;

        new getDayWorkTime().execute();

        timeListView = (ListView) findViewById(R.id.time_list_view);
        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter t = timeListView.getAdapter();
                Time time = (Time) t.getItem(position);
                if (!time.isTimeChoose()) {
                    view.setBackgroundColor(2131427347);
                    time.chooseTime(true);
                    times.add(time.getTime());
                    Collections.sort(times);
                } else {
                    view.setBackgroundColor(0);
                    time.chooseTime(false);
                    times.remove(time.getTime());
                    Collections.sort(times);
                }
            }
        });

        takePlaceBtn = (Button) findViewById(R.id.take_place);
        takePlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkTime()) {
                    new canMakeReservation().execute();
                }
            }
        });

    }

    @NotNull
    private Boolean checkTime() {
        if (times.size() > 0) {
            for (int i = 0; i < times.size() - 1; ++i) {
                int first = (int) times.get(i);
                int second = (int) times.get(i+1);
                if (first + 1 != second) {
                    Utils.showAlertDialog("Время должно быть без пропусков", m_context);
                    return false;
                }
            }
        } else {
            Utils.showAlertDialog("Ничего не выбрано", m_context);
            return false;
        }

        return true;
    }

    private Integer getStartTime() {
        TimeList t = (TimeList) timeListView.getAdapter();
        ArrayList<Time> t1 = t.getList();
        for (int i = 0; i < t1.size(); ++i) {
            if (t1.get(i).isTimeChoose()) {
                return t1.get(i).getTime();
            }
        }

        return 0;
    }

    private Integer getDuration() {
        int duration = 0;
        TimeList t = (TimeList) timeListView.getAdapter();
        ArrayList<Time> t1 = t.getList();
        for (int i = 0; i < t1.size(); ++i) {
            if (t1.get(i).isTimeChoose()) {
                ++duration;
            }
        }

        return duration;
    }

    class getDayWorkTime extends AsyncTask<Context, Void, String> {

        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            HttpURLConnection connection = null;
            InputStream is = null;
            BufferedReader readerBuf = null;

            String city = "";
            try {
                city = URLEncoder.encode(cityName, "UTF-8");
            } catch(UnsupportedEncodingException e) {

            }

            String requestParams = "city=" + city + "&office=" + officeName + "&day=" + dayOfWeek;
            try {
                URL requestUrl = new URL(url + "/?" + requestParams);

                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    is = connection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    readerBuf = new BufferedReader(new InputStreamReader(is));

                    String line = "";
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

            ArrayList<Time> times = new ArrayList<>();
            TimeList timeList = new TimeList(m_context);

            JSONObject dataJsonObj = null;
            JSONArray list = null;
            Integer code = 0, start = 0, end = 0;

            try {
                dataJsonObj = new JSONObject(strJson);

                if (dataJsonObj.has("work_day")) {
                    list = dataJsonObj.getJSONArray("work_day");
                    start = list.getInt(0);
                    end = list.getInt(1);
                }
                if (dataJsonObj.has("code")) {
                    code = dataJsonObj.getInt("code");
                }

                for (int i = start; i < end; ++i){
                    Time time = new Time();
                    time.setTime(i);

                    times.add(time);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            timeList.setList(times);
            if (null != timeListView) {
                timeListView.setAdapter(timeList);
            }
        }
    }

    class canMakeReservation extends AsyncTask<Context, Void, String> {

        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            HttpURLConnection connection = null;
            InputStream is = null;
            BufferedReader readerBuf = null;

            String city = "", plan = "";
            try {
                city = URLEncoder.encode(cityName, "UTF-8");
                plan = URLEncoder.encode(planName, "UTF-8");
            } catch(UnsupportedEncodingException e) {

            }

            SharedPreferences sPref = getApplicationContext()
                    .getSharedPreferences(AuthorizationActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

            String savedUserID = sPref.getString("userID", "");

            String requestParams = "city=" + city + "&office=" + officeName
                    + "&plan=" + plan + "&date=" + date + "&startTime=" + getStartTime()
                    + "&duration=" + getDuration() + "&planPrice=" + planPrice
                    + "&userID=" + savedUserID;
            try {
                URL requestUrl = new URL(url1 + "/?" + requestParams);

                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    is = connection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    readerBuf = new BufferedReader(new InputStreamReader(is));

                    String line = "";
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

            JSONObject dataJsonObj = null;
            Integer code = 0, nonReserveTime = 0;

            try {
                dataJsonObj = new JSONObject(strJson);
                if (dataJsonObj.has("code")) {
                    code = dataJsonObj.getInt("code");
                }
                if (code == 0) {
                    if (dataJsonObj.has("nonReserve")) {
                        canReserve = false;
                        nonReserveTime = dataJsonObj.getInt("nonReserve");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseTimeActivity.this);
                        builder.setTitle("Важное сообщение!")
                            .setMessage("Нельзя занять место в " + nonReserveTime + "часов")
                            .setCancelable(false);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                if (code == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseTimeActivity.this);
                    builder.setTitle("Важное сообщение!")
                        .setMessage("Все свободно, можно занимать")
                        .setCancelable(false)
                        .setNegativeButton("ОК, пока еще подумаю",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                        .setPositiveButton("ОК, занимаю",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    int startTime = (int) times.get(0);
                                    intent.putExtra("startTime", startTime);
                                    intent.putExtra("duration", times.size());
                                    setResult(1, intent);
                                    dialog.cancel();
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
