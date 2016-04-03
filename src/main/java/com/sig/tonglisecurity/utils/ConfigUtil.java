package com.sig.tonglisecurity.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.Display;


import com.sig.tonglisecurity.bean.FundBean;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 配置参数工具类
 * 封装了一些字符串转换的方法
 */
@SuppressLint("SimpleDateFormat")
public class ConfigUtil {

    public static final String TAG = "ConfigUtil";
    private static final String v_1 = "股";
    private static final String v_2 = "混";
    private static final String v_3 = "指";
    private static final String v_4 = "QD";
    private static final String v_5 = "债";
    private static final String v_6 = "货";
    private static final String v_7 = "保";
    private static final String v_1_c = "股票型";
    private static final String v_2_c = "混合型";
    private static final String v_3_c = "指数型";
    private static final String v_4_c = "QDII";
    private static final String v_5_c = "债券型";
    private static final String v_6_c = "货币型";
    private static final String v_7_c = "保本型";

    public static int getLabelsTextSize(Activity context) {
        int t_size = 12;
        if (getDisplayW(context) <= 480) {
            t_size = 8;
        }
        return t_size;
    }

    @SuppressWarnings({"deprecation"})
    public static int getDisplayW(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    public static int getTabPix(Activity context) {
        return getDisplayW(context) / 4;
    }

    public static String getNowTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        return dateFormat.format(now);
    }

    public static String getRefreshTime(String date) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        String str = dateFormat.format(now);
        try {
            if (date == null || date.equals("")) {
                dateFormat = new SimpleDateFormat("HH:mm:ss");
                str = dateFormat.format(now);
                return "";
            }
            String[] p = date.split(" ");
            String days[] = p[0].split("-");
            String m_b = days[0];
            String d_b = days[1];


            String[] p_ = date.split(" ");
            String days_n[] = p_[0].split("-");
            String m_n = days_n[0];
            String d_n = days_n[1];
            if (m_b.equals("m_n")) {
                int margin = Integer.parseInt(d_n) - Integer.parseInt(d_b);
                dateFormat = new SimpleDateFormat("HH:mm:ss");
                str = dateFormat.format(now);
                if (margin == 0) {
                    return "今天 " + str;
                } else if (margin == 1) {
                    return "昨天 " + str;
                } else if (margin == 2) {
                    return "前天 " + str;
                } else {
                    dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                    str = dateFormat.format(now);
                    return str;
                }
            } else {
                dateFormat = new SimpleDateFormat("HH:mm:ss");
                str = dateFormat.format(now);
                return "今天  " + str;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
        return str;
    }

    public static String getNowDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(now);
    }

