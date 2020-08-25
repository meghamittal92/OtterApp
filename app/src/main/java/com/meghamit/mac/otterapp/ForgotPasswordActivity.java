package com.meghamit.mac.otterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.input_email)
    EditText _email;

    @BindView(R.id.btn_passwordReset)
    Button passwordResetButton;

    @BindView(R.id.link_goToLogin)
    TextView link_goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
        setGoToLoginClickableText();
    }


    private void setGoToLoginClickableText() {
        String text = "Go to Login";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
            }
        };
        ss.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        link_goToLogin.setText(ss);
        link_goToLogin.setMovementMethod(LinkMovementMethod.getInstance());

    }
   public void resetPassword(View view) {
        String registeredEmail = _email.getText().toString().toLowerCase();

       Boolean isValidEmail = ParseServerAccessor.validateEmail(registeredEmail);
       if(isValidEmail) {
           //ParseUSer.pass
           ParseUser.requestPasswordResetInBackground(registeredEmail, new RequestPasswordResetCallback() {
               @Override
               public void done(ParseException e) {
                   if (e == null) {
                       Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG).show();
                   } else {
                       int exceptionCode = e.getCode();

                       if (ParseException.EMAIL_NOT_FOUND == exceptionCode || ParseException.INVALID_EMAIL_ADDRESS == exceptionCode) {
                           Toast.makeText(getApplicationContext(), "This email address is not registered", Toast.LENGTH_LONG).show();
                       } else {
                           Log.e("Error", "Error resetting password");
                           e.printStackTrace();
                           Toast.makeText(getApplicationContext(), "An error occurred. Please try again later", Toast.LENGTH_LONG).show();

                       }
                   }
               }
           });
       }
       else
       {
           Toast.makeText(getApplicationContext(), "This email address is not registered", Toast.LENGTH_LONG).show();

       }

   }

}
