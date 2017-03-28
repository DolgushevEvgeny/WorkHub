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
    private PasswordField m_currentPasswordView, m_newPasswordView, m_repeatPasswordView;
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

    private void validateCurrentPassword() {
        String currentPassword = getStringFromSharedPreferences("password", m_context);
        String inputCurrentPassword = this.getText().toString().trim();
        if (!currentPassword.equals(inputCurrentPassword)) {
            m_currentPasswordViewError.setVisibility(View.VISIBLE);
            setConfirmButtonEnable(false);
        } else {
            m_currentPasswordViewError.setVisibility(View.GONE);
            m_isValidated = true;
            if (m_newPasswordView.isValidated() && m_repeatPasswordView.isValidated()) {
                setConfirmButtonEnable(true);
            }
        }
    }

    private void validateNewPassword() {
        String newPassword = this.getText().toString().trim();
        if (newPassword.length() < 6) {
            m_newPasswordViewError.setText("Ваш пароль должен одержать не менее 6 знаков.");
            m_newPasswordViewError.setVisibility(View.VISIBLE);
            setConfirmButtonEnable(false);
            return;
        }

        String repeatedPassword = m_repeatPasswordView.getText().toString().trim();
        if (!repeatedPassword.equals(newPassword)) {
            m_newPasswordViewError.setText("Пароли не совпадают.");
            m_newPasswordViewError.setVisibility(View.VISIBLE);
            setConfirmButtonEnable(false);
        } else {
            m_newPasswordViewError.setVisibility(View.GONE);
            m_isValidated = true;
            if (m_currentPasswordView.isValidated() && m_repeatPasswordView.isValidated()) {
                setConfirmButtonEnable(true);
            }
        }
    }

    private void validateRepeatedPassword() {
        String repeatedPassword = this.getText().toString().trim();
        if (repeatedPassword.length() < 6) {
            m_repeatPasswordViewError.setText("Ваш пароль должен одержать не менее 6 знаков.");
            m_repeatPasswordViewError.setVisibility(VISIBLE);
            setConfirmButtonEnable(false);
            return;
        }

        String newPassword = m_newPasswordView.getText().toString().trim();
        if (!repeatedPassword.equals(newPassword)) {
            m_repeatPasswordViewError.setText("Пароли не совпадают.");
            m_repeatPasswordViewError.setVisibility(View.VISIBLE);
            setConfirmButtonEnable(false);
        } else {
            m_repeatPasswordViewError.setVisibility(View.GONE);
            m_isValidated = true;
            validateNewPassword();
            if (m_currentPasswordView.isValidated() && m_newPasswordView.isValidated()) {
                setConfirmButtonEnable(true);
            }
        }
    }

    private void setConfirmButtonEnable(boolean enable) {
        m_confirmBtn.setEnabled(enable);
        if (enable) {
            m_confirmBtn.setBackground(getResources().getDrawable(R.drawable.selector));
        } else {
            m_confirmBtn.setBackgroundColor(getResources().getColor(R.color.blocker));
        }
    }

    public void setCurrentPasswordField(PasswordField currentPasswordView) {
        m_currentPasswordView = currentPasswordView;
    }

    public void setNewPasswordField(PasswordField newPasswordView) {
        m_newPasswordView = newPasswordView;
    }

    public void setRepeatedPasswordField(PasswordField repeatedPasswordView) {
        m_repeatPasswordView = repeatedPasswordView;
    }

    public void setCurrentPasswordFieldError(TextView currentPasswordViewError) {
        m_currentPasswordViewError = currentPasswordViewError;
    }

    public void setNewPasswordFieldError(TextView newPasswordViewError) {
        m_newPasswordViewError = newPasswordViewError;
    }

    public void setRepeatedPasswordFieldError(TextView repeatedPasswordViewError) {
        m_repeatPasswordViewError = repeatedPasswordViewError;
    }

    public void setConfirmButton(Button confirmBtn) {
        m_confirmBtn = confirmBtn;
    }

    public boolean isValidated() {
        return m_isValidated;
    }
}
