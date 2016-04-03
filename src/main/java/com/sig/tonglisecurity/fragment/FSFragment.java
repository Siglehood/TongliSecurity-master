package com.sig.tonglisecurity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.view.FSView;


/**
 * 钱袋子
 */
public class FSFragment extends Fragment {

    public static final String TAG = "FSFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "FSFragment-->onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.i(TAG, "3+FSFragment****");
        View v = inflater.inflate(R.layout.c_a_fs, container, false);
        FSView fs = new FSView(getActivity(), v);
        fs.initBaseView(v);
        return v;
    }
}
