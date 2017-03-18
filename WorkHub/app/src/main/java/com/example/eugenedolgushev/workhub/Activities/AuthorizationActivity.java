package com.example.eugenedolgushev.workhub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class AuthorizationActivity extends AppCompatActivity {
    private EditText loginField, passwordField;
    private Button loginButton;

    public static final String SHARED_PREFERENCES_NAME = "MyStorage";
    private static final String URL = "http://192.168.0.32:3000/login";
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginField.getText().toString();
                String password = passwordField.getText().toString();

                new ParseData().execute(login, password);
            }
        });

        getSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSharedPreferences();
    }

    private void getSharedPreferences() {
        SharedPreferences sPref = getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        String savedUserID = sPref.getString("userID", "");
        if (savedUserID.length() != 0) {
            Intent intent = new Intent(AuthorizationActivity.this, MyReservationsActivity.class);
            intent.putExtra("userID", savedUserID);
            startActivity(intent);
        }
    }

    private void putSharedPreferences() {
        SharedPreferences sPref = getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("userID", userID);
        editor.putString("password", passwordField.getText().toString().trim());
        editor.commit();
    }

    class ParseData extends AsyncTask<String, Void, String> {

        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String param = "login=" + params[0] + "&password=" + params[1];
            try {
                URL requestUrl = new URL(URL + "/?" + param);

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
            try {
                dataJsonObj = new JSONObject(strJson);
                if (dataJsonObj.has("id")) {
                    userID = dataJsonObj.getString("id");
                } else {
                    return;
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }

            try {
                if (Integer.parseInt(userID) == -1) {

                }
            } catch(NumberFormatException e) {
                putSharedPreferences();
                getSharedPreferences();
            }

        }
    }
}
