package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.eugenedolgushev.workhub.AsyncTasks.CanTakePlace;
import com.example.eugenedolgushev.workhub.MyFocusChange;
import com.example.eugenedolgushev.workhub.MyViews.MyEditText;
import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CardPayActivity extends AppCompatActivity {
    private MyEditText cardNumber1 = null, cardNumber2 = null, cardNumber3 = null, cardNumber4 = null,
            cardValidPeriodMonth = null, cardValidPeriodYear = null, cardOwner = null, cardSafeCode = null;

    private Button payBtn = null;
    private ProgressBar spinner = null;
    private ArrayList<String> reservations;
    private static final String URL = "http://192.168.0.32:3000/getResers";
    private static final String URL1 = "http://192.168.0.32:3000/setReservation";
    private Context m_context;
    private ArrayList<ArrayList<String>> datesR = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> timesR = new ArrayList<>();
    private boolean haveProcessed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);
        setTitle("Оплата картой");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_context = this;

        reservations = getIntent().getExtras().getStringArrayList("reservations");

        spinner = (ProgressBar) findViewById(R.id.progressBar);

        cardNumber1 = (MyEditText) findViewById(R.id.card_number_holder_1);
        cardNumber2 = (MyEditText) findViewById(R.id.card_number_holder_2);
        cardNumber3 = (MyEditText) findViewById(R.id.card_number_holder_3);
        cardNumber4 = (MyEditText) findViewById(R.id.card_number_holder_4);

        cardValidPeriodMonth = (MyEditText) findViewById(R.id.card_valid_period_month);
        cardValidPeriodYear = (MyEditText) findViewById(R.id.card_valid_period_year);

        cardOwner = (MyEditText) findViewById(R.id.card_owner);
        cardSafeCode = (MyEditText) findViewById(R.id.card_safe_code);

        payBtn = (Button) findViewById(R.id.pay_button);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                if (checkAllFields(cardNumber1, cardNumber2, cardNumber3, cardNumber4, cardValidPeriodMonth,
                        cardValidPeriodYear, cardOwner, cardSafeCode)) {

                    final Integer counter = 1;
                    haveProcessed = false;
                    final CanTakePlace canTakePlaceTask = new CanTakePlace(new CanTakePlace.AsyncResponse() {
                        @Override
                        public void processFinish(ArrayList<String> dates, ArrayList<Integer> times, String message) {
                            if (dates.size() > 0) {
                                datesR.add(dates);
                                timesR.add(times);
                            }
                            if (counter < reservations.size()) {
                                test(reservations.get(counter), counter + 1);
                                if (datesR.size() > 0) {
                                    Utils.showAlertDialog(createMessage(), m_context);
                                } else {
                                    for (int i = 0; i < reservations.size(); ++i) {
                                        new SetReservation().execute(reservations.get(i));
                                    }
                                    Intent intent = new Intent(CardPayActivity.this, PayResultActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                if (datesR.size() > 0) {
                                    Utils.showAlertDialog(createMessage(), m_context);
                                } else {
                                    for (int i = 0; i < reservations.size(); ++i) {
                                        new SetReservation().execute(reservations.get(i));
                                    }
                                    Intent intent = new Intent(CardPayActivity.this, PayResultActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    }, m_context);

                    canTakePlaceTask.execute(reservations.get(0));
                }
                spinner.setVisibility(View.INVISIBLE);
            }
        });

        cardNumber1.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardNumber2.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardNumber3.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardNumber4.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardValidPeriodMonth.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardValidPeriodYear.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardSafeCode.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
        cardOwner.setOnFocusChangeListener(new MyFocusChange(CardPayActivity.this));
    }

    private void test(String reservationJsonObj, final Integer counter) {
        CanTakePlace canTakePlaceTask = new CanTakePlace(new CanTakePlace.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<String> dates, ArrayList<Integer> times, String message) {
                if (dates.size() > 0) {
                    datesR.add(dates);
                    timesR.add(times);
                }
                if (counter < reservations.size()) {
                    test(reservations.get(counter), counter + 1);
                }
            }
        }, m_context);

        canTakePlaceTask.execute(reservationJsonObj);
    }

    private Boolean checkAllFields(MyEditText... fields) {
        for (MyEditText view : fields) {
            view.clearFocus();
            if (!view.getValidation()) {
                view.setBackgroundResource(R.drawable.edit_text_border);
                AlertDialog.Builder builder = new AlertDialog.Builder(CardPayActivity.this);
                builder.setTitle("Ошибка")
                    .setMessage("Заполните выделенное поле")
                    .setCancelable(true);
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        }
        return true;
    }

    private String createMessage() {
        String result = "";
        for (int i = 0; i < datesR.size(); ++i) {
            String item = " в ";
            ArrayList<Integer> timesItem = timesR.get(i);
            for (int j = 0; j < timesItem.size(); ++j) {
                item += timesItem.get(i).toString();
                item += j < timesItem.size() - 1 ? ", " : "";
            }
            result += "Нельзя занять " + datesR.get(i).get(0) + item;
            result += i < datesR.size() - 1 ? "; " : "";
        }
        return result;
    }

    class SetReservation extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String resultJson = "", reservation = "";
            try {
                reservation = URLEncoder.encode(params[0], "UTF-8");
            } catch(UnsupportedEncodingException e) {

            }

            try {
                URL requestUrl = new URL(URL1 + "/?" + "reservation=" + reservation);

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
