package com.imatrixteam.jsflutter;

import android.content.Context;
import android.util.Log;

import com.eclipsesource.v8.V8Object;

import java.util.HashMap;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import static android.content.ContentValues.TAG;

public class MXJSFlutterApp {

    //Flutter通道
    private static final String FLUTTER_METHED_CHANNEL_NAME = "js_flutter.js_flutter_app_channel";
    MethodChannel flutterChannel;

    private Context mContext;
    private String appName;
    private V8Object jsAppObj;
    private MXJSEngine jsEngine;
    private MXJSExecutor jsExecutor;


    public MXJSFlutterApp(Context context, String appName) {
        this.mContext = context;
        this.appName = appName;

        setupJSEngine();
        setUpChannel(((MXFlutterActivity) context).getFlutterView());
    }

    private void setupJSEngine() {

        jsEngine = MXJSEngine.getInstance(mContext);
        jsExecutor = jsEngine.jsExecutor;
        //todo 调试时，指向本地路径，可以热重载
        String jsBasePath = "";

        //JSFlutter JS运行库搜索路径
        String jsFlutterFrameworkDir = "framework";
        jsEngine.addSearchDir(jsFlutterFrameworkDir);

        //app业务代码搜索路径
        String jsAppCoreDir = "app_test";
        jsEngine.addSearchDir(jsAppCoreDir);

        String jsBasicLibPath = jsFlutterFrameworkDir + "/" + "js_basic_lib.js";
        jsExecutor.executeScriptPath(jsBasicLibPath, new MXJSExecutor.ExecuteScriptCallback() {
            @Override
            public void onComplete(Object value) {
                Log.d(TAG, "setupJSEngine onComplete: " + value);
            }
        });

    }

    void setUpChannel(BinaryMessenger messenger) {
        flutterChannel = new MethodChannel(messenger, FLUTTER_METHED_CHANNEL_NAME);

        // flutter --> native
        flutterChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                Log.e(TAG, "flutterChannel flutter -> native: " + methodCall.method + " ,args: " + methodCall.arguments);
                if (MXJSFlutterApp.this == null) {
                    return;
                }
                if (methodCall.method.equals("callJS")) {
                    MXJSFlutterApp.this.jsExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (jsAppObj == null) {
                                return;
                            }
                            jsExecutor.invokeJSValue(jsAppObj, "nativeCall", methodCall.arguments, new MXJSExecutor.InvokeJSValueCallback() {
                                @Override
                                public void onSuccess(Object value) {
                                    Log.d(TAG, "setUpChannel flutterChannel onSuccess: " + value);
                                }

                                @Override
                                public void onFail(Error error) {
                                    Log.d(TAG, "setUpChannel flutterChannel onFail: " + error);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void unsetup() {

    }

    public void runApp() {
        runAppWithPageName("");
    }

    public void runAppWithPageName(String pageName) {
        jsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 向JS中注入对象
                V8Object v8Object = new V8Object(jsExecutor.getV8Runtime());

                MXNativeJSFlutterApp app = new MXNativeJSFlutterApp();
                v8Object.registerJavaMethod(app, "setCurrentJSApp", "setCurrentJSApp", new Class<?>[]{V8Object.class});
                v8Object.registerJavaMethod(app, "callFlutterReloadApp", "callFlutterReloadApp", new Class<?>[]{V8Object.class, String.class});
                v8Object.registerJavaMethod(app, "callFlutterWidgetChannel", "callFlutterWidgetChannel", new Class<?>[]{String.class, V8Object.class,});

                jsExecutor.getV8Runtime().add("MXNativeJSFlutterApp", v8Object);
            }
        });

        jsExecutor.executeScriptPath("app_test/main.js", new MXJSExecutor.ExecuteScriptCallback() {
            @Override
            public void onComplete(Object value) {
                Log.d(TAG, "MXJSFlutterApp runAppWithPageName, execute complete: " + value);
            }
        });
    }

    public void exitApp() {
//        this.jsAppObj.release();
//        this.jsExecutor.invalidate();
    }


    /**
     * 向 js 注入的对象
     */
    class MXNativeJSFlutterApp {

        //js --> native
        public void setCurrentJSApp(V8Object jsApp) {
            Log.d(TAG, "MXNativeJSFlutterApp setCurrentJSApp jsApp: " + jsApp);
            jsAppObj = (V8Object) jsExecutor.getV8Runtime().get("currentJSApp");
        }

        //js --> flutter
        public void callFlutterReloadApp(V8Object jsApp, String widgetData) {
            Log.d(TAG, "MXNativeJSFlutterApp callFlutterReloadApp widgetData: " + widgetData);

            jsAppObj = (V8Object) jsExecutor.getV8Runtime().get("currentJSApp");
            MXJSFlutterEngine.getInstance(mContext).callFlutterReloadAppWithJSWidgetData(widgetData);
        }

        //js --> flutter
        public void callFlutterWidgetChannel(String methodName, V8Object args) {
            String[] datas = args.getKeys();
            HashMap dataMap = new HashMap();
            for (int i = 0; i < datas.length; i++) {
                dataMap.put(datas[i], args.get(datas[i]));
            }

            Log.e(Constants.TAG, "MXNativeJSFlutterApp js -> native: " + methodName + " args: " + dataMap);

            ((MXFlutterActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(Constants.TAG, "MXNativeJSFlutterApp flutterChannel native -> flutter: " + methodName + " args: " + dataMap);
                    flutterChannel.invokeMethod(methodName, dataMap);
                }
            });
        }
    }
}
