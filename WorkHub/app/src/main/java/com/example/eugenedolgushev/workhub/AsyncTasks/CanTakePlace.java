package com.example.eugenedolgushev.workhub.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

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

public class CanTakePlace extends AsyncTask<String, Void, String> {
    private Context m_context;
    private AsyncResponse m_delegate;
    String resultJson = "";
    private static final String URL = "http://192.168.0.32:3000/canTakePlace";
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Integer> times = new ArrayList<>();

    public interface AsyncResponse {
        void processFinish(ArrayList<String> dates, ArrayList<Integer> times);
    }

    public CanTakePlace(AsyncResponse delegate, Context context){
        this.m_delegate = delegate;
        this.m_context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String reservation = "";
        try {
            reservation = URLEncoder.encode(params[0], "UTF-8");
        } catch(UnsupportedEncodingException e) {

        }

        try {
            URL requestUrl = new URL(URL + "/?" + "reservation=" + reservation);

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

        try {
            JSONObject dataJsonObj = new JSONObject(strJson);
            Integer code = 0;
            if (dataJsonObj.has("code")) {
                code = dataJsonObj.getInt("code");
            }
            switch (code) {
                case 0:
                    placeNotFree(dataJsonObj);
                    break;
                case 1:

                    break;
                case 2:

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        m_delegate.processFinish(dates, times);
    }

    private void placeFree(JSONObject dataJsonObj) {

    }

    private void placeNotFree(JSONObject dataJsonObj) {
        String date = "";
        try {
            if (dataJsonObj.has("date")) {
                dates.add(dataJsonObj.getString("date"));
            }
            if (dataJsonObj.has("nonReserveTimes")) {
                JSONArray list = dataJsonObj.getJSONArray("nonReserveTimes");
                for (int i = 0; i < list.length(); ++i) {
                    times.add(list.getInt(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
