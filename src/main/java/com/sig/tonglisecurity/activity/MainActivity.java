package com.sig.tonglisecurity.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.adapter.NavigatePagerAdapter;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.bean.Channel;
import com.sig.tonglisecurity.fragment.JijinFragment;
import com.sig.tonglisecurity.interfaces.IndexOverListener;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.DataUtil;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.widget.PageControlView;


public class MainActivity extends ParentActivity {

    public static final String TAG = "MainActivity";

    public static AccountInfo MyAccount = new AccountInfo();
    public static Channel MyRechargeChannel = null;
    public static Channel MyRedeemChannel = null;
    public int currentBottomPosition = 0;
    public TabHost mTabHost;
    public RelativeLayout bottom_view;
    TabWidget mTabs;
    AnimationListener mAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment f = fragmentManager.findFragmentById(R.id.jijin_view);
            if (currentBottomPosition != 0) {
                ((JijinFragment) f).disPop();
            } else {
                ((JijinFragment) f).showPop();
            }
        }
    };
    private ViewPager viewpager;
    private PageControlView page_control;
    /**
     * ViewPager 监听器
     */
    OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            page_control.generatePageControl(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private NavigatePagerAdapter adapter;
    private int[] ids = {
            R.drawable.index_01,
            R.drawable.index_02,
            R.drawable.index_03,
            R.drawable.index_04,
    };
    private Activity context;
    private ImageView tab_selected;
    /**
     * Tab 监听器
     */
    OnTabChangeListener mOnTabChangeListener = new OnTabChangeListener() {

        @Override
        public void onTabChanged(String tabId) {
            startTabAnim(Integer.valueOf(tabId));

            if (!tabId.equals("2")) {

                LogUtil.i(TAG, "MainAVY: 点击 [ bottom ] : " + tabId);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate("login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    };
    private RelativeLayout index_view;
    IndexOverListener mIndexOverListener = new IndexOverListener() {
        @Override
        public void mIndexOver() {
            LogUtil.i(TAG, "indexOver:");

            DataUtil.saveNavigate(context, true);
            index_view.setVisibility(View.GONE);
        }
    };
    private LinearLayout tab01, tab02, tab03, tab04;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "MainAVY --> onCreate");

        context = this;

        setContentView(R.layout.a_main);
        bottom_view = (RelativeLayout) findViewById(R.id.bottom_view);

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabs = (TabWidget) findViewById(android.R.id.tabs);
        mTabs.setDividerDrawable(null);

        if (DataUtil.getNavigate(context)) {
            initContent();
        } else {
            initContent();
            index_view = (RelativeLayout) findViewById(R.id.index_view);
            viewpager = (ViewPager) findViewById(R.id.viewpager_index);
            viewpager.setCurrentItem(0);

            // 自定义组件 : com.mobile.widget.PageControlView
            page_control = (PageControlView) findViewById(R.id.pageControlView_index);
            page_control.image = new int[]{R.drawable.gray_dot, R.drawable.black_dot};
            page_control.count = 4;
            page_control.generatePageControl(0);

            // 导航栏适配器
            adapter = new NavigatePagerAdapter(this, ids, mIndexOverListener, page_control);
            viewpager.setAdapter(adapter);
            viewpager.setOnPageChangeListener(mOnPageChangeListener);
        }
    }

    /**
     * 初始化窗体
     */
    private void initContent() {
        mTabHost.setup();

        //item_tab_view.xml [布局文件]
        tab01 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_tab_view, null);
        tab01.setBackgroundResource(R.drawable.tab_jijin_drawable);

        tab02 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_tab_view, null);
        tab02.setBackgroundResource(R.drawable.tab_member_drawable);

        tab03 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_tab_view, null);
        tab03.setBackgroundResource(R.drawable.tab_fs_drawable);

        tab04 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_tab_view, null);
        tab04.setBackgroundResource(R.drawable.tab_settings_drawable);

        mTabHost.addTab(mTabHost.newTabSpec("0")
                .setIndicator(tab01)
                .setContent(R.id.jijin_view));

        mTabHost.addTab(mTabHost.newTabSpec("1")
                .setIndicator(tab02)
                .setContent(R.id.member_view));

        mTabHost.addTab(mTabHost.newTabSpec("2")
                .setIndicator(tab03)
                .setContent(R.id.fs_view));

        mTabHost.addTab(mTabHost.newTabSpec("3")
                .setIndicator(tab04)
                .setContent(R.id.settings_view));

        mTabHost.setCurrentTab(0);    //设当前 Tab 为 0

        mTabHost.setOnTabChangedListener(mOnTabChangeListener);

        tab_selected = new ImageView(context);
        tab_selected.setBackgroundResource(R.drawable.tab_scroll);
        int width = (ConfigUtil.getTabPix(context) - ConfigUtil.getTabPix(context)) / 2;
        LayoutParams layoutParams = new LayoutParams(ConfigUtil.getTabPix(context),
                LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);  // CENTER_VERTICAL
        if (width > 0) {
            layoutParams.setMargins(width, 0, width, 0);
        }
        bottom_view.addView(tab_selected, layoutParams);
    }

    // TabButton 小动画
    public void startTabAnim(int id) {

        LogUtil.i(TAG, "Tab: 动画");   //每点一下 Tab 跑一次

        int fromXDelta = currentBottomPosition * ConfigUtil.getTabPix(context);
        int toXDelta = id * ConfigUtil.getTabPix(context);
        Animation animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
        animation.setDuration(200);
        animation.setFillAfter(true);
        tab_selected.startAnimation(animation);
        currentBottomPosition = id;
        animation.setAnimationListener(mAnimationListener);
    }

    /**
     * gotoFirst
     */
    public void gotoFirst() {
        mTabHost.setCurrentTab(0);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.jijin_view);
        f.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment f = fragmentManager.findFragmentById(R.id.jijin_view);
            if (fragmentManager.popBackStackImmediate("login", FragmentManager.POP_BACK_STACK_INCLUSIVE)) {
                return true;
            }
            ((JijinFragment) f).onKeyback();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
