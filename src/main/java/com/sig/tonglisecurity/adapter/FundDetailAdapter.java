package com.sig.tonglisecurity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.FundDetailBean;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * 基金信息适配器
 */
public class FundDetailAdapter extends BaseAdapter {

    public static final String TAG = "FundDetailAdapter";
    public boolean isLoad = false;
    List x = new ArrayList();
    List y = new ArrayList();
    GraphicalView chart;
    private Activity context;
    private LayoutInflater mInflater;
    private RelativeLayout fund_ten_view, fund_ten_z_view;
    private TextView manager_name, fund_code, count, total_count;
    private TextView date, state, fund_type, num, type;
    private TextView toggle_txt;
    private TextView rate_now, rate_last, row_now, row_last;
    private ImageView star_01, star_02, star_03, star_04, star_05;
    private LinearLayout parent;
    private OnClickListener mOnClick;
    private FundDetailBean fund;

    /**
     * 基金信息 Adapter 构造器
     *
     * @param fund     　基金类型
     * @param context  上下文
     * @param mOnClick 点击监听器
     */
    public FundDetailAdapter(FundDetailBean fund, Activity context, OnClickListener mOnClick) {
        super();

        LogUtil.i(TAG, "FundDetailAdapter 构造器");

        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mOnClick = mOnClick;
        this.fund = fund;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup p) {
        convertView = mInflater.inflate(R.layout.item_list_fund_detail_view, null);
        fund_ten_view = (RelativeLayout) convertView.findViewById(R.id.fund_ten_view);
        fund_ten_z_view = (RelativeLayout) convertView.findViewById(R.id.fund_ten_z_view);
        fund_ten_view.setOnClickListener(mOnClick);
        fund_ten_z_view.setOnClickListener(mOnClick);

        manager_name = (TextView) convertView.findViewById(R.id.manager_name);
        fund_code = (TextView) convertView.findViewById(R.id.fund_code);
        count = (TextView) convertView.findViewById(R.id.count);
        total_count = (TextView) convertView.findViewById(R.id.total_count);
        date = (TextView) convertView.findViewById(R.id.date);
        state = (TextView) convertView.findViewById(R.id.state);
        fund_type = (TextView) convertView.findViewById(R.id.fund_type);
        num = (TextView) convertView.findViewById(R.id.num);
        type = (TextView) convertView.findViewById(R.id.type);
        toggle_txt = (TextView) convertView.findViewById(R.id.toggle_txt);
        toggle_txt.setOnClickListener(mOnClick);

        star_01 = (ImageView) convertView.findViewById(R.id.star_01);
        star_02 = (ImageView) convertView.findViewById(R.id.star_02);
        star_03 = (ImageView) convertView.findViewById(R.id.star_03);
        star_04 = (ImageView) convertView.findViewById(R.id.star_04);
        star_05 = (ImageView) convertView.findViewById(R.id.star_05);

        rate_now = (TextView) convertView.findViewById(R.id.rate_now);
        rate_last = (TextView) convertView.findViewById(R.id.rate_last);
        row_now = (TextView) convertView.findViewById(R.id.row_now);
        row_last = (TextView) convertView.findViewById(R.id.row_last);

        parent = (LinearLayout) convertView.findViewById(R.id.chart_parent);
        fund_code.setText(fund_code.getText() + fund.fund_code);

        if (fund.manager.equals("")) {
            manager_name.setText(manager_name.getText() + "--");
        } else {
            manager_name.setText(manager_name.getText() + fund.manager);
        }
        if (fund.netvalue.equals("")) {
            count.setText(count.getText() + "--");
        } else {
            count.setText(count.getText() + fund.netvalue);
        }
        if (fund.netvalue_total.equals("")) {
            total_count.setText(total_count.getText() + "--");
        } else {
            total_count.setText(total_count.getText() + fund.netvalue_total);
        }
        if (fund.found_date.equals("")) {
            date.setText(date.getText() + "--");
        } else {
            date.setText(date.getText() + fund.found_date);
        }
        if (fund.scale.equals("")) {
            state.setText(state.getText() + "--");
        } else {
            state.setText(state.getText() + ConfigUtil.formatDouble(fund.scale, 2) + "��Ԫ");
        }
        if (fund.type.equals("")) {
            fund_type.setText(fund_type.getText() + "--");
        } else {
            fund_type.setText(fund_type.getText() + ConfigUtil.getFundTypeTxtComplete(fund.type));
        }
        if (fund.attention_num.equals("")) {
            num.setText(num.getText() + "--");
        } else {
            num.setText(num.getText() + fund.attention_num);
        }
        if (fund.rate_thisyear.equals("")) {
            rate_now.setText("--");
        } else {
            rate_now.setText(fund.rate_thisyear + "%");
        }
        if (fund.rate_nearyear.equals("")) {
            rate_last.setText("--");
        } else {
            rate_last.setText(fund.rate_nearyear + "%");
        }
        if (fund.rank_thisyear.equals("")) {
            row_now.setText("--");
        } else {
            row_now.setText(fund.rank_thisyear);
        }
        if (fund.rank_nearyear.equals("")) {
            row_last.setText("--");
        } else {
            row_last.setText(fund.rank_nearyear);
        }
        int star = Integer.parseInt(fund.level);
        switch (star) {
            case 1:
                star_01.setBackgroundResource(R.drawable.star_s);
                break;
            case 2:
                star_01.setBackgroundResource(R.drawable.star_s);
                star_02.setBackgroundResource(R.drawable.star_s);
                break;
            case 3:
                star_01.setBackgroundResource(R.drawable.star_s);
                star_02.setBackgroundResource(R.drawable.star_s);
                star_03.setBackgroundResource(R.drawable.star_s);
                break;
            case 4:
                star_01.setBackgroundResource(R.drawable.star_s);
                star_02.setBackgroundResource(R.drawable.star_s);
                star_03.setBackgroundResource(R.drawable.star_s);
                star_04.setBackgroundResource(R.drawable.star_s);
                break;
            case 5:
                star_01.setBackgroundResource(R.drawable.star_s);
                star_02.setBackgroundResource(R.drawable.star_s);
                star_03.setBackgroundResource(R.drawable.star_s);
                star_04.setBackgroundResource(R.drawable.star_s);
                star_05.setBackgroundResource(R.drawable.star_s);
                break;

            default:
                break;
        }
        if (fund.type.equals("5") || fund.type.equals("6")) {
            fund_ten_view.setVisibility(View.GONE);
            fund_ten_z_view.setVisibility(View.VISIBLE);
        } else {
            fund_ten_view.setVisibility(View.VISIBLE);
            fund_ten_z_view.setVisibility(View.GONE);
        }
        if (!isLoad) {
            return convertView;
        }

        x.clear();
        y.clear();
        int days_count = fund.mNetvalueFivedaysBean.days.size();
        double[] days = new double[days_count];
        for (int i = 0; i < days_count; i++) {
            days[i] = i + 1;
        }
        x.add(days);
        double[] netvalues = new double[days_count];
        for (int i = 0; i < days_count; i++) {
            netvalues[i] = Double.parseDouble(fund.mNetvalueFivedaysBean.netvalues.get(days_count - 1 - i));
        }
        y.add(netvalues);
        initXYLine();

        return convertView;
    }

