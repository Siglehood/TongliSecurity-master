package com.sig.tonglisecurity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.interfaces.IndexOverListener;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.widget.PageControlView;


/**
 * 用户帮助适配器
 */
public class NavigatePagerAdapter extends PagerAdapter {

    public static final String TAG = "NavigatePagerAdapter";
    FrameLayout imageLayout;
    private LayoutInflater inflater;
    private int[] ids;
    private Activity context;
    private PageControlView page_control;
    private IndexOverListener mIndexOverListener;
    private ImageView imageView;
    private ImageButton start_btn;
    private FrameLayout left_view, right_view;
    private FrameLayout start_view;
    OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doOpenDoor();
        }
    };

    public NavigatePagerAdapter(Activity context, int[] ids,
                                IndexOverListener mIndexOverListener, PageControlView page_control) {
        inflater = context.getLayoutInflater();
        this.ids = ids;
        this.context = context;
        this.page_control = page_control;
        this.mIndexOverListener = mIndexOverListener;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container) {
    }

    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public Object instantiateItem(View view, int position) {
        LogUtil.i(TAG, "导航页: instantiateItem");

        imageLayout = (FrameLayout) inflater.inflate(R.layout.item_naviagte_pager_image, null);
        imageView = (ImageView) imageLayout.findViewById(R.id.image);
        start_btn = (ImageButton) imageLayout.findViewById(R.id.start_btn);
        start_btn.setOnClickListener(mOnClick);
        start_view = (FrameLayout) imageLayout.findViewById(R.id.start_view);
        left_view = (FrameLayout) imageLayout.findViewById(R.id.left_view);
        right_view = (FrameLayout) imageLayout.findViewById(R.id.right_view);

        if (position == ids.length - 1) {
            start_btn.setVisibility(View.VISIBLE);
        }

        imageView.setBackgroundResource(ids[position]);
        ((ViewPager) view).addView(imageLayout, 0);

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

    @Override
    public void startUpdate(View container) {
    }

    /**
     * 动画效果
     */
    @SuppressLint("ResourceAsColor")
    private void doOpenDoor() {

        LogUtil.i(TAG, "doOpenDoor");

        page_control.setVisibility(View.GONE);
        start_view.setVisibility(View.GONE);
        Animation leftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_left);
        Animation rightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_right);

        left_view.setAnimation(leftOutAnimation);
        right_view.setAnimation(rightOutAnimation);
        left_view.startAnimation(leftOutAnimation);
        right_view.startAnimation(rightOutAnimation);
        leftOutAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                left_view.setVisibility(View.GONE);
                right_view.setVisibility(View.GONE);
                mIndexOverListener.mIndexOver();
            }
        });
    }
}  

