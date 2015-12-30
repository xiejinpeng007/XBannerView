package com.example.xiejinpeng.bannerview.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.xiejinpeng.bannerview.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiejinpeng on 15/11/13.
 */
public class BannerView extends ViewPager {

    private Listener listener;
    private List<ImageView> viewList;
    private List<ImageView> indexViewList;
    private int bannerListSize;
    private long autoScrollPeriod = 0;
    private Timer timer;
    private ImageLoader imageLoader;
    private Activity activity;
    private BannerViewAdapter bannerViewAdapter;

    public BannerView(Context context) {
        super(context);
        if (isInEditMode())
            return;
        this.activity = (Activity) context;

    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode())
            return;
        this.activity = (Activity) context;
    }


    private void setRequestData(int bannerListSize, Listener listener) {
        this.bannerListSize = bannerListSize;
        this.listener = listener;
        initUIL();
        initViewList();
        bannerViewAdapter = new BannerViewAdapter(getViewList());
        setAdapter(bannerViewAdapter);

    }

    /*初始化Universal ImageLoader配置*/
    private void initUIL() {

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));
        }
        imageLoader = ImageLoader.getInstance();
    }

    private void initViewList() {

        viewList = new ArrayList<>();

        for (int n = 0; n <= 4; n++) {
            for (int i = 0; i <= bannerListSize - 1; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                viewList.add(imageView);
                viewList.get(i + (n * bannerListSize)).setImageDrawable(viewList.get(i).getDrawable());
            }
        }

    }

    public List<ImageView> getViewList() {
        return viewList;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        setCurrentItem(viewList.size() * 100 + bannerListSize);
        for (int i = 0; i <= bannerListSize - 1; i++) {
            setBannerView(i);
        }
    }


    private void setBannerView(final int i) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        imageLoader.loadImage(listener.getImgUrl(i), options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                //设置图片和点击事件
                viewList.get(i).setImageBitmap(loadedImage);
                initDefaultBannerOnClickListener(viewList, i);
                for (int n = 1; n <= 4; n++) {
                    viewList.get(i + (n * bannerListSize)).setImageDrawable(viewList.get(i).getDrawable());
                    initDefaultBannerOnClickListener(viewList, i + n * bannerListSize);
                }
                //设置图片等比例填充满屏幕宽度
                DisplayMetrics displayMetrics = new DisplayMetrics();
                if (activity == null)
                    return;
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int displayWidth = displayMetrics.widthPixels;
                int displayWidth = getWidth();
                if (getParent().getClass().getName().equals("android.widget.RelativeLayout"))
                    setLayoutParams(new RelativeLayout.LayoutParams(getWidth(), loadedImage.getHeight() * displayWidth / loadedImage.getWidth()));
                else if (getParent().getClass().getName().equals("android.widget.LinearLayout"))
                    setLayoutParams(new LinearLayout.LayoutParams(getWidth(), loadedImage.getHeight() * displayWidth / loadedImage.getWidth()));
                bannerViewAdapter.notifyDataSetChanged();

                if (i == 0)
                    startAutoScroll();

            }
        });
    }

    private void initDefaultOnpageChangeListener() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indexViewList.size(); i++) {
                    indexViewList.get(i).setImageResource(R.mipmap.page_control_off);
                }
                indexViewList.get(position % bannerListSize).setImageResource(R.mipmap.page_control_on);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDefaultBannerOnClickListener(List<ImageView> viewList, final int index) {

        viewList.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listener.getOnClickUrl(index % bannerListSize).isEmpty()) {
                    Uri uri = Uri.parse(listener.getOnClickUrl(index % bannerListSize));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }
            }
        });
    }


    /*可选的参数设定*/

    private void setAutoScrollPeriod(long autoScrollPeriod) {
        this.autoScrollPeriod = autoScrollPeriod;
    }

    private void startAutoScroll() {
        final BannerHanlder bannerHanlder = new BannerHanlder();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bannerHanlder.sendEmptyMessage(0);
            }
        }, autoScrollPeriod, autoScrollPeriod);
    }

    class BannerHanlder extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                setCurrentItem(getCurrentItem() + 1);
            }
        }
    }

    /*添加index的相关数据*/
    /*放置Index的linearlayout,当前index图片，默认index图片,每个index的左上右下margin值*/

    private void setIndexData(LinearLayout llBannerindex, int Selected, int unSelected, int marginLeft, int marginTop, int marginRight, int marginBottom) {

        indexViewList = new ArrayList<>();
        for (int i = 0; i < bannerListSize; i++) {
            ImageView imageView = new ImageView(getContext());
            if (i == 0) {
                imageView.setImageResource(Selected);

            } else {
                imageView.setImageResource(unSelected);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(dpTppx(getContext(), marginLeft), dpTppx(getContext(), marginTop), dpTppx(getContext(), marginRight), dpTppx(getContext(), marginBottom));
            imageView.setLayoutParams(params);
            indexViewList.add(imageView);
            llBannerindex.addView(imageView);
        }
        initDefaultOnpageChangeListener();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timer.cancel();
    }
    /*根据手机的分辨率从 dp 的单位 转成为 px*/

    private static int dpTppx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /*建造器*/
    public static class Builder {
        private int bannerSize;
        private BannerView.Listener listener;
        private long autoScrollPeriod = 0;
        private BannerView bannerView;
        private boolean isSetIndexData = false;
        private LinearLayout llBannerindex;
        private int Selected, unSelected, marginLeft, marginTop, marginRight, marginBottom;

        public Builder(BannerView bannerView) {
            this.bannerView = bannerView;
        }

        public Builder setBannerSize(int bannerSize) {
            this.bannerSize = bannerSize;
            return this;
        }

        public Builder setListener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setAutoScrollPeriod(long autoScrollPeriod) {
            this.autoScrollPeriod = autoScrollPeriod;
            return this;
        }

        public Builder setIndexData(LinearLayout llBannerindex, int Selected, int unSelected, int marginLeft, int marginTop, int marginRight, int marginBottom) {
            isSetIndexData = true;
            this.llBannerindex = llBannerindex;
            this.Selected = Selected;
            this.unSelected = unSelected;
            this.marginLeft = marginLeft;
            this.marginTop = marginTop;
            this.marginBottom = marginBottom;
            return this;
        }

        public BannerView create() {
            bannerView.setRequestData(bannerSize, listener);
            bannerView.setAutoScrollPeriod(autoScrollPeriod);
            if (isSetIndexData)
                bannerView.setIndexData(llBannerindex, Selected, unSelected, marginLeft, marginTop, marginRight, marginBottom);
            return bannerView;
        }
    }

    /*根据list数据长度自动生成的默认Adapter*/
    public class BannerViewAdapter extends PagerAdapter {

        List<ImageView> viewList;


        public BannerViewAdapter(List<ImageView> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewList.get(position % (viewList.size()));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = viewList.get(position % (viewList.size()));
            container.removeView(view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


    public interface Listener {
        String getImgUrl(int position);

        String getOnClickUrl(int position);
    }

}