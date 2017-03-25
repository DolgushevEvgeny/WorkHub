package com.example.eugenedolgushev.workhub.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.eugenedolgushev.workhub.DefaultValues.GET_CITIES_URL;
import static com.example.eugenedolgushev.workhub.DefaultValues.MAIN_URL;


public class GetCities extends AsyncTask<String, Void, String> {

    private Context m_context;
    private AsyncResponse m_delegate;

    public interface AsyncResponse {
        void processFinish(CharSequence[] cities);
    }

    public GetCities(AsyncResponse delegate, Context context) {
        m_context = context;
        m_delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String resultJson = "";
        try {
            URL requestUrl = new URL(MAIN_URL + GET_CITIES_URL + "/?");

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
        CharSequence[] resultCities = {};
        try {
            dataJsonObj = new JSONObject(strJson);
            List<String> cities = new ArrayList<>();
            JSONArray jsonCities = dataJsonObj.getJSONArray("cities");
            for (int i = 0; i < jsonCities.length(); ++i) {
                cities.add(jsonCities.getString(i));
            }
            resultCities = cities.toArray(new CharSequence[cities.size()]);
        } catch (JSONException e) {

        }

        m_delegate.processFinish(resultCities);
    }
}
