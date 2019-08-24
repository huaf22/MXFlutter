package com.imatrixteam.jsflutter;

import com.eclipsesource.v8.V8Object;

import java.lang.ref.WeakReference;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MXJSFlutterApp {

    static MXJSFlutterEngine jsFlutterEngineStatic;

    private String appName;
    private MXJSFlutterEngine jsFlutterEngine;

    private MXJSEngine jsEngine;
    private MXJSExecutor jsExecutor;

    private V8Object jsAppObj;

    //Flutter通道
    private static final String FLUTTER_METHED_CHANNEL_NAME = "js_flutter.js_flutter_app_channel";
    MethodChannel jsFlutterAppChannel;

    public MXJSFlutterApp initWithAppName(String appName, MXJSFlutterEngine jsFlutterEngine, String appRootPath) {
        this.appName = appName;
        this.jsFlutterEngine = jsFlutterEngine;
        jsFlutterEngineStatic = jsFlutterEngine;

        setUpChannel();
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

    }

    public void runAppWithPageName(String pageName) {

    }

    private void exitApp() {
        this.jsAppObj = null;
        this.jsEngine = null;
    }

    private MXJSExecutor jsExecutor() {
        return this.jsEngine.
    }
}
