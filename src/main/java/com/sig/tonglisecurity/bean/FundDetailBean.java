package com.sig.tonglisecurity.bean;

/**
 * 基金信息
 */
public class FundDetailBean extends FundBean {

    public String level = "0";
    public String netvalue_total = "";
    public String found_date = "";
    public String scale = "";
    public String attention_num = "";
    public String manager = "";
    public String rank_thisyear = "";
    public String rank_nearyear = "";

    public NetvalueFivedaysBean mNetvalueFivedaysBean = new NetvalueFivedaysBean();

}
