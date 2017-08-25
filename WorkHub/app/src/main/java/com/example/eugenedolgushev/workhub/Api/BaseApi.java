package com.example.eugenedolgushev.workhub.Api;

import com.example.eugenedolgushev.workhub.AsyncTask.OnAsyncTaskCompleted;
import com.example.eugenedolgushev.workhub.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BaseApi {

    private AsyncHttpClient asyncHttpClient;
    private static final String BASIC_URL = BuildConfig.BASIC_URL;

    public BaseApi() {
        this.asyncHttpClient = new AsyncHttpClient();
    }

    protected void get(final String url, RequestParams queryParams, final OnAsyncTaskCompleted asyncListener) {
        this.asyncHttpClient.get(BASIC_URL + url, queryParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("meta")) {
                        JSONObject meta = response.getJSONObject("meta");
                        if (meta.has("success")) {
                            boolean success = meta.getBoolean("success");
                            if (success) {
                                if (response.has("data")) {
                                    Object data = response.get("data");
                                    asyncListener.taskCompleted(data);
                                }
                            } else {
                                if (meta.has("error")) {
                                    String error = meta.getString("error");
                                    asyncListener.taskCompleted(error);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                asyncListener.taskCompleted(errorResponse);
            }
        });
    }

    protected void post(final String url, RequestParams queryParams, final OnAsyncTaskCompleted asyncListener) {
        this.asyncHttpClient.post(BASIC_URL + url, queryParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("meta")) {
                        JSONObject meta = response.getJSONObject("meta");
                        if (meta.has("success")) {
                            boolean success = meta.getBoolean("success");
                            if (success) {
                                if (response.has("data")) {
                                    Object data = response.get("data");
                                    asyncListener.taskCompleted(data);
                                }
                            } else {
                                if (meta.has("error")) {
                                    String error = meta.getString("error");
                                    asyncListener.taskCompleted(error);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                asyncListener.taskCompleted(errorResponse);
            }
        });
    }

    protected RequestParams prepareParams(RequestParams params, final String key, final String value) {
        params.put(key, value);
        return params;
    }
}
