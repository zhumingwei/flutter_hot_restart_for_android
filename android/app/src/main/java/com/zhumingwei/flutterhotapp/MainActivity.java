package com.zhumingwei.flutterhotapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import static android.content.Context.MODE_PRIVATE;
import static com.zhumingwei.flutterhotapp.MyApplication.LOAD_KEY;
import static com.zhumingwei.flutterhotapp.MyApplication.TAG;

public class MainActivity extends FlutterActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyFlutterLoader.getInstance();

    }

    @Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        return new MyFlutterEngine(context);
    }

    @Override
    public Uri onProvideReferrer() {
        return super.onProvideReferrer();
    }

    public static void log (String s){
        Log.d("Flutter", s);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        flutterEngine.getPlugins().add(new MyFlutterPlugin());
    }
}

class MyFlutterPlugin implements FlutterPlugin {
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        MethodChannel methodChannel = new MethodChannel(binding.getBinaryMessenger(),"a.b/test");
        methodChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
                if(call.method.equals("load_new")){
                    SharedPreferences sharedPreferences = binding.getApplicationContext().getSharedPreferences("private",MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(LOAD_KEY,true).commit();
                    Log.d(TAG, "onMethodCall: load_new =="+ true);
                    result.success(null);
                } else if(call.method.equals("revert")){
                    SharedPreferences sharedPreferences = binding.getApplicationContext().getSharedPreferences("private",MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(LOAD_KEY,false).commit();
                    Log.d(TAG, "onMethodCall: load_new =="+ false);
                    result.success(null);
                }else {
                    result.error("-1","not support",null);
                }
            }
        });
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    }
}

