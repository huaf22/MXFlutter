package com.imatrixteam.jsflutter;

import com.eclipsesource.v8.V8Object;

import java.lang.ref.WeakReference;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import android.content.Context;

import com.eclipsesource.v8.V8Array;

public class MXJSFlutterApp {

    static MXJSFlutterEngine jsFlutterEngineStatic;

    private Context mContext;

    private String appName;
    private MXJSFlutterEngine jsFlutterEngine;

    private V8Object jsAppObj;
    private MXJSEngine jsEngine;
    private MXJSExecutor jsExecutor;

    //Flutter通道
    private static final String FLUTTER_METHED_CHANNEL_NAME = "js_flutter.js_flutter_app_channel";
    MethodChannel jsFlutterAppChannel;

    public MXJSFlutterApp initWithAppName(Context context, String appName, MXJSFlutterEngine jsFlutterEngine) {
        this.mContext = context;
        this.appName = appName;
        this.jsFlutterEngine = jsFlutterEngine;
        jsFlutterEngineStatic = jsFlutterEngine;

        setUpChannel(((MXFlutterActivity)context).getFlutterView());
        setupJSEngine();
        return this;
    }

    private void setupJSEngine() {

        jsEngine = MXJSEngine.getInstance(mContext);
        jsExecutor = jsEngine.jsExecutor;

        //todo 调试时，指向本地路径，可以热重载
        String jsBasePath = "";

        //JSFlutter JS运行库搜索路径
        String jsFlutterFrameworkDir = "js_flutter_js_framework_lib";
        jsEngine.addSearchDir(jsFlutterFrameworkDir);

        //app业务代码搜索路径
        String jsAppCoreDir = "js_flutter_scr/app_test";
        jsEngine.addSearchDir(jsAppCoreDir);

        String jsBasicLibPath = jsFlutterFrameworkDir + "/" +  "js_basic_lib.js";
        jsExecutor.executeScriptPath(jsBasicLibPath, new MXJSExecutor.ExecuteScriptCallback() {
            @Override
            public void onComplete(Object value) {

            }
        });

    }

    //flutter --> js
    void setUpChannel(BinaryMessenger flutterViewController) {
        final WeakReference _weakThis = new WeakReference(MXJSFlutterApp.this);
        jsFlutterAppChannel = new MethodChannel(flutterViewController,FLUTTER_METHED_CHANNEL_NAME);
        jsFlutterAppChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                MXJSFlutterApp app = (MXJSFlutterApp)_weakThis.get();
                if(app == null)
                    return;

                if(methodCall.method.equals("callJS")){
                    if (jsAppObj != null) {
                        app.jsExecutor.invokeJSValue(jsAppObj,"nativeCall", methodCall.arguments, new MXJSValueCallback());
                    }
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
        MXNativeJSFlutterApp MXNativeJSFlutterApp = new MXNativeJSFlutterApp();
        jsExecutor.runtime.registerJavaMethod(MXNativeJSFlutterApp, "setCurrentJSApp",
                "setCurrentJSApp", new Class<?>[]{V8Object.class});
        jsExecutor.runtime.registerJavaMethod(MXNativeJSFlutterApp,
                "callFlutterReloadApp", "callFlutterReloadApp", new Class<?>[]{V8Object.class, String.class});
        jsExecutor.runtime.registerJavaMethod(MXNativeJSFlutterApp,
                "callFlutterWidgetChannel", "callFlutterWidgetChannel", new Class<?>[]{V8Object.class, V8Array.class});

        jsExecutor.executeScriptPath("main.js", new MXJSExecutor.ExecuteScriptCallback() {
            @Override
            public void onComplete(Object value) {

            }
        });
    }

    public void exitApp() {
        this.jsAppObj = null;
        this.jsEngine = null;
    }


    //js 注入对象
    class MXNativeJSFlutterApp {

        //js --> native
        public void setCurrentJSApp(V8Object jsApp) {
            jsAppObj = jsApp;
        }

        //js --> flutter
        public void callFlutterReloadApp(V8Object jsApp, String widgetData) {
            jsAppObj = jsApp;
            jsFlutterEngine.callFlutterReloadAppWithJSWidgetData(widgetData);
        }

        //js --> flutter
        public void callFlutterWidgetChannel(String methodName, V8Array args) {
            jsFlutterAppChannel.invokeMethod(methodName, args);
        }
    }
}
