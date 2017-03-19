package com.example.eugenedolgushev.workhub.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.eugenedolgushev.workhub.Plan;
import com.example.eugenedolgushev.workhub.PlanList;
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

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;

public class GetPlans extends AsyncTask<String, Void, String> {

    private Context m_context;
    private AsyncResponse m_delegate;

    public interface AsyncResponse {
        void processFinish(PlanList planList);
    }

    public GetPlans(AsyncResponse delegate, Context context){
        this.m_delegate = delegate;
        this.m_context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String city = "", officeName = params[1], resultJson = "";
        try {
            city = URLEncoder.encode(params[0], "UTF-8");
        } catch(UnsupportedEncodingException e) {

        }

        String savedUserID = getStringFromSharedPreferences("userID", m_context);

        String requestParams = "userID=" + savedUserID + "&city=" + city + "&office=" + officeName;
        try {
            URL requestUrl = new URL(params[2] + "/?" + requestParams);

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

        ArrayList<Plan> plans = null;
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
                    plans = fullPlanList(dataJsonObj);
                    break;
                case 2:
                    emptyPlanList(dataJsonObj);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PlanList planList = new PlanList(m_context);
        if (plans == null) {
            planList.setList(new ArrayList<Plan>());
        } else {
            planList.setList(plans);
        }

        m_delegate.processFinish(planList);
    }

    private ArrayList<Plan> fullPlanList(JSONObject jsonObj) {
        ArrayList<Plan> plans = new ArrayList<>();
        JSONArray list = null;
        try {
            if (jsonObj.has("records")) {
                list = jsonObj.getJSONArray("records");
            }
            for (int i = 0; i < list.length(); ++i){
                Plan plan = new Plan();
                JSONObject recordJsonObj = list.getJSONObject(i);
                if (recordJsonObj.has("name")) {
                    plan.setPlanName(recordJsonObj.getString("name"));
                }
                if (recordJsonObj.has("price")) {
                    plan.setPlanPrice(recordJsonObj.getInt("price"));
                }

                plans.add(plan);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return plans;
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

    private void emptyPlanList(JSONObject jsonObj) {
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
