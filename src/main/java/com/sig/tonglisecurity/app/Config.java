package com.sig.tonglisecurity.app;

/**
 * 参数
 */
public class Config {
    /**
     * 上传手机信息密码
     */
    public static final String UP_PWD = "123456";
    /**
     * 查询所有基金类别
     */
    public static final int GetFundAll_code = 1;
    /**
     * 查询基金详情
     */
    public static final int fund_info_detail_code = GetFundAll_code + 1;
    /**
     * 本地数据库版本号
     */
    public static final int database_version = 6;
    /**
     * 广告条轮播速度
     */
    public static final int ad_title_speed = 3 * 1000;
    /**
     * activity请求代码
     */
    public static final int ACTIVITY_REQUEST_CODE = 5001;
    /**
     * 收藏请求代码
     */
    public static final int FAV_ACTIVITY_REQUEST_CODE = 5002;
    public static final String LIST_POSITION = "list_position";
    public static final String FAV_FLAG = "fav_flag";
}
