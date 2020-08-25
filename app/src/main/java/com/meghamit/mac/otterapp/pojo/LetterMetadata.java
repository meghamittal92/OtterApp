package com.meghamit.mac.otterapp.pojo;

import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by mac on 13/04/20.
 */

@ParseClassName("LetterMetadata")
public class LetterMetadata extends ParseObject {

    public LetterMetadata() {
       super();
    }



    public String getTitle() {
        return getString(Constants.LetterMetadata.TITLE);
    }
    public Date getDateSent() {return getDate(Constants.LetterMetadata.DATE_SENT);}
    public Date getDateReceived() {return getDate(Constants.LetterMetadata.DATE_RECEIVED);}
    public String getStatus() {
        return getString(Constants.LetterMetadata.STATUS);
    }
    public int getFromPostBox() {
        return getInt(Constants.LetterMetadata.FROM_POSTBOX);
    }
    public int getToPostBox() {
        return getInt(Constants.LetterMetadata.TO_POSTBOX);
    }
    public int getDaysInTransit() { return getInt(Constants.LetterMetadata.DAYS_IN_TRANSIT);
    }

    public void setTitle(String title) { put(Constants.LetterMetadata.TITLE, title);
    }
    public void setDateSent(Date dateSent) { put(Constants.LetterMetadata.DATE_SENT, dateSent);}
    public void setDateReceived(Date dateReceived) { put(Constants.LetterMetadata.DATE_RECEIVED, dateReceived);}
    public void setStatus(LetterStatus status) {
        put(Constants.LetterMetadata.STATUS, status.toString());
    }
    public void setFromPostBox(int fromPostBox) {
        put(Constants.LetterMetadata.FROM_POSTBOX, fromPostBox);
    }
    public void setToPostBox(int toPostBox) {
        put(Constants.LetterMetadata.TO_POSTBOX, toPostBox);
    }
    public void setDaysInTransit(int daysInTransit) {
        put(Constants.LetterMetadata.DAYS_IN_TRANSIT, daysInTransit);
    }

//    public void setLetterData(ParseFile letterData)
//    {
//        put(Constants.LetterMetadata.LETTER_DATA, letterData);
//    }




}
