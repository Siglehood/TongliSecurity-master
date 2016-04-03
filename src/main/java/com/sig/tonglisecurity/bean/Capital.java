package com.sig.tonglisecurity.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 持仓查询结果数据
 */
public class Capital {
    private String capitals;
    private String wallet_balance;
    private String year_rate;
    private String year_profit;
    private String fund_code;
    private String fund_share;
    private String fund_capital;
    private String fund_channel;
    private String fund_fee_type;
    private String fund_bonus;
    private List<SalesChannel> channels = new ArrayList<SalesChannel>();

    public List<SalesChannel> getSalsChannels() {
        return channels;
    }

    public void addSalesChannel(SalesChannel channel) {
        channels.add(channel);
    }

    public void clearSalesChannel() {
        channels.clear();
    }

    public String getCapitals() {
        return capitals;
    }

    public void setCapitals(String capitals) {
        this.capitals = capitals;
    }

    public String getWallet_balance() {
        return wallet_balance;
    }

    public void setWallet_balance(String wallet_balance) {
        this.wallet_balance = wallet_balance;
    }

    public String getYear_profit() {
        return year_profit;
    }

    public void setYear_profit(String year_profit) {
        this.year_profit = year_profit;
    }

    public String getFund_share() {
        double allSalesChannelsShares = 0;
        for (SalesChannel channel : channels) {
            allSalesChannelsShares += Double.parseDouble(channel.getFund_share());
        }
        return "" + allSalesChannelsShares;
    }

    public void setFund_share(String fund_share) {
        this.fund_share = fund_share;
    }

    public String getFund_capital() {
//		return fund_capital;
        double allSalesChannelsCapitals = 0;
        for (SalesChannel channel : channels) {
            allSalesChannelsCapitals += Double.parseDouble(channel.getFund_capital());
        }
        return "" + allSalesChannelsCapitals;
    }

    public void setFund_capital(String fund_capital) {
        int point = fund_capital.indexOf(".");
        if (point != -1) {
            this.fund_capital = fund_capital.substring(0, point + 3 > fund_capital.length() ? fund_capital.length() : point + 3);
        } else {
            this.fund_capital = fund_capital;
        }
    }

    public String getFund_channel() {
        String str = fund_channel;
        if (fund_fee_type != null) {
            if (fund_fee_type.contains("ǰ�շ�")) {
                str += "(ǰ)";
            } else if (fund_fee_type.contains("���շ�")) {
                str += "(��)";
            }
        }
        return str;
    }

    public void setFund_channel(String fund_channel) {
        this.fund_channel = fund_channel;
    }

    public String getFund_code() {
        return fund_code;
    }

    public void setFund_code(String fund_code) {
        this.fund_code = fund_code;
    }

    public String getYear_rate() {
        return year_rate;
    }

    public void setYear_rate(String year_rate) {
        this.year_rate = year_rate;
    }

    public String getFund_fee_type() {
        return fund_fee_type;
    }

    public void setFund_fee_type(String fund_fee_type) {
        this.fund_fee_type = fund_fee_type;
    }

    public String getFund_bonus() {
        return fund_bonus;
    }

    public void setFund_bonus(String fund_bonus) {
        this.fund_bonus = fund_bonus;
    }
}
