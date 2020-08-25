package com.meghamit.mac.otterapp.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum FunnyLetterTitle {

    I_AM_LAZY ("I am lazy"),
    I_AM_TOO_LAZY_TO_PUT_A_TITLE("I am too lazy to put a title"),
    I_AM_LAZIER_THAN_FALIA("I am lazier than falia"),
    THE_UNIVERSE_IS_EXPANDING("The universe is expanding"),
    FORTY_TWO("Forty_two");

    String value;
    FunnyLetterTitle(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    private static final List<FunnyLetterTitle> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static FunnyLetterTitle randomTitle()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
