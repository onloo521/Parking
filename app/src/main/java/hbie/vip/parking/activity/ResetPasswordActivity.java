package hbie.vip.parking.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import hbie.vip.parking.R;
import hbie.vip.parking.app.ExitApplication;
import hbie.vip.parking.bean.UserInfo;
import hbie.vip.parking.net.UserRequest;
import hbie.vip.parking.utils.LogUtils;
import hbie.vip.parking.utils.NetBaseUtils;
import hbie.vip.parking.utils.ToastUtils;

/**
 * 修改密码
 *
 * @author Administrator
 *
 */
public class ResetPasswordActivity extends Activity implements View.OnClickListener {
    private Button btn_submit;
    private RelativeLayout back;
    private EditText et_pwd, et_pwd_affirm;
    private static final int KEY = 1;
    private Context mContext;
    private UserInfo user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        ExitApplication.getInstance().addActivity(this);
        mContext = this;
        initView();
    }

    // 初始化
    private void initView() {
        back = (RelativeLayout) findViewById(R.id.relativeLayout_reset_password_back);
        back.setOnClickListener(this);
        btn_submit = (Button) findViewById(R.id.btn_reset_password_submit);
        btn_submit.setOnClickListener(this);
        et_pwd = (EditText) findViewById(R.id.et_reset_password);
        et_pwd_affirm = (EditText) findViewById(R.id.et_reset_password_affirm);
        // 监听多个输入框
        user = new UserInfo();
        dialog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_password_submit:
                if (isCanDo()) {
                    String pwd = et_pwd_affirm.getText().toString();
                    dialog.setMessage("正在修改...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    if (NetBaseUtils.isConnnected(mContext)) {
                        new UserRequest(mContext, handler).updateUserPassWord(pwd, KEY);
                    }else{
                        ToastUtils.ToastShort(mContext, "网络问题，请稍后重试！");
                    }
                }
                break;
            case R.id.relativeLayout_reset_password_back:
                finish();
                break;
        }
    }

    private Boolean isCanDo() {
        String pwd=et_pwd.getText().toString();
        String pwd_affirm=et_pwd_affirm.getText().toString();
        if (pwd.isEmpty()||pwd_affirm.isEmpty()) {
            ToastUtils.ToastLong(mContext, "请将两处密码填写完整！");
            return false;
        }
        if (!pwd_affirm.equals(pwd)) {
            ToastUtils.ToastLong(mContext, "两处密码不相同！");
            return false;
        }
        return true;
    }
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEY:
                    if (msg.obj != null) {
                        try {
                            String result = (String) msg.obj;
                            JSONObject json = new JSONObject(result);
                            String state = json.getString("status");
                            LogUtils.i("UserData", "---->" + json.toString());
                            if (state.equals("success")) {
                                user.setUserPassword(et_pwd_affirm.getText().toString());
                                user.writeData(mContext);
                                dialog.setMessage("修改成功！");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                dialog.dismiss();
                                finish();
                            }else{
                                dialog.setMessage("修改失败！请与服务商联络");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
            }
        };
    };
}