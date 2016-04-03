package com.sig.tonglisecurity.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.activity.ForgetPasswordActivity;
import com.sig.tonglisecurity.activity.MainActivity;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.LogUtil;

import org.json.JSONObject;

import java.util.Random;

public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    protected Dialog dialog_p;
    private TextView title;
    private TextView left_btn;
    private TextView right_btn;
    private Button login_sign_in;
    private EditText login_number;
    private EditText login_password;
    private CheckBox login_keep_password;
    private Activity context;
    private Spinner mSpinnerIdType;
    private View verification_layout;
    private TextView verification_input;
    private TextView verification_code;
    private HttpRequestAsyncTask.TaskListenerWithState mHttpTaskListener = new HttpRequestAsyncTask.TaskListenerWithState() {

        @Override
        public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
            dismissProgressDialog();
            String req = request.getRequestUrl();
            switch (info.getState()) {
                case STATE_NO_NETWORK_CONNECT:
                    Toast.makeText(context, context.getString(R.string.MemberView_01), Toast.LENGTH_SHORT).show();
                    break;
                case STATE_TIME_OUT:
                    Toast.makeText(context, context.getString(R.string.MemberView_02), Toast.LENGTH_SHORT).show();
                    break;
                case STATE_UNKNOWN:
                    Toast.makeText(context, context.getString(R.string.MemberView_03), Toast.LENGTH_SHORT).show();
                    break;
                case STATE_OK:
                    try {
                        JSONObject jsonObject = new JSONObject(info.getResult());
                        if (req.equals(Urls.URL_acco_login_new)) {  //

                            if (jsonObject.getString("result").equals("1")) {
                                saveCacheAccount();
                                MainActivity.MyAccount.setSession_id(jsonObject
                                        .getString("session_id"));
                                MainActivity.MyAccount.setName(jsonObject
                                        .getString("name"));
                                Intent broadcastIntent = new Intent(
                                        "com.hctforgf.gff.signin");
                                broadcastIntent.putExtra("session_id",
                                        jsonObject.getString("session_id"));
                                broadcastIntent.putExtra("from_wallet_login", true);
                                context.sendBroadcast(broadcastIntent);
                                verification_layout.setVisibility(View.GONE);
                                changeVerification();
                                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                                fragmentManager.popBackStackImmediate("login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            } else {
                                verification_layout.setVisibility(View.VISIBLE);
                                changeVerification();
                                Toast.makeText(context, jsonObject.getString("result"), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
        }
    };
    private OnClickListener mOnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            LogUtil.i(TAG, "onClick");
            switch (view.getId()) {
                case R.id.login_sign_in:

                    if (login_number.getText().toString().equals("")) {
                        showToast(context.getString(R.string.MemberView_01));
                        return;
                    }
                    if (login_password.getText().toString().equals("")) {
                        showToast(context.getString(R.string.MemberView_02));
                        return;
                    }
                    if (verification_layout.getVisibility() == View.VISIBLE && !verification_input.getText().toString().equals(verification_code.getText().toString())) {
                        Toast.makeText(context, context.getString(R.string.MemberView_03), Toast.LENGTH_SHORT).show();
                        changeVerification();
                        return;
                    }
                    login();
                    break;
                case R.id.login_change_verification:
                    changeVerification();
                    break;
                case R.id.left_btn:
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    fragmentManager.popBackStackImmediate("login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    break;
                case R.id.right_btn:
                    break;
                case R.id.login_forget_pasword:
                    getActivity().startActivity(new Intent(context, ForgetPasswordActivity.class));
                    break;
            }
        }
    };
    private View verification_change;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.i(TAG, "LoginFragment----->*******");
        context = getActivity();
        View v = inflater.inflate(R.layout.c_a_member, container, false);
        initBaseView(v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "LoginFragment --> onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化
     *
     * @param v
     */
    public void initBaseView(View v) {
        title = (TextView) v.findViewById(R.id.title_txt);
        title.setText(getString(R.string.login_tab_title));
        left_btn = (TextView) v.findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(mOnClick);
        right_btn = (TextView) v.findViewById(R.id.right_btn);

        login_sign_in = (Button) v.findViewById(R.id.login_sign_in);
        login_sign_in.setOnClickListener(mOnClick);
        login_number = (EditText) v.findViewById(R.id.login_id_number);
        login_password = (EditText) v.findViewById(R.id.login_id_password);
        login_keep_password = (CheckBox) v.findViewById(R.id.login_keep_password);
        login_keep_password.setChecked(true);
        v.findViewById(R.id.login_forget_pasword).setOnClickListener(mOnClick);

        mSpinnerIdType = (Spinner) v.findViewById(R.id.spinner_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mSpinnerIdType.setPadding(10, 0, 0, 0);
        }
        mSpinnerIdType.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                v.getContext(), R.array.id_types, R.layout.my_simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
        mSpinnerIdType.setAdapter(adapter);
        login_number.setText(getCacheAccount());
        mSpinnerIdType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                login_number.setText(getCacheAccount());
                login_password.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initVerificationView(v);
    }

    /**
     * 初始化验证框
     *
     * @param parent
     */
    private void initVerificationView(View parent) {
        verification_layout = parent.findViewById(R.id.login_verification_layout);
        verification_input = (TextView) parent.findViewById(R.id.login_verification_input);
        verification_code = (TextView) parent.findViewById(R.id.login_verification);
        verification_change = parent.findViewById(R.id.login_change_verification);
        verification_change.setOnClickListener(mOnClick);
    }

    /**
     * 修改验证结果
     */
    private void changeVerification() {
        String verification = "";
        Random ran = new Random(System.currentTimeMillis());
        verification += String.valueOf(ran.nextInt(10));
        verification += String.valueOf(ran.nextInt(10));
        verification += String.valueOf(ran.nextInt(10));
        verification += String.valueOf(ran.nextInt(10));
        verification_code.setText(verification);
    }

    /**
     * 返回账号信息
     *
     * @return
     */
    private String getCacheAccount() {
        if (mSpinnerIdType.getSelectedItem() == null) {
            return "";
        }
        SharedPreferences prefer = getActivity().getSharedPreferences("account_history", getActivity().MODE_PRIVATE);
        if (prefer != null) {
            return prefer.getString(mSpinnerIdType.getSelectedItem().toString(), "");
        }
        return "";
    }

    /**
     * 登录
     */
    private void login() {
        showProgressDialog(context, context.getString(R.string.dlg_login));
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_acco_login_new);
        String identity_type = "0";
        switch (mSpinnerIdType.getSelectedItemPosition()) {
            case 0:
                identity_type = "0";
                break;
            case 1:
                identity_type = "7";
                break;
            case 2:
                identity_type = "1";
                break;
            case 3:
                identity_type = "2";
                break;
            case 4:
                identity_type = "3";
                break;
            case 5:
                identity_type = "5";
                break;
            case 6:
                identity_type = "8";
                break;
            case 7:
                identity_type = "9";
                break;
            default:
                break;
        }
        request
                .putParam("identity_type", identity_type)
                .putParam("identity_num", login_number.getText().toString())
                .putParam("password", login_password.getText().toString());
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    private void saveCacheAccount() {
        if (mSpinnerIdType.getSelectedItem() == null) {
            return;
        }
        context.getSharedPreferences("account_history",
                Context.MODE_PRIVATE)
                .edit()
                .putString(
                        mSpinnerIdType.getSelectedItem().toString(),
                        login_keep_password.isChecked() ? login_number
                                .getText().toString() : "")
                .commit();
    }

    /**
     * 显示提示框
     *
     * @param msg
     */
    protected void showToast(String msg) {
        Toast tr = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        tr.setGravity(Gravity.CENTER, 0, 0);
        tr.show();
    }

    /**
     * 显示进度条
     *
     * @param a   上下文对象
     * @param msg 进度条显示文本
     */
    protected void showProgressDialog(Context a, String msg) {
        dialog_p = new ProgressDialog(a);
        ((ProgressDialog) dialog_p)
                .setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ((ProgressDialog) dialog_p).setMessage(msg);
        if (null != dialog_p && !dialog_p.isShowing()) {
            try {
                dialog_p.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭提示框
     */
    protected void dismissProgressDialog() {
        if (null != dialog_p && dialog_p.isShowing()) {
            try {
                dialog_p.dismiss();
                dialog_p = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
