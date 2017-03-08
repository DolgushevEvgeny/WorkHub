package com.example.eugenedolgushev.workhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.eugenedolgushev.workhub.R;

public class PayResultActivity extends AppCompatActivity {
    private Button finalBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        finalBtn = (Button) findViewById(R.id.final_button);
        finalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayResultActivity.this, MyReservationsActivity.class);
                startActivity(intent);
            }
        });
    }
}
