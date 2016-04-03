package com.sig.tonglisecurity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.view.SettingsView;


/**
 * 设置
 */
public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    private SettingsView settings_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.v(TAG, "Contentfragment_onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.i(TAG, "4+SettingsFragment*****");
        View v = inflater.inflate(R.layout.c_a_settings, container, false);
        settings_view = new SettingsView(getActivity(), v);
        settings_view.initBaseView(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (settings_view != null) {
            settings_view.setNoticeShow();
        }
    }
}
