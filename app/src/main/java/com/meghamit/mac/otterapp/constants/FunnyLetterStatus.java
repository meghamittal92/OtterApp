package com.meghamit.mac.otterapp.constants;

public enum FunnyLetterStatus {


    SHOULD_HAVE_REACHED_BY_NOW_BUT_IT_HASNT("Should have reached by now, but it hasn't......Seems fishy."),
    JUST_ABOUT_TO_REACH ("Just about to reach"),
    FORWARDED_TO_OTHER_OFFICE("Forwarded to other post office"),
    TRAVELLING_IN_THE_AIR_SOMEWHERE ("Travelling in the air somewhere"),
    RESTING_ON_THE_WAY("Resting on the way"),
    GOD_KNOWS_WHERE("God knows where"),
    DELIVERED_BUT_NOT_OPENED("Delivered but not opened, still catching dust in a corner somewhere."),
    READ("Delivered and read, it is being mulled over right now.");


    String value;

    FunnyLetterStatus(String value) {
     this.value = value;
    }

    public String getValue() {
        return value;
    }
}


