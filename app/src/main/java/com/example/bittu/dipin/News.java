package com.example.bittu.dipin;


public class News {
    private String mHeadline;
    private String mDate;
    private String mImgUrl;
    private String mDescription;
    private String mAuthor;
    private String mUrl;

    public News(){

    }

    public News(String headline,String date , String imgUrl,String description,String author,String url){
        mHeadline = headline;
        mDate = date;
        mImgUrl = imgUrl;
        mDescription = description;
        mAuthor = author;
        mUrl = url;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getDate() {
        return mDate;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setHeadline(String mHeadline) {
        this.mHeadline = mHeadline;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setImgUrl(String mImgUrl) {
        this.mImgUrl = mImgUrl;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
