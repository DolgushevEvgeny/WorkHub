package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditText extends EditText {
    private Boolean m_isValidated = false;

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setValidation(final Boolean validated) {
        this.m_isValidated = validated;
    }

    public Boolean getValidation() {
        return m_isValidated;
    }
}
