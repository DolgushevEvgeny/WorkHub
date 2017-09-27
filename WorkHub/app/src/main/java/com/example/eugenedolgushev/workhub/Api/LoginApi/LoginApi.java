package com.example.eugenedolgushev.workhub.Api.LoginApi;

import android.content.Context;

import com.example.eugenedolgushev.workhub.Api.BaseApi;
import com.example.eugenedolgushev.workhub.Api.OnAsyncTaskCompleted;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class LoginApi extends BaseApi {

    private static final String URL = "login";
    private Context context;

    public LoginApi(final Context context) {
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
                    if (object.has("name")) {
                        String userName = object.getString("userName");
                        setStringToSharedPreferences("userName", userName, context);
                    }
                    if (object.has("surname")) {
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
