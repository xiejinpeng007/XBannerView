## BannerView 2.0

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
