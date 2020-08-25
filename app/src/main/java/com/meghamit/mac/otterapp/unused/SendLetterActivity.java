package com.meghamit.mac.otterapp.unused;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.meghamit.mac.otterapp.postOffice.SendLetterSuccssActivity;
import com.meghamit.mac.otterapp.R;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Deprecated
public class SendLetterActivity extends AbstractBaseActivity {

    private static final int FILE_REQUEST_CODE = 1;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 2;

    @BindView(R.id.uploadFileButton)
    Button uploadFileButton;

    @BindView(R.id.toPostBoxEditText)
    EditText toPostBoxEditText;

    @BindView(R.id.letterTitleEditText)
    EditText letterTitleEditText;

    @BindView(R.id.fileChosenTextView)
    TextView fileChosenTextView;

    @BindView(R.id.fileUploadStatusTextView)
    TextView fileUploadStatusTextView;

    @BindView(R.id.sendLetterButton)
    Button sendLetterButton;

    @BindView(R.id.uploadFileprogressBar)
    ProgressBar progressBar;

    @BindView(R.id.sendLetterErrorTextView)
    TextView sendLetterErrorTextView;

    ParseUser receivingUser;
    String postBoxNumberString;
    Integer postBoxNumber;
    String letterTitle;

    Subscription parseFileUploadSubscription;

    ParseFile uploadedParseFile;
    ProgressDialog sendLetterProgressDialog;
   // Boolean isPostBoxNumberValid = true;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        sendLetterProgressDialog = new ProgressDialog(this, R.style.AppTheme_PopupOverlay);
        //uploadFileButton.setEnabled(false);

        toPostBoxEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && ((postBoxNumberString == null) || !postBoxNumberString.equals(toPostBoxEditText.getText().toString()))) {
                    postBoxNumberString = toPostBoxEditText.getText().toString();
                    validatePostBoxNumber(postBoxNumberString);
                }
            }
        });


        letterTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                Log.i("INFO", "in letter title on focus change");
                if (!hasFocus && ((letterTitle == null) || !letterTitle.equals(letterTitleEditText.getText().toString()))) {
                    String title = letterTitleEditText.getText().toString();
                    letterTitle = letterTitleEditText.getText().toString();
                    if (!isLetterTitleValid(title)) {
                        letterTitleEditText.setError("between 4 and 50 alphanumeric characters");
                    } else {
                        letterTitleEditText.setError(null);
                    }
                }
            }
        });



    }

    private boolean isLetterTitleValid(String title) {
        //TODO : SQL Injection checks
        Log.i("INFO", "INSIDE ISLETTERTITLEVALID, title is :" + title );
        if ((!title.isEmpty()) &&( title.length() < 4 || title.length() > 50)) {
            Log.i("INFO", "INSIDE ISLETTERTITLEVALID, returning false" );
            return false;
        }
        return true;
    }


    private void validatePostBoxNumber(String postBoxNumberString) {
        if (!postBoxNumberString.isEmpty()) {
            Observable<ParseUser> observable = getParseUserFromPostBoxStringObservable(postBoxNumberString);
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ParseUser>() {
                @Override
                public void onCompleted() {
                    toPostBoxEditText.setError(null);
                }

                @Override
                public void onError(Throwable e) {
                    toPostBoxEditText.setError("Invalid postBox Number");

                }

                @Override
                public void onNext(ParseUser parseUser) {
                    receivingUser = parseUser;

                }
            });
        }
        else
        {
            toPostBoxEditText.setError(null);
        }
    }
    private Observable<ParseUser> getParseUserFromPostBoxStringObservable(String postBoxNumberString) {


               Observable<ParseUser> observable =  Observable.create(new Observable.OnSubscribe<ParseUser>() {
                    @Override
                    public void call(Subscriber<? super ParseUser> subscriber) {

                            postBoxNumber = Integer.parseInt(postBoxNumberString);
                            ParseUser parseUser = ParseServerAccessor.getParseUserFromPostBox(postBoxNumber);

                            if (parseUser == null) {
                                subscriber.onError(new Throwable("user not found"));

                            } else {
                                subscriber.onNext(parseUser);
                                subscriber.onCompleted();
                            }
                    }
                });
    return observable;

    }


    public void sendLetter(View view) {

        clearSendLetterErrors();

        //resetGlobalVars();
       // sendLetterButton.requestFocus();
       // toPostBoxEditText.clearFocus();
        //letterTitleEditText.clearFocus();
        boolean isValid = basicInputValidations();

        if(isValid) {
            sendLetterProgressDialog.show();
            sendLetterAsync();

            // SendLetterTask sendLetterTask = new SendLetterTask(this);


        }
        else
        {
            focusErredTextbox();
        }
    }

    private void clearSendLetterErrors() {
        sendLetterErrorTextView.setText("");
    }

    private void focusErredTextbox() {

        if(toPostBoxEditText.getError() != null)
        {
            toPostBoxEditText.requestFocus();
        }
        else
        {
            letterTitleEditText.requestFocus();
        }
    }

    private void resetGlobalVars(){
        postBoxNumberString = "";
        letterTitle = "";
        receivingUser = null;
        postBoxNumber = null;
    }

    private void sendLetterAsync() {
//        getParseUserFromPostBoxStringObservable(postBoxNumberString).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ParseUser>() {
//
//
//            @Override
//            public void onCompleted() {
//                //sendLetterProgressDialog.dismiss();
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                sendLetterProgressDialog.dismiss();
//                toPostBoxEditText.setError("Invalid postBox Number");
//                focusErredTextbox();
//
//            }
//
//            @Override
//            public void onNext(ParseUser parseUser) {
//                receivingUser = parseUser;
//            }
//        });
        //sendLetterProgressDialog.show();
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                postBoxNumber = Integer.parseInt(postBoxNumberString);
                ParseUser parseUser = ParseServerAccessor.getParseUserFromPostBox(postBoxNumber);

                if (parseUser == null) {
                    subscriber.onError(new Exception("Invalid postBoxNumber"));
                } else {

                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    //Letter le
                    LetterMetadata letterMetadata = new LetterMetadata();
                    letterMetadata.setDateSent(new Date());
                    letterMetadata.setTitle(letterTitle);
                    letterMetadata.setFromPostBox((int) currentUser.get(Constants.User.POST_BOX));
                    letterMetadata.setToPostBox(postBoxNumber);
                    letterMetadata.setStatus(LetterStatus.QUEUED_TO_SEND);

                    Letter letter = new Letter();
                    letter.setLetterData(uploadedParseFile);

                    try {
                        letterMetadata.save();
                        ParseRelation<LetterMetadata> letterMetadataParseRelation = letter.getRelation(Constants.Letter.LETTER_METADATA);
                        letterMetadataParseRelation.add(letterMetadata);
                        //letter.setLetterMetadata(letterMetadata.getObjectId());
                        letter.save();

                    } catch (ParseException e) {
                        //e.printStackTrace();
                        subscriber.onError(e);
                    }

                    subscriber.onCompleted();
                    // sendLetterProgressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(), "Suuceessfully sent!", Toast.LENGTH_LONG);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                //sendLetterProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "SUCCESS!!", Toast.LENGTH_LONG).show();

                Log.i("INFO", "Inside onCompleted");
               // new SendLetterSuccessTask(getApplicationContext()).execute(sendLetterProgressDialog);


            }

            @Override
            public void onError(Throwable e) {

                sendLetterProgressDialog.dismiss();
                if(e.getMessage().equals("Invalid postBoxNumber"))
                {
                    toPostBoxEditText.setError("Invalid postBox Number");
                focusErredTextbox();
                }
                else {
                    //Toast.makeText(getApplicationContext(), "FAILED!!", Toast.LENGTH_LONG).show();
                    sendLetterErrorTextView.setText("Failed to Send! Please try again later");
                }
            }

            @Override
            public void onNext(Void aVoid) {

            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_send_letter;
    }

    public void uploadLetter(View view) {

        sendLetterButton.setVisibility(View.INVISIBLE);
        fileUploadStatusTextView.setText("");

            if (!checkPermissionForReadExtertalStorage()) {
                try {
                    requestPermissionForReadExtertalStorage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            // chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    FILE_REQUEST_CODE
            );

    }


    private Boolean basicInputValidations()
    {

        boolean isValid = true;
        letterTitle = letterTitleEditText.getText().toString();
        postBoxNumberString = toPostBoxEditText.getText().toString();
        if (letterTitle.isEmpty()) {
            letterTitleEditText.setError("Please enter a letter title");
            isValid = false;
        } else if (letterTitleEditText.getError() != null) {
            isValid = false;
        } else if (!isLetterTitleValid(letterTitle)) {
            letterTitleEditText.setError("between 4 and 50 alphanumeric characters");
            isValid = false;
        }

        if (postBoxNumberString.isEmpty()) {
            toPostBoxEditText.setError("Please enter a PostBox number");
            isValid = false;
        } else if (toPostBoxEditText.getError() != null) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri fileURI = data.getData();
            Log.i("INFO!!", "File URI is: "+ fileURI);
            //File file = new File(fileURI);
            String filePath = new FileUtils(this).getPath(fileURI);
            Log.i("INFO!!!", "Path of file is " + filePath);


            fileChosenTextView.setText("File Chosen :" + filePath);



            parseFileUploadSubscription  = uploadParseFileAsync(filePath).subscribeOn(Schedulers.newThread())
                    .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                    .doOnTerminate(() -> progressBar.setVisibility(View.INVISIBLE))
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ParseFile>() {
                        @Override
                        public void onCompleted() {
                            fileUploadStatusTextView.setText("File Uploaded successfully");
                            sendLetterButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            fileUploadStatusTextView.setText("File Upload failed. Please choose another file");
                        }

                        @Override
                        public void onNext(ParseFile parseFile) {
                            uploadedParseFile = parseFile;
                        }
                    });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if((parseFileUploadSubscription != null )&& (!parseFileUploadSubscription.isUnsubscribed())) {
            parseFileUploadSubscription.unsubscribe();
        }
    }

    public Observable<ParseFile> uploadParseFileAsync(final String filePath) {
        // Insert network call here!
        return Observable.create(new Observable.OnSubscribe<ParseFile>() {
            @Override
            public void call(Subscriber<? super ParseFile> subscriber) {

                //ParseFile parseFile = ParseServerAccessor.uploadFileToParse(getContentResolver(),new Uri(filePath));
                ParseFile parseFile = null;
                subscriber.onNext(parseFile);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });

    }
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public class SendLetterSuccessTask extends AsyncTask<ProgressDialog, Void, ProgressDialog> {
        private Context context;
        private ProgressDialog progressDialog;

        public SendLetterSuccessTask(Context context) {
            this.context = context;
        }



        @Override
        protected ProgressDialog doInBackground(ProgressDialog... progressDialogs) {
            Log.i("INFO", "Inside do in background");
            startActivity(new Intent(getApplicationContext(), SendLetterSuccssActivity.class));
            Log.i("INFO", "Inside do in background : After start activity");
            return progressDialogs[0];
        }

        @Override
        protected void onPreExecute() {
           // progressDialog = new ProgressDialog(context);
            //progressDialog.show();
            //sendLetterProgressDialog.show();
        }

        @Override
        protected void onPostExecute(ProgressDialog progressDialog) {

            progressDialog.dismiss();
        }
    }

//    public String getPath(Uri uri) {
//
//        String path = null;
//        String[] projection = { MediaStore.Files.FileColumns.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//
//        if(cursor == null){
//            path = uri.getPath();
//        }
//        else{
//            cursor.moveToFirst();
//            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
//            path = cursor.getString(column_index);
//            cursor.close();
//        }
//
//        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
//    }


//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//        Log.i("INFO", "IN TEXT CHANGED");
//
//        if((!letterTitleEditText.getText().toString().isEmpty()) && (letterTitleEditText.getError() == null) && (toPostBoxEditText.getError() == null) && ( !toPostBoxEditText.getText().toString().isEmpty()))
//        {
//            Log.i("INFO", "SETTING TO TRUE!");
//            uploadFileButton.setEnabled(true);
//        }
//        else
//        {
//            uploadFileButton.setEnabled(false);
//        }
//    }

//    class SendLetterTask extends AsyncTask<String, String, String> {
//
//        private ProgressDialog progressDialog;
//        private SendLetterActivity sendLetterActivity;
//
//        public SendLetterTask(SendLetterActivity sendLetterActivity) {
//            this.sendLetterActivity = sendLetterActivity;
//            progressDialog = new ProgressDialog(sendLetterActivity, R.style.AppTheme_Dark_Dialog);
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog.setMessage("Sending...");
//            progressDialog.show();
//
//        }
//
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//            if(result != null && result.equalsIgnoreCase("Success")) {
//                sendLetterActivity.onSendLetterSuccess();
//            }
//            else
//            {
//                sendLetterActivity.onSendLetterFailed(result);
//            }
//        }
//    }
}