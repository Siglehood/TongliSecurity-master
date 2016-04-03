package com.sig.tonglisecurity.task;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;


import com.sig.tonglisecurity.bean.AddDeviceResultBean;
import com.sig.tonglisecurity.bean.FundBean;
import com.sig.tonglisecurity.bean.FundFeerateResultBean;
import com.sig.tonglisecurity.bean.FundTenStockResultBean;
import com.sig.tonglisecurity.bean.GetFundAllResultBean;
import com.sig.tonglisecurity.bean.GetHelpsResultBean;
import com.sig.tonglisecurity.bean.GetMessageByPageResultBean;
import com.sig.tonglisecurity.bean.GetTopicInfoResultBean;
import com.sig.tonglisecurity.bean.GetVersionCodeResultBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.http.HttpUtils;
import com.sig.tonglisecurity.http.JsonParseUtil;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.http.UrlsOther;
import com.sig.tonglisecurity.utils.LogUtil;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UIAsyncTask extends BaseHandlerUI implements Runnable {

    public static final String TAG = "UIAsyncTask";
    public boolean hasCacheData = true;
    private DatabaseAdapter mDatabaseAdapter;
    private Handler mHandler = new Handler();
    private List<FundBean> fav_entrys = new ArrayList<FundBean>();
    private List<FundBean> database_entrys = new ArrayList<FundBean>();
    private int request_code;
    private String result;
    private Context context = null;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private GetTopicInfoResultBean mGetTopicInfoResultBean;
    private FundTenStockResultBean mFundTenstockResultBean;
    private FundFeerateResultBean mFundFeerateResultBean;
    private GetHelpsResultBean mGetHelpsResultBean;
    private GetMessageByPageResultBean mGetMessageByPageBeanResult;
    private GetVersionCodeResultBean mGetVersionCodeResultBean;

    public UIAsyncTask(Handler mHandler, DatabaseAdapter mDatabaseAdapter,
                       int request_code) {
        super();
        this.mHandler = mHandler;
        this.mDatabaseAdapter = mDatabaseAdapter;
        this.request_code = request_code;
    }

    public UIAsyncTask(Context context, Handler mHandler, int request_code) {
        super();
        this.context = context;
        this.mHandler = mHandler;
        this.request_code = request_code;
    }

    public UIAsyncTask(Context context, Handler mHandler,
                       List<NameValuePair> params, int request_code) {
        super();
        this.context = context;
        LogUtil.i(TAG, "Context: " + this.context);
        this.mHandler = mHandler;
        this.params = params;
        this.request_code = request_code;
    }

    //
    public UIAsyncTask(Context context, Handler mHandler, List<NameValuePair> params,
                       int request_code, boolean hasCacheData) {
        super();
        this.context = context;
        this.mHandler = mHandler;
        this.params = params;
        this.request_code = request_code;
        this.hasCacheData = hasCacheData;
    }

    public UIAsyncTask(Context context, Handler mHandler,
                       List<NameValuePair> params, int request_code,
                       DatabaseAdapter mDatabaseAdapter) {
        super();
        this.mHandler = mHandler;
        this.params = params;
        this.request_code = request_code;
        this.mDatabaseAdapter = mDatabaseAdapter;
        this.context = context;
    }

    @Override
    public void run() {
        excute();
    }

    // < 构造器 > 定义结束  - - - - - - -  - - - - - - - - - - - -

    private void excute() {
        switch (request_code) {
            case REQUEST_GET_FAV:
                fav_entrys = getFavData();
                if (mHandler != null) {

                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, fav_entrys)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, fav_entrys));
                    }

                }
                break;

            case REQUEST_GET_INIT_DATA:
                database_entrys = getDataBase();
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, database_entrys)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, database_entrys));
                    }
                }
                break;

            case REQUEST_GET_TOPIC_DATA:

                result = HttpUtils.doGet(UrlsOther.URL_getTopicInfo, null);

                LogUtil.i(TAG, "获取导航栏的专栏 --> " + result);

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("result").equals("3")) {  // 如果网页无法加载
                        // 则从 assets 中读取 txt
                        InputStream in = context.getAssets().open(
                                "topics_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        // 字节流
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);    //
                        }
                        in.close();  // 关闭输入流
                        result = bos.toString("UTF-8");
                        //result = bos.toString( "gb2312" );   // 因为我 Tomcat 是 gb2312
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mGetTopicInfoResultBean = JsonParseUtil.getTopicInfo(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mGetTopicInfoResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA,
                                mGetTopicInfoResultBean));
                    }
                }
                break;

            case REQUEST_fund_tenstock:    //
                result = HttpUtils.doPost(Urls.URL_fund_tenstock, params);
                mFundTenstockResultBean = JsonParseUtil.getFundTenstock(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mFundTenstockResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(
                                TASK_NOTIFY_RETURN_DATA, mFundTenstockResultBean));
                    }
                }
                break;

            case REQUEST_fund_tenbond:    //
                result = HttpUtils.doPost(Urls.URL_fund_tenbond, params);
                mFundTenstockResultBean = JsonParseUtil.getFundTenstock(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mFundTenstockResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, mFundTenstockResultBean));
                    }
                }
                break;

            case REQUEST_fund_feerate:
                result = HttpUtils.doPost(Urls.URL_fund_feerate, params);
                mFundFeerateResultBean = JsonParseUtil.fundFeerate(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mFundFeerateResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, mFundFeerateResultBean));
                    }
                }
                break;

            case REQUEST_getHelps:
                result = HttpUtils.doGet(UrlsOther.URL_getHelps, null);
                try {

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("result").equals("3")) {  //
                        InputStream in = context.getAssets().open(
                                "helps_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        //
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);  //
                        }
                        in.close();    //
                        result = bos.toString("UTF-8");
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                mGetHelpsResultBean = JsonParseUtil.getHelps(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mGetHelpsResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, mGetHelpsResultBean));
                    }
                }
                break;

            case REQUEST_getMessageByPage:  // massages_info.txt
                result = HttpUtils.doPost(UrlsOther.URL_getMessageByPage, params);
                LogUtil.i(TAG, "推送的消息，服务器返回的 -->" + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("result").equals("3")) {
                        InputStream in = context.getAssets().open(
                                "massages_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                        in.close();
                        result = bos.toString("UTF-8");
                        LogUtil.i(TAG, "推送的消息，本地文件获取的*********%%%%--->" + result);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mGetMessageByPageBeanResult = JsonParseUtil.getMessageByPage(result, context, mDatabaseAdapter);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mGetMessageByPageBeanResult)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, mGetMessageByPageBeanResult));
                    }
                }
                break;

            case REQUEST_getVersion:
                result = HttpUtils.doPost(UrlsOther.URL_getVersion, params);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("result").equals("3")) {

                        InputStream in = context.getAssets().open("versionCode_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                        in.close();
                        result = bos.toString("UTF-8");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                mGetVersionCodeResultBean = JsonParseUtil.getVersion(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mGetVersionCodeResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, mGetVersionCodeResultBean));
                    }
                }
                break;

            case REQUEST_add_device:
                result = HttpUtils.doPost(UrlsOther.URL_addDevice, params);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("result").equals("3")) {
                        InputStream in = context.getAssets().open(
                                "add_advice_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                        in.close();
                        result = bos.toString("UTF-8");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                AddDeviceResultBean bean = JsonParseUtil.getAddDevice(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, bean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, bean));
                    }
                }
                break;

            case REQUEST_get_allfund:
                result = HttpUtils.doPost(Urls.URL_fund_info_all, params);
                LogUtil.i(TAG, "REQUEST_get_allfund -->" + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("result").equals("3") && !hasCacheData) {
                        InputStream in = context.getAssets().open(
                                "all_fund_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                        in.close();
                        result = bos.toString("UTF-8");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GetFundAllResultBean mGetFundAllResultBean = JsonParseUtil.getFundAll(result);
                if (mHandler != null) {
                    if (!mHandler.hasMessages(TASK_NOTIFY_RETURN_DATA, mGetFundAllResultBean)) {
                        mHandler.sendMessage(mHandler.obtainMessage(TASK_NOTIFY_RETURN_DATA, mGetFundAllResultBean));
                    }
                }
                break;

            default:
                break;
        }
    }

    private List<FundBean> getDataBase() {
        List<FundBean> entrys = getData();
        List<FundBean> fav_data = getFavDataBase();
        for (FundBean f : entrys) {
            for (FundBean fundBean : fav_data) {
                if (fundBean.fund_code.equals(f.fund_code)) {
                    f.is_fav = "1";
                    break;
                }
            }
        }
        return entrys;
    }

    //  ��ȡ�ղ��б����
    private List<FundBean> getFavDataBase() {
        List<FundBean> fav_data = new ArrayList<FundBean>();
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFavFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    FundBean entry = new FundBean();
                    entry.fund_code = c.getString(c.getColumnIndex("fund_code"));
                    entry.f_id = c.getInt(c.getColumnIndex("_id"));
                    fav_data.add(entry);
                }
            }
        }
        mDatabaseAdapter.close_funds();
        return fav_data;
    }

    private List<FundBean> getFavData() {
        List<FundBean> list = new ArrayList<FundBean>();
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFavFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                FundBean entry = new FundBean();
                entry.f_id = c.getInt(c.getColumnIndex("_id"));
                entry.fund_code = c.getString(c.getColumnIndex("fund_code"));
                entry.fund_name = c.getString(c.getColumnIndex("fund_name"));
                entry.type = c.getString(c.getColumnIndex("type"));
                entry.day_growth = c.getString(c.getColumnIndex("day_growth"));
                entry.netvalue = c.getString(c.getColumnIndex("netvalue"));
                entry.rate_thounds_date = c.getString(c.getColumnIndex("rate_thounds_date"));
                entry.rate_sevenday = c.getString(c.getColumnIndex("rate_sevenday"));
                entry.rate_thounds = c.getString(c.getColumnIndex("rate_thounds"));
                entry.netvalue_date = c.getString(c.getColumnIndex("netvalue_date"));
                entry.rate_threemonth = c.getString(c.getColumnIndex("rate_threemonth"));
                entry.rate_thisyear = c.getString(c.getColumnIndex("rate_thisyear"));
                entry.rate_nearyear = c.getString(c.getColumnIndex("rate_nearyear"));
                entry.is_fav = c.getString(c.getColumnIndex("is_fav"));
                entry.date = c.getString(c.getColumnIndex("date"));
                list.add(entry);
            }
        }
        mDatabaseAdapter.close_funds();
        return updateFav(list);
    }


    private List<FundBean> updateFav(List<FundBean> list) {
        List<FundBean> entrys;
        List<FundBean> list_6 = new ArrayList<FundBean>();
        List<FundBean> list_c = new ArrayList<FundBean>();

        entrys = getData();
        mDatabaseAdapter.open_fund();
        mDatabaseAdapter.deleteAllFavFundData();
        for (FundBean f_fav : list) {
            for (FundBean f : entrys) {
                if (f_fav.fund_code.equals(f.fund_code)) {
                    f_fav.date = f.date;
                    f_fav.day_growth = f.day_growth;
                    f_fav.fund_name = f.fund_name;
                    f_fav.netvalue = f.netvalue;
                    f_fav.netvalue_date = f.netvalue_date;
                    f_fav.rate_nearyear = f.rate_nearyear;
                    f_fav.rate_sevenday = f.rate_sevenday;
                    f_fav.rate_thounds = f.rate_thounds;
                    f_fav.rate_thounds_date = f.rate_thounds_date;
                    f_fav.rate_threemonth = f.rate_threemonth;
                    f_fav.type = f.type;
                    f_fav.rate_thisyear = f.rate_thisyear;
                    break;
                }

            }
            if (f_fav.type.equals("6")) {
                f_fav.title02 = "�������";
                f_fav.title03 = "�����껯";
                f_fav.show_value = f_fav.rate_sevenday;
                list_6.add(f_fav);
            } else {
                f_fav.title02 = "��λ��ֵ";
                f_fav.title03 = "���ǵ��";
                f_fav.show_value = f_fav.day_growth;
                list_c.add(f_fav);
            }
            mDatabaseAdapter.insertFavFundData(
                    f_fav.fund_code,
                    f_fav.fund_name,
                    f_fav.type,
                    f_fav.netvalue,
                    f_fav.day_growth,
                    f_fav.netvalue_date,
                    f_fav.rate_sevenday,
                    f_fav.rate_thounds,
                    f_fav.rate_thounds_date,
                    f_fav.rate_threemonth,
                    f_fav.rate_thisyear,
                    f_fav.rate_nearyear,
                    f_fav.is_fav,
                    f_fav.date);
        }
        mDatabaseAdapter.close_funds();
        list_6.addAll(list_c);
        return list_6;
    }

    private List<FundBean> getData() {
        List<FundBean> entrys = new ArrayList<FundBean>();
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    FundBean entry = new FundBean();
                    entry.f_id = c.getInt(c.getColumnIndex("_id"));
                    entry.fund_code = c.getString(c.getColumnIndex("fund_code"));
                    entry.fund_name = c.getString(c.getColumnIndex("fund_name"));
                    entry.type = c.getString(c.getColumnIndex("type"));
                    entry.day_growth = c.getString(c.getColumnIndex("day_growth"));
                    entry.netvalue = c.getString(c.getColumnIndex("netvalue"));
                    entry.rate_thounds_date = c.getString(c.getColumnIndex("rate_thounds_date"));
                    entry.rate_sevenday = c.getString(c.getColumnIndex("rate_sevenday"));
                    entry.rate_thounds = c.getString(c.getColumnIndex("rate_thounds"));
                    entry.netvalue_date = c.getString(c.getColumnIndex("netvalue_date"));
                    entry.rate_threemonth = c.getString(c.getColumnIndex("rate_threemonth"));
                    entry.rate_thisyear = c.getString(c.getColumnIndex("rate_thisyear"));
                    entry.rate_nearyear = c.getString(c.getColumnIndex("rate_nearyear"));
                    entry.is_fav = c.getString(c.getColumnIndex("is_fav"));
                    entry.date = c.getString(c.getColumnIndex("date"));
                    entrys.add(entry);
                }
            }
            c.close();
        }
        mDatabaseAdapter.close_funds();
        return entrys;
    }
}
