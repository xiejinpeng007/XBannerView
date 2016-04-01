package com.example.xiejinpeng.bannerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


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
    private boolean isLoop = true;

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


    private void initBannerView(int bannerListSize, Listener listener) {
        this.bannerListSize = bannerListSize;
        this.listener = listener;
        initUIL();
        initViewList();
        initAdapter();

    }


    //初始化Universal ImageLoader配置

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

    private void initAdapter() {
        bannerViewAdapter = new BannerViewAdapter(getViewList(), isLoop);
        setAdapter(bannerViewAdapter);
    }

    public List<ImageView> getViewList() {
        return viewList;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (isLoop)
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
                setBannerOnClickListener(viewList, i);
                for (int n = 1; n <= 4; n++) {
                    viewList.get(i + (n * bannerListSize)).setImageDrawable(viewList.get(i).getDrawable());
                    setBannerOnClickListener(viewList, i + n * bannerListSize);
                }
                //设置图片等比例填充满屏幕宽度
                DisplayMetrics displayMetrics = new DisplayMetrics();
                if (activity == null)
                    return;
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                //int displayWidth = displayMetrics.widthPixels;
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

    private void setOnpageChangeListener(final int selectedRes, final int unSelectedRes) {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indexViewList.size(); i++) {
                    indexViewList.get(i).setImageResource(unSelectedRes);
                }
                indexViewList.get(position % bannerListSize).setImageResource(selectedRes);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setBannerOnClickListener(List<ImageView> viewList, final int index) {

        viewList.get(index).setOnClickListener(new OnClickListener() {
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

    private void setIsLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

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


    private void setIndexData(LinearLayout bannerIndexLinearLayout, int SelectedRes, int unSelectedRes, int marginStart, int marginTop, int marginEnd, int marginBottom) {

        indexViewList = new ArrayList<>();
        for (int i = 0; i < bannerListSize; i++) {
            ImageView imageView = new ImageView(getContext());
            if (i == 0) {
                imageView.setImageResource(SelectedRes);

            } else {
                imageView.setImageResource(unSelectedRes);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(dpTppx(getContext(), marginStart), dpTppx(getContext(), marginTop), dpTppx(getContext(), marginEnd), dpTppx(getContext(), marginBottom));
            imageView.setLayoutParams(params);
            indexViewList.add(imageView);
            bannerIndexLinearLayout.addView(imageView);
        }
        setOnpageChangeListener(SelectedRes, unSelectedRes);
    }

    //取消用于控制轮播的timer

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timer.cancel();
    }

    //根据手机的分辨率从 dp 的单位 转成为 px

    private static int dpTppx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     *  Buider
     *  所有参数均由Buider进行设置
     */


    public static class Builder {
        private int bannerSize;
        private BannerView.Listener listener;
        private long autoScrollPeriod = 0;
        private BannerView bannerView;
        private boolean isSetIndexData = false, isLoop = true;
        private LinearLayout llBannerindex;
        private int Selected, unSelected, marginStart, marginTop, marginEnd, marginBottom;

        /**
         * 构造方法参数
         *
         * @param bannerView bannerView的实例
         * @param bannerSize bannerView显示图片的个数
         * @param listener   用于获取banner图片url和jumpUrl的接口
         */

        public Builder(BannerView bannerView, int bannerSize, BannerView.Listener listener) {
            this.bannerView = bannerView;
            this.bannerSize = bannerSize;
            this.listener = listener;
        }

        /**
         * @param autoScrollPeriod 自动轮播的时间间隔 单位：ms
         */

        public Builder setAutoScrollPeriod(long autoScrollPeriod) {
            this.autoScrollPeriod = autoScrollPeriod;
            return this;
        }


        /**
         * 添加index的相关数据
         *
         * @param bannerIndexLinearLayout 放置Index的linearlayout
         * @param SelectedRes             选中的index图片
         * @param unSelectedRes           未选中的index图片
         * @param marginStart             每个index图片的margin值
         */

        public Builder setIndexData(LinearLayout bannerIndexLinearLayout, int SelectedRes, int unSelectedRes, int marginStart, int marginTop, int marginEnd, int marginBottom) {
            isSetIndexData = true;
            this.llBannerindex = bannerIndexLinearLayout;
            this.Selected = SelectedRes;
            this.unSelected = unSelectedRes;
            this.marginStart = marginStart;
            this.marginTop = marginTop;
            this.marginEnd = marginEnd;
            this.marginBottom = marginBottom;
            return this;
        }

        /**
         * @param isLoop 设置是否循环
         */

        public Builder isLoop(boolean isLoop) {
            this.isLoop = isLoop;
            return this;
        }

        public BannerView create() {
            bannerView.setAutoScrollPeriod(autoScrollPeriod);
            bannerView.setIsLoop(isLoop);
            bannerView.initBannerView(bannerSize, listener);
            if (isSetIndexData)
                bannerView.setIndexData(llBannerindex, Selected, unSelected, marginStart, marginTop, marginEnd, marginBottom);

            return bannerView;
        }
    }

    //根据list数据长度自动生成的默认Adapter

    public class BannerViewAdapter extends PagerAdapter {

        List<ImageView> viewList;
        boolean isLoop = true;

        public BannerViewAdapter(List<ImageView> viewList, boolean isLoop) {
            this.viewList = viewList;
            this.isLoop = isLoop;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return isLoop ? Integer.MAX_VALUE : (viewList.size() / 5);
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