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

    public MXJSFlutterApp initWithAppName(Context context, String appName, MXJSFlutterEngine jsFlutterEngine, String appRootPath) {
        this.mContext = context;
        this.appName = appName;
        this.jsFlutterEngine = jsFlutterEngine;
        jsFlutterEngineStatic = jsFlutterEngine;

        setUpChannel(((MXFlutterActivity)context).getFlutterView());
        setupJSEngine();

        return this;

    }

    private void setupJSEngine() {

    }

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
                    app.jsExecutor.invokeJSValue(jsAppObj,"nativeCall", methodCall.arguments, new MXJSValueCallback());
                }
            }
        });
    }

    void callFlutterWidgetChannelWithMethodName(String methodName, Object args){
        jsFlutterAppChannel.invokeMethod(methodName, args);
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
            public void onComplete() {

            }
        });
    }

    public void exitApp() {
        this.jsAppObj = null;
        this.jsEngine = null;
    }

    private MXJSExecutor jsExecutor() {
        return this.jsEngine.jsExecutor;
    }

    class MXNativeJSFlutterApp {
        public void setCurrentJSApp(V8Object jsApp) {
            jsAppObj = jsApp;
        }

        public void callFlutterReloadApp(V8Object jsApp, String widgetData) {
            jsAppObj = jsApp;
            jsFlutterEngine.callFlutterReloadAppWithJSWidgetData(widgetData);
        }

        public void callFlutterWidgetChannel(String method, V8Array arguments) {

        }
    }
}
