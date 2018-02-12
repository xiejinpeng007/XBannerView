## BannerView
### 2.0
### Usage

```
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
                        
```


---
  
  

### 1.0
#### github上很多库虽然效果华丽但总觉得使用麻烦且自定义样式困难，很多时候项目也只会用到无限滚动、点击跳转这几个基本功能，所以写了一个比较轻量级的banner方便使用。

#### <li>支持的功能：图片无限滚动，点击图片跳转，自定义指示器位置和图片以及margin参数。
#### <li>支持的数据：大部分banner的API都会给一个包含imageurl和linkurl的ArrayList。所以控件支持的也是这样的数据。
## 依赖：
#### 在project build.gradle中加入仓库url
        allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	
#### 在module build.gradle中加入依赖
    dependencies {
	        compile 'com.github.xiejinpeng007:BannerView:1.0'
	}
#### 图片加载器依赖于Universal ImageLoader


## 使用：
#### 第一步完成用于获取list数据的回调接口BannerView.Listenner

        listener = new BannerView.Listener() {
            /**
             * 获取图片的imageUrl
             */

            @Override
            public String getImgUrl(int position) {
                return bannerDataList.get(position).getImageUri();
            }

            /**
             *    获取点击后跳转的OnClickUrl
             *    不需要点击事件则返回null即可
             */

            @Override
            public String getOnClickUrl(int position) {
                return bannerDataList.get(position).getLinkUri();
            }
        };

#### 第二步使用BannerView内置的Builder完成参数设置

        /**
         * 构造方法参数 控件bannerView的实例,图片个数,用于获取url的接口
         * 可选设置: setAutoScrollPeriod(int period) 滚动的时间间隔
         *          isLoop(boolean isLoop)          是否无限循环
         *          setIndexData(LinearLayout bannerIndexLinearLayout, int SelectedRes, int unSelectedRes, int marginStart, int marginTop, int marginEnd, int marginBottom)
         *                                          放置Index的linearlayout,选中的index图片,未选中的index图片,每个index图片的margin值
         */


        new BannerView.Builder(bannerView, bannerDataList.size(), listener)
                .setAutoScrollPeriod(7000)
                .isLoop(false)
                .setIndexData(indexView, R.mipmap.page_control_on, R.mipmap.page_control_off, 17, 0, 0, 0)
                .create();
    }
