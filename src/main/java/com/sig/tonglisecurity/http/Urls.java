package com.sig.tonglisecurity.http;

/**
 * 保存了 URL 信息
 */
public class Urls {

    // public static final String URL_PRE =
    // "https://trade.gffunds.com.cn/app/api/0.9/";
    //	public static final String URL_PRE = "http://192.168.1.156:8080/tongli/";    //
    //	public static final String URL_PRE = "http://192.168.1.156:8080/tongli/";    //
    //	public static final String URL_PRE = "http://192.168.1.156:8080/tongli/";    //

    public static final String URL_PRE = "http://192.168.1.156:8080/tongli/DataGetter?data=";       //
    public static final String URL_PRE_new = "http://192.168.1.156:8080/tongli/DataGetter?data=";   //
    //public static final String URL_PRE_new = "https://trade.com.cn/app/api/1.0/"; //

    //public static final String URL_base_ping = URL_PRE_new + "002.htm";

    //public static final String URL_base_fund_info = URL_PRE_new + "002.htm";

    //调用: FSView --> onTaskOver
    public static final String URL_base_logout = URL_PRE_new + "002.htm";

    public static final String URL_acco_login_new = URL_PRE_new + "002.htm";

    //MemberView
    public static final String URL_acco_hold_new = URL_PRE_new + "002.htm";

    //MemberView
    public static final String URL_acco_profit_new = URL_PRE_new + "002.htm";

    //MemberView
    public static final String URL_acco_trade_new = URL_PRE_new + "002.htm";

    //调用: FSView --> query
    public static final String URL_wallet_rate_logout = URL_PRE_new + "002.htm";


    //RechargeActivity.querySession(); 中调用
    public static final String URL_wallet_rate_login = URL_PRE_new + "002.htm";

    //RechargeActivity.refresh(); 中调用
    public static final String URL_wallet_channel = URL_PRE_new + "002.htm";

    //调用: RechargeConfirmActivity.recharge();
    public static final String URL_wallet_recharge = URL_PRE_new + "002.htm";


    //public static final String URL_wallet_redeemf_channel = URL_PRE_new + "002.htm";

    //调用: RechargeResultActivity.onTaskOver();
    //调用: RechargeResultActivity.redeem();
    // ...
    public static final String URL_wallet_redeemf = URL_PRE_new + "002.htm";

    //调用: WalletQueryActivity.refresh();
    public static final String URL_wallet_trade = URL_PRE_new + "002.htm";

    public static final String URL_fund_info_all = URL_PRE_new + "all_fund_info";

    public static final String URL_fund_info_detail = URL_PRE_new + "one_fund_info";

    public static final String URL_fund_tenstock = URL_PRE_new + "002.htm";   //没有对应 txt

    public static final String URL_fund_tenbond = URL_PRE_new + "002.htm";    //没有对应 txt

    public static final String URL_fund_feerate = URL_PRE_new + "002.htm";    //没有对应 txt

}
