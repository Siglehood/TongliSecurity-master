package com.sig.tonglisecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.ConfigUtil;

import java.text.NumberFormat;

/**
 * 快速兑现输入界面
 */
public class RedeemFastInputActivity extends ParentActivity implements OnClickListener, TextWatcher {
    private TextView title;
    private TextView left_btn;
    private Button next_step;
    private EditText redeem_amount;
    private TextView redeem_amount_in_words;
    private CheckBox redeem_read;
    private String beforeTextChanged;
    private int isFormated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_redeem_step2);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.wallet_fast_cash));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        next_step = (Button) findViewById(R.id.next_step);
        next_step.setOnClickListener(this);
        redeem_amount = (EditText) findViewById(R.id.redeem_amount);
        redeem_amount_in_words = (TextView) findViewById(R.id.redeem_amount_in_words);
        redeem_amount.addTextChangedListener(this);

        redeem_read = (CheckBox) findViewById(R.id.redeem_read);

        String htmlLinkText = "����ϸ�Ķ�"
                + "<a style=\"color:red;\" href=\"���ǳ����ӡ���\">���㷢�������ֱ����һ�������ط���Э�顷</a>"
                + "��ͬ���������";
        redeem_read.setText(Html.fromHtml(htmlLinkText));
        redeem_read.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = redeem_read.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) redeem_read.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan();
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp
                        .getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            redeem_read.setText(style);
        }

        redeem_read.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                next_step.setEnabled(isChecked);
            }
        });

        ((TextView) findViewById(R.id.redeem_channel))
                .setText(MainActivity.MyRedeemChannel.getName());
        ((TextView) findViewById(R.id.redeem_avalaibe_amount))
                .setText(ConfigUtil
                        .getFormatAmount(MainActivity.MyRedeemChannel
                                .getAmount()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                if (redeem_amount.getText().toString().equals("")) {
                    Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Double.parseDouble(redeem_amount.getText().toString().replace(",", "")) > Double.parseDouble(MainActivity.MyRedeemChannel.getAmount())) {
                    Toast.makeText(this, "输入的金额超过最大值", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, RedeemFastConfirmActivity.class);
                intent.putExtra("amount", redeem_amount.getText().toString().replace(",", ""));
                startActivity(intent);
                break;
            case R.id.left_btn:
                finish();
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        String str = s.toString();
        try {
            if (!str.equals("") && Double.parseDouble(str.replace(",", "")) > 500000) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        beforeTextChanged = str;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = s.toString().replace(",", "");
        if (str.equals(".")) {
            redeem_amount.setText("0.");
            return;
        }
        try {
            if (!str.equals("") && Double.parseDouble(str) > 500000) {
                redeem_amount.setText(beforeTextChanged);
                Toast.makeText(this, "金额超过 50 万", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String str2 = redeem_amount.getText().toString().replace(",", "");
            if (str2.equals("")) {
                redeem_amount_in_words.setText("");
            } else {
                redeem_amount_in_words.setText(ConfigUtil
                        .digitUppercase(str2));
            }
        } catch (Exception e) {
            Toast.makeText(this, "错误，请重新输入", Toast.LENGTH_SHORT).show();
        }

        int dot = str.indexOf(".");
        if (dot != -1 && dot + 3 < str.length()) {
            str = str.substring(0, dot + 3);
        }
        try {
            if (++isFormated % 2 == 0) {
                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(2);
                if (!str.equals("") && Double.parseDouble(str) > 500000) {
                    redeem_amount.setText(beforeTextChanged);
                    return;
                }
                if (!str.endsWith(".")) {
                    redeem_amount.setText(format.format(Double.parseDouble(str)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private class MyURLSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(context, ForgetPasswordActivity.class);
            intent.putExtra("agreement", true);
            context.startActivity(intent);
        }
    }
}
