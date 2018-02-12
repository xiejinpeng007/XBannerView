package com.example.xiejinpeng.bannerviewdemo.Activity.model;

public class BannerModel {
    private String url;
    private String targetUrl;
    private String Title;
    private String subTitle;

    public BannerModel(String url, String targetUrl, String title, String subTitle) {
        this.url = url;
        this.targetUrl = targetUrl;
        Title = title;
        this.subTitle = subTitle;
    }

    public String getUrl() {
        return url;
    }


    public String getTargetUrl() {
        return targetUrl;
    }

    public String getTitle() {
        return Title;
    }

    public String getSubTitle() {
        return subTitle;
    }


}