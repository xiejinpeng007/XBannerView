##BannerView
####github上很多库虽然效果华丽但总觉得使用麻烦且自定义样式困难，很多时候项目也只会用到无限滚动、点击跳转这几个基本功能，所以写了一个比较轻量级的banner方便使用。

####<li>支持的功能：图片无限滚动，点击图片跳转，自定义指示器位置和图片以及margin参数。
####<li>支持的数据：大部分banner的API都会给一个包含imageurl和linkurl的ArrayList。所以控件支持的也是这样的数据。
##依赖：
####本控件暂时没有上传公共maven库，取出BannerView.class到项目中即可
####图片加载器依赖于Universal ImageLoader
    compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.3'


##使用：
####第一步完成用于获取list数据的回调接口BannerView.Listenner

        listener = new BannerView.Listener() {
            /*获取图片的imageUrl*/
            @Override
            public String getImgUrl(int position) {
                return bannerDataList.get(position).getImageUri();
            }
            /*获取点击后跳转的OnClickUrl*/
            /*不需要点击事件则返回null即可*/
            @Override
            public String getOnClickUrl(int position) {
                return bannerDataList.get(position).getLinkUri();
            }
        };

####第二步使用BannerView内置的Builder完成参数设置

        new BannerView.Builder(bannerView)       //必须设置的参数:控件bannerView的实例
                .setBannerListSize(bannerDataList.size()) //必须设置的参数:图片个数
                .setListener(listener)           //必须设置的参数:listener实例
                .setAutoScrollPeriod(7000)       //可选设置:滚动的时间间隔
                .setIndexData(indexView, R.mipmap.page_control_on, R.mipmap.page_control_off, 17, 0, 0, 0)  //可选设置:放置Index的Linearlayout,当前index图片，默认index图片,每个index的左上右下margin值
                .create();
