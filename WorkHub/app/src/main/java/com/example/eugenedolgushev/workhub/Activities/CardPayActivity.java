package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.eugenedolgushev.workhub.AsyncTasks.CanTakePlace;
import com.example.eugenedolgushev.workhub.MyEditText;
import com.example.eugenedolgushev.workhub.MyFocusChange;
import com.example.eugenedolgushev.workhub.R;
import com.example.eugenedolgushev.workhub.Utils;

import java.util.ArrayList;

public class CardPayActivity extends AppCompatActivity {
    private MyEditText cardNumber1 = null, cardNumber2 = null, cardNumber3 = null, cardNumber4 = null,
            cardValidPeriodMonth = null, cardValidPeriodYear = null, cardOwner = null, cardSafeCode = null;

    private Button payBtn = null;
    private ProgressBar spinner = null;
    private ArrayList<String> reservations;
    private static final String URL = "http://192.168.0.32:3000/getResers";
    private Context m_context;
    private ArrayList<ArrayList<String>> datesR = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> timesR = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);

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
                    final CanTakePlace canTakePlaceTask = new CanTakePlace(new CanTakePlace.AsyncResponse() {
                        @Override
                        public void processFinish(ArrayList<String> dates, ArrayList<Integer> times) {
                            datesR.add(dates);
                            timesR.add(times);
                            if (counter < reservations.size()) {
                                test(reservations.get(counter), counter + 1);
                            }
                        }
                    }, m_context);

                    canTakePlaceTask.execute(reservations.get(0));

                    Intent intent = new Intent(CardPayActivity.this, PayResultActivity.class);
                    startActivity(intent);
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
            public void processFinish(ArrayList<String> dates, ArrayList<Integer> times) {
                datesR.add(dates);
                timesR.add(times);
                if (counter < reservations.size()) {
                    test(reservations.get(counter), counter + 1);
                } else {
                    Utils.showAlertDialog(createMessage(), m_context);
                }
            }
        }, m_context);

        canTakePlaceTask.execute(reservationJsonObj);
    }

    private Boolean checkAllFields(MyEditText... fields) {
        for (MyEditText view : fields) {
            view.setFocusable(false);
            if (!view.getValidation()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CardPayActivity.this);
                builder.setTitle("Ошибка")
                        .setMessage("Заполните выделенное поле")
                        .setCancelable(true);
                AlertDialog alert = builder.create();
                alert.show();
                view.setFocusable(true);
                view.setBackgroundResource(R.drawable.edit_text_border);
                return false;
            }
            view.setFocusable(true);
        }
        return true;
    }

    private String createMessage() {
        String result = "";
        for (int i = 0; i < datesR.size(); ++i) {
            result += "Нельзя занять " + datesR.get(i);
        }
        return result;
    }
}
