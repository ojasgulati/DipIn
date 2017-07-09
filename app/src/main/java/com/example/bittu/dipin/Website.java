package com.example.bittu.dipin;

import android.graphics.drawable.Drawable;

public class Website {
    private String mPlatformTitle;
    private Drawable mPlatformImage;

    public Website(String platformTitle, Drawable platformImage){
        mPlatformTitle = platformTitle;
        mPlatformImage = platformImage;
    }

    public String getPlatformTitle() {
        return mPlatformTitle;
    }

    public Drawable getPlatformImage() {
        return mPlatformImage;
    }

    public void setPlatformTitle(String platformTitle) {
        this.mPlatformTitle = platformTitle;
    }

    public void setPlatformImage(Drawable platformImage) {
        this.mPlatformImage = platformImage;
    }


}
