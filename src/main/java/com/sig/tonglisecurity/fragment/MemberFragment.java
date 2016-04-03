package com.sig.tonglisecurity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.view.MemberView;


/**
 * 账号
 */
public class MemberFragment extends Fragment {

    public static final String TAG = "MemberFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.v("Contentfragment", "Contentfragment_onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.i(TAG, "2+MemberFragment*******");
        View v = inflater.inflate(R.layout.c_a_member, container, false);
        MemberView member = new MemberView(getActivity(), v);
        member.initBaseView(v);
        return v;
    }


}
