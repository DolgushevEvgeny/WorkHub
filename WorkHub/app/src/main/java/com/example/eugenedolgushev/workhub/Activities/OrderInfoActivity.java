package com.example.eugenedolgushev.workhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.R;

import java.util.ArrayList;

public class OrderInfoActivity extends AppCompatActivity {
    private Button cardPayBtn = null;
    private TextView yourPlanView = null, yourPriceView = null, yourDurationView = null, totalSumView = null;

    private String planName = "";
    private Integer planPrice = 0, duration = 0, totalSum = 0;
    private ArrayList<String> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        setTitle("Информация о заказе");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        planName = getIntent().getExtras().getString("planName");
        planPrice = getIntent().getExtras().getInt("planPrice");
        duration = getIntent().getExtras().getInt("duration");
        totalSum = getIntent().getExtras().getInt("totalSum");
        reservations = getIntent().getExtras().getStringArrayList("reservations");

        yourPlanView = (TextView) findViewById(R.id.your_plan);
        yourPlanView.setText(planName);

        yourPriceView = (TextView) findViewById(R.id.your_price);
        yourPriceView.setText(String.valueOf(planPrice));

        yourDurationView = (TextView) findViewById(R.id.your_duration);
        yourDurationView.setText(String.valueOf(duration));

        totalSumView = (TextView) findViewById(R.id.order_total_sum);
        totalSumView.setText(String.valueOf(totalSum));

        cardPayBtn = (Button) findViewById(R.id.card_pay_button);
        cardPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfoActivity.this, CardPayActivity.class);
                intent.putStringArrayListExtra("reservations", reservations);
                startActivityForResult(intent, 1);
            }
        });
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
