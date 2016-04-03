package com.sig.tonglisecurity.bean;

public class FundTitleBean {
    public boolean isSelected = false;
    public String name = "";
    public int type_sort;

    public FundTitleBean(boolean isSelected, String name, int type_sort) {
        super();
        this.isSelected = isSelected;
        this.name = name;
        this.type_sort = type_sort;
    }


}
