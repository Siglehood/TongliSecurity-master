package com.sig.tonglisecurity.bean;

/**
 * 充值渠道 javabean
 */
public class Channel {
    private String name;
    private String code;
    private String account;
    private String card_no;
    private String recharge_max = "";
    private String recharge_min = "";
    private int support_withhold;
    private int support_redeemf;
    private String amount;
    private String fee_rate;
    private String fund_code;
    private String bankIdCode;
    private String capitalMode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getRecharge_max() {
        return recharge_max;
    }

    public void setRecharge_max(String recharge_max) {
        this.recharge_max = recharge_max;
    }

    public String getRecharge_min() {
        return recharge_min;
    }

    public void setRecharge_min(String recharge_min) {
        this.recharge_min = recharge_min;
    }

    public int getSupport_withhold() {
        return support_withhold;
    }

    public void setSupport_withhold(int support_withhold) {
        this.support_withhold = support_withhold;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee_rate() {
        return fee_rate;
    }

    public void setFee_rate(String fee_rate) {
        this.fee_rate = fee_rate;
    }

    public String getFund_code() {
        return fund_code;
    }

    public void setFund_code(String fund_code) {
        this.fund_code = fund_code;
    }

    public String getBankIdCode() {
        return bankIdCode;
    }

    public void setBankIdCode(String bankIdCode) {
        this.bankIdCode = bankIdCode;
    }

    public String getCapitalMode() {
        return capitalMode;
    }

    public void setCapitalMode(String capitalMode) {
        this.capitalMode = capitalMode;
    }

    public int getSupport_redeemf() {
        return support_redeemf;
    }

    public void setSupport_redeemf(int support_redeemf) {
        this.support_redeemf = support_redeemf;
    }
}
