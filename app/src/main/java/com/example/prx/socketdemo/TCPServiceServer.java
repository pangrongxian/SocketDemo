package com.example.prx.socketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.example.prx.socketdemo.utils.MyUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by prx on 2016/4/4 02:07.
 * Email:pangrongxian@gmail.com
 */
public class TCPServiceServer extends Service{

    private static final String TAG = "TAG_Server";

    private boolean mIsServiceDestoryed = false;

    private String[] mDefiendMessage = new String[]{"你好啊，哈哈",
                                                    "请问你叫什么名字",
                                                    "今天天气不错啊",
                                                    "我可以和多个客户端同时聊天",
                                                    "据说努力的人运气都不会太差"};

    @Override
    public void onCreate() {
        Log.d(TAG, "客户端连接到服务端，启动服务");
        new Thread(new TcpService()).start();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }

    private class TcpService implements Runnable{
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestoryed){
                //接收客户端的请求
                Log.d(TAG, "接收客户端的请求");
                try {
                    final Socket client = serverSocket.accept();
                    Log.d(TAG, "接受了客户端的请求");
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                //响应客户端
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();//启动线程
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket socket) throws IOException {
        //用于接收客户端消息
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //用于向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        Log.d(TAG, "欢迎来到聊天室");
        out.println("欢迎来到聊天室！");

        while (!mIsServiceDestoryed){
            String str = in.readLine();
            System.out.println("msg from client" + str.toString());
            Log.d(TAG, str);
            if (str == null){
                Log.d(TAG, "客户端断开");
                break;
            }
            int i = new Random().nextInt(mDefiendMessage.length);
            String msg = mDefiendMessage[i];
            out.println(msg);
            Log.d(TAG, ("服务端给客户端发消息：" + msg));
        }
        Log.d(TAG, "client quite.");
        //关闭流
        MyUtils.close(out);
        MyUtils.close(in);
        socket.close();
    }

}
