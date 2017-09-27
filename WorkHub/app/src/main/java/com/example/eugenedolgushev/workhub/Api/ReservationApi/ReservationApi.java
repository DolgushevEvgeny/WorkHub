package com.example.eugenedolgushev.workhub.Api.ReservationApi;

import android.content.Context;

import com.example.eugenedolgushev.workhub.Api.BaseApi;
import com.example.eugenedolgushev.workhub.Api.OnAsyncTaskCompleted;
import com.example.eugenedolgushev.workhub.Reservation;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class ReservationApi extends BaseApi{

    private static final String URL = "MyReservations";
    private Context context;

    public ReservationApi(final Context context) {
        super();
        this.context = context;
    }

    public void getReservations(RequestParams params, final ReservationApiListener listener) {
        super.get(URL, params, new OnAsyncTaskCompleted() {
            @Override
            public void taskCompleted(Object data) {
                JSONArray list = (JSONArray)data;
                try {
                    ArrayList<Reservation> reservations = new ArrayList<>();
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
                        if (recordJsonObj.has("officeAddress")) {
                            reservation.setOfficeAddress(recordJsonObj.getString("officeAddress"));
                        }
                        if (recordJsonObj.has("status")) {
                            reservation.setReservationStatus(recordJsonObj.getString("status"));
                        }

                        reservations.add(reservation);
                    }
                    listener.onReservationsLoad(reservations);
                } catch(JSONException e) {
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
