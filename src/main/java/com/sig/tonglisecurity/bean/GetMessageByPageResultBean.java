package com.sig.tonglisecurity.bean;

import java.util.ArrayList;
import java.util.List;

public class GetMessageByPageResultBean {
    public String state = "";
    public int currentPage;
    public int totalPages;

    public List<MessageBean> msgList = new ArrayList<MessageBean>();
}
