package com.zhumingwei.flutterhotapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zhumingwei
 * @date 2020/9/17 15:38
 * @email zdf312192599@163.com
 */
public class Util {
    public static int copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        int LENGTH = 1024;
        byte buffer[] = new byte[LENGTH];
        int len;
        while((len = inputStream.read(buffer,0,LENGTH)) != -1){
            outputStream.write(buffer,0,len);
            outputStream.flush();
        }
        return 0;
    }
}
