package com.sig.tonglisecurity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.TopicInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 广告适配器
 * ViewPager
 */
public class ImagePagerAdapter extends PagerAdapter {

    public List<Integer> imgs = new ArrayList<Integer>();
    public List<TopicInfoBean> list = new ArrayList<TopicInfoBean>();
    ImageLoader imageLoader;
    Context context;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private OnClickListener mOnClick;

    public ImagePagerAdapter(Context context, OnClickListener mOnClick) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ad_null_default_pic)
                .showImageForEmptyUri(R.drawable.ad_null_default_pic)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        this.mOnClick = mOnClick;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object instantiateItem(View view, int position) {
        final FrameLayout imageLayout = (FrameLayout) inflater.inflate(R.layout.item_pager_image, null);
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        int p;
        if (list.size() > 0) {
            p = position % list.size();
            imageLoader.displayImage(list.get(p).pic, imageView, options, new ImageLoadingListener() {


                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                    imageView.setAnimation(anim);
                    anim.start();

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            p = position % imgs.size();
            imageView.setImageResource(imgs.get(p));
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            imageView.setAnimation(anim);
            anim.start();
        }
        ((ViewPager) view).addView(imageLayout);
        imageLayout.setOnClickListener(mOnClick);
        imageLayout.setId(1000);
        imageLayout.setTag(p);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
