package com.example.eugenedolgushev.workhub.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.eugenedolgushev.workhub.Activities.AuthorizationActivity;
import com.example.eugenedolgushev.workhub.Office;
import com.example.eugenedolgushev.workhub.OfficeList;
import com.example.eugenedolgushev.workhub.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class GetOffices extends AsyncTask<String, Void, String> {

    private Context m_context = null;
    private AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(OfficeList officeList);
    }

    public GetOffices(AsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.m_context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String city = "", resultJson = "";
        try {
            city = URLEncoder.encode(params[0], "UTF-8");
        } catch(UnsupportedEncodingException e) {

        }

        SharedPreferences sPref = m_context
                .getSharedPreferences(AuthorizationActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        String savedUserID = sPref.getString("userID", "");

        String param = "userID=" + savedUserID + "&city=" + city;
        try {
            URL requestUrl = new URL(params[1] + "/?" + param);

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

        ArrayList<Office> offices = null;
        try {
            JSONObject dataJsonObj = new JSONObject(strJson);
            Integer code = 0;
            if (dataJsonObj.has("code")) {
                code = dataJsonObj.getInt("code");
            }
            switch (code) {
                case 0:
                    nonExistingUser(dataJsonObj);
                    break;
                case 1:
                    offices = fullOfficeList(dataJsonObj);
                    break;
                case 2:
                    emptyOfficeList(dataJsonObj);
                    break;
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        OfficeList officeList = new OfficeList(m_context);
        if (offices == null) {
            officeList.setList(new ArrayList<Office>());
        } else {
            officeList.setList(offices);
        }

        delegate.processFinish(officeList);
    }

    private ArrayList<Office> fullOfficeList(JSONObject jsonObj) {
        ArrayList<Office> offices = new ArrayList<>();
        JSONArray list = null;
        try {
            if (jsonObj.has("records")) {
                list = jsonObj.getJSONArray("records");
            }
            for (int i = 0; i < list.length(); ++i){
                Office office = new Office();
                JSONObject recordJsonObj = list.getJSONObject(i);
                if (recordJsonObj.has("city")) {
                    office.setCityName(recordJsonObj.getString("city"));
                }
                if (recordJsonObj.has("name")) {
                    office.setOfficeName(recordJsonObj.getString("name"));
                }
                if (recordJsonObj.has("address")) {
                    office.setOfficeAddress(recordJsonObj.getString("address"));
                }
                if (recordJsonObj.has("mapPosition")) {
                    JSONArray location = recordJsonObj.getJSONArray("mapPosition");
                    office.setLatitude(location.getDouble(0));
                    office.setLongitude(location.getDouble(1));
                }

                offices.add(office);
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }

        return offices;
    }

    private void nonExistingUser(JSONObject jsonObj) {
        String message = "";
        try {
            if (jsonObj.has("message")) {
                message = jsonObj.getString("message");
            }
            Utils.showAlertDialog(message, m_context);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void emptyOfficeList(JSONObject jsonObj) {
        String message = "";
        try {
            if (jsonObj.has("message")) {
                message = jsonObj.getString("message");
            }
            Utils.showAlertDialog(message, m_context, (Activity) m_context);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
