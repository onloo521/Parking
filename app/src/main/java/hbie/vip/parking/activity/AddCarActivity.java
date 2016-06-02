package hbie.vip.parking.activity;

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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import hbie.vip.parking.BaseActivity;
import hbie.vip.parking.R;
import hbie.vip.parking.app.ExitApplication;
import hbie.vip.parking.bean.UserInfo;
import hbie.vip.parking.net.UserRequest;
import hbie.vip.parking.utils.NetBaseUtils;
import hbie.vip.parking.utils.ToastUtils;

/**
 * Created by mac on 16/5/13.
 */
public class AddCarActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout back;
    private ProgressDialog dialog;
    private Context mContext;
    private String carId,addBrand,addNumber,addType,addEngine;
    private EditText editBrand,editNumber,editType,editEngine;
    private Button btnSave;
    private static final int ADD_KEY=1;
    private static final int SET_CURRENT_CAR_KEY=2;
    private UserInfo user;
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_KEY:
                    String data = (String) msg.obj;
                    try {
                        JSONObject json = new JSONObject(data);
                        String state=json.getString("status");
                        if (state.equals("success")) {
                            carId = json.getString("data");
                            if (NetBaseUtils.isConnnected(mContext)) {
                                new UserRequest(mContext, handler).updateMemberCurrentCar(user.getUserId(),addNumber, SET_CURRENT_CAR_KEY);
                            }
                        }else{
                            ToastUtils.ToastShort(mContext, json.getString("data"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SET_CURRENT_CAR_KEY:
                    String cardata = (String) msg.obj;
                    try {
                        JSONObject json = new JSONObject(cardata);
                        String state=json.getString("status");
                        if (state.equals("success")) {
                            user.setCurrentcar(addNumber);
                            user.setUserCurrentCarId(carId);
                            user.setUserCurrentCarType(addType);
                            user.setUserCurrentCarEnginenumber(addEngine);
                            user.setUserCurrentCarBrand(addBrand);
                            user.writeData(mContext);
                            ToastUtils.ToastShort(mContext, "提交成功！");
                            finish();
                        }else{
                            ToastUtils.ToastShort(mContext, json.getString("data"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_car_data);
        ExitApplication.getInstance().addActivity(this);
        mContext = this;
        user = new UserInfo();
        dialog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
        initView();
    }
    private void initView() {
        back = (RelativeLayout) findViewById(R.id.relativeLayout_editoril_material_back);
        back.setOnClickListener(this);
        editBrand =(EditText)findViewById(R.id.ed_add_car_brand_text);
        editNumber =(EditText)findViewById(R.id.et_add_car_number_text);
        editType =(EditText)findViewById(R.id.et_add_car_type_text);
        editEngine =(EditText)findViewById(R.id.et_add_car_engine_text);
        btnSave=(Button)findViewById(R.id.btn_save_submit);
        btnSave.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativeLayout_editoril_material_back:
                finish();
                break;
            case R.id.btn_save_submit:
                getSubmitPhone();
//                finish();
                break;
        }
    }
    /**
     * 判断数据 向服务器请求
     */
    private void getSubmitPhone() {

        addBrand=editBrand.getText().toString();
        addNumber=editNumber.getText().toString();
        addType=editType.getText().toString();
        addEngine=editEngine.getText().toString();
        if (addNumber == null || addNumber.equals("")) {
//            editNumber.setError(Html.fromHtml("<font color=#E10979>请输入地址！</font>"));
            Toast.makeText(getApplicationContext(), "请填写车牌号！", Toast.LENGTH_SHORT).show();
        }else if(addBrand ==null || addBrand.equals("")){
            ToastUtils.ToastShort(mContext, "请填写品牌");
        }else if(addType ==null || addType.equals("")){
            ToastUtils.ToastShort(mContext, "请填写车型");
        }else if(addEngine ==null || addEngine.equals("")){
            ToastUtils.ToastShort(mContext, "请填写发动机号");
        }
        else {
//            dialog = new ProgressDialog(AddCarActivity.this);
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setMessage("正在提交....");
//            dialog.show();
//            dialog.dismiss();
            user.readData(mContext);
            new UserRequest(mContext, handler).addCar(user.getUserId(), addNumber,addBrand,addType,addEngine, ADD_KEY);
        }
    }
}
