package io.flutter.embedding.engine;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

/**
 * @author zhumingwei
 * @date 2020/9/22 16:55
 * @email zdf312192599@163.com
 */
public class FlutterJNIMock {
    private static String TAG = "FlutterJNIMock";
    public static void nativeInit(
            @NonNull Context context,
            @NonNull String[] args,
            @Nullable String bundlePath,
            @NonNull String appStoragePath,
            @NonNull String engineCachesPath){
        Log.d(TAG,"加载");
        Log.d(TAG, Arrays.toString(args));
        Log.d(TAG, String.valueOf(bundlePath));
        Log.d(TAG, String.valueOf(appStoragePath));
        Log.d(TAG, String.valueOf(engineCachesPath));
        FlutterJNI.nativeInit(context, args, bundlePath, appStoragePath, engineCachesPath);
    }
}
