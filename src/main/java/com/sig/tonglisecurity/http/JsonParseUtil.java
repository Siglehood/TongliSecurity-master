package com.sig.tonglisecurity.http;

import android.content.Context;
import android.database.Cursor;


import com.sig.tonglisecurity.bean.AddDeviceResultBean;
import com.sig.tonglisecurity.bean.FeeratesOtherBean;
import com.sig.tonglisecurity.bean.FeeratesRecoBean;
import com.sig.tonglisecurity.bean.FeeratesRedeemBean;
import com.sig.tonglisecurity.bean.FeeratesSubBean;
import com.sig.tonglisecurity.bean.FundBean;
import com.sig.tonglisecurity.bean.FundDetailBean;
import com.sig.tonglisecurity.bean.FundFeerateResultBean;
import com.sig.tonglisecurity.bean.FundInfoDetailResultBean;
import com.sig.tonglisecurity.bean.FundTenStockResultBean;
import com.sig.tonglisecurity.bean.FundTenstockBean;
import com.sig.tonglisecurity.bean.GetFundAllResultBean;
import com.sig.tonglisecurity.bean.GetHelpsResultBean;
import com.sig.tonglisecurity.bean.GetMessageByPageResultBean;
import com.sig.tonglisecurity.bean.GetTopicInfoResultBean;
import com.sig.tonglisecurity.bean.GetVersionCodeResultBean;
import com.sig.tonglisecurity.bean.HelpsBean;
import com.sig.tonglisecurity.bean.MessageBean;
import com.sig.tonglisecurity.bean.NetvalueFivedaysBean;
import com.sig.tonglisecurity.bean.TopicInfoBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Json 解析工具类
 */
public class JsonParseUtil {

    public static final String TAG = "GetTopicInfoResultBean";

    public static GetTopicInfoResultBean getTopicInfo(String result) {
        GetTopicInfoResultBean bean = new GetTopicInfoResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            bean.state = jsonObject.getString("state");
            bean.amount = jsonObject.getInt("amount");
            JSONArray topicInfos = jsonObject.getJSONArray("topicInfos");
            List<TopicInfoBean> list = new ArrayList<TopicInfoBean>();
            int length = topicInfos.length();
            for (int i = 0; i < length; i++) {
                TopicInfoBean t = new TopicInfoBean();
                JSONObject j = (JSONObject) topicInfos.opt(i);
                t.order = j.getString("order");
                t.title = j.getString("title");
                t.url = j.getString("url");
                t.pic = j.getString("pic");
                list.add(t);
            }
            bean.list = list;
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return bean;
    }

    public static FundInfoDetailResultBean getFundDetail(String result) {
        FundInfoDetailResultBean bean = new FundInfoDetailResultBean();
        FundDetailBean mFundDetailBean = new FundDetailBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            mFundDetailBean.fund_name = jsonObject.getString("fund_name");
            mFundDetailBean.attention_num = jsonObject.getString("attention_num");
            mFundDetailBean.manager = jsonObject.getString("manager");
            mFundDetailBean.rate_nearyear = jsonObject.getString("rate_nearyear");
            if (jsonObject.has("rank_thisyear")) {
                mFundDetailBean.rank_thisyear = jsonObject.getString("rank_thisyear");
            }
            if (jsonObject.has("rank_nearyear")) {
                mFundDetailBean.rank_nearyear = jsonObject.getString("rank_nearyear");
            }
            if (jsonObject.has("scale")) {
                mFundDetailBean.scale = jsonObject.getString("scale");
            }
            if (jsonObject.has("level")) {
                mFundDetailBean.level = jsonObject.getString("level");
            }
            mFundDetailBean.rate_thisyear = jsonObject.getString("rate_thisyear");
            mFundDetailBean.netvalue = jsonObject.getString("netvalue");
            mFundDetailBean.type = jsonObject.getString("type");
            mFundDetailBean.found_date = jsonObject.getString("found_date");
            mFundDetailBean.netvalue_total = jsonObject.getString("netvalue_total");
            JSONObject netvalue_fivedays = jsonObject.getJSONObject("netvalue_fivedays");
            JSONArray days_array = netvalue_fivedays.getJSONArray("days");
            JSONArray netvalues_array = netvalue_fivedays.getJSONArray("netvalues");
            List<String> days = new ArrayList<String>();
            List<String> netvalues = new ArrayList<String>();
            NetvalueFivedaysBean mNetvalueFivedaysBean = new NetvalueFivedaysBean();
            for (int i = 0; i < days_array.length(); i++) {
                days.add(days_array.optString(i));
            }
            mNetvalueFivedaysBean.days = days;
            for (int i = 0; i < netvalues_array.length(); i++) {
                netvalues.add(netvalues_array.optString(i));
            }
            mNetvalueFivedaysBean.netvalues = netvalues;
            mFundDetailBean.mNetvalueFivedaysBean = mNetvalueFivedaysBean;
            bean.mFundDetailBean = mFundDetailBean;
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bean;
    }

