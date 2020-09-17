package com.zhumingwei.flutterhotapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhumingwei.flutterhotapp.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.flutter.BuildConfig;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.plugin.platform.PlatformViewsController;
import io.flutter.util.PathUtils;

import static android.content.Context.MODE_PRIVATE;
import static com.zhumingwei.flutterhotapp.MyApplication.LOAD_KEY;

/**
 * @author zhumingwei
 * @date 2020/9/2 13:58
 * @email zhumingwei@bilibili.com
 */
public class MyApplication extends Application {
    public static String LOAD_KEY = "load_key";
    public static String TAG = "Flutter";
    public static Application context;
    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        MyFlutterLoader.getInstance().startInitialization(this);
        prepare();
    }

    private void prepare() {
        //TODO 替换自己想要的逻辑,这里只是简单地复制asset到本地目录模拟下载
        InputStream olds = null;
        File outputfile = new File(getFilesDir().getAbsoluteFile() + "/" + "target.so");
        try {
            olds = getAssets().open("version1_libapp.so");

            File out;
            FileOutputStream patchouts;
            out = outputfile;
            patchouts = new FileOutputStream(out);
            Util.copy(olds, patchouts);
            patchouts.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (olds != null) {
                try {
                    olds.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downloadRequest(String urlStr, File dstFile) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = httpURLConnection.getInputStream();
                fos = new FileOutputStream(dstFile);
                byte[] buffer = new byte[8192];
                int len = 0;
                while ((len = is.read(buffer)) >= 0) {
                    if (len > 0)
                        fos.write(buffer, 0, len);
                }
                fos.flush();

            }catch (Exception e){

            }finally {
                fos.close();
                is.close();
            }

            Log.d("Flutter","下载成功");
            Log.d("Flutter",dstFile.getAbsolutePath()+" "+ dstFile.length());

        } else {
            // ... do something with unsuccessful response

        }
    }
}

class MyFlutterLoader extends FlutterLoader{
    public static String TAG = "Flutter";
    private static MyFlutterLoader instance;

    @NonNull
    public static MyFlutterLoader getInstance() {
        if (instance == null) {
            instance = new MyFlutterLoader();
        }
        return instance;
    }
    private static final String AOT_SHARED_LIBRARY_NAME = "aot-shared-library-name";
    private static final String SNAPSHOT_ASSET_PATH_KEY = "snapshot-asset-path";
    private static final String VM_SNAPSHOT_DATA_KEY = "vm-snapshot-data";
    private static final String ISOLATE_SNAPSHOT_DATA_KEY = "isolate-snapshot-data";
    private static final String FLUTTER_ASSETS_DIR_KEY = "flutter-assets-dir";

