package com.example.prx.socketdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.prx.socketdemo.adapter.MsgAdapter;
import com.example.prx.socketdemo.entity.Msg;
import com.example.prx.socketdemo.utils.MyUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAG_Client";

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;

    private Button mSendButton;
    private ListView mMessageListView;
    private EditText mMessageEditText;

    private PrintWriter mPrintWriter;
    private Socket mClientSocket;

    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    private boolean isFirst = true;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_RECEIVE_NEW_MSG:{
                    //设置收到服务端的消息
                    Msg receiverMsg  = new Msg(msg.obj.toString(),Msg.TYPE_RECEIVED);
                    msgList.add(receiverMsg);
                    if (isFirst){
                        adapter = new MsgAdapter(ClientActivity.this,msgList);
                        mMessageListView.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                        mMessageListView.setSelection(mMessageListView.getBottom());
                        isFirst = false;
                    }
                    break;
                }

                case MESSAGE_SOCKET_CONNECTED:{
                    mSendButton.setEnabled(true);
                }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageListView = (ListView) findViewById(R.id.msg_container);
        mSendButton = (Button) findViewById(R.id.send);
        mMessageEditText = (EditText) findViewById(R.id.msg);


        mSendButton.setOnClickListener(this);
        //1.启动服务
        Intent intent = new Intent(this,TCPServiceServer.class);
        startService(intent);
        Log.d(TAG, "启动服务");

        new Thread(){
            @Override
            public void run() {
                //连接服务
                Log.d(TAG, "开启线程，连接服务");
                connectTCPServer();
            }
        }.start();

    }


    @Override
    protected void onDestroy() {
        if (mClientSocket != null){
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    /**
     * 连接服务端的方法
     */
    private void connectTCPServer(){
        Socket socket = null;
        /**
         * 使用循环，超时连接策略
         */
        while (socket == null){
            try {
                socket = new Socket("localhost",8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream())),true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "connect tcp server success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                Log.d(TAG, "connect tcp server failed,retry...");
                e.printStackTrace();
            }
        }

        /**
         * 接收服务端的消息
         */
        try {
            Log.d(TAG, "进入接收服务端的消息...");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!ClientActivity.this.isFinishing()){
                String msg = br.readLine();
                if (msg != null){
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showMsg = "server " + time +":" + "\n" +  msg + "\n";
                    Log.d(TAG + "收到服务端的消息：", showMsg.toString());
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,showMsg).sendToTarget();
                }
            }
            Log.d(TAG, "quit...");
            MyUtils.close(mPrintWriter);
            MyUtils.close(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {//客户端发出消息
        if (v == mSendButton){
            final String msg = mMessageEditText.getText().toString();
            if (!TextUtils.isEmpty(msg) && mPrintWriter != null){
                mPrintWriter.println(msg);//发送消息到服务端
                mMessageEditText.setText("");
                String time = formatDateTime(System.currentTimeMillis());
                final String showMsg = "server " + time +":" + "\n" +  msg + "\n";
                //设置客户端发出的消息
                Msg sendMsg = new Msg(showMsg,Msg.TYPE_SENT);
                msgList.add(sendMsg);
                if (isFinishing()){
                    adapter = new MsgAdapter(ClientActivity.this,msgList);
                    mMessageListView.setAdapter(adapter);
                }else {
                    adapter.notifyDataSetChanged();
                    mMessageListView.setSelection(mMessageListView.getBottom());
                    isFirst = false;
                }

            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long time){
      return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }
}
