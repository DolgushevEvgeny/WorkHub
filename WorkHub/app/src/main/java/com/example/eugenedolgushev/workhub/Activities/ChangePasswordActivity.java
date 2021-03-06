package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.MyViews.PasswordField;
import com.example.eugenedolgushev.workhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.eugenedolgushev.workhub.DefaultValues.CHANGE_PASSWORD_URL;
import static com.example.eugenedolgushev.workhub.DefaultValues.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;
import static com.example.eugenedolgushev.workhub.Utils.showConfirmDialog;

public class ChangePasswordActivity extends AppCompatActivity {

    private Context m_context;
    private PasswordField currentPasswordView, newPasswordView, repeatPasswordView;
    private Button confirmPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle("Сменить пароль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_context = this;

        confirmPasswordBtn = (Button) findViewById(R.id.confirm_password_button);
        confirmPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection(m_context)) {
                    new ChangePassword().execute(newPasswordView.getText().toString());
                } else {
                    showAlertDialog("Нет подключения к интернету", m_context);
                }
            }
        });

        currentPasswordView = (PasswordField) findViewById(R.id.current_password_view);
        newPasswordView = (PasswordField) findViewById(R.id.new_password_view);
        repeatPasswordView = (PasswordField) findViewById(R.id.repeat_password_view);

        currentPasswordView.setViewID(R.id.current_password_view);
        currentPasswordView.setCurrentPasswordFieldError((TextView) findViewById(R.id.current_password_view_error));
        currentPasswordView.setNewPasswordField(newPasswordView);
        currentPasswordView.setRepeatedPasswordField(repeatPasswordView);
        currentPasswordView.setConfirmButton(confirmPasswordBtn);

        newPasswordView.setViewID(R.id.new_password_view);
        newPasswordView.setNewPasswordFieldError((TextView) findViewById(R.id.new_password_view_error));
        newPasswordView.setRepeatedPasswordField(repeatPasswordView);
        newPasswordView.setCurrentPasswordField(currentPasswordView);
        newPasswordView.setConfirmButton(confirmPasswordBtn);

        repeatPasswordView.setViewID(R.id.repeat_password_view);
        repeatPasswordView.setRepeatedPasswordFieldError((TextView) findViewById(R.id.repeat_password_view_error));
        repeatPasswordView.setRepeatedPasswordField(repeatPasswordView);
        repeatPasswordView.setNewPasswordFieldError((TextView) findViewById(R.id.new_password_view_error));
        repeatPasswordView.setNewPasswordField(newPasswordView);
        repeatPasswordView.setCurrentPasswordField(currentPasswordView);
        repeatPasswordView.setConfirmButton(confirmPasswordBtn);
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

    class ChangePassword extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String resultJson = "", password = "";
            try {
                password = URLEncoder.encode(params[0], "UTF-8");
            } catch(UnsupportedEncodingException e) {

            }

            String savedUserID = getStringFromSharedPreferences("userID", m_context);
            String requestParams = "userID=" + savedUserID + "&password=" + password;
            try {
                URL requestUrl = new URL(MAIN_URL + CHANGE_PASSWORD_URL + "/?" + requestParams);

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

            Integer code = 0;
            String message = "";
            try {
                JSONObject dataJsonObj = new JSONObject(strJson);
                if (dataJsonObj.has("code")) {
                    code = dataJsonObj.getInt("code");
                }
                if (dataJsonObj.has("message")) {
                    message = dataJsonObj.getString("message");
                    showConfirmDialog(message, m_context);
                }
            } catch (JSONException e) {

            }
        }
    }
}
