package com.example.eugenedolgushev.workhub.AsyncTask;

public interface OnAsyncTaskCompleted {

    void taskCompleted(Object data);

    void taskCompleted(final String errorMsg);
}
