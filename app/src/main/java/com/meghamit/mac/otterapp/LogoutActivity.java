package com.meghamit.mac.otterapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.meghamit.mac.otterapp.constants.Constants;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Log.i("INFO", "Before logout");
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.remove(Constants.Installation.USER_ID);
        try {
            parseInstallation.save();
        } catch (ParseException e) {
            Log.e("ERROR", "Error clearing installation object");
            e.printStackTrace();
        }
        ParseUser.logOut();
        Log.i("INFO", "After logout");
        Intent intent = new Intent(this, MainActivity.class);

        finishAffinity();
        startActivity(intent);
        //finish();
    }
}
