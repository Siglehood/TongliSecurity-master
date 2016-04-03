package com.sig.tonglisecurity.utils;



import com.sig.tonglisecurity.bean.Capital;
import com.sig.tonglisecurity.bean.Profit;
import com.sig.tonglisecurity.bean.Trade;

import java.util.List;

/**
 * 排序工具类
 */
public class SortUtil {

    public static final String TAG = "SortUtil";

    public static void sortCapitals(List<Object> list, String attributeName,
                                    Order order) {
        double[] array = new double[list.size()];
        if (attributeName.equals("fund_share")) {
            for (int i = 0; i < list.size(); i++) {
                array[i] = Double.parseDouble(((Capital) list.get(i)).getFund_share());
            }
        } else if (attributeName.equals("fund_capital")) {
            for (int i = 0; i < list.size(); i++) {
                array[i] = Double.parseDouble(((Capital) list.get(i)).getFund_capital());
            }
        } else {
            return;
        }
        Capital temp;
        double tempd;
        boolean swaped = false;
        switch (order) {
            case ASC:
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i; j++) {
                        if (array[j + 1] < array[j]) {
                            temp = (Capital) list.get(j + 1);
                            list.set(j + 1, list.get(j));
                            list.set(j, temp);
                            tempd = array[j + 1];
                            array[j + 1] = array[j];
                            array[j] = tempd;
                            swaped = true;
                        }
                    }
                    if (!swaped) {
                        return;
                    }
                }
                break;
            case DESC:
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i; j++) {
                        if (array[j + 1] > array[j]) {
                            temp = (Capital) list.get(j + 1);
                            list.set(j + 1, list.get(j));
                            list.set(j, temp);
                            tempd = array[j + 1];
                            array[j + 1] = array[j];
                            array[j] = tempd;
                            swaped = true;
                        }
                    }
                    if (!swaped) {
                        return;
                    }
                }
                break;
            default:
                break;
        }

    }

    public static void sortTrades(List<Object> list, String attributeName,
                                  Order order) {
        LogUtil.i(TAG, "sortTrades");
        double[] array = new double[list.size()];
        if (attributeName.equals("confirm_amount")) {
            for (int i = 0; i < list.size(); i++) {
                array[i] = Double.parseDouble(((Trade) list.get(i)).getConfirm_amount());
            }
        } else {
            return;
        }
        Trade temp;
        double tempd;
        boolean swaped = false;
        switch (order) {
            case ASC:
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i; j++) {
                        if (array[j + 1] < array[j]) {
                            temp = (Trade) list.get(j + 1);
                            list.set(j + 1, list.get(j));
                            list.set(j, temp);
                            tempd = array[j + 1];
                            array[j + 1] = array[j];
                            array[j] = tempd;
                            swaped = true;
                        }
                    }
                    if (!swaped) {
                        return;
                    }
                }
                break;
            case DESC:
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i; j++) {
                        if (array[j + 1] > array[j]) {
                            temp = (Trade) list.get(j + 1);
                            list.set(j + 1, list.get(j));
                            list.set(j, temp);
                            tempd = array[j + 1];
                            array[j + 1] = array[j];
                            array[j] = tempd;
                            swaped = true;
                        }
                    }
                    if (!swaped) {
                        return;
                    }
                }
                break;
            default:
                break;
        }

    }

    public static void sortProfits(List<Object> list, String attributeName,
                                   Order order) {
        double[] array = new double[list.size()];
        if (attributeName.equals("profit")) {
            for (int i = 0; i < list.size(); i++) {
                array[i] = Double.parseDouble(((Profit) list.get(i)).getProfit());
            }
        } else if (attributeName.equals("profit_rate")) {
            for (int i = 0; i < list.size(); i++) {
                array[i] = Double.parseDouble(((Profit) list.get(i)).getProfit_rate2());
            }
        } else {
            return;
        }
        Profit temp;
        boolean swaped = false;
        double tempd;
        switch (order) {
            case ASC:
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i; j++) {
                        if (array[j + 1] < array[j]) {
                            temp = (Profit) list.get(j + 1);
                            list.set(j + 1, list.get(j));
                            list.set(j, temp);
                            tempd = array[j + 1];
                            array[j + 1] = array[j];
                            array[j] = tempd;
                            swaped = true;
                        }
                    }
                    if (!swaped) {
                        return;
                    }
                }
                break;
            case DESC:
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i; j++) {
                        if (array[j + 1] > array[j]) {
                            temp = (Profit) list.get(j + 1);
                            list.set(j + 1, list.get(j));
                            list.set(j, temp);
                            tempd = array[j + 1];
                            array[j + 1] = array[j];
                            array[j] = tempd;
                            swaped = true;
                        }
                    }
                    if (!swaped) {
                        return;
                    }
                }
                break;
            default:
                break;
        }

    }

    public enum Order {
        ASC, DESC, DEFAULT
    }
}
