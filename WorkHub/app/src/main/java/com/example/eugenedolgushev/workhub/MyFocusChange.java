package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.view.View;

import com.example.eugenedolgushev.workhub.MyViews.MyEditText;

import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class MyFocusChange implements View.OnFocusChangeListener {
    Context m_context = null;

    public MyFocusChange(Context context) {
        this.m_context = context;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case (R.id.card_number_holder_1):
                action(hasFocus, (MyEditText) view, 4);
                break;
            case (R.id.card_number_holder_2):
                action(hasFocus, (MyEditText) view, 4);
                break;
            case (R.id.card_number_holder_3):
                action(hasFocus, (MyEditText) view, 4);
                break;
            case (R.id.card_number_holder_4):
                action(hasFocus, (MyEditText) view, 4);
                break;
            case (R.id.card_valid_period_month):
                action(hasFocus, (MyEditText) view, 2);
                break;
            case (R.id.card_valid_period_year):
                action(hasFocus, (MyEditText) view, 2);
                break;
            case (R.id.card_safe_code):
                action(hasFocus, (MyEditText) view, 3);
                break;
            case (R.id.card_owner):
                action(hasFocus, (MyEditText) view, ((MyEditText) view).getText().toString());
                break;
        }
    }

    private void action(Boolean focus, MyEditText view, Integer digits) {
        if (focus) {
            view.setBackground(null);
        } else {
            validateCardNumber(view, digits);
        }
    }

    private void action(Boolean focus, MyEditText view, String value) {
        if (focus) {
            view.setBackground(null);
        } else {
            validateCardOwner(view, value);
        }
    }

    private void validateCardOwner(MyEditText editTextView, String value) {
        if (value.length() == 0) {
            editTextView.setBackgroundResource(R.drawable.edit_text_border);
            showAlertDialog("Заполните поле владельца карты", m_context);
        } else {
            editTextView.setValidation(true);
        }
    }

    private void validateCardNumber(MyEditText editTextView, Integer digits) {
        String value = editTextView.getText().toString();
        if (value.length() != digits) {
            editTextView.setBackgroundResource(R.drawable.edit_text_border);
            showAlertDialog("Введите " + digits + " числовых знака", m_context);
        } else {
            editTextView.setValidation(true);
        }
    }
}
