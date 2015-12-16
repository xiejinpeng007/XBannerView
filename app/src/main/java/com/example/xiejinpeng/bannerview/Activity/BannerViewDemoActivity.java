package com.example.xiejinpeng.bannerview.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.xiejinpeng.bannerview.R;
import com.example.xiejinpeng.bannerview.View.BannerView;
import com.example.xiejinpeng.bannerview.model.BannerData;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BannerViewDemoActivity extends AppCompatActivity {

    @Bind(R.id.bv_demo)
    BannerView bannerView;
    @Bind(R.id.ll_index)
    LinearLayout indexView;
    List<BannerData> datas;
    BannerView.Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_view_demo);
        ButterKnife.bind(this);
        initData();
        initBannerView();
    }

    private void initData(){
        BannerData bannerData = new BannerData();
        bannerData.setImageUri("http://apriimagesbucket.s3.amazonaws.com/1447325912.png");
        bannerData.setLinkUri("http://www.yahoo.co.jp");
        BannerData bannerData2 = new BannerData();
        bannerData2.setImageUri("http://apriimagesbucket.s3.amazonaws.com/1447325929.png");
        bannerData2.setLinkUri("http://www.yahoo.co.jp");
        BannerData bannerData3 = new BannerData();
        bannerData3.setImageUri("http://apriimagesbucket.s3.amazonaws.com/1447325943.png");
        bannerData3.setLinkUri("http://www.yahoo.co.jp");
        BannerData bannerData4 = new BannerData();
        bannerData4.setImageUri("http://apriimagesbucket.s3.amazonaws.com/1447325964.png");
        bannerData4.setLinkUri("http://www.yahoo.co.jp");
        BannerData bannerData5 = new BannerData();
        bannerData5.setImageUri("http://apriimagesbucket.s3.amazonaws.com/1447325984.png");
        bannerData5.setLinkUri("http://www.yahoo.co.jp");


        datas =new ArrayList<>();
        datas.add(bannerData);
        datas.add(bannerData2);
        datas.add(bannerData3);
        datas.add(bannerData4);
        datas.add(bannerData5);

         listener = new BannerView.Listener() {
            @Override
            public String getImgUrl(int position) {
                return datas.get(position).getImageUri();
            }
            //不需要点击事件则无需设置
            @Override
            public String getOnClickUrl(int position) {
                return datas.get(position).getLinkUri();
            }
        };
    }

    private void initBannerView(){
        //唯一必须设置的方法
        //参数分别是广告数量;实例化的BannerView.Listenner
        bannerView.setData(datas.size(),7000,listener);

//        //选择设置的方法
//        //参数为放置index的LinearLayout;选中的image,未选中的image,每个index的左上右下margin值
        bannerView.setIndexImage(indexView,R.mipmap.page_control_on,R.mipmap.page_control_off,17,0,0,0);
//
//        //自动滚动的事件 ms
        bannerView.bannerAutoScroll(7000);
    }

}
