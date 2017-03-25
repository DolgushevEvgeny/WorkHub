package com.example.eugenedolgushev.workhub.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.eugenedolgushev.workhub.DefaultValues.LOGIN_URL;
import static com.example.eugenedolgushev.workhub.DefaultValues.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class Login extends AsyncTask<String, Void, String> {

    private String userID;
    private Context m_context;
    private AsyncResponse m_delegate;

    public interface AsyncResponse {
        void processFinish(String userID);
    }

    public Login(AsyncResponse delegate, Context context) {
        m_context = context;
        m_delegate = delegate;
    }

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
        try {
            dataJsonObj = new JSONObject(strJson);
            if (dataJsonObj.has("code")) {
                code = dataJsonObj.getInt("code");
            }

            switch (code) {
                case 0:
                    userID = String.valueOf(-1);
                    String message = dataJsonObj.getString("message");
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
            m_delegate.processFinish(userID);
        }
    }

    private void userReceived(JSONObject jsonObject) {
        String userName, userSurname;
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
