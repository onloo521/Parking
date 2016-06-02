package hbie.vip.parking.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hbie.vip.parking.R;
import hbie.vip.parking.adapter.OrderListAdapter;
import hbie.vip.parking.bean.UserInfo;
import hbie.vip.parking.bean.order.order;
import hbie.vip.parking.dao.CommunalInterfaces;
import hbie.vip.parking.net.UserRequest;
import hbie.vip.parking.ui.list.PullToRefreshListView;
import hbie.vip.parking.utils.LogUtils;
import hbie.vip.parking.utils.NetBaseUtils;
import hbie.vip.parking.utils.ToastUtils;

/**
 * Created by mac on 16/5/9.
 */
public class InformationFragment  extends Fragment {
    private Activity activity;
    private PullToRefreshListView lv_comprehensive;
    private Context mContext;
    private UserInfo user;
    private SharedPreferences sp;
    private TextView tv_car_current_number, tv_car_park,tv_car_time,tv_car_timelong,tv_car_money;
    private ImageView bt_car_Logo;
    private ListView lv_order;
    private OrderListAdapter orderListAdapter;
    private TextView detail_loading;
    private ArrayList<order.OrderData> orderList;
    private final static int GET_CAR_LOCATION=1;
    private final static int GET_ORDER_LIST=2;

    @SuppressWarnings("static-access")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        sp = mContext.getSharedPreferences("list", mContext.MODE_PRIVATE);
        user = new UserInfo(mContext);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_main, container, false);
        detail_loading = (TextView) view.findViewById(R.id.detail_loading);
        lv_comprehensive = (PullToRefreshListView) view
                .findViewById(R.id.lv_comprehensive);
        lv_comprehensive.setPullLoadEnabled(true);
        lv_comprehensive.setScrollLoadEnabled(false);
        lv_order = lv_comprehensive.getRefreshableView();
        orderList = new ArrayList<order.OrderData>();
        return view;
    }
    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }
    //    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        bt_car_Logo=(ImageView)getView().findViewById(R.id.iv_car_logo);
        tv_car_current_number =(TextView) getView().findViewById(R.id.tv_car_current_info);
        tv_car_money=(TextView)getView().findViewById(R.id.tv_car_money);
        tv_car_park = (TextView)getView().findViewById(R.id.tv_car_park);
        tv_car_time=(TextView)getView().findViewById(R.id.tv_car_time);
        tv_car_timelong = (TextView)getView().findViewById(R.id.tv_car_timelong);
        tv_car_park.setText("暂无");
        tv_car_time.setText("暂无");
        tv_car_time.setText("暂无");
        tv_car_money.setText("暂无");
    }
    @Override
    public void onResume() {
        super.onResume();
        // 获取数据，网络获取数据并保存
        LogUtils.i("UserData", "------->onResume");
        getData();
    }


    /**
     * 获取数据，网络获取数据并保存
     */
    private void getData() {
        user.readData(mContext);
        LogUtils.i("UserData", "------->onResume-->"+user.getUserId() +user.getCurrentcar());

        new UserRequest(mContext, handler).getMemberCurrentCar(user.getUserId(), GET_ORDER_LIST);
        if(user.getCurrentcar()!=null && !user.getCurrentcar().equals("null")){
            bt_car_Logo.setSelected(true);
            tv_car_current_number.setText(user.getUserCurrentCarBrand()+"   "+user.getUserCurrentCarType()+"  "+user.getCurrentcar());
        if (NetBaseUtils.isConnnected(mContext)) {
            new UserRequest(mContext, handler).getCarLocation(user.getUserId(), user.getCurrentcar(), GET_CAR_LOCATION);
        }
        }else{
            bt_car_Logo.setSelected(false);
            tv_car_current_number.setText("暂无");
        }
        if (NetBaseUtils.isConnnected(mContext)) {
            new UserRequest(mContext, handler).getUnpayOrderListByOwner(user.getUserId(), GET_ORDER_LIST);
        }else{
            ToastUtils.ToastShort(mContext, "网络问题，请稍后重试！");
        }
    }
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_CAR_LOCATION:
                    if (msg.obj != null) {
                        String result = (String) msg.obj;
                        JSONObject json;
                        try{
                            json = new JSONObject(result);
                            String state = json.getString("status");
                            String data = json.getString("data");
                            if (state.equals(CommunalInterfaces._STATE)) {
                                JSONObject item = new JSONObject(data);
                                tv_car_park.setText(item.getString("park")+"    "+item.getString("position"));
                                tv_car_time.setText(item.getString("start_time"));
                                tv_car_timelong.setText("2小时");
                                tv_car_money.setText("10元");
                            }else{
                                tv_car_park.setText("暂无");
                                tv_car_time.setText("暂无");
                                tv_car_time.setText("暂无");
                                tv_car_money.setText("暂无");
                            }
                        }catch (JSONException e1){
                            e1.printStackTrace();
                            ToastUtils.ToastShort(mContext, "没有数据！");
                        }
                    }
                    break;
                case GET_ORDER_LIST:
                    if (msg.obj != null) {
                        String result = (String) msg.obj;
                        JSONObject obj;
                        orderList.clear();
                        try {
                            obj = new JSONObject(result);
                            if (obj.optString("status").equals(CommunalInterfaces._STATE)) {
                                user.readData(mContext);
                                if(obj.getString("data")!=null && obj.getString("data")!=""){
                                JSONArray hwArray = obj.getJSONArray("data");
                                if(hwArray != null && hwArray.length()>0) {
                                    JSONObject itemObject=new JSONObject(obj.optString("data"));
                                    for (int i = 0; i < hwArray.length(); i++) {
                                        itemObject = hwArray.optJSONObject(i);
                                        if(itemObject.getString("is_current_car").equals("0")) {
                                            order.OrderData orderData = new order.OrderData();
//                                            orderData.setId(itemObject.getString("id"));
                                            orderData.setCarnumber(user.getCurrentcar());
                                            orderData.setDate(itemObject.getString("date"));
                                            orderData.setTime(itemObject.getString("time"));
                                            orderData.setPrice(itemObject.getString("price"));
                                            orderData.setMoney(itemObject.getString("money"));
                                            orderData.setStatus(itemObject.getString("pay_status"));
                                            orderList.add(orderData);
                                        }
                                    }
                                    orderListAdapter = new OrderListAdapter(activity, orderList);
                                    lv_order.setAdapter(orderListAdapter);
                                }else{
                                    ToastUtils.ToastShort(mContext, "没有数据！");
                                }
                                }
                            }
                        } catch (JSONException e1) {
                                e1.printStackTrace();
                                ToastUtils.ToastShort(mContext, "没有数据！");
                            }
                    }else{
                        ToastUtils.ToastShort(mContext, "没有数据！");
                    }
                    break;
            }
        };
    };
}