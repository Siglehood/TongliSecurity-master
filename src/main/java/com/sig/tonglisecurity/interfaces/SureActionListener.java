package com.sig.tonglisecurity.interfaces;


import com.sig.tonglisecurity.bean.FundTitleBean;

/**
 * 筛选监听器
 */
public interface SureActionListener {
    /**
     * 菜单隐藏操作
     */
    void sureAction(int type, FundTitleBean bean);
}
