package com.sig.tonglisecurity.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 五日
 */
public class NetvalueFivedaysBean {

    public List<String> days = new ArrayList<String>();
    public List<String> netvalues = new ArrayList<String>();

    @Override
    public String toString() {

        String paramsStr = "";
        for (int i = 0; i < days.size(); i++) {
            if (paramsStr.equals("")) {
                paramsStr += days.get(i);
            } else {
                paramsStr += "%" + days.get(i);
            }
        }
        for (int i = 0; i < netvalues.size(); i++) {
            if (paramsStr.equals("")) {
                paramsStr += netvalues.get(i);
            } else {
                paramsStr += "%" + netvalues.get(i);
            }
        }
        return paramsStr;
    }
}
