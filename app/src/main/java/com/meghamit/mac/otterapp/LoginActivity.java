package com.meghamit.mac.otterapp;

import android.app.AlertDialog;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;

import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meghamit.mac.otterapp.constants.Constants;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email) EditText _email;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    @BindView(R.id.linkForgotPassword) TextView linkForgotPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Log.i("INFO", "LOGIN!!!!!!!!!!!!");
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        setCreateAccountCLickableText();
        setForgotPasswordClickableText();

    }

    private void setForgotPasswordClickableText() {
        String text = "Forgot Password?";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        };
        ss.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkForgotPassword.setText(ss);
        linkForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

    }
    private void setCreateAccountCLickableText() {
        String text = "No account yet? Create one";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                Log.i("INFO", "After start activity for result");
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                //overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
            }
        };
        ss.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        _signupLink.setText(ss);
        _signupLink.setMovementMethod(LinkMovementMethod.getInstance());

    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            LoginResult loginResult = new LoginResult(-1, "Invalid email/password", true);
            onLoginFailed(loginResult);
            return;
        }

        _loginButton.setEnabled(false);

        final String username = _email.getText().toString().toLowerCase().trim();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.


        new LoginTask(this).execute(username, password);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
                //Log.i("INFO", "In onActivityResult!!!!!!" + data.getStringExtra("username") + data.getStringExtra("password"));
                _email.setText(data.getStringExtra("username"));
               _passwordText.setText(data.getStringExtra("password"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, UserHome2Activity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(LoginResult result) {

        if(result.isError()) {
            if (result.getErrorCode() == 205) {
                Toast.makeText(getBaseContext(), "Unverified account. Please click the link in your email.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Login failed: " + result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _email.getText().toString().toLowerCase();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _email.setError("invalid email");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 30) {
            _passwordText.setError("between 4 and 30 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    class LoginTask extends AsyncTask<String, String, LoginResult> {

        private AlertDialog alertDialog;
        private LoginActivity loginActivity;

        public LoginTask(LoginActivity loginActivity) {
            this.loginActivity = loginActivity;
            AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity);
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(R.layout.layout_loading_dialog);
            alertDialog = builder.create();

//            progressDialog = new ProgressDialog(loginActivity,
//                    R.style.AppTheme_PopupOverlay);
//            progressDialog.getWindow().setGravity(Gravity.CENTER);
        }
        @Override
        protected LoginResult doInBackground(String... strings) {
            LoginResult result = new LoginResult(0, null, false);
           // Integer result = 0;//Success result
            try {
                //clean up logins on other devices
                Log.i("INFO", "Trying to login with username : " + strings[0]  + " and password : " + strings[1]);
                ParseUser currentUser = ParseUser.logIn(strings[0], strings[1]);
                //ParseServerAccessor.cleanUpOnOtherDevices(currentUser);
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put(Constants.Installation.USER_ID, currentUser);
                installation.save();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("ERROR", "Login failed error code is :" + e.getCode());
                result.setErrorCode(e.getCode());
                result.setErrorMessage(e.getMessage());
                result.setIsError(true);
            }
            return result;
        }
        @Override
        protected void onPreExecute() {
           // progressDialog.setMessage("Logging in...");
            alertDialog.show();

        }

        @Override
        protected void onPostExecute(LoginResult result) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            if(!result.isError()) {
                loginActivity.onLoginSuccess();
            }
            else
            {
                loginActivity.onLoginFailed(result);
            }
        }
    }

    private class LoginResult {
        int errorCode;
        String errorMessage;
        Boolean isError;

        public Boolean isError() {
            return isError;
        }

        public void setIsError(Boolean isError) {
            this.isError = isError;
        }

        public LoginResult(int errorCode, String errorMessage, Boolean isError) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.isError = isError;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }


}
