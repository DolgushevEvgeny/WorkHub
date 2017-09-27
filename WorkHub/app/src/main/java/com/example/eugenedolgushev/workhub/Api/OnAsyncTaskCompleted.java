package com.example.eugenedolgushev.workhub.Api;

public interface OnAsyncTaskCompleted {

    void taskCompleted(Object data);

    void taskCompleted(final String errorMsg);
}
