package com.example.eugenedolgushev.workhub.MyViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.R;

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;

public class PasswordField extends android.support.v7.widget.AppCompatEditText {

    private Context m_context;
    private int m_viewID;
    private boolean m_isValidated;
    private PasswordField m_newPasswordView;
    private TextView m_currentPasswordViewError, m_newPasswordViewError, m_repeatPasswordViewError;
    private Button m_confirmBtn;

    public PasswordField(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
        m_isValidated = false;
    }

    public void setViewID(int ID) {
        m_viewID = ID;
    }

    public void addLinks(TextView view) {
        switch (m_viewID) {
            case R.id.current_password_view:
                m_currentPasswordViewError = view;
                break;
            case R.id.new_password_view:
                m_newPasswordViewError = view;
                break;
        }
    }

    public void addLinks(TextView view, PasswordField newPassword, Button confirmBtn) {
        m_repeatPasswordViewError = view;
        m_newPasswordView = newPassword;
        m_confirmBtn = confirmBtn;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        switch (m_viewID) {
            case (R.id.current_password_view):
                validateCurrentPassword();
                break;
            case (R.id.new_password_view):
                validateNewPassword();
                break;
            case (R.id.repeat_password_view):
                validateRepeatedPassword();
                break;
        }
    }

    private void validateNewPassword() {
        String newPassword = this.getText().toString().trim();
        if (newPassword.length() < 6) {
            m_newPasswordViewError.setText("Ваш пароль должен одержать не менее 6 знаков.");
            m_newPasswordViewError.setVisibility(View.VISIBLE);
        } else {
            m_newPasswordViewError.setVisibility(View.GONE);
            m_isValidated = true;
        }
    }

    private void validateRepeatedPassword() {
        String repeatedPassword = this.getText().toString().trim();
        if (repeatedPassword.length() < 6) {
            m_repeatPasswordViewError.setText("Ваш пароль должен одержать не менее 6 знаков.");
            m_repeatPasswordViewError.setVisibility(VISIBLE);
            return;
        }

        String newPassword = m_newPasswordView.getText().toString().trim();
        if (!repeatedPassword.equals(newPassword)) {
            m_repeatPasswordViewError.setText("Пароли не совпадают.");
            m_repeatPasswordViewError.setVisibility(View.VISIBLE);
        } else {
            m_repeatPasswordViewError.setVisibility(View.GONE);
            m_isValidated = true;
            m_confirmBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            m_confirmBtn.setEnabled(true);
        }
    }

    private void validateCurrentPassword() {
        String currentPassword = getStringFromSharedPreferences("password", m_context);
        String inputCurrentPassword = this.getText().toString().trim();
        if (!currentPassword.equals(inputCurrentPassword)) {
            m_currentPasswordViewError.setVisibility(View.VISIBLE);
        } else {
            m_currentPasswordViewError.setVisibility(View.GONE);
            m_isValidated = true;
        }
    }

    public boolean isValidated() {
        return m_isValidated;
    }
}
