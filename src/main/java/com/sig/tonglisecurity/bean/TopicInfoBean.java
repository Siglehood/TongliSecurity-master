package com.sig.tonglisecurity.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 帮助信息 javabean
 */
public class TopicInfoBean implements Parcelable {

    public static final Creator<TopicInfoBean> CREATOR = new Creator<TopicInfoBean>() {

        @Override
        public TopicInfoBean createFromParcel(Parcel source) {
            return new TopicInfoBean(source);
        }

        @Override
        public TopicInfoBean[] newArray(int size) {
            return new TopicInfoBean[size];
        }
    };
    public String order = "";
    public String title = "";
    public String url = "";
    public String pic = "";

    public TopicInfoBean() {
        super();
    }

    TopicInfoBean(Parcel p) {
        order = p.readString();
        title = p.readString();
        url = p.readString();
        pic = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int i) {
        p.writeString(order);
        p.writeString(title);
        p.writeString(url);
        p.writeString(pic);
    }


}
