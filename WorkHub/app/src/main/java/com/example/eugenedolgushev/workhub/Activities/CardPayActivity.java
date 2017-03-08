package com.example.eugenedolgushev.workhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.eugenedolgushev.workhub.MyEditText;
import com.example.eugenedolgushev.workhub.MyFocusChange;
import com.example.eugenedolgushev.workhub.R;

public class CardPayActivity extends AppCompatActivity {
    private MyEditText cardNumber1 = null, cardNumber2 = null, cardNumber3 = null, cardNumber4 = null,
            cardValidPeriodMonth = null, cardValidPeriodYear = null, cardOwner = null, cardSafeCode = null;

    private Button payBtn = null;
    private ProgressBar spinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);

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
                try {
                    if (checkAllFields(cardNumber1, cardNumber2, cardNumber3, cardNumber4, cardValidPeriodMonth,
                            cardValidPeriodYear, cardOwner, cardSafeCode)) {
                        Thread.sleep(3000);
                        Intent intent = new Intent(CardPayActivity.this, PayResultActivity.class);
                        startActivity(intent);
                    }
                } catch (InterruptedException e) {

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
}
