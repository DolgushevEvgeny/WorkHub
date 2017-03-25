package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eugenedolgushev.workhub.AsyncTasks.Login;
import com.example.eugenedolgushev.workhub.R;

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class AuthorizationActivity extends AppCompatActivity {
    private EditText loginField;
    TextInputEditText passwordField;
    private Button loginButton;
    private Context m_context;

    private boolean hasCity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        setTitle("Авторизация");

        m_context = this;

        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (TextInputEditText) findViewById(R.id.password_field);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection(m_context)) {
                    String login = loginField.getText().toString();
                    String password = passwordField.getText().toString();

                    new Login(new Login.AsyncResponse() {
                        @Override
                        public void processFinish(String userID) {
                            putSharedPreferences(userID);
                            getUserID();
                        }
                    }, m_context).execute(login, password);
                } else {
                    showAlertDialog("Нет подключения к интернету", m_context);
                }
            }
        });

        getCity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginField.setText("");
        passwordField.setText("");

        if (hasCity) {
            getUserID();
        }
    }

    private void getCity() {
        String city = getStringFromSharedPreferences("cities", m_context);
        if (city.length() == 0) {
            if (hasConnection(m_context)) {
                Intent intent = new Intent(AuthorizationActivity.this, ChangeCityActivity.class);
                startActivityForResult(intent, 1);
            } else {
                showAlertDialog("Нет подключения к интернету", m_context);
            }
        } else {
            hasCity = true;
            getUserID();
        }
    }

    private void getUserID() {
        String savedUserID = getStringFromSharedPreferences("userID", m_context);
        if (savedUserID.length() != 0) {
            if (hasConnection(m_context)) {
                Intent intent = new Intent(AuthorizationActivity.this, MyReservationsActivity.class);
                intent.putExtra("userID", savedUserID);
                startActivity(intent);
            } else {
                showAlertDialog("Нет подключения к интернету", m_context);
            }
        }
    }

    private void putSharedPreferences(String userID) {
        setStringToSharedPreferences("userID", userID, m_context);
        setStringToSharedPreferences("password", passwordField.getText().toString().trim(), m_context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(m_context);
                builder.setTitle("Ошибка")
                    .setMessage("Нужно обязательно выбрать город")
                    .setCancelable(false)
                    .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AuthorizationActivity.this, ChangeCityActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                getCity();
            }
        }
    }
}
