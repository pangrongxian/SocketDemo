package com.example.prx.socketdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.prx.socketdemo.R;
import com.example.prx.socketdemo.entity.Msg;

import java.util.ArrayList;
import java.util.List;

public class MsgAdapter extends BaseAdapter {

    private List<Msg> msgList = new ArrayList<Msg>();
    private Context context;
    private LayoutInflater inflater;

    public MsgAdapter (Context context,List<Msg> msgList) {
        this.msgList = msgList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public MsgAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        int viewType = msgList.get(position).getType();
        //数据源
        if (viewType == Msg.TYPE_RECEIVED) {//0
            convertView = inflater.inflate(R.layout.chat_listitem_left, null);
            holder.tv_item_msg = (TextView) convertView.findViewById(R.id.tv_item_msg);
            holder.tv_item_msg.setText((msgList.get(position).getContent()).toString());
        } else if (viewType == Msg.TYPE_SENT) {//1
            convertView = inflater.inflate(R.layout.chat_listitem_right, null);
            holder.tv_item_msg = (TextView) convertView.findViewById(R.id.tv_item_msg);
            holder.tv_item_msg.setText((msgList.get(position).getContent()).toString());
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_item_msg;
    }

}
