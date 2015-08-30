package com.example.peter.connectd.models;

import android.graphics.drawable.Drawable;

public class SearchResult {
    private String mFullName;
    public SearchResult(String fullName, Drawable icon) {
        mFullName = fullName;
    }

    @Override
    public String toString() {
        return mFullName;
    }
}
