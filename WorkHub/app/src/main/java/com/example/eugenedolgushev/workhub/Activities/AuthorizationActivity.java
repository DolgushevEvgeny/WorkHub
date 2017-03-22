package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eugenedolgushev.workhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.eugenedolgushev.workhub.DefaultValues.LOGIN_URL;
import static com.example.eugenedolgushev.workhub.DefaultValues.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class AuthorizationActivity extends AppCompatActivity {
    private EditText loginField;
    TextInputEditText passwordField;
    private Button loginButton;
    private Context m_context;

    private String userID = "", userName = "", userSurname = "";
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
                String login = loginField.getText().toString();
                String password = passwordField.getText().toString();

                new ParseData().execute(login, password);
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
            Intent intent = new Intent(AuthorizationActivity.this, ChangeCityActivity.class);
            startActivityForResult(intent, 1);
        } else {
            hasCity = true;
            getUserID();
        }
    }

    private void getUserID() {
        String savedUserID = getStringFromSharedPreferences("userID", m_context);
        if (savedUserID.length() != 0) {
            Intent intent = new Intent(AuthorizationActivity.this, MyReservationsActivity.class);
            intent.putExtra("userID", savedUserID);
            startActivity(intent);
        }
    }

    private void putSharedPreferences() {
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

    class ParseData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String resultJson = "", param = "login=" + params[0] + "&password=" + params[1];
            try {
                URL requestUrl = new URL(MAIN_URL + LOGIN_URL + "/?" + param);

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

            JSONObject dataJsonObj = null;
            Integer code = 0;
            String message = "";
            try {
                dataJsonObj = new JSONObject(strJson);
                if (dataJsonObj.has("code")) {
                    code = dataJsonObj.getInt("code");
                }

                switch (code) {
                    case 0:
                        userID = String.valueOf(-1);
                        message = dataJsonObj.getString("message");
                        showAlertDialog(message, m_context);
                        break;
                    case 1:
                        userReceived(dataJsonObj.getJSONObject("user"));
                        break;
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }

            try {
                Integer.parseInt(userID);
            } catch(NumberFormatException e) {
                putSharedPreferences();
                getUserID();
            }
        }

        private void userReceived(JSONObject jsonObject) {
            try {
                if (jsonObject.has("_id")) {
                    userID = jsonObject.getString("_id");
                }
                if (jsonObject.has("userName")) {
                    userName = jsonObject.getString("userName");
                    setStringToSharedPreferences("userName", userName, m_context);
                }
                if (jsonObject.has("userSurname")) {
                    userSurname = jsonObject.getString("userSurname");
                    setStringToSharedPreferences("userSurname", userSurname, m_context);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
