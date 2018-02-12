package com.example.xiejinpeng.bannerviewdemo.Activity.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.xiejinpeng.bannerview.BannerView;
import com.example.xiejinpeng.bannerviewdemo.Activity.model.BannerModel;
import com.example.xiejinpeng.bannerviewdemo.Activity.repository.BannerRepository;
import com.example.xiejinpeng.bannerviewdemo.R;
import com.example.xiejinpeng.bannerviewdemo.databinding.ActivityBannerViewDemoBinding;

import java.util.List;

public class BannerViewDemoActivity extends AppCompatActivity {

    private ActivityBannerViewDemoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_banner_view_demo);

        initBannerView();
    }


    private void initBannerView() {
        List<BannerModel> data = new BannerRepository().getBannerData();

        binding.banner.setAdapter(
                new BannerView.AdapterBuilder<BannerModel>()
                        .setData(data)
                        .setGetImageUrl(BannerModel::getUrl)
                        .setOnClick(m -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getTargetUrl()));
                            if (intent.resolveActivity(getPackageManager()) != null)
                                startActivity(intent);
                        })
                        .setGetTitle(BannerModel::getTitle)
                        .setGetSubTitle(BannerModel::getSubTitle)
                        .setAutoScrollMills(5000L)
                        .setShowIndicator(true)
                        .build());
    }


}
