package hbie.vip.parking.activity.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
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

import static hbie.vip.parking.bean.car.Car.CarData;

/**
 * Created by mac on 16/5/16.
 */
public class UpdateCarActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout back;
    private ProgressDialog dialog;
    private Context mContext;
    private String addBrand,addNumber,addType,addEngine;
    private EditText editBrand,editNumber,editType,editEngine;
    private Button btnSave;
    private static final int ADD_KEY=1;
    private static final int SET_CURRENT_CAR_KEY=2;
    private static final  int UN_BIND_CAR_KEY=3;
    private UserInfo user;
    private Bundle bundle;
    private CarData carinfo = null;
    PopupMenu popupMenu;
    Menu menu;
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_KEY:
                    String data = (String) msg.obj;
                    try {
                        JSONObject json = new JSONObject(data);
                        String state=json.getString("status");
                        if (state.equals("success")) {
                            ToastUtils.ToastShort(mContext, "提交成功！");
                            finish();
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
                            user.setCurrentcar(carinfo.getCarnumber());
                            user.setUserCurrentCarType(carinfo.getCartype());
                            user.setUserCurrentCarEnginenumber(carinfo.getEnginenumber());
                            user.setUserCurrentCarBrand(carinfo.getBrand());
                            user.writeData(mContext);
                            ToastUtils.ToastShort(mContext, "提交成功！"+user.getCurrentcar());
                            finish();
                        }else{
                            ToastUtils.ToastShort(mContext, json.getString("data"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UN_BIND_CAR_KEY:
                    String undata = (String) msg.obj;
                    try {
                        JSONObject json = new JSONObject(undata);
                        String state=json.getString("status");
                        if (state.equals("success")) {
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
        setContentView(R.layout.update_add_car_data);
        ExitApplication.getInstance().addActivity(this);
//        bundle = getIntent().getExtras();
        Intent intent = this.getIntent();
        carinfo = (CarData)intent.getSerializableExtra("carinfo");
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
        user.readData(mContext);
        if(carinfo!=null){
            editBrand.setText(carinfo.getBrand());
            editEngine.setText(carinfo.getEnginenumber());
            editType.setText(carinfo.getCartype());
            editNumber.setText(carinfo.getCarnumber());
        }
        btnSave=(Button)findViewById(R.id.btn_save_submit);
        btnSave.setOnClickListener(this);
        popupMenu = new PopupMenu(this, findViewById(R.id.popupmenu_btn));
        menu = popupMenu.getMenu();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popupmenu, menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.setcurrent:
                        if (NetBaseUtils.isConnnected(mContext)) {
                            new UserRequest(mContext, handler).updateMemberCurrentCar(user.getUserId(),carinfo.getCarnumber(), SET_CURRENT_CAR_KEY);
                        }
                        break;
                    case R.id.unbind:
                        if (NetBaseUtils.isConnnected(mContext)) {
                            new UserRequest(mContext, handler).unBindCar(user.getUserId(),carinfo.getCarnumber(), UN_BIND_CAR_KEY);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
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
    // 监听事件

        public void popupmenu(View v) {
        popupMenu.show();
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
            Toast.makeText(getApplicationContext(), "请填写车牌号！", Toast.LENGTH_SHORT).show();
        }else if(addBrand ==null || addBrand.equals("")){
            ToastUtils.ToastShort(mContext, "请填写品牌！");
        }else if(addType ==null || addType.equals("")){
            ToastUtils.ToastShort(mContext, "请填写车型！");
        }else if(addEngine ==null || addEngine.equals("")){
            ToastUtils.ToastShort(mContext, "请填写发动机号！");
        }
        else {

            dialog = new ProgressDialog(UpdateCarActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("正在提交....");
            dialog.show();
            dialog.dismiss();
            user.readData(mContext);
            new UserRequest(mContext, handler).updateCar(user.getUserId(),carinfo.getId(), addNumber,addBrand,addType,addEngine, ADD_KEY);
        }
    }
}

