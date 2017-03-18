package com.example.eugenedolgushev.workhub.MyViews;

import android.content.Context;
import android.util.AttributeSet;

public class MyEditText extends android.support.v7.widget.AppCompatEditText {
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
