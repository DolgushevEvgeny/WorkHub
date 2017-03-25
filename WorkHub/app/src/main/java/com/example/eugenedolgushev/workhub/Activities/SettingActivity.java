package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.eugenedolgushev.workhub.R;

import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class SettingActivity extends AppCompatActivity {

    private Context m_context;
    private LinearLayout changePasswordView, changeCityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Настройки");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_context = this;

        changePasswordView = (LinearLayout) findViewById(R.id.change_password_view);
        changePasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection(m_context)) {
                    Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                } else {
                    showAlertDialog("Нет подключения к интернету", m_context);
                }
            }
        });

        changeCityView = (LinearLayout) findViewById(R.id.change_city_view);
        changeCityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection(m_context)) {
                    Intent intent = new Intent(SettingActivity.this, ChangeCityActivity.class);
                    startActivity(intent);
                } else {
                    showAlertDialog("Нет подключения к интернету", m_context);
                }
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
