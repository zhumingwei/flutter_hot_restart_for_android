package com.zhumingwei.flutterhotapp;

import android.content.Context;
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

public class MainActivity extends FlutterActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyFlutterLoader.getInstance();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            File dir = new File(applicationInfo.nativeLibraryDir);
            File[] fs= dir.listFiles();
            log("get list fs.size = " + fs.length);
            for (File f : fs) {
                log(f.getAbsolutePath());
            }
            log("end get list");
//            log("create file");
//            File newFile = new File(dir.getAbsoluteFile() + "/" + "newFile");
//            newFile.createNewFile();
//            log("end create file");
//            log("create dir");
//            File newDir = new File(dir.getAbsoluteFile() + "/" + "newDir");
//            newDir.mkdir();
//            log("end create dir");
//
//            log("get list fs.size = " + fs.length);
//            for (File f : fs) {
//                log(f.getAbsolutePath());
//            }
//            log("end get list");

        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        }

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
}
