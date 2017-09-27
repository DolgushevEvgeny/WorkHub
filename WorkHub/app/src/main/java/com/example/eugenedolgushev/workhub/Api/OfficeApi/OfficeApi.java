package com.example.eugenedolgushev.workhub.Api.OfficeApi;

import android.content.Context;

import com.example.eugenedolgushev.workhub.Api.BaseApi;
import com.example.eugenedolgushev.workhub.Api.OnAsyncTaskCompleted;
import com.example.eugenedolgushev.workhub.Model.Office;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class OfficeApi extends BaseApi{

    private static final String URL = "getOffices";
    private static OfficeApi instance;

    private OfficeApi() {
        super();
    }

    public static OfficeApi getInstance() {
        if (instance == null) {
            instance = new OfficeApi();
        }
        return instance;
    }

    public void getOffices(RequestParams params, final OfficeApiListener listener, final Context context) {
        super.get(URL, params, new OnAsyncTaskCompleted() {
            @Override
            public void taskCompleted(Object data) {
                JSONArray list = (JSONArray)data;
                try {
                    ArrayList<Office> offices = new ArrayList<>();
                    for (int i = 0; i < list.length(); ++i) {
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
                        if (recordJsonObj.has("latitude")) {
                            office.setLatitude(recordJsonObj.getDouble("latitude"));
                        }
                        if (recordJsonObj.has("longitude")) {
                            office.setLongitude(recordJsonObj.getDouble("longitude"));
                        }
                        if (recordJsonObj.has("schedule")) {
                            JSONArray jsonWorkTimeList = recordJsonObj.getJSONArray("schedule");
                            ArrayList<ArrayList<Integer>> workTimeList = new ArrayList<>();
                            for (int j = 0; j < jsonWorkTimeList.length(); ++j) {
                                JSONArray item = jsonWorkTimeList.getJSONArray(j);
                                ArrayList<Integer> workTimeItem = new ArrayList<>();
                                for (int k = 0; k < item.length(); ++k) {
                                    workTimeItem.add(item.getInt(k));
                                }
                                workTimeList.add(workTimeItem);
                            }
                            office.setWorkTimeList(workTimeList);
                        }
                        offices.add(office);
                    }
                    listener.onOfficesLoad(offices);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void taskCompleted(String errorMsg) {
                showAlertDialog(errorMsg, context);
            }
        });
    }
}