    public static FundTenStockResultBean getFundTenstock(String result) {
        FundTenStockResultBean bean = new FundTenStockResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("datasrc")) {
                bean.datasrc = jsonObject.getString("datasrc");
            }
            if (jsonObject.has("stocks") || jsonObject.has("bonds")) {
                JSONArray tens = null;
                if (jsonObject.has("stocks")) {
                    tens = jsonObject.getJSONArray("stocks");
                } else if (jsonObject.has("bonds")) {
                    tens = jsonObject.getJSONArray("bonds");
                }
                List<FundTenstockBean> list = new ArrayList<FundTenstockBean>();
                int length = 0;
                if (tens != null) {
                    length = tens.length();
                }
                for (int i = 0; i < length; i++) {
                    FundTenstockBean t = new FundTenstockBean();
                    JSONObject j = (JSONObject) tens.opt(i);
                    if (j.has("code")) {
                        t.code = j.getString("code");
                    }
                    if (j.has("name")) {
                        t.name = j.getString("name");
                    }
                    if (j.has("netvalue_ratio")) {
                        t.netvalue_ratio = j.getString("netvalue_ratio");
                    }
                    list.add(t);
                }
                bean.list = list;
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bean;
    }

    public static FundFeerateResultBean fundFeerate(String result) {
        FundFeerateResultBean bean = new FundFeerateResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("feerates_reco")) {
                JSONArray recos = jsonObject.getJSONArray("feerates_reco");
                List<FeeratesRecoBean> list_reco = new ArrayList<FeeratesRecoBean>();
                int length = recos.length();
                for (int i = 0; i < length; i++) {
                    FeeratesRecoBean t = new FeeratesRecoBean();
                    JSONObject j = (JSONObject) recos.opt(i);
                    if (j.has("amount")) {
                        t.amount = j.getString("amount");
                    }
                    if (j.has("feerate_reco")) {
                        t.feerate_reco = j.getString("feerate_reco");
                    }
                    if (j.has("fee_pattern")) {
                        t.fee_pattern = j.getString("fee_pattern");
                    }
                    list_reco.add(t);
                }
                bean.list_reco = list_reco;
            }
            if (jsonObject.has("feerates_sub")) {
                JSONArray subs = jsonObject.getJSONArray("feerates_sub");
                List<FeeratesSubBean> list_sub = new ArrayList<FeeratesSubBean>();
                int length = subs.length();
                for (int i = 0; i < length; i++) {
                    FeeratesSubBean t = new FeeratesSubBean();
                    JSONObject j = (JSONObject) subs.opt(i);
                    if (j.has("amount")) {
                        t.amount = j.getString("amount");
                    }
                    if (j.has("feerate_sub")) {
                        t.feerate_sub = j.getString("feerate_sub");
                    }
                    if (j.has("fee_pattern")) {
                        t.fee_pattern = j.getString("fee_pattern");
                    }
                    list_sub.add(t);
                }
                bean.list_sub = list_sub;
            }
            if (jsonObject.has("feerates_redeem")) {
                JSONArray redeems = jsonObject.getJSONArray("feerates_redeem");
                List<FeeratesRedeemBean> list_redeem = new ArrayList<FeeratesRedeemBean>();
                int length = redeems.length();
                for (int i = 0; i < length; i++) {
                    FeeratesRedeemBean t = new FeeratesRedeemBean();
                    JSONObject j = (JSONObject) redeems.opt(i);
                    if (j.has("amount")) {
                        t.amount = j.getString("amount");
                    }
                    if (j.has("feerate_redeem")) {
                        t.feerate_redeem = j.getString("feerate_redeem");
                    }
                    list_redeem.add(t);
                }
                bean.list_redeem = list_redeem;
            }
            if (jsonObject.has("feerates_other")) {
                JSONArray others = jsonObject.getJSONArray("feerates_other");
                List<FeeratesOtherBean> list_other = new ArrayList<FeeratesOtherBean>();
                int length = others.length();
                for (int i = 0; i < length; i++) {
                    FeeratesOtherBean t = new FeeratesOtherBean();
                    JSONObject j = (JSONObject) others.opt(i);
                    if (j.has("fee_name")) {
                        t.fee_name = j.getString("fee_name");
                    }
                    if (j.has("fee_rate")) {
                        t.fee_rate = j.getString("fee_rate");
                    }
                    list_other.add(t);
                }
                bean.list_other = list_other;
            }

        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static GetHelpsResultBean getHelps(String result) {
        GetHelpsResultBean bean = new GetHelpsResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            bean.state = jsonObject.getString("state");
            bean.amount = jsonObject.getInt("amount");
            JSONArray helps = jsonObject.getJSONArray("helps");
            List<HelpsBean> list = new ArrayList<HelpsBean>();
            int length = helps.length();
            for (int i = 0; i < length; i++) {
                HelpsBean t = new HelpsBean();
                JSONObject j = (JSONObject) helps.opt(i);
                t.ask = j.getString("ask");
                t.reply = j.getString("reply");
                list.add(t);
            }
            bean.list = list;
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bean;
    }