    static Field  field_initialized;
    static Field  field_resourceExtractor;
    static Field  field_flutterAssetsDir;
    static Field  field_settings;
    static Field  method_settings_logTag;
    static Method  method_resourceExtractor;
    static {
        try {
            field_initialized = FlutterLoader.class.getDeclaredField("initialized");

        field_initialized.setAccessible(true);

        field_settings = FlutterLoader.class.getDeclaredField("settings");
        field_settings.setAccessible(true);

        method_settings_logTag = field_settings.getType().getDeclaredField("logTag");
        method_settings_logTag.setAccessible(true);

        field_flutterAssetsDir = FlutterLoader.class.getDeclaredField("flutterAssetsDir");
        field_flutterAssetsDir.setAccessible(true);

        field_resourceExtractor = FlutterLoader.class.getDeclaredField("resourceExtractor");
        field_resourceExtractor.setAccessible(true);

        method_resourceExtractor = field_resourceExtractor.getType().getDeclaredMethod("waitForCompletion");
        method_resourceExtractor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "static errro :"+e.getMessage());
        }

    }
    boolean initialized() {
            try{
                return field_initialized.getBoolean(this);
            }catch (Exception e) {
                Log.e(TAG, "initialized get: error: " + e.getMessage());
                return false;
            }
    }

    void setInitallized(boolean init){
        try{
            field_initialized.setBoolean(this,init);
        }catch (Exception e) {
            Log.e(TAG, "setInitallized get: error: " + e.getMessage());
        }
    }

    Object resourceExtractor(){
        try{
            return field_resourceExtractor.get(this);
        }catch (Exception e) {
            Log.e(TAG, "resourceExtractor get: error: " + e.getMessage());
            return null;
        }
    }

    void invokeWaitForCompletion(Object object){
        try{
            method_resourceExtractor.invoke(object);
        }catch (Exception e) {
            Log.e(TAG, "invokeWaitForCompletion get: error: " + e.getMessage());
        }
    }

    String field_flutterAssetsDir(){
        try {
            return (String) field_flutterAssetsDir.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "field_flutterAssetsDir get: error: " + e.getMessage());
            return "";
        }
    }

    public void ensureInitializationComplete(
            @NonNull Context applicationContext, @Nullable String[] args) {
        if (initialized()) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "ensureInitializationComplete must be called on the main thread");
        }
        try {
            if (resourceExtractor() != null) {
                invokeWaitForCompletion(resourceExtractor());
            }

            List<String> shellArgs = new ArrayList<>();
            shellArgs.add("--icu-symbol-prefix=_binary_icudtl_dat");

            ApplicationInfo applicationInfo = applicationContext
                    .getPackageManager()
                    .getApplicationInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA);
            shellArgs.add(
                    "--icu-native-lib-path="
                            + applicationInfo.nativeLibraryDir
                            + File.separator
                            + "libflutter.so");

            if (args != null) {
                Collections.addAll(shellArgs, args);
            }

            String kernelPath = null;
            Log.d(TAG, "ensureInitializationComplete: io.flutter.BuildConfig.DEBUG" + io.flutter.BuildConfig.DEBUG);
            Log.d(TAG, "ensureInitializationComplete: BuildConfig.JIT_RELEASE" + BuildConfig.JIT_RELEASE);
            if (io.flutter.BuildConfig.DEBUG || BuildConfig.JIT_RELEASE) {
                String snapshotAssetPath =
                        PathUtils.getDataDirectory(applicationContext) + File.separator + field_flutterAssetsDir();
                kernelPath = snapshotAssetPath + File.separator + "kernel_blob.bin";
                shellArgs.add("--" + SNAPSHOT_ASSET_PATH_KEY + "=" + snapshotAssetPath);
                shellArgs.add("--" + VM_SNAPSHOT_DATA_KEY + "=" + "vm_snapshot_data");
                shellArgs.add("--" + ISOLATE_SNAPSHOT_DATA_KEY + "=" + "isolate_snapshot_data");
            } else {
                setReleaseArgs(shellArgs,applicationInfo,applicationContext);
            }

            shellArgs.add("--cache-dir-path=" + PathUtils.getCacheDirectory(applicationContext));

            String tag = (String)method_settings_logTag.get(field_settings.get(this));
            if (tag != null) {
                shellArgs.add("--log-tag=" + tag);
            }

            String appStoragePath = PathUtils.getFilesDir(applicationContext);
            String engineCachesPath = PathUtils.getCacheDirectory(applicationContext);
            FlutterJNI.nativeInit(
                    applicationContext,
                    shellArgs.toArray(new String[0]),
                    kernelPath,
                    appStoragePath,
                    engineCachesPath);

            setInitallized(true);
        } catch (Exception e) {
            Log.e(TAG, "Flutter initialization failed.", e);
            throw new RuntimeException(e);
        }
    }

    private void setReleaseArgs(List<String> shellArgs, ApplicationInfo applicationInfo, Context applicationContext) {
        File dest = new File(applicationContext.getFilesDir().getAbsoluteFile() + "/" + "target.so");
        SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("private",MODE_PRIVATE);
        boolean loadnew = sharedPreferences.getBoolean(LOAD_KEY,false);
        Log.e(TAG, "loadnew=="+loadnew);
        Log.e(TAG, "dest.exists()=="+dest.exists());
        if (loadnew && dest.exists()){
            Log.e(TAG, "setReleaseArgs: 加载新的so le");
            shellArgs.add(
                    "--"
                            + AOT_SHARED_LIBRARY_NAME
                            + "="
                            + dest.getAbsolutePath());
        }else{
            shellArgs.add("--" + AOT_SHARED_LIBRARY_NAME + "=" + "libapp.so");
            shellArgs.add(
                    "--"
                            + AOT_SHARED_LIBRARY_NAME
                            + "="
                            + applicationInfo.nativeLibraryDir
                            + File.separator
                            + "libapp.so");

        }

    }
}

class MyFlutterEngine extends FlutterEngine{

    public MyFlutterEngine(@NonNull Context context) {
        this(context, null);
    }

    public MyFlutterEngine(@NonNull Context context, @Nullable String[] dartVmArgs) {
        this(context, MyFlutterLoader.getInstance(), new FlutterJNI(), dartVmArgs, true);
    }

    public MyFlutterEngine(@NonNull Context context, @Nullable String[] dartVmArgs, boolean automaticallyRegisterPlugins) {
        this( context,
                MyFlutterLoader.getInstance(),
                new FlutterJNI(),
                dartVmArgs,
                automaticallyRegisterPlugins);
    }

    public MyFlutterEngine(@NonNull Context context, @NonNull FlutterLoader flutterLoader, @NonNull FlutterJNI flutterJNI) {
        super(context, flutterLoader, flutterJNI);
    }

    public MyFlutterEngine(@NonNull Context context, @NonNull FlutterLoader flutterLoader, @NonNull FlutterJNI flutterJNI, @Nullable String[] dartVmArgs, boolean automaticallyRegisterPlugins) {
        super(context, flutterLoader, flutterJNI, dartVmArgs, automaticallyRegisterPlugins);
    }

    public MyFlutterEngine(@NonNull Context context, @NonNull FlutterLoader flutterLoader, @NonNull FlutterJNI flutterJNI, @NonNull PlatformViewsController platformViewsController, @Nullable String[] dartVmArgs, boolean automaticallyRegisterPlugins) {
        super(context, flutterLoader, flutterJNI, platformViewsController, dartVmArgs, automaticallyRegisterPlugins);
    }
}