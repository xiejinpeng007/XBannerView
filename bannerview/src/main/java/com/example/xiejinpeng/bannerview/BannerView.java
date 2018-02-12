package com.example.xiejinpeng.bannerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.example.xiejinpeng.bannerview.databinding.B01BannerItemBinding;
import com.example.xiejinpeng.bannerview.functional.Action1;
import com.example.xiejinpeng.bannerview.functional.Func1;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiejinpeng on 15/11/13.
 */
public class BannerView extends RelativeLayout {

    private Disposable autoScroller;

    private AtomicBoolean activeAutoScroll = new AtomicBoolean(true);

    private ViewPager bannerPager;

    private final String INDI_LAYOUT = "indi_layout";

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(PagerAdapter adapter) {

        initBannerPager();

        bannerPager.setAdapter(adapter);

        initAttrs(adapter);

    }

    private void initBannerPager() {
        if (bannerPager != null)
            return;
        bannerPager = new ViewPager(getContext());
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(bannerPager, params);
    }


    private void initAttrs(PagerAdapter pagerAdapter) {

        if (pagerAdapter == null) {
            setVisibility(GONE);
            return;
        }

        //
        if (pagerAdapter instanceof BannerView.BAdapter) {
            BannerView.BAdapter adapter = (BannerView.BAdapter) pagerAdapter;

            //pager style
            bannerPager.setPageMargin(dp2px(6f));

            if (!adapter.isDataExist()) {
                getLayoutParams().height = dp2px(140);
            } else if (adapter.isSinglePage()) {
                setClipChildren(true);
                setClipToPadding(true);
                setPadding(
                        0, 0,
                        0, 0);
            } else {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
                setClipChildren(false);
                setClipToPadding(false);
                setPadding(
                        dp2px(20f), 0,
                        dp2px(20f), 0);
            }

            //auto scroll
            long ms = adapter.getAutoScrollMills();
            if (!adapter.isSinglePage() && ms > 0 &&
                    (autoScroller == null || autoScroller.isDisposed()))
                autoScroller = Observable.interval(ms, ms, TimeUnit.MILLISECONDS)
                        .filter(v -> activeAutoScroll.get())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(v -> bannerPager.setCurrentItem(
                                bannerPager.getCurrentItem() + 1 < adapter.getCount() ? bannerPager.getCurrentItem() + 1 : 0,
                                true)
                                , e -> Log.d("banner", e.getMessage()));


            // init indicator

            if (adapter.showIndicator) {
                initIndicator(adapter);
            }


            bannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (adapter.showIndicator)
                        Observable.range(0, adapter.getDataCount())
                                .subscribe(i ->
                                                ((LinearLayout) (findViewWithTag(INDI_LAYOUT)))
                                                        .getChildAt(i)
                                                        .setSelected(i == (position % adapter.getDataCount())),
                                        e -> initIndicator(adapter));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            bannerPager.setOffscreenPageLimit(1);

            bannerPager.setCurrentItem((adapter.getCount() / 2));

        }

    }

    private void initIndicator(BAdapter adapter) {
        LinearLayout indiLayout = findViewWithTag(INDI_LAYOUT) != null
                ? (LinearLayout) findViewWithTag(INDI_LAYOUT) : new LinearLayout(getContext());

        indiLayout.removeAllViews();

        if (findViewWithTag(INDI_LAYOUT) != null) {
            bringChildToFront(indiLayout);
        } else {
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    dp2px(6));
            params.addRule(ALIGN_PARENT_BOTTOM);
            params.addRule(CENTER_HORIZONTAL);
            params.setMargins(0, 0, 0, dp2px(6));
            indiLayout.setLayoutParams(params);
            indiLayout.setOrientation(LinearLayout.HORIZONTAL);
            indiLayout.setTag(INDI_LAYOUT);
            addView(indiLayout, params);
        }

        Stream.range(0, adapter.getDataCount())
                .filter(i -> adapter.getDataCount() > 1)
                .forEach(i -> {

                    View indi = new View(getContext());
                    indi.setBackgroundResource(R.drawable.selector_banner);

                    LinearLayout.LayoutParams indiParams =
                            new LinearLayout.LayoutParams(dp2px(6), dp2px(6));

                    indiParams.gravity = Gravity.CENTER;
                    indiParams.setMargins(dp2px(8), 0, dp2px(8), 0);

                    indi.setLayoutParams(indiParams);

                    indiLayout.addView(indi);
                });
    }

    public void setActiveAutoScroll(boolean active) {
        activeAutoScroll.set(active);
    }

    public int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        fix wrap_content

