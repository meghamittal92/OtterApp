package com.meghamit.mac.otterapp;

import android.app.Application;
import android.util.Log;

import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by mac on 08/04/20.
 */

public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("INFO", "STARTER IS RUN !!!!!");

        // Enable Local Datastore.
        // mlJAX4PnlDSa
        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(LetterMetadata.class);
        ParseObject.registerSubclass(Letter.class);
        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server("https://parseapi.back4app.com/")
                .enableLocalDataStore()
                .build()
        );


//        ParsePush.subscribeInBackground("", new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
//                } else {
//                    Log.e("com.parse.push", "failed to subscribe for push", e);
//                }
//            }
//        });
//        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        ArrayList<String> channels = new ArrayList<>();
        channels.add("News");
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("channels", channels);
        installation.put("GCMSenderId", "699165510124");
        installation.saveInBackground();


    }
}