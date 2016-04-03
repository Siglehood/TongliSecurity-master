package com.sig.tonglisecurity.bean;

/**
 * 渠道
 */
public class SalesChannel {
    private String fund_channel;
    private String fund_share;
    private String fund_capital;

    public SalesChannel(String fund_channel, String fund_share,
                        String fund_capital) {
        this.fund_capital = fund_capital;
        this.fund_channel = fund_channel;
        this.fund_share = fund_share;
    }

    public String getFund_channel() {
        return fund_channel;
    }

    public void setFund_channel(String fund_channel) {
        this.fund_channel = fund_channel;
    }

    public String getFund_share() {
        return fund_share;
    }

    public void setFund_share(String fund_share) {
        this.fund_share = fund_share;
    }

    public String getFund_capital() {
        return fund_capital;
    }

    public void setFund_capital(String fund_capital) {
        this.fund_capital = fund_capital;
    }

}
