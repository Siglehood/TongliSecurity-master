package com.sig.tonglisecurity.bean;


import com.sig.tonglisecurity.utils.ConfigUtil;

/**
 * 交易查询结果数据
 */
public class Trade {
    private String fund_code;
    private String time;
    private String busi_name;
    private String trade_type;//钱袋子交易查询类型
    private String confirm_amount;
    private String confirm_share;
    private String state;
    private String channel;
    private String fee_type;
    private String fee;
    private String request_amount;
    private String request_share;

    public String getFund_code() {
        return fund_code;
    }

    public void setFund_code(String fund_code) {
        this.fund_code = fund_code;
    }

    public String getBusi_name() {
        return busi_name;
    }

    public void setBusi_name(String busi_name) {
        this.busi_name = busi_name;
    }

    public String getConfirm_share() {
        return confirm_share;
    }

    public void setConfirm_share(String confirm_share) {
        this.confirm_share = confirm_share;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTime() {
        if (time == null)
            return time;
        return ConfigUtil.formatDate(time);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getConfirm_amount() {
        return confirm_amount;
    }

    public void setConfirm_amount(String confirm_amount) {
        this.confirm_amount = confirm_amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getRequest_amount() {
        return request_amount;
    }

    public void setRequest_amount(String request_amount) {
        this.request_amount = request_amount;
    }

    public String getRequest_share() {
        return request_share;
    }

    public void setRequest_share(String request_share) {
        this.request_share = request_share;
    }

}