    public static String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String getImei(Context app) {
        TelephonyManager telephonyManager = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getPhoneNumber(Context app) {

        TelephonyManager telephonyManager = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    public static int getVersionCode(Context app) {
        PackageInfo packageInfo;
        try {
            packageInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getVersionName(Context app) {
        PackageInfo packageInfo;
        try {
            packageInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFundTypeTxt(String type) {
        if (type.equals("1")) {
            return v_1;
        } else if (type.equals("2")) {
            return v_2;
        } else if (type.equals("3")) {
            return v_3;
        } else if (type.equals("4")) {
            return v_4;
        } else if (type.equals("5")) {
            return v_5;
        } else if (type.equals("6")) {
            return v_6;
        } else if (type.equals("7")) {
            return v_7;
        } else {
            return v_1;
        }
    }

    public static String getFundTypeTxtComplete(String type) {
        if (type.equals("1")) {
            return v_1_c;
        } else if (type.equals("2")) {
            return v_2_c;
        } else if (type.equals("3")) {
            return v_3_c;
        } else if (type.equals("4")) {
            return v_4_c;
        } else if (type.equals("5")) {
            return v_5_c;
        } else if (type.equals("6")) {
            return v_6_c;
        } else if (type.equals("7")) {
            return v_7_c;
        } else {
            return v_1_c;
        }
    }

    public static String formatDouble(String str, int numCount) {
        try {
            String[] array = str.split("\\.");
            if (array.length > 1) {
                if (array[1].length() <= numCount) {
                    int c = numCount - array[1].length();
                    for (int i = 0; i < c; i++) {
                        array[1] += "0";
                    }
                } else {
                    array[1] = array[1].substring(0, numCount);
                }
            } else {
                str += ".";
                for (int i = 0; i < numCount; i++) {
                    str += "0";
                }
                return str;
            }
            return array[0] + "." + array[1];

        } catch (Exception e) {
            return "0.000";
        }
    }

    public static double[] bubbleSort(double[] list) {
        double score[] = list;
        for (int i = 0; i < list.length; i++) {
            score[i] = list[i];
        }

        for (int i = 0; i < score.length - 1; i++) {
            for (int j = 0; j < score.length - i - 1; j++) {
                if (score[j] < score[j + 1]) {
                    double temp = score[j];
                    score[j] = score[j + 1];
                    score[j + 1] = temp;
                }
            }
        }
        return score;
    }

    public static List<FundBean> bubbleSort(List<FundBean> entitys) {
        List<FundBean> beans = new ArrayList<FundBean>();
        FundBean[] list = new FundBean[entitys.size()];
        for (int i = 0; i < entitys.size(); i++) {
            list[i] = entitys.get(i);
        }
        for (int i = 0; i < list.length - 1; i++) {    //�����n-1������
            for (int j = 0; j < list.length - i - 1; j++) {    //�Ե�ǰ�������score[0......length-i-1]��������(j�ķ�Χ�ܹؼ������Χ��������С��)
                if (Double.valueOf(list[j].show_value) < Double.valueOf(list[j + 1].show_value)) {    //��С��ֵ����������
                    FundBean temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < list.length; i++) {
            beans.add(list[i]);
        }

        return beans;
    }

    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
        return false;
    }

    public static String digitUppercase(String digital) {
        String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String s = "";
        digital = digital.replaceFirst("0*", "");
        int dotPosition = digital.indexOf(".");

        if (dotPosition == -1) {
            dotPosition = digital.length();
        } else {
            if (digital.length() > dotPosition + 1) {
                int jiao = Integer.parseInt(digital.substring(dotPosition + 1, dotPosition + 2));
                if (jiao != 0) {
                    s = digit[jiao] + "角";
                }
            }
            if (digital.length() > dotPosition + 2) {
                int fen = Integer.parseInt(digital.substring(dotPosition + 2, dotPosition + 3));
                if (fen != 0) {
                    s += digit[fen] + "分";
                }
            }
        }

        //以下处理整数部分
        if (s.equals("")) {
            s = "元整";
        }
        String unit[] = {"元", "万", "亿"};
        String intPart;
        if (dotPosition > 12) {
            return "万亿+";
        } else {
            intPart = digital.substring(0, dotPosition);
        }
        int length = intPart.length();
        for (int i = 0; length - i * 4 - 4 >= 0; i++) {
            String sub = intPart.substring(length - i * 4 - 4, length - i * 4);
            String subChinese4 = getChinese4(sub);
            if (!subChinese4.equals("")) {
                s = subChinese4 + unit[i] + s;
            }
        }
        if (length % 4 != 0) {
            String subChinese4 = getChinese4(intPart.substring(0, length % 4));
            if (!subChinese4.equals("")) {
                s = subChinese4 + unit[length / 4] + s;
            }
        }
        s = s.replace("元元", "元");
        if (s.substring(0, 1).equals("零")) {
            s = s.substring(1);
        }
        if (s.equals("元整")) {
            return "零元整";
        }
        return s;
    }

    private static String getChinese4(String char4) {
        String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String unit[] = {"", "拾", "佰", "仟"};
        String chinese = "";
        int length = char4.length();
        for (int i = 0; i < length; i++) {
            int number = Integer.parseInt(char4.substring(length - i - 1, length - i));
            if (number != 0) {
                chinese = digit[number] + unit[i] + chinese;
            } else {
                if (!chinese.equals("") && !chinese.substring(0, 1).equals("零")) {
                    chinese = digit[number] + chinese;
                }
            }
        }
        return chinese;
    }

    public static String formatDate(String date) {
        try {
            StringBuilder str = new StringBuilder();
            str.append(date.subSequence(0, 4)).append("-")
                    .append(date.subSequence(4, 6)).append("-")
                    .append(date.subSequence(6, 8));
            return str.toString();
        } catch (Exception e) {
            return date;
        }
    }

    public static String formatTime(String time) {
        try {
            StringBuilder str = new StringBuilder();
            str.append(time.subSequence(0, 2)).append(":")
                    .append(time.subSequence(2, 4)).append(":")
                    .append(time.subSequence(4, 6));
            return str.toString();
        } catch (Exception e) {
            return time;
        }
    }

    public static String getHidedAccount(String account) {
        try {
            int headLength = 6;
            int tailLength = 4;
            String head = account.substring(0, headLength);
            String tail = account.substring(account.length() - tailLength,
                    account.length());
            String hide = "";
            for (int i = 0; i < account.length() - headLength - tailLength && i < 6; i++) {
                hide += "*";
            }
            return head + hide + tail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormatAmount(String amount) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        return format.format(Double.parseDouble(amount));
    }

    /**
     * 数字半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        if (input.contains("0")) {
            input = input.replace("0", "０");
        }
        if (input.contains("1")) {
            input = input.replace("1", "１");
        }
        if (input.contains("2")) {
            input = input.replace("2", "２");
        }
        if (input.contains("3")) {
            input = input.replace("3", "３");
        }
        if (input.contains("4")) {
            input = input.replace("4", "４");
        }
        if (input.contains("5")) {
            input = input.replace("5", "５");
        }
        if (input.contains("6")) {
            input = input.replace("6", "６");
        }
        if (input.contains("7")) {
            input = input.replace("7", "７");
        }
        if (input.contains("8")) {
            input = input.replace("8", "８");
        }
        if (input.contains("9")) {
            input = input.replace("9", "９");
        }
        return input;
    }

    public static String ToDBC_(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String StringFilter(String str) throws PatternSyntaxException {
        str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");//替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
