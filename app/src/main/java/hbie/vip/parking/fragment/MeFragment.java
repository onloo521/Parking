package hbie.vip.parking.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hbie.vip.parking.R;
import hbie.vip.parking.activity.EditorilPersonalDataActivity;
import hbie.vip.parking.app.ExitApplication;
import hbie.vip.parking.bean.UserInfo;
import hbie.vip.parking.main.LoginActivity;
import hbie.vip.parking.net.UserRequest;
import hbie.vip.parking.net.request.constant.NetBaseConstant;
import hbie.vip.parking.ui.OtherGridView;
import hbie.vip.parking.ui.RoundImageView;
import hbie.vip.parking.utils.HeadPicture;
import hbie.vip.parking.utils.LogUtils;
import hbie.vip.parking.utils.NetBaseUtils;
import hbie.vip.parking.utils.ToastUtils;

/**
 * Created by mac on 16/5/9.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    private IntentFilter myIntentFilter;
    private RelativeLayout clear,upgrade;
    private Button quit;
    private TextView tv_me_apply_zhuangjia;
    private TextView tv_name, tv_phone, tv_position;
    private RoundImageView iv_head;
    private ImageView iv_editor;
    private RelativeLayout iv_clear,iv_upgrade;
    private ImageView iv_first, iv_second, iv_third;

    private OtherGridView gridView;
//    private MeGridAdapter gridAdapter;
//    private ArrayList<TagInfo> tags;
    private Context mContext;
    private LinearLayout ll_tag;
    private UserInfo user;
//    private ArrayList<WorkRingInfo> workRingInfos = new ArrayList<WorkRingInfo>();
    private ArrayList<String> imgS = new ArrayList<String>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private SharedPreferences sp;
    private LinearLayout ll_fans, ll_focus, ll_friend;
    private TextView tv_fans, tv_focus, tv_friend;
    private View v_fans, v_focus;

    private static final int GET_USET_INFO_KEY = 1;
    private static final int SPECIAL_KEY = 3;
    private static final int GET_USER_FRIEND_COUNT = 4;
    private static final int GET_USER_FANS_COUNT = 5;
    private static final int GET_USER_FOCUSKA_COUNT = 6;
    private ProgressDialog dialog;
    private String TAG="MeFragment.class";
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_USET_INFO_KEY:
                    if (msg.obj != null) {
                        String result = (String) msg.obj;
                        JSONObject json;
                        try {
                            json = new JSONObject(result);
                            LogUtils.i("UserData", "---->" + json.toString());
                            String state = json.getString("status");
                            String data = json.getString("data");
                            if (state.equals("success")) {
                                JSONObject item = new JSONObject(data);
                                String userId = item.getString("id");
                                LogUtils.i("UserId", "---->" + userId.toString());
                                user.setUserId(userId);
                                user.setUserName(item.getString("user_name"));
                                user.setUserPhone(item.getString("user_phone"));
                                user.setUserSex(item.getString("sex"));
                                user.setUserImg(item.getString("avatar"));
                                user.setuserAddr(item.getString("addr"));
                                user.setCurrentcar(item.getString("current_car"));
                                user.setUserBranch(item.getString("bank_branch"));
                                LogUtils.i("UserData", "---->setUserPosition" + item.getString("addr"));
                                user.writeData(mContext);
                                user.readData(mContext);
                                LogUtils.i("UserData", "---->getUserPosition" + user.getUserAddr());
                                displayData();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                }).start();

                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                break;
            }
        };
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        sp = mContext.getSharedPreferences("list", mContext.MODE_PRIVATE);
        dialog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
        initView();
    }

    private void initView() {
        iv_head = (RoundImageView) getView().findViewById(R.id.iv_me_fragment_head);
        iv_editor = (ImageView) getView().findViewById(R.id.iv_me_fragment_editer);
        iv_editor.setOnClickListener(this);
        iv_clear=(RelativeLayout)getView().findViewById(R.id.me_clear);
        iv_clear.setOnClickListener(this);
        iv_upgrade =(RelativeLayout)getView().findViewById(R.id.me_upgrade);
        iv_upgrade.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_name = (TextView) getView().findViewById(R.id.tv_fragmentmine_name);
        tv_phone = (TextView) getView().findViewById(R.id.tv_fragmentmine_phone);
        tv_position = (TextView) getView().findViewById(R.id.tv_me_fragment_position);
          quit = (Button) getView().findViewById(R.id.btn_quit_submit);
          quit.setOnClickListener(this);
        user = new UserInfo();
//        // 显示图片的配置
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_square).showImageOnFail(R.drawable.default_square).cacheInMemory(true).cacheOnDisc(true).build();
    }
    @Override
    public void onResume() {
        super.onResume();
        // 获取数据，网络获取数据并保存
        getData();
        // 数据显示
        displayData();
    }
    /**
     * 数据的显示
     */
    private void displayData() {
        user.readData(mContext);
        if (!user.getUserImg().equals("")) {
            LogUtils.i(TAG,NetBaseConstant.NET_HOST + "/" + user.getUserImg());
            imageLoader.displayImage(NetBaseConstant.NET_HOST + "/" + user.getUserImg(), iv_head, options);
        } else {
            new HeadPicture().getHeadPicture(iv_head);
        }
        tv_name.setText(user.getUserName());
        tv_phone.setText(user.getUserPhone());
        tv_position.setText(user.getUserAddr());
        LogUtils.i("MeFragment", "---->getUserPosition" + user.getUserAddr());
    }



    /**
     * 获取数据，网络获取数据并保存
     */
    private void getData() {
        user = new UserInfo(mContext);

    }
    /**
     * 获取用户基本资料
     */
    private void getUserInfo() {
        if (NetBaseUtils.isConnnected(mContext)) {
            new UserRequest(mContext, handler).GetUserInfo(user.getUserId(), GET_USET_INFO_KEY);
        }else{
            ToastUtils.ToastShort(mContext, "网络问题，请稍后重试！");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_me_fragment_head:
                startActivity(new Intent(mContext, EditorilPersonalDataActivity.class));
                break;
            case R.id.iv_me_fragment_editer:
                startActivity(new Intent(mContext, EditorilPersonalDataActivity.class));
                break;
            case R.id.btn_quit_submit:
                getDialog();
                break;
            case R.id.me_clear:
                ToastUtils.ToastShort(mContext, "缓存已清理！");
                break;
            case R.id.me_upgrade:
                ToastUtils.ToastShort(mContext, "当前版本V1.1,已是最新版！");
                break;
        }
    }
    /**
     * 退出确认对话框
     */
    private void getDialog() {
        LayoutInflater inflaterDl = LayoutInflater.from(mContext);
        FrameLayout layout = (FrameLayout) inflaterDl.inflate(
                R.layout.quit_dialog, null);

        // 对话框
        final Dialog dialog = new AlertDialog.Builder(mContext)
                .create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        // 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_ok);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.clearDataExceptPhone(mContext);
                SharedPreferences.Editor e=sp.edit();
                e.clear();
                e.commit();
                startActivity(new Intent(mContext, LoginActivity.class));
//                MeFragment.this.finish();
                ExitApplication.getInstance().exit();
            }
        });

        // 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.dialog_cancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
