package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.MyViews.PasswordField;
import com.example.eugenedolgushev.workhub.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private Context m_context;
    private PasswordField currentPasswordView, newPasswordView, repeatPasswordView;
    private Button confirmPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle("Сменить пароль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_context = this;

        confirmPasswordBtn = (Button) findViewById(R.id.confirm_password_button);
        confirmPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        currentPasswordView = (PasswordField) findViewById(R.id.current_password_view);
        currentPasswordView.setViewID(R.id.current_password_view);
        newPasswordView = (PasswordField) findViewById(R.id.new_password_view);
        newPasswordView.setViewID(R.id.new_password_view);
        repeatPasswordView = (PasswordField) findViewById(R.id.repeat_password_view);
        repeatPasswordView.setViewID(R.id.repeat_password_view);

        currentPasswordView.addLinks((TextView) findViewById(R.id.current_password_view_error));
        newPasswordView.addLinks((TextView) findViewById(R.id.new_password_view_error));
        repeatPasswordView.addLinks((TextView) findViewById(R.id.repeat_password_view_error),
                newPasswordView, confirmPasswordBtn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
