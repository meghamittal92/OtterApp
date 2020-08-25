package com.meghamit.mac.otterapp.postOffice;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.FunnyLetterStatus;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.postbox.ReceivedLetter2Activity;
import com.parse.ParseFile;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SentLetterActivity extends AbstractBaseActivity {

    @BindView(R.id.sentLetterTitle)
    TextView letterTitle;

    @BindView(R.id.sentLetterDateSent)
    TextView dateSent;

    @BindView(R.id.sentLetterFromPostBox)
    TextView fromPostBox;

    @BindView(R.id.sentLetterToPostBox)
    TextView toPostBox;

    @BindView(R.id.sentLetterStatus)
    TextView statusTextView;

    @BindView(R.id.sentLetterDateReceived)
    TextView dateReceived;

    @BindView(R.id.dateReceivedTagTextView)
            TextView dateReceivedTag;

    @BindView(R.id.expectedToReachTextView)
            TextView expectedToReachTextView;
    @BindView(R.id.viewLetterLink)
            TextView viewLetterLink;

    @BindView(R.id.viewLetterProgressBar)
    ProgressBar viewLetterProgressBar;

    @BindView(R.id.checkStatusProgressBar)
            ProgressBar checkStatusProgressBar;

    Intent showLetterIntent;

     Bundle bundle;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    //@BindView(R.id.dateReceived)
    //TextView dateReceivedText;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        Log.i("INFO", "In sent letter view activity");



        //Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.i("INFO", "Letter Title is" + bundle.getString(Constants.LetterMetadata.TITLE));
        letterTitle.setText(bundle.getString(Constants.LetterMetadata.TITLE));
        dateSent.setText(formatter.format(bundle.getSerializable(Constants.LetterMetadata.DATE_SENT)));
        fromPostBox.setText(Integer.toString(bundle.getInt(Constants.LetterMetadata.FROM_POSTBOX, -1)));
        toPostBox.setText(Integer.toString(bundle.getInt(Constants.LetterMetadata.TO_POSTBOX, -1)));
       // status.setText(bundle.getString(Constants.LetterMetadata.STATUS));

//        if (bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED) != null ) {
//            dateReceived.setText(bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED).toString());
//        } else {
//            //dateReceivedText.setText("");
//            dateReceived.setText("UNKNOWN");
//        }
        setViewLetterClickableText();
        checkLetterStatus(statusTextView);
    }

    private void setViewLetterClickableText() {
        String text = "View Letter";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
               // Toast.makeText(getApplicationContext(), "Failed to fetch sent Letter", Toast.LENGTH_LONG).show();
                Observable<Letter> fetchLetterObservable = createFetchLetterObservable();

                fetchLetterObservable.subscribeOn(Schedulers.newThread())
                        .doOnSubscribe(() -> viewLetterProgressBar.setVisibility(View.VISIBLE))
                        .doOnSubscribe(() -> viewLetterLink.setVisibility(View.INVISIBLE))
                        .doOnTerminate(() -> viewLetterProgressBar.setVisibility(View.INVISIBLE))
                      // .doOnError((e) -> Toast.makeText(getApplicationContext(), "Failed to fetch sent Letter", Toast.LENGTH_LONG).show())
                        .doOnTerminate(() -> viewLetterLink.setVisibility(View.VISIBLE))
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Letter>() {
                            @Override
                            public void onCompleted() {

                            }

                    @Override
                    public void onError(Throwable e) {
                      //  Toast.makeText(getApplicationContext(), "Failed to fetch sent Letter", Toast.LENGTH_LONG).show();
                        //Getting called from incorrect thread exception on making toast
                        Log.e("ERROR", "Failed to fetch sent letter");

                    }

                    @Override
                            public void onNext(Letter letter) {
                                if(letter != null)
                                {
                                    ParseFile parseFile = (ParseFile) letter.get(Constants.Letter.LETTER_DATA);
                                    ParseFile imageFile = (ParseFile) letter.get(Constants.Letter.LETTER_IMAGE);
                                    Boolean isLetterImagePresent = false;

                                    if(imageFile != null)
                                    {
                                        isLetterImagePresent = true;


                                    }
                                    String letterData = ParseServerAccessor.readFile(parseFile);
                                  showLetterIntent = new Intent(getApplicationContext(), ReceivedLetter2Activity.class);

                                    showLetterIntent.putExtra(Constants.Letter.LETTER_DATA, letterData);
                                    showLetterIntent.putExtra(Constants.IntentExtra.IS_LETTER_IMAGE_PRESENT, isLetterImagePresent);
                                    showLetterIntent.putExtra(Constants.Letter.OBJECT_ID, letter.getObjectId());
                                    startActivity(showLetterIntent);



                                }
                            }
                        });

            }
        };
        ss.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewLetterLink.setText(ss);
        viewLetterLink.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private Observable<Letter> createFetchLetterObservable() {

        return Observable.create(new Observable.OnSubscribe<Letter>() {


            @Override
            public void call(Subscriber<? super Letter> subscriber) {
                Letter letter = ParseServerAccessor.getLetter(bundle.getString(Constants.LetterMetadata.OBJECT_ID));
                if(letter != null) {
                    subscriber.onNext(letter);
                }
                else
                {
                    subscriber.onError(new Exception("Failed to fetch sent letter"));
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sent_letter;
    }

    public void checkLetterStatus(View view) {

         new CheckStatusTask(this).execute();


    }

    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private class CheckStatusTask extends AsyncTask<Void, Void, String> {
        private SentLetterActivity sentLetterActivity;
        public CheckStatusTask(SentLetterActivity sentLetterActivity) {
            this.sentLetterActivity = sentLetterActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusTextView.setVisibility(View.INVISIBLE);
            checkStatusProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String status) {
            checkStatusProgressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(status);
            if(!( LetterStatus.SENT.toString().equals(status) || LetterStatus.OPENED.toString().equals(status)))
            {
                Date dateSent = (Date) bundle.getSerializable(Constants.LetterMetadata.DATE_SENT);
                Log.i("INFO", "Date sent is : " + dateSent);
                Date today = new Date();
                int daysBetween = daysBetween(dateSent, today);
                int daysInTransit = bundle.getInt(Constants.LetterMetadata.DAYS_IN_TRANSIT);
                int daysLeft = daysInTransit - daysBetween;
                Log.i("INFO", "DaysBetween, daysInTransit: " + daysBetween + "," + daysInTransit);

                if(daysLeft > 0 )
                {
                    switch (daysLeft)
                    {
                        case 1 :
                            status = FunnyLetterStatus.JUST_ABOUT_TO_REACH.getValue();
                            break;

                        case 2 :
                            status = FunnyLetterStatus.FORWARDED_TO_OTHER_OFFICE.getValue();
                            break;
                        case 3 :
                            status = FunnyLetterStatus.TRAVELLING_IN_THE_AIR_SOMEWHERE.getValue();
                            break;
                        case 4 :
                            status = FunnyLetterStatus.RESTING_ON_THE_WAY.getValue();
                            break;
                        default:
                            status = FunnyLetterStatus.GOD_KNOWS_WHERE.getValue();
                    }
                    expectedToReachTextView.setText("Expected to reach in " + daysLeft + " days");
                }
                else
                {
                    status = FunnyLetterStatus.SHOULD_HAVE_REACHED_BY_NOW_BUT_IT_HASNT.getValue();
                }


            }

            else {

                //Setting date received
                dateReceivedTag.setVisibility(View.VISIBLE);
                if (bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED) != null) {
                    dateReceived.setText(formatter.format(bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED)));
                }
                else
                {
                    dateReceived.setText("UNKNOWN");
                }


                //setting status
                if(LetterStatus.SENT.toString().equals(status))
                {
                    status = FunnyLetterStatus.DELIVERED_BUT_NOT_OPENED.getValue();
                }
                else if(LetterStatus.OPENED.toString().equals(status))
                {
                    status = FunnyLetterStatus.READ.getValue();
                }
            }
            statusTextView.setText(status);
            statusTextView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            String status = ParseServerAccessor.getLetterStatus(bundle.getString(Constants.LetterMetadata.OBJECT_ID));

            return  status;
        }
    }
}
