package com.meghamit.mac.otterapp.accessor;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mac on 17/04/20.
 */

public class ParseServerAccessor  {

    private static final int max_letters_per_page = 10;

    /**
     * Only gets letters with the status "SENT"
     * @param parseUser
     * @return
     */
    public static List<com.meghamit.mac.otterapp.pojo.LetterMetadata> getReceivedLetters(final ParseUser parseUser, int pageNum) {
        List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.LetterMetadata> query = ParseQuery.getQuery(com.meghamit.mac.otterapp.pojo.LetterMetadata.class);
        Log.i("INFO", "To is: " + parseUser.get(Constants.User.POST_BOX));
        try {
            letterMetadata =   query.whereEqualTo(Constants.LetterMetadata.TO_POSTBOX, parseUser.get(Constants.User.POST_BOX))
                    .whereContainedIn(Constants.LetterMetadata.STATUS , Arrays.asList(LetterStatus.SENT.toString(), LetterStatus.OPENED.toString()))
                    .addDescendingOrder(Constants.LetterMetadata.DATE_RECEIVED)
                    .setLimit(max_letters_per_page)
                    .setSkip(pageNum * max_letters_per_page)
                    .find();

            Log.i("INFO", "Query result is " + letterMetadata.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return letterMetadata;
    }

    public static List<com.meghamit.mac.otterapp.pojo.LetterMetadata> getSentLetters(ParseUser parseUser, int pageNum) {

        List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.LetterMetadata> query = ParseQuery.getQuery(com.meghamit.mac.otterapp.pojo.LetterMetadata.class);
        Log.i("INFO", "From is: " + parseUser.get(Constants.User.POST_BOX));
        try {
            letterMetadata =   query.whereEqualTo(Constants.LetterMetadata.FROM_POSTBOX, parseUser.get(Constants.User.POST_BOX))
                    .addDescendingOrder(Constants.LetterMetadata.DATE_SENT)
                    .setLimit(max_letters_per_page)
                    .setSkip(pageNum * max_letters_per_page)
                    .find();
            Log.i("INFO", "Query result is " + letterMetadata.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return letterMetadata;

    }

    public static String getLetterStatus(String objectId) {

        List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.LetterMetadata> query = ParseQuery.getQuery(com.meghamit.mac.otterapp.pojo.LetterMetadata.class);
        query.whereEqualTo(Constants.LetterMetadata.OBJECT_ID, objectId);
        query.selectKeys(Arrays.asList(Constants.LetterMetadata.STATUS));

        try {
            letterMetadata =  query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!letterMetadata.isEmpty())
        {
            return letterMetadata.get(0).getStatus();
        }
        /* / TO DO : */
        return null;
    }

    public static Letter getLetterFromObjectId(String objectId) {

        List<com.meghamit.mac.otterapp.pojo.Letter> letters = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.Letter> query = ParseQuery.getQuery(com.meghamit.mac.otterapp.pojo.Letter.class);
        query.whereEqualTo(Constants.Letter.OBJECT_ID, objectId);

        try {
            letters =  query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!letters.isEmpty())
        {
            return letters.get(0);
        }
        /* / TO DO : */
        return null;
    }

    public static LetterMetadata getLetterMetadataFromObjectId(String objectId) {

        List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.LetterMetadata> query = ParseQuery.getQuery(LetterMetadata.class);
        query.whereEqualTo(Constants.LetterMetadata.OBJECT_ID, objectId);

        try {
            letterMetadata =  query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!letterMetadata.isEmpty())
        {
            return letterMetadata.get(0);
        }
        /* / TO DO : */
        return null;
    }
    public static ParseUser getParseUserFromPostBox(int postBoxNumber) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(Constants.User.POST_BOX, postBoxNumber);
        List<ParseUser> parseUsers = new ArrayList<>();

        try
        {
            parseUsers = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!parseUsers.isEmpty()) {
            return  parseUsers.get(0);
        }
         /* / TO DO : */
        return null;
    }

    public static ParseFile uploadFileToParse(String filename, byte[] bytes) throws ParseException {
        ParseFile parseFile = new ParseFile(filename , bytes);
            parseFile.save();
            Log.i("INFO!!", "After parseFile save");
            //po.save();

        return parseFile;
    }

    public static ParseFile uploadFileToParse(ContentResolver contentResolver, Uri fileUri, String filename) {

        Log.i("INFO", "File uri is: " + fileUri);
        ParseFile parseFile = null;
        try {
            BufferedInputStream in =  new BufferedInputStream(contentResolver.openInputStream(fileUri));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int read;
            byte[] buff = new byte[1024];

            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }

            out.flush();

            byte[] audioBytes = out.toByteArray();

            // Create the ParseFile
            parseFile = new ParseFile(filename, audioBytes);
            //po.put(columnName, parseFile);

            // Upload the file into Parse Cloud

            parseFile.save();
            Log.i("INFO!!", "After parseFile save");
            //po.save();

        } catch (Exception e) {
            Log.e("ERROR", "upload to parse failed", e);
        }
        return parseFile;
    }

    public static Letter getLetter(LetterMetadata letterMetadata) {

        ParseQuery<Letter> parseQuery = ParseQuery.getQuery(Letter.class);
        parseQuery.whereEqualTo(Constants.Letter.LETTER_METADATA, letterMetadata);

        List<com.meghamit.mac.otterapp.pojo.Letter> letters = new ArrayList<>();
        try {
            letters =  parseQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!letters.isEmpty())
        {
            return letters.get(0);
        }
        /* / TO DO : */
        return null;
    }

    public static Letter getLetter(String letterMetadataObjectId) {

        LetterMetadata letterMetadata  = getLetterMetadataFromObjectId(letterMetadataObjectId);

        if(letterMetadata != null) {
            ParseQuery<Letter> parseQuery = ParseQuery.getQuery(Letter.class);
            parseQuery.whereEqualTo(Constants.Letter.LETTER_METADATA, letterMetadata);

            List<com.meghamit.mac.otterapp.pojo.Letter> letters = new ArrayList<>();
            try {
                letters = parseQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!letters.isEmpty()) {
                return letters.get(0);
            }
        }
        /* / TO DO : */
        return null;

    }
    public static String readFile(ParseFile parseFile) {
        //Get the text file


//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            File file = parseFile.getFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return text.toString();
    }

    public static void cleanUpOnOtherDevices(ParseUser currentUser) throws ParseException {

        ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
        String currentInstallationId = currentInstallation.getInstallationId();

        Log.i("INFO", "ObjectId of current user is :" + currentUser.getObjectId());
        ParseQuery<ParseSession> parseSessionQuery = ParseSession.getQuery();
        //getting and deleting all sessions of user on other devices
        parseSessionQuery.whereNotEqualTo("installationId", currentInstallationId).whereEqualTo("user", currentUser);

        //Log.i("INFO", "Parse quesry is :" + parseSessionQuery.);

        List<ParseSession> parseSessions = parseSessionQuery.find();

        Log.i("INFO", "List of sessions size is:" + parseSessions.size());
        ParseSession.deleteAll(parseSessions);
//        for(ParseSession parseSession : parseSessions) {
//            Log.i("INFO", "Before deleting session: " + parseSession.getObjectId());
//
//        }

        // Remove user from Installation mapping to avoid sending push there.

//        ParseQuery<ParseInstallation> parseInstallationParseQuery = ParseInstallation.getQuery();
//        parseInstallationParseQuery.whereEqualTo(Constants.Installation.USER_ID, currentUser.getObjectId());
//        parseInstallationParseQuery.whereNotEqualTo("installationId", currentInstallationId);
//
//        List<ParseInstallation> parseInstallations = parseInstallationParseQuery.find();
//
//        for(ParseInstallation parseInstallation: parseInstallations) {
//            parseInstallation.put(Constants.Installation.USER_ID, null);
//            parseInstallation.save();
//
//        }

    }

    public static Boolean validateEmail(String registeredEmail) {


        Boolean isValidEmail = false;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(Constants.User.EMAIL, registeredEmail);
        List<ParseUser> parseUsers = new ArrayList<>();

        try
        {
            parseUsers = query.find();
            if( parseUsers.size() == 1) {
                isValidEmail = true;
            }
        } catch (ParseException e) {
            Log.e("ERROR", "Error querying user table for email validation");
            e.printStackTrace();
        }


        return isValidEmail;
    }
}
