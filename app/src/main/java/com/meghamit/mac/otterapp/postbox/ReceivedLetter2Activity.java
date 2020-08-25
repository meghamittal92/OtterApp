package com.meghamit.mac.otterapp.postbox;


import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.postOffice.SendLetter2Activity;
import com.parse.ParseException;
import com.parse.ParseFile;

public class ReceivedLetter2Activity extends AbstractBaseActivity {

    @BindView(R.id.receivedLetterTextView)
    TextView receivedLetterTextView;

    @BindView(R.id.receivedLetterImageView)
    ImageView receivedLetterImageView;

    @BindView(R.id.receivedLetterImageProgressBar)
    ProgressBar receivedLetterImageProgressBar;

    Bitmap imageBitMap;

    String letterStatus;

    int fromPostBox;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        receivedLetterTextView.setText(intent.getStringExtra(Constants.Letter.LETTER_DATA));

        Boolean isLetterImagePresent = intent.getBooleanExtra(Constants.IntentExtra.IS_LETTER_IMAGE_PRESENT, false);

        letterStatus = intent.getStringExtra(Constants.LetterMetadata.STATUS);
        String letterObjectId = intent.getStringExtra(Constants.Letter.OBJECT_ID);
        fromPostBox = intent.getIntExtra(Constants.LetterMetadata.FROM_POSTBOX, -1);

        if(isLetterImagePresent)
        {
            receivedLetterImageView.setVisibility(View.INVISIBLE);
            receivedLetterImageProgressBar.setVisibility(View.VISIBLE);

            Observable.create(new Observable.OnSubscribe<Letter>() {
                @Override
                public void call(Subscriber<? super Letter> subscriber) {

                    Letter letter = ParseServerAccessor.getLetterFromObjectId(letterObjectId);

                    if (letter == null) {
                        subscriber.onError(new Throwable("letter not found"));

                    } else {
                        subscriber.onNext(letter);
                        subscriber.onCompleted();
                    }
                }

            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Letter>() {
                @Override
                public void onCompleted() {

                    //Toast.makeText(getApplicationContext(), "yaya subscriber completed", Toast.LENGTH_LONG).show();
                    receivedLetterImageProgressBar.setVisibility(View.INVISIBLE);
                    receivedLetterImageView.setImageBitmap(imageBitMap);
                    receivedLetterImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Letter letter) {
                   ParseFile imageParseFile =  letter.getLetterImage();
                    try {
                        if(imageParseFile != null) {
                            byte[] data = imageParseFile.getData();
                            if (data != null) {
                                 imageBitMap = BitmapFactory.decodeByteArray(data, 0, data.length);

                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else
        {
            receivedLetterImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_received_letter2;
    }

    public void replyToLetter(View view) {


        Intent intent = new Intent(this, SendLetter2Activity.class);
        intent.putExtra(Constants.IntentExtra.REPLY_TO_POST_BOX, fromPostBox);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {


        Intent returnIntent = new Intent();
        if(  !LetterStatus.OPENED.toString().equals(letterStatus)) {
            //unopenedLetter was opened
            returnIntent.putExtra(Constants.IntentExtra.RECEIVED_LETTERS_ADAPTER_NEEDS_REFRESH, "true");
        }
        setResult(RESULT_OK,returnIntent);
        super.onBackPressed();
    }
}