    @SuppressLint("ResourceAsColor")
    private void initXYLine() {
        String[] titles = new String[]{"��λ��ֵ"};
        int values_count = ((double[]) x.get(0)).length;
        XYMultipleSeriesDataset dataset = buildDataset(titles, x, y);
        int[] colors = new int[]{context.getResources().getColor(
                R.color.k_color)};
        PointStyle[] styles = new PointStyle[]{PointStyle.POINT};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);
        renderer.setMarginsColor(context.getResources().getColor(
                R.color.k_bg));
        if (values_count > 5) {
            for (int i = 0; i < values_count; i++) {
                int x_1 = 0;
                int x_2 = (values_count / 2) / 2;
                int x_3 = values_count / 2;
                int x_4 = (values_count + x_3) / 2;
                int x_5 = values_count - 1;
                if (i == x_1
                        || i == x_2
                        || i == x_3
                        || i == x_4
                        || i == x_5) {
                    renderer.addXTextLabel(i + 1, fund.mNetvalueFivedaysBean.days.get(values_count - i - 1));//����x���������ʾ
                } else {
                    renderer.addXTextLabel(i + 1, "");
                }
            }
        } else {
            for (int i = 0; i < values_count; i++) {
                renderer.addXTextLabel(i + 1, fund.mNetvalueFivedaysBean.days.get(values_count - i - 1));//����x���������ʾ
            }
        }
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setShowGrid(true);
        renderer.setXLabels(0);
        renderer.setLabelsTextSize(ConfigUtil.getLabelsTextSize(context));
        renderer.setLegendTextSize(10);
        renderer.setLegendHeight(20);
        renderer.setMargins(new int[]{0, 60, 20, 60});
        renderer.setShowAxes(true);
        double score[] = new double[values_count];
        score = ConfigUtil.bubbleSort((double[]) y.get(0));
        renderer.setInScroll(true);
        renderer.setZoomEnabled(false, false);
        renderer.setPanEnabled(false, false);

        setChartSettings(renderer, "", "", "", 1, values_count + 1, score[values_count - 1] - 0.01,
                score[0] + 0.01, Color.BLACK, Color.BLACK);
        chart = ChartFactory.getLineChartView(context, dataset, renderer);
        parent.removeAllViews();
        parent.addView(chart);
        parent.setOnClickListener(mOnClick);
    }

    private XYMultipleSeriesDataset buildDataset(String[] titles, List xValues,
                                                 List yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = 1;
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i]);
            double[] xV = (double[]) xValues.get(i);
            double[] yV = (double[]) yValues.get(i);
            int seriesLength = xV.length;

            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYMultipleSeriesRenderer buildRenderer(int[] colors,
                                                   PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = 1;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            r.setLineWidth(2.5f);
            r.setShowLegendItem(true);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String title, String xTitle, String yTitle, double xMin,
                                    double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setYLabelsPadding(5);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setXLabelsColor(labelsColor);
        renderer.setYLabelsColor(0, labelsColor);
        renderer.setXLabelsAlign(Align.LEFT);
    }
}
