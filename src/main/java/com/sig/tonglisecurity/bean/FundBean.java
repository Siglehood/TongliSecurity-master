package com.sig.tonglisecurity.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FundBean implements Parcelable {

    public static final Creator<FundBean> CREATOR = new Creator<FundBean>() {

        @Override
        public FundBean createFromParcel(Parcel source) {
            return new FundBean(source);
        }

        @Override
        public FundBean[] newArray(int size) {
            return new FundBean[size];
        }
    };
    public int f_id;
    public String title01 = "基金简称";
    public String title02 = "万份收益";
    public String title03 = "七日年化";
    public String show_value = "";
    public String fund_code = "";
    public String fund_name = "";
    public String type = "";
    public String netvalue = "";
    public String day_growth = "";//����Ϊ�մ�
    public String rate_sevenday = "";//����Ϊ�մ�
    public String rate_thounds = "";//����Ϊ�մ�
    public String rate_thounds_date = "";//����Ϊ��
    public String netvalue_date = "";
    public String rate_threemonth = "";
    public String rate_thisyear = "";
    public String rate_nearyear = "";
    public String is_fav = "0";//0,δ�ղأ�1���ղ�
    public String date = "";

    public FundBean() {
        super();
    }

    FundBean(Parcel p) {
        f_id = p.readInt();
        title01 = p.readString();
        title02 = p.readString();
        title03 = p.readString();
        show_value = p.readString();
        fund_code = p.readString();
        fund_name = p.readString();
        type = p.readString();
        netvalue = p.readString();
        day_growth = p.readString();
        rate_sevenday = p.readString();
        rate_thounds = p.readString();
        rate_thounds_date = p.readString();
        netvalue_date = p.readString();
        rate_threemonth = p.readString();
        rate_thisyear = p.readString();
        rate_nearyear = p.readString();
        is_fav = p.readString();
        date = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(f_id);
        p.writeString(title01);
        p.writeString(title02);
        p.writeString(title03);
        p.writeString(show_value);
        p.writeString(fund_code);
        p.writeString(fund_name);
        p.writeString(type);
        p.writeString(netvalue);
        p.writeString(day_growth);
        p.writeString(rate_sevenday);
        p.writeString(rate_thounds);
        p.writeString(rate_thounds_date);
        p.writeString(netvalue_date);
        p.writeString(rate_threemonth);
        p.writeString(rate_thisyear);
        p.writeString(rate_nearyear);
        p.writeString(is_fav);
        p.writeString(date);

    }

}
