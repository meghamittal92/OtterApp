package com.meghamit.mac.otterapp.pojo;

import com.meghamit.mac.otterapp.constants.Constants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by mac on 22/04/20.
 */

@ParseClassName("Letter")
public class Letter extends ParseObject {

    public Letter() {
        super();
    }

//    public LetterMetadata getLetterMetadata() {
//        return (LetterMetadata)get(Constants.Letter.LETTER_METADATA);
//    }

    public ParseFile getLetterData() {
        return getParseFile(Constants.Letter.LETTER_DATA);
    }

    public void setLetterData(ParseFile parseFile) {
        put(Constants.Letter.LETTER_DATA, parseFile);
    }

    public ParseFile getLetterImage() {
        return getParseFile(Constants.Letter.LETTER_IMAGE);
    }

    public void setLetterImage(ParseFile parseFile) {
        put(Constants.Letter.LETTER_IMAGE, parseFile);
    }

}
