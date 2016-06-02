package hbie.vip.parking.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hbie.vip.parking.R;
import hbie.vip.parking.activity.FindCarOrderActivity;
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
public class PayFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout pay, search;
    private Activity activity;
    private PullToRefreshListView lv_comprehensive;
    private Context mContext;
    private UserInfo user;
    private SharedPreferences sp;
    private TextView detail_loading;
    private Button btn_hadpay,btn_hadunpay;
    private ListView lv_order;
    private OrderListAdapter orderListAdapter;
    private ArrayList<order.OrderData> orderList;
    private final static int GET_CAR_LOCATION=1;
    private final static int GET_ORDER_LIST=2;
    private final static int GET_NOPAY_LIST=3;
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        sp = mContext.getSharedPreferences("list", mContext.MODE_PRIVATE);
        user = new UserInfo(mContext);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        search=(RelativeLayout)view.findViewById(R.id.relativeLayout_pay_data_search);
        search.setOnClickListener(this);
        detail_loading = (TextView) view.findViewById(R.id.detail_loading);
        lv_comprehensive = (PullToRefreshListView) view
                .findViewById(R.id.lv_comprehensive);
        lv_comprehensive.setPullLoadEnabled(true);
        lv_comprehensive.setScrollLoadEnabled(false);
        lv_order = lv_comprehensive.getRefreshableView();
        orderList = new ArrayList<order.OrderData>();
        btn_hadpay =(Button) view.findViewById(R.id.showorderinfo_pay);
        btn_hadunpay =(Button) view.findViewById(R.id.showorderinfo_unpay);
        btn_hadpay.setOnClickListener(this);
        btn_hadunpay.setOnClickListener(this);
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

        if (NetBaseUtils.isConnnected(mContext)) {
            new UserRequest(mContext, handler).getUnpayOrderListByOwner(user.getUserId(), GET_ORDER_LIST);
        }else{
            ToastUtils.ToastShort(mContext, "网络问题，请稍后重试！");
        }
    }
//    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showorderinfo_pay:
                btn_hadpay.setTextColor(this.getResources().getColor(R.color.green));
                btn_hadunpay.setTextColor(Color.BLACK);
                if (NetBaseUtils.isConnnected(mContext)) {
                    new UserRequest(mContext, handler).getCarHistoryOrder(user.getUserId(), GET_ORDER_LIST);
                }else{
                    ToastUtils.ToastShort(mContext, "网络问题，请稍后重试！");
                }
                break;
            case R.id.showorderinfo_unpay:
                btn_hadpay.setTextColor(Color.BLACK);
                btn_hadunpay.setTextColor(this.getResources().getColor(R.color.green));
                if (NetBaseUtils.isConnnected(mContext)) {
                    new UserRequest(mContext, handler).getUnpayOrderListByOwner(user.getUserId(), GET_NOPAY_LIST);
                }else{
                    ToastUtils.ToastShort(mContext, "网络问题，请稍后重试！");
                }
                break;
            case R.id.relativeLayout_pay_data_search:
                Intent intent = new Intent(mContext, FindCarOrderActivity.class);
//                intent.putExtras(bundle);
                startActivity(intent);
//                IntentUtils.getIntent(mContext, UpdatePersonalPhoneActivity.class);
                break;
            case R.id.btn_quit_submit:
//                getDialog();
                break;
        }

    }
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_NOPAY_LIST:
                    if (msg.obj != null) {
                        String result = (String) msg.obj;
                        JSONObject obj;
                        orderList.clear();
                        try {
                            obj = new JSONObject(result);
                            if (obj.optString("status").equals(CommunalInterfaces._STATE)) {
                                user.readData(mContext);
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
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            ToastUtils.ToastShort(mContext, "没有数据！");
                        }
                    }else{
                        ToastUtils.ToastShort(mContext, "没有数据！");
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