    public static GetMessageByPageResultBean getMessageByPage(String result, Context context, DatabaseAdapter mDatabaseAdapter) {

        GetMessageByPageResultBean bean = new GetMessageByPageResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            bean.state = jsonObject.getString("state");
            if (jsonObject.has("currentPage")) {
                bean.currentPage = jsonObject.getInt("currentPage");
            }
            if (jsonObject.has("totalPages")) {
                bean.totalPages = jsonObject.getInt("totalPages");
            }
            List<MessageBean> msgList_db = getMsgFromDB(mDatabaseAdapter);
            if (jsonObject.has("messages")) {
                JSONArray messages = jsonObject.getJSONArray("messages");
                List<MessageBean> list = new ArrayList<MessageBean>();
                int length = messages.length();
                for (int i = 0; i < length; i++) {
                    MessageBean t = new MessageBean();
                    JSONObject j = (JSONObject) messages.opt(i);
                    t.title = j.getString("title");
                    t.context = j.getString("context");
                    t.url = j.getString("url");
                    t.date = j.getString("date");
                    for (MessageBean messageBean : msgList_db) {
                        if (messageBean.title.equals(t.title)) {
                            t.hasRead = true;
                            break;
                        }
                    }
                    list.add(t);
                }
                bean.msgList = list;
            }

        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bean;
    }

    private static List<MessageBean> getMsgFromDB(DatabaseAdapter mDatabaseAdapter) {
        List<MessageBean> msgList_db = new ArrayList<MessageBean>();
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllMsgData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    MessageBean bean = new MessageBean();
                    bean.f_id = c.getInt(c.getColumnIndex("_id"));
                    bean.title = c.getString(c.getColumnIndex("title"));
                    bean.context = c.getString(c.getColumnIndex("context"));
                    bean.url = c.getString(c.getColumnIndex("url"));
                    bean.date = c.getString(c.getColumnIndex("date"));
                    msgList_db.add(bean);
                }
            }
            c.close();
        }
        mDatabaseAdapter.close_funds();
        return msgList_db;
    }

    public static GetVersionCodeResultBean getVersion(String result) {

        GetVersionCodeResultBean bean = new GetVersionCodeResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            bean.state = jsonObject.getString("state");
            if (jsonObject.has("versionCode")) {
                bean.versionCode = jsonObject.getString("versionCode");
            }
            if (jsonObject.has("versionState")) {
                bean.versionState = jsonObject.getString("versionState");
            }
            if (jsonObject.has("highestCode")) {
                bean.highestCode = jsonObject.getString("highestCode");
            }
            if (jsonObject.has("explain")) {
                bean.explain = jsonObject.getString("explain");
            }
            if (jsonObject.has("download")) {
                bean.download = jsonObject.getString("download");
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bean;
    }

    public static AddDeviceResultBean getAddDevice(String result) {

        AddDeviceResultBean bean = new AddDeviceResultBean();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            bean.state = jsonObject.getString("state");
            if (jsonObject.has("msg")) {
                bean.msg = jsonObject.getString("msg");
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bean;
    }

    //读取: all_fund_info.txt
    //返回: GetFundAllResultBean(实例对象), 存放所有基金的信息
    public static GetFundAllResultBean getFundAll(String result) {

        GetFundAllResultBean bean = new GetFundAllResultBean();
        JSONObject jsonObject;

        LogUtil.i(TAG, "推送: all_fund_info");

        try {
            jsonObject = new JSONObject(result);
            JSONArray funds = jsonObject.getJSONArray("funds");
            List<FundBean> fund_list = new ArrayList<FundBean>();
            int length = funds.length();
            for (int i = 0; i < length; i++) {
                FundBean f = new FundBean();
                JSONObject j = (JSONObject) funds.opt(i);
                f.rate_thounds_date = j.getString("rate_thounds_date");
                f.fund_name = j.getString("fund_name");
                f.fund_code = j.getString("fund_code");
                if (j.has("rate_nearyear")) {
                    f.rate_nearyear = j.getString("rate_nearyear").trim();
                }
                if (j.has("rate_thisyear")) {
                    f.rate_thisyear = j.getString("rate_thisyear").trim();
                }
                if (j.has("netvalue_date")) {
                    f.netvalue_date = j.getString("netvalue_date").trim();
                }
                if (j.has("day_growth")) {
                    f.day_growth = j.getString("day_growth").trim();
                }
                if (j.has("netvalue")) {
                    f.netvalue = j.getString("netvalue").trim();
                }
                if (j.has("rate_sevenday")) {
                    f.rate_sevenday = j.getString("rate_sevenday").trim();
                }
                if (j.has("rate_thounds")) {
                    f.rate_thounds = j.getString("rate_thounds").trim();
                }
                f.type = j.getString("type");
                if (j.has("rate_threemonth")) {
                    f.rate_threemonth = j.getString("rate_threemonth").trim();
                }
                fund_list.add(f);
            }
            bean.fund_list = fund_list;
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return bean;
    }
}
