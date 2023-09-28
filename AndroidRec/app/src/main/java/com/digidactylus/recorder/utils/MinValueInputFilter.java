package com.digidactylus.recorder.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class MinValueInputFilter implements InputFilter {
    private int minValue;

    public MinValueInputFilter(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public CharSequence filter(
            CharSequence source, int start, int end,
            Spanned dest, int dstart, int dend
    ) {
        try {
            String newVal = dest.toString().substring(0, dstart) +
                    source.toString().substring(start, end) +
                    dest.toString().substring(dend);

            int input = Integer.parseInt(newVal);

            if (isInRange(minValue, Integer.MAX_VALUE, input)) {
                return null; // Accept the input
            }
        } catch (NumberFormatException nfe) {
            // Ignore invalid input
        }
        return ""; // Reject the input
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
