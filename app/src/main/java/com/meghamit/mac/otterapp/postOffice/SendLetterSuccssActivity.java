package com.meghamit.mac.otterapp.postOffice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.UserHome2Activity;


public class SendLetterSuccssActivity extends AbstractBaseActivity {

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {

        super.onViewReady(savedInstanceState, intent);
       // ProgressDialog progressDialog =  new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        //setContentView(R.layout.activity_send_letter_succss);
      //  progressDialog.show();
//        Observable.create(new Observable.OnSubscribe<Void>() {
//            @Override
//            public void call(Subscriber<? super Void> subscriber) {
//
//                setContentView(R.layout.activity_send_letter_succss);
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
//            @Override
//            public void onCompleted() {
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Void aVoid) {
//
//            }
//        });

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_send_letter_succss;
    }

    public void goHome(View view) {

        Intent intent = new Intent(this, UserHome2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        goHome(null);
    }
}
