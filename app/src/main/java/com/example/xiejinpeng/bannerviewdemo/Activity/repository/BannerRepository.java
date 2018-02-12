package com.example.xiejinpeng.bannerviewdemo.Activity.repository;

import com.example.xiejinpeng.bannerviewdemo.Activity.model.BannerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiejinpeng on 2018/2/12.
 */

public class BannerRepository {


    public List<BannerModel> getBannerData() {
        List<BannerModel> data = new ArrayList<>();

        data.add(new BannerModel(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/300px-Google_2015_logo.svg.png",
                "www.google.com",
                "This is the Title",
                "This is the SubTitle"));

        data.add(new BannerModel(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/300px-Google_2015_logo.svg.png",
                "www.google.com",
                "This is the Title",
                "This is the SubTitle"));

        data.add(new BannerModel(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/300px-Google_2015_logo.svg.png",
                "www.google.com",
                "This is the Title",
                "This is the SubTitle"));

        return data;
    }
}
