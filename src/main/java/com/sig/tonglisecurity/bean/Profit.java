package com.sig.tonglisecurity.bean;


/**
 * 盈亏查询结果数据
 */
public class Profit {
    private String fund_code;
    private String profit;
    private String profit_rate;
    private String capital_begin;
    private String cost;
    private String capital_end;

    public String getFund_code() {
        return fund_code;
    }

    public void setFund_code(String fund_code) {
        this.fund_code = fund_code;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        int point = profit.indexOf(".");
        if (point != -1) {
            this.profit = profit.substring(0, point + 3 > profit
                    .length() ? profit.length() : point + 3);
        } else {
            this.profit = profit;
        }
    }

    public String getProfit_rate() {
        String result = profit_rate;
        int point = result.indexOf(".");
        if (point != -1) {
            result = result.substring(0, point + 4 > result
                    .length() ? result.length() : point + 4)
                    + "%";
        } else {
            result = result + "%";
        }
        return result;
    }

    public void setProfit_rate(String profit_rate) {
        this.profit_rate = profit_rate;
    }

    public String getProfit_rate2() {
        return profit_rate;
    }

    public String getCapital_begin() {
        return capital_begin;
    }

    public void setCapital_begin(String capital_begin) {
        this.capital_begin = capital_begin;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCapital_end() {
        return capital_end;
    }

    public void setCapital_end(String capital_end) {
        this.capital_end = capital_end;
    }
}
