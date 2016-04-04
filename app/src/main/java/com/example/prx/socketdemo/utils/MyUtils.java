package com.example.prx.socketdemo.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by prx on 2016/4/4 02:30.
 * Email:pangrongxian@gmail.com
 */
public class MyUtils {

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
