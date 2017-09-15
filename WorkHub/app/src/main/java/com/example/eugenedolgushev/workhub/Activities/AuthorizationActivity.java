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

import com.example.eugenedolgushev.workhub.Api.LoginApi.LoginApiListener;
import com.example.eugenedolgushev.workhub.Api.LoginApi.LoginApi;
import com.example.eugenedolgushev.workhub.R;
import com.loopj.android.http.RequestParams;

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class AuthorizationActivity extends AppCompatActivity {
    private EditText loginField;
    TextInputEditText passwordField;
    private Button loginButton;
    private Context context;
    private LoginApi loginApi;

    private boolean hasCity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        setTitle("Авторизация");

        context = this;

        loginApi = new LoginApi(context);

        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (TextInputEditText) findViewById(R.id.password_field);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection(context)) {
                    String login = loginField.getText().toString();
                    String password = passwordField.getText().toString();

                    RequestParams params  = new RequestParams();
                    params.put("login", login);
                    params.put("password", password);
                    login(params);
                } else {
                    showAlertDialog("Нет подключения к интернету", context);
                }
            }
        });

        getCity();
    }

    private void login(final RequestParams params) {
        loginApi.login(params, new LoginApiListener() {
            @Override
            public void onLogin(final String userID) {
                putSharedPreferences(userID);
                getUserID();
            }
        });
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
        String city = getStringFromSharedPreferences("cities", context);
        if (city.length() == 0) {
            if (hasConnection(context)) {
                Intent intent = new Intent(AuthorizationActivity.this, ChangeCityActivity.class);
                startActivityForResult(intent, 1);
            } else {
                showAlertDialog("Нет подключения к интернету", context);
            }
        } else {
            hasCity = true;
            getUserID();
        }
    }

    private void getUserID() {
        String savedUserID = getStringFromSharedPreferences("userID", context);
        if (savedUserID.length() != 0) {
            Intent intent = new Intent(AuthorizationActivity.this, MyReservationsActivity.class);
            intent.putExtra("userID", savedUserID);
            startActivity(intent);
        }
    }

    private void putSharedPreferences(String userID) {
        setStringToSharedPreferences("userID", userID, context);
        setStringToSharedPreferences("password", passwordField.getText().toString().trim(), context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
