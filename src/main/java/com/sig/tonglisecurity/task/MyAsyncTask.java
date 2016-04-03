package com.sig.tonglisecurity.task;


import android.content.Context;
import android.os.AsyncTask;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.FundInfoDetailResultBean;
import com.sig.tonglisecurity.bean.GetFundAllResultBean;
import com.sig.tonglisecurity.http.HttpUtils;
import com.sig.tonglisecurity.http.JsonParseUtil;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.interfaces.FundInfoDetailListener;
import com.sig.tonglisecurity.interfaces.GetAllFundListener;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    public static final String TAG = "MyAsyncTask";

    String fund_detail;
    GetFundAllResultBean mGetFundAllResultBean = new GetFundAllResultBean();
    FundInfoDetailResultBean mFundInfoDetailResultBean = new FundInfoDetailResultBean();
    private Map<String, String> map;
    private List<NameValuePair> params;
    private int code;
    private Context context;
    private GetAllFundListener mGetAllFundListener;
    private FundInfoDetailListener mFundInfoDetailListener;

    public MyAsyncTask(FundInfoDetailListener mFundInfoDetailListener, int code, List<NameValuePair> params, Context c) {
        super();
        initMyAsyncTask(code, params, c);
        this.mFundInfoDetailListener = mFundInfoDetailListener;
    }

    public void initMyAsyncTask(int code, Map<String, String> map) {
        this.map = map;
        this.code = code;
    }

    public void initMyAsyncTask(int code, List<NameValuePair> params, Context c) {
        params.add(new BasicNameValuePair("device_info", ConfigUtil.getImei(c)));
        params.add(new BasicNameValuePair("app_version", ConfigUtil.getVersionName(c)));
        params.add(new BasicNameValuePair("market", c.getString(R.string.channel_str)));
        this.params = params;
        this.code = code;
        this.context = c;
    }

    @Override
    protected String doInBackground(String... arg) {
        switch (code) {
            case Config.GetFundAll_code:
                LogUtil.i(TAG, "GetFundAll_code[107]");
                doResult(HttpUtils.doPost(Urls.URL_fund_info_all, params));
                break;

            case Config.fund_info_detail_code:
                fund_detail = HttpUtils.doPost(Urls.URL_fund_info_detail, params);

                try {
                    JSONObject jsonObject = new JSONObject(fund_detail);

                    if (jsonObject.getString("result").equals("3")) {   // 一基金
                        InputStream in = context.getAssets().open(
                                "one_fund_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                        in.close();
                        fund_detail = bos.toString("UTF-8");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doResult(fund_detail);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(String result) {
        switch (code) {
            case Config.GetFundAll_code:        //
                mGetAllFundListener.getAllFund(mGetFundAllResultBean);
                break;
            case Config.fund_info_detail_code:  //
                mFundInfoDetailListener.mFundInfoDetail(mFundInfoDetailResultBean);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    private void doResult(String result) {
        switch (code) {
            case Config.GetFundAll_code:   //
                LogUtil.i(TAG, "MyAsyncTask --> 全部基金");
                try {
                    mGetFundAllResultBean = JsonParseUtil.getFundAll(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Config.fund_info_detail_code:
                LogUtil.i(TAG, "MyAsyncTask --> 基金详情");
                try {
                    mFundInfoDetailResultBean = JsonParseUtil.getFundDetail(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }
}