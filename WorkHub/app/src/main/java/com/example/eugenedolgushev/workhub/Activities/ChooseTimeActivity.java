package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.eugenedolgushev.workhub.AsyncTasks.CanTakePlace;
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

import static com.example.eugenedolgushev.workhub.DefaultValues.GET_DAYWORKTIME;
import static com.example.eugenedolgushev.workhub.DefaultValues.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class ChooseTimeActivity extends AppCompatActivity {

    private Context m_context = null;
    private ListView timeListView = null;
    private Button takePlaceBtn = null;

    private static final String url = "http://192.168.0.32:3000/getDayWorkTime";
    private String officeName = "", cityName = "", planName = "", date = "", officeAddress = "";
    private Integer dayOfWeek = 0, planPrice = 0;
    private List timesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_time);
        setTitle("Выберите время");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        officeName = getIntent().getExtras().getString("officeName");
        cityName = getIntent().getExtras().getString("cityName");
        planName = getIntent().getExtras().getString("planName");
        date = getIntent().getExtras().getString("date");
        dayOfWeek = getIntent().getExtras().getInt("dayOfWeek");
        planPrice = getIntent().getExtras().getInt("planPrice");
        officeAddress = getIntent().getExtras().getString("officeAddress");

        m_context = this;

        if (hasConnection(m_context)) {
            new getDayWorkTime().execute();
        } else {
            showAlertDialog("Нет подключения к интернету", m_context);
        }

        timeListView = (ListView) findViewById(R.id.time_list_view);
        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter t = timeListView.getAdapter();
                Time time = (Time) t.getItem(position);
                if (!time.isTimeChoose()) {
                    view.setBackgroundColor(2131427347);
                    time.chooseTime(true);
                    timesList.add(time.getTime());
                    Collections.sort(timesList);
                } else {
                    view.setBackgroundColor(0);
                    time.chooseTime(false);
                    timesList.remove(time.getTime());
                    Collections.sort(timesList);
                }
            }
        });

        takePlaceBtn = (Button) findViewById(R.id.take_place);
        takePlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection(m_context)) {
                    if (checkTime()) {
                        final CanTakePlace canTakePlaceTask = new CanTakePlace(new CanTakePlace.AsyncResponse() {
                            @Override
                            public void processFinish(final ArrayList<String> dates, final ArrayList<Integer> times, String message) {
                                if (dates.size() == 0) {
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
                                                    int startTime = (int) timesList.get(0);
                                                    intent.putExtra("startTime", startTime);
                                                    intent.putExtra("duration", timesList.size());
                                                    setResult(1, intent);
                                                    dialog.cancel();
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                } else {
                                    Utils.showAlertDialog(message, m_context);
                                }
                            }
                        }, m_context);

                        canTakePlaceTask.execute(makeJson());
                    }
                } else {
                    showAlertDialog("Нет подключения к интернету", m_context);
                }
            }
        });
    }

    @NotNull
    private Boolean checkTime() {
        if (timesList.size() > 0) {
            for (int i = 0; i < timesList.size() - 1; ++i) {
                int first = (int) timesList.get(i);
                int second = (int) timesList.get(i+1);
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
                URL requestUrl = new URL(MAIN_URL + GET_DAYWORKTIME + "/?" + requestParams);

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

    private String makeJson() {
        String savedUserID = getStringFromSharedPreferences("userID", m_context);

        return "{ \"office\" : \"" + officeName + "\", " +
                "\"city\" : \"" + cityName + "\", " +
                "\"officeAddress\" : \"" + officeAddress + "\", " +
                "\"plan\" : \"" + planName + "\", " +
                "\"date\" : \"" + date + "\", " +
                "\"startTime\" : " + getStartTime() + ", " +
                "\"duration\" : " + getDuration() + ", " +
                "\"planPrice\" : " + planPrice + ", " +
                "\"userID\" : \"" + savedUserID + "\" }";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
