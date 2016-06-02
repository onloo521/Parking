package hbie.vip.parking.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import hbie.vip.parking.R;
import hbie.vip.parking.bean.order.order;

/**
 * Created by mac on 16/5/17.
 */
public class OrderListAdapter extends BaseAdapter {
    private List<order.OrderData> orderDataList;
    private LayoutInflater inflater;
    private Context mContext;
    public OrderListAdapter( Context mContext,List<order.OrderData> orderDataList) {
        this.mContext = mContext;
        this.orderDataList = orderDataList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return orderDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final order.OrderData orderData =orderDataList.get(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.order_pay_list,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.car_number.setText(orderDataList.get(position).getCarnumber());
        holder.pay_money.setText(orderDataList.get(position).getMoney());
        holder.btn_car_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                getBundle(position, "orderinfo", UpdateCarActivity.class, "缴费信息");

            }
        });
        return convertView;
    }
    @SuppressWarnings("rawtypes")
    public void getBundle(final int position, String key, Class clazz, String str) {
        order.OrderData orderdataInfo = orderDataList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, orderdataInfo);
        Intent intent = new Intent(mContext, clazz);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
    class ViewHolder{
        TextView car_number,pay_money;
        Button btn_car_pay;
        public ViewHolder(View convertView) {
            car_number = (TextView) convertView.findViewById(R.id.tv_wait_car_number);
            btn_car_pay = (Button) convertView.findViewById(R.id.btn_order_pay);
            pay_money=(TextView)convertView.findViewById(R.id.tv_car_all_pay);
        }
    }
    public static long getTodayZero() {
        Date date = new Date();
        long l = 24*60*60*1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
    }
}