//        int height = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if (h > height) height = h;
//        }
//
//        if (height != 0) {
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static class BAdapter<T> extends PagerAdapter {

        private List<T> data;
        private Func1<T, String> getImageUrl;
        private Action1<T> onClick;
        private Func1<T, String> getTitle;
        private Func1<T, String> getSubTitle;
        private long autoScrollMills;
        private boolean showIndicator;

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data = data;
        }

        public void setGetImageUrl(Func1<T, String> getImageUrl) {
            this.getImageUrl = getImageUrl;
        }

        public void setOnClick(Action1<T> onClick) {
            this.onClick = onClick;
        }

        public void setGetTitle(Func1<T, String> getTitle) {
            this.getTitle = getTitle;
        }

        public void setGetSubTitle(Func1<T, String> getSubTitle) {
            this.getSubTitle = getSubTitle;
        }

        public long getAutoScrollMills() {
            return autoScrollMills;
        }

        public void setAutoScrollMills(long autoScrollMills) {
            this.autoScrollMills = autoScrollMills;
        }

        public void setShowIndicator(boolean showIndicator) {
            this.showIndicator = showIndicator;
        }

        public BAdapter(List data) {
            this.data = data;
        }


        @Override
        public int getCount() {
            return isSinglePage() ? 1 : data.size() * 100;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            final int dataPosition = position % data.size();

            View item = LayoutInflater
                    .from(container.getContext())
                    .inflate(R.layout.b0_1_banner_item, null, false);
            container.addView(item);
            B01BannerItemBinding binding = B01BannerItemBinding.bind(item);

            //bind data
            binding.bannerImage.setOnClickListener(
                    v -> onClick.call(data.get(dataPosition)));

            binding.title.setText(
                    Optional.ofNullable(getTitle)
                            .map(f -> f.call(data.get(dataPosition)))
                            .orElse(""));

            binding.subTitle.setText(
                    Optional.ofNullable(getSubTitle)
                            .map(f -> f.call(data.get(dataPosition)))
                            .orElse(""));

            Glide.with(container.getContext())
                    .load(getImageUrl.call(data.get(dataPosition)))
                    .into(binding.bannerImage);

            return item;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private boolean isSinglePage() {
            return data.size() == 1;
        }

        private boolean isDataExist() {
            return data != null;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        private int getDataCount() {
            return data.size();
        }

    }

    public static class AdapterBuilder<T> {

        private List<T> data;
        private Action1<T> onClick;
        private Func1<T, String> getImageUrl;
        private Func1<T, String> getTitle;
        private Func1<T, String> getSubTitle;
        private long autoScrollMills;
        private boolean showIndicator;

        public AdapterBuilder<T> setData(List<T> data) {
            this.data = data;
            return this;
        }

        public AdapterBuilder<T> setGetImageUrl(Func1<T, String> getImageUrl) {
            this.getImageUrl = getImageUrl;
            return this;
        }

        public AdapterBuilder<T> setOnClick(Action1<T> onClick) {
            this.onClick = onClick;
            return this;
        }

        public AdapterBuilder<T> setAutoScrollMills(long autoScrollMills) {
            this.autoScrollMills = autoScrollMills;
            return this;
        }

        public AdapterBuilder<T> setGetTitle(Func1<T, String> getTitle) {
            this.getTitle = getTitle;
            return this;
        }

        public AdapterBuilder<T> setGetSubTitle(Func1<T, String> getSubTitle) {
            this.getSubTitle = getSubTitle;
            return this;
        }

        public AdapterBuilder<T> setShowIndicator(boolean showIndicator) {
            this.showIndicator = showIndicator;
            return this;
        }

        public BAdapter build() {
            if (data == null || data.isEmpty())
                return null;

            BAdapter adapter = new BAdapter<T>(data);
            adapter.setGetImageUrl(getImageUrl);
            adapter.setOnClick(onClick);
            adapter.setAutoScrollMills(autoScrollMills);
            adapter.setShowIndicator(showIndicator);
            adapter.setGetTitle(getTitle);
            adapter.setGetSubTitle(getSubTitle);
            return adapter;
        }

    }

}
