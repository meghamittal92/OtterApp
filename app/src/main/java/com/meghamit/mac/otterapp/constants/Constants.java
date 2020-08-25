package com.meghamit.mac.otterapp.constants;

/**
 * Created by mac on 14/04/20.
 */

public final class Constants {

    public static final int RECEIVED_LETTER_ACTIVITY_REQUEST_CODE = 2;
    public static class LetterMetadata {
        public static final String TITLE = "Title";
        public static final String DATE_SENT = "DateSent";
        public static final String DATE_RECEIVED = "DateReceived";
        public static final String FROM_POSTBOX = "FromPostBox";
        public static final String TO_POSTBOX = "ToPostBox";
        public static final String STATUS = "Status";
        public static final String OBJECT_ID = "objectId";
        public static final String DAYS_IN_TRANSIT = "daysInTransit";


    }

    public static class Letter {
        public static final String LETTER_METADATA = "letterMetadata";
        public static final String LETTER_DATA = "letterData";
        public static final String LETTER_IMAGE = "letterImage";
        public static final String OBJECT_ID = "objectId";
    }

    public static class User {
        public static final String POST_BOX = "postBox";
        public static final String EMAIL = "email";
    }

    public static class IntentExtra {
        public static final String CURRENT_USER_POST_BOX = "currentUserPostBox";
        public static final String IS_LETTER_IMAGE_PRESENT = "isLetterImagePresent";
        public static final String REPLY_TO_POST_BOX = "replyToPostBox";
        public static final String RECEIVED_LETTERS_ADAPTER_NEEDS_REFRESH = "receivedLettersAdapterNeedsRefresh";


    }
    public static class Installation {
        public static final String USER_ID = "userId";
    }

}
