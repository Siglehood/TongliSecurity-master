package com.sig.tonglisecurity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.view.JijinView;


/**
 * 基金 Fragment
 */
public class JijinFragment extends Fragment {

    public static final String TAG = "JijinFragment";

    private JijinView jjView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "JijinFragment --> onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.c_a_jijin, container, false);
        jjView = new JijinView(getActivity(), v);
        jjView.initBaseView(v);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (jjView != null) {
            jjView.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onKeyback() {
        if (jjView != null) {
            jjView.onKeyBack();
        }
    }

    public void disPop() {
        if (jjView != null) {
            jjView.disPop();
        }
    }

    public void showPop() {
        if (jjView != null) {
            jjView.showPop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (jjView != null) {
            jjView.showNetNotice();
        }
    }
}
