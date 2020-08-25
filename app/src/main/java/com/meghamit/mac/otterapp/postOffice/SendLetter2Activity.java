package com.meghamit.mac.otterapp.postOffice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.FunnyLetterTitle;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.meghamit.mac.otterapp.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SendLetter2Activity extends AbstractBaseActivity {

    private static final int FILE_REQUEST_CODE = 1;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 4;
    private static final String DEFAULT_FILE_NAME = "DefaultFileName";


    @BindView(R.id.toPostBoxEditText)
    EditText toPostBoxEditText;

    @BindView(R.id.letterTitleEditText)
    EditText letterTitleEditText;


    @BindView(R.id.sendLetterErrorTextView)
    TextView sendLetterErrorTextView;

    @BindView(R.id.letterContentEditText)
            TextView letterContentEditText;
    @BindView(R.id.fileUploadStatusTextView)
            TextView fileUploadStatusTextView;

    @BindView(R.id.uploadFileprogressBar)
    ProgressBar progressBar;

//    @BindView(R.id.letterContentErrorTextView)
//    TextView letterContentErrorTextView;

    @BindView(R.id.thumbnailImageView)
    ImageView thumbnailImageView;

    ParseUser receivingUser;
    String postBoxNumberString;
    Integer postBoxNumber;
    String letterTitle;

    Subscription parseFileUploadSubscription;

    ParseFile uploadedImageFile;

    private static final int THUMBNAIL_SIZE = 128;
    //ProgressDialog sendLetterProgressDialog;

    AlertDialog alertDialog;
    Pattern p = Pattern.compile("[^A-Za-z0-9 \\?\\!\\']");
    @BindView(R.id.attachImageFab)
    FloatingActionButton attachImageFab;
    private File capturedFile;
    private Uri capturedFileUri;
    // Boolean isPostBoxNumberValid = true;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        //fab.shrink();
        //sendLetterProgressDialog = new ProgressDialog(this, R.style.AppTheme_PopupOverlay);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();
        int replyToPostBox = intent.getIntExtra(Constants.IntentExtra.REPLY_TO_POST_BOX, -1);


        toPostBoxEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && ((postBoxNumberString == null) || !postBoxNumberString.equals(toPostBoxEditText.getText().toString()))) {
                    postBoxNumberString = toPostBoxEditText.getText().toString().trim();
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

        if(replyToPostBox != -1)
        {
           toPostBoxEditText.setText(String.valueOf(replyToPostBox));
        }

    }

    private boolean isLetterTitleValid(String title) {
        //TODO : SQL Injection checks
        Matcher m = p.matcher(title);
        // boolean b = m.matches();
        boolean specialChars = m.find();
        //Log.i("INFO", "New code running yo");

        if ((!title.isEmpty()) &&( title.length() < 4 || title.length() > 50 || specialChars)) {
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

       // clearSendLetterErrors();
        boolean isValid = basicInputValidations();

        if(isValid) {
            alertDialog.show();
            sendLetterAsync();

        }
        else
        {
            focusErredTextbox();
        }
    }


    private void focusErredTextbox() {

        if(toPostBoxEditText.getError() != null)
        {
            toPostBoxEditText.requestFocus();
        }
        else if (letterTitleEditText.getError() != null)
        {
            letterTitleEditText.requestFocus();
        }
        else
        {
            letterContentEditText.requestFocus();
        }
//        else if(letterContentErrorTextView.getVisibility() == View.VISIBLE)
//        {
//            Log.i("INFO", "letterContentErrorTextView is visible, should become focussed now");
//            letterContentErrorTextView.requestFocus();
//        }
    }

    private void sendLetterAsync() {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                postBoxNumber = Integer.parseInt(postBoxNumberString);
                int daysInTransit = ThreadLocalRandom.current().nextInt(2, 5 + 1);
                ParseUser parseUser = ParseServerAccessor.getParseUserFromPostBox(postBoxNumber);

                if (parseUser == null) {
                    subscriber.onError(new Exception("Invalid postBoxNumber"));
                } else {
                    receivingUser = parseUser;
                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    ParseFile parseFile = null;
                    //Letter le
                    LetterMetadata letterMetadata = new LetterMetadata();
                    letterMetadata.setDateSent(new Date());
                    letterMetadata.setTitle(letterTitle);
                    letterMetadata.setFromPostBox((int) currentUser.get(Constants.User.POST_BOX));
                    letterMetadata.setToPostBox(postBoxNumber);
                    letterMetadata.setStatus(LetterStatus.QUEUED_TO_SEND);
                    letterMetadata.setDaysInTransit(daysInTransit);

                    Letter letter = new Letter();
                    try {
                        String content = " ";
                        if(!letterContentEditText.getText().toString().isEmpty())
                        {
                            content = letterContentEditText.getText().toString();
                        }

                        parseFile = ParseServerAccessor.uploadFileToParse(letterTitle + ".txt", content.getBytes());
                    }
                    catch (ParseException parseException)
                    {
                        subscriber.onError(parseException);
                    }
                    if(parseFile != null) {
                        letter.setLetterData(parseFile);
                        if(uploadedImageFile != null)
                        {
                            letter.setLetterImage(uploadedImageFile);
                        }

                        try {
                            letterMetadata.save();
                        } catch (ParseException e) {
                            //e.printStackTrace();
                            subscriber.onError(e);
                        }

                        try {
                            ParseRelation<LetterMetadata> letterMetadataParseRelation = letter.getRelation(Constants.Letter.LETTER_METADATA);
                            letterMetadataParseRelation.add(letterMetadata);
                            //letter.setLetterMetadata(letterMetadata.getObjectId());
                            letter.save();

                        } catch (ParseException e) {
                            //e.printStackTrace();

                            try {
                                Log.i("INFO", "TRying to delete letter metadata");
                                letterMetadata.delete();
                                Log.i("INFO", "After delete");
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                                Log.e("ERROR", "Rollback failed");
                            } finally {
                                subscriber.onError(e);
                            }

                        }

                    }
                    subscriber.onCompleted();
                }
                // sendLetterProgressDialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Suuceessfully sent!", Toast.LENGTH_LONG);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                //sendLetterProgressDialog.dismiss();
               //  Toast.makeText(getApplicationContext(), "SUCCESS!!", Toast.LENGTH_LONG).show();

                Log.i("INFO", "Inside onCompleted");
                new SendLetterSuccessTask(getApplicationContext()).execute();
            }

            @Override
            public void onError(Throwable e) {
                alertDialog.dismiss();
                if(e.getMessage().equals("Invalid postBoxNumber"))
                {
                    toPostBoxEditText.setError("Invalid postBox Number");
                    focusErredTextbox();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed to Send! Please try again later!!", Toast.LENGTH_LONG).show();
                    //sendLetterErrorTextView.setText("Failed to Send! Please try again later");
                }
            }

            @Override
            public void onNext(Void aVoid) {

            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_send_letter2;
    }



    private Boolean basicInputValidations()
    {

        boolean isValid = true;
        letterTitle = letterTitleEditText.getText().toString();
        postBoxNumberString = toPostBoxEditText.getText().toString().trim();
        String letterContent = letterContentEditText.getText().toString();
        if (letterTitle.isEmpty()) {
//            letterTitleEditText.setError("Please enter a letter title");
            //Setting ramdom letter title when it is empty.
            letterTitle = FunnyLetterTitle.randomTitle().getValue();
            isValid = true;
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

        if(uploadedImageFile == null && letterContent.isEmpty())
        {
            letterContentEditText.setError("Write or Attach something you lazy!");
           // letterContentErrorTextView.setText("Write or Attach something you lazy!");
            //letterContentErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else
        {
            //letterContentErrorTextView.setVisibility(View.INVISIBLE);
            letterContentEditText.setError(null);
        }

        return isValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void attachImage(View view) {
      selectImage(view);
    }


    public void selectImage(View view) {

        fileUploadStatusTextView.setText("");
        thumbnailImageView.setVisibility(View.INVISIBLE);
        if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        // chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("image/*");
        try {
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    FILE_REQUEST_CODE
            );
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please Install a File Manager",Toast.LENGTH_SHORT).show();
        }

    }

    public void takePhoto(View view) {

        fileUploadStatusTextView.setText("");
        thumbnailImageView.setVisibility(View.INVISIBLE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            try {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }catch (Exception e) {
                Log.e("ERROR", "Error requesting camera permissions");
                e.printStackTrace();
                throw e;
            }
        }

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        capturedFile = new File(this.getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        capturedFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", capturedFile);
        //capturedFileUri = Uri.fromFile(capturedFile);
        takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedFileUri);
        startActivityForResult(takePhotoIntent,CAPTURE_IMAGE_REQUEST_CODE );
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri fileURI = data.getData();

            uploadFileToParseUsingUri(fileURI);

        }

        else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            uploadFileToParseUsingUri(capturedFileUri);
        }
    }

    private void uploadFileToParseUsingUri(Uri fileURI) {
        Log.i("INFO!!", "File URI is: "+ fileURI);


        try {
           Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileURI);
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(imageBitmap,
                  THUMBNAIL_SIZE, THUMBNAIL_SIZE);


        parseFileUploadSubscription  = uploadParseFileAsync(fileURI).subscribeOn(Schedulers.newThread())
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnTerminate(() -> progressBar.setVisibility(View.INVISIBLE))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ParseFile>() {
                    @Override
                    public void onCompleted() {
                        thumbnailImageView.setImageBitmap(thumbImage);
                        thumbnailImageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        fileUploadStatusTextView.setText("File Upload failed.");
                        thumbnailImageView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(ParseFile parseFile) {
                        uploadedImageFile = parseFile;
                    }
                });
        } catch (IOException e) {
            Log.e("ERROR", "File not found for upload", e);
            fileUploadStatusTextView.setText("File Upload failed.");
            thumbnailImageView.setVisibility(View.INVISIBLE);
        }
    }

    public Observable<ParseFile> uploadParseFileAsync(final Uri fileUri) {
        // Insert network call here!
        return Observable.create(new Observable.OnSubscribe<ParseFile>() {
            @Override
            public void call(Subscriber<? super ParseFile> subscriber) {

                ContentResolver contentResolver= getContentResolver();
                String fileName = getFileNameFromUri(contentResolver, fileUri);
                ParseFile parseFile = ParseServerAccessor.uploadFileToParse(contentResolver, fileUri,fileName );
                if(parseFile == null)
                {
                    subscriber.onError(new Exception("File upload failed"));
                }
                subscriber.onNext(parseFile);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });

    }

    public class SendLetterSuccessTask extends AsyncTask<Void, Void, Void> {
        private Context context;

        public SendLetterSuccessTask(Context context) {
            this.context = context;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("INFO", "Inside do in background");
            startActivity(new Intent(getApplicationContext(), SendLetterSuccssActivity.class));
            Log.i("INFO", "Inside do in background : After start activity");
            return null;
        }

        @Override
        protected void onPreExecute() {
            // progressDialog = new ProgressDialog(context);
            //progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            alertDialog.dismiss();
        }
    }
    private String getFileNameFromUri(ContentResolver resolver, Uri uri) {
        String name = DEFAULT_FILE_NAME;
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        if( returnCursor != null) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            if(nameIndex >= 0) {
                name = returnCursor.getString(nameIndex);
            }
            returnCursor.close();
        }
        return name;
    }
}