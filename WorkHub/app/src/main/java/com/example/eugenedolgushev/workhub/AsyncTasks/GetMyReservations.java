package com.example.eugenedolgushev.workhub.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.eugenedolgushev.workhub.Reservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.eugenedolgushev.workhub.Activities.AuthorizationActivity.SHARED_PREFERENCES_NAME;

public class GetMyReservations extends AsyncTask<String, Void, String> {

    private Context m_context = null;
    private AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(ArrayList<Reservation> reservations);
    }

    public GetMyReservations(AsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.m_context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences sPref = m_context
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        String savedUserID = sPref.getString("userID", "");
        String param = "userID=" + savedUserID, resultJson = "";;
        try {
            java.net.URL requestUrl = new URL(params[0] + "/?" + param);

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

        ArrayList<Reservation> reservations = new ArrayList<>();

        JSONObject dataJsonObj;
        JSONArray list;

        try {
            dataJsonObj = new JSONObject(strJson);
            if (dataJsonObj.has("records")) {
                list = dataJsonObj.getJSONArray("records");
            } else {
                return;
            }

            for (int i = 0; i < list.length(); ++i){
                Reservation reservation = new Reservation();
                JSONObject recordJsonObj = list.getJSONObject(i);
                if (recordJsonObj.has("city")) {
                    reservation.setOfficeCity(recordJsonObj.getString("city"));
                }
                if (recordJsonObj.has("plan")) {
                    reservation.setReservationPlanName(recordJsonObj.getString("plan"));
                }
                if (recordJsonObj.has("office")) {
                    reservation.setOfficeName(recordJsonObj.getString("office"));
                }
                if (recordJsonObj.has("date")) {
                    reservation.setReservationDate(recordJsonObj.getString("date"));
                }
                if (recordJsonObj.has("startTime")) {
                    reservation.setStartTime(recordJsonObj.getInt("startTime"));
                }
                if (recordJsonObj.has("duration")) {
                    reservation.setDuration(recordJsonObj.getInt("duration"));
                }
                if (recordJsonObj.has("planPrice")) {
                    reservation.setReservationSum(recordJsonObj.getInt("planPrice") *
                            reservation.getDuration());
                }

                reservations.add(reservation);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        delegate.processFinish(reservations);
    }
}
