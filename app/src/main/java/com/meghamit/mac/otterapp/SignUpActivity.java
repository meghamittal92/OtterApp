package com.meghamit.mac.otterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

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

import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";


    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        setLoginOnClick();

//        _loginLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Finish the registration screen and return to the Login activity
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(R., R.ani);
//            }
//        });
    }

    private void setLoginOnClick() {
        String text = "Already a member? Login";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // Finish the registration screen and return to the Login activity

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
               // overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
            }
        };
        ss.setSpan(clickableSpan, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        _loginLink.setText(ss);
        _loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Invalid email/password");
            return;
        }

        _signupButton.setEnabled(false);


       // String name = _nameText.getText().toString();
        //String address = _addressText.getText().toString();
        final String email = _emailText.getText().toString().toLowerCase();
        //final String username = _username.getText().toString();
        //String mobile = _mobileText.getText().toString();
        final String password = _passwordText.getText().toString();
       // String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new SignUpTask(this).execute(email, email, password);


     }


    public void onSignupSuccess(String username, String password) {
        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("password", password);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), "Sign up failed: " + message, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

       // String username = _username.getText().toString();
        //String address = _addressText.getText().toString();
        String email = _emailText.getText().toString().toLowerCase();
        //String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

//        if (name.isEmpty() || name.length() < 3) {
//            _nameText.setError("at least 3 characters");
//            valid = false;
//        } else {
//            _nameText.setError(null);
//        }

//        if (address.isEmpty()) {
//            _addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            _addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

//        if (mobile.isEmpty() || mobile.length() != 10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 30) {
            _passwordText.setError("between 4 and 30  characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 30 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }


    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // don't forget to change the line below with the names of your Activities
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}

class SignUpTask extends AsyncTask<String, String, String> {

    private AlertDialog alertDialog;
    private SignUpActivity signUpActivity;
    private String username;
    private String password;

    public SignUpTask(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
        AlertDialog.Builder builder = new AlertDialog.Builder(signUpActivity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();
//        progressDialog = new ProgressDialog(signUpActivity,
//                R.style.AppTheme_PopupOverlay);
//        progressDialog.getWindow().setGravity(Gravity.CENTER);
    }
    @Override
    protected void onPreExecute() {
       // progressDialog.setMessage("Creating Account...");

        alertDialog.show();

    }

    @Override
    protected void onPostExecute(String result) {
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if(result != null && result.equalsIgnoreCase("Success")) {
            signUpActivity.onSignupSuccess(this.username, this.password);
        }
        else
        {
            signUpActivity.onSignupFailed(result);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = null;

        this.username= strings[0];
        this.password = strings[2];
         result = parseSignUp(strings[0], strings[1], strings[2]);

         Log.i("INFO", "in doinbackground, result is : " + result);
         return result;
    }

    public String parseSignUp(final String username, final String email, final String password) {
        // depending on success
        Log.i("INFO", "In Parse Sign UP");
        final ParseUser user = new ParseUser();
        // Set the user's username and password, which can be obtained by a forms
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        //user.put("postBoxNumber", -1);
        Log.i("INFO", "In Parse Sign UP : username" + username + "password:" + password);
        try {
            user.signUp();
            return "Success";
        } catch (ParseException e) {
            e.printStackTrace();
            ParseUser.logOut();
            return e.getMessage();

        }


    }
}