package com.example.eugenedolgushev.workhub.Api.LoginApi.impl;

import android.content.Context;

import com.example.eugenedolgushev.workhub.Api.BaseApi;
import com.example.eugenedolgushev.workhub.Api.LoginApi.LoginApiListener;
import com.example.eugenedolgushev.workhub.AsyncTask.OnAsyncTaskCompleted;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class LoginApiImpl extends BaseApi {

    private static final String URL = "login";
    private Context context;

    public LoginApiImpl(final Context context) {
        super();
        this.context = context;
    }

    public void login(RequestParams params, final LoginApiListener listener) {
        super.get(URL, params, new OnAsyncTaskCompleted() {
            @Override
            public void taskCompleted(Object data) {
                String userID = null;
                JSONObject object = (JSONObject)data;
                try {
                    if (object.has("_id")) {
                        userID = object.getString("_id");
                    }
                    if (object.has("userName")) {
                        String userName = object.getString("userName");
                        setStringToSharedPreferences("userName", userName, context);
                    }
                    if (object.has("userSurname")) {
                        String userSurname = object.getString("userSurname");
                        setStringToSharedPreferences("userSurname", userSurname, context);
                    }
                    listener.onLogin(userID);
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
