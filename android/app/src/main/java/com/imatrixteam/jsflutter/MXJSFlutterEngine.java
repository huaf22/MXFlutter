package com.imatrixteam.jsflutter;

import android.content.Context;

public class MXJSFlutterEngine {

    private static MXJSFlutterEngine INSTANCE;
    private String rootPath;
    private MXJSFlutterApp currentApp;
    private Context mContext;

    private MXJSFlutterEngine(Context context) {
        this.mContext = context;
    }

    public static MXJSFlutterEngine getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MXJSFlutterEngine.class) {
                INSTANCE = new MXJSFlutterEngine(context);
            }
        }
        return INSTANCE;
    }

    public void setup() {

    }

    public void unsetup() {
        if (currentApp != null) {
            currentApp.exitApp();
            currentApp = null;
        }
    }

    public void runApp(String appName, String pageName) {
        setup();
        currentApp = new MXJSFlutterApp(mContext, appName);
        currentApp.runAppWithPageName(pageName);
    }

    public boolean showPage(String pageName) {
        return true;
    }

    public void callFlutterReloadAppWithJSWidgetData(String widgetData) {
        ((MXFlutterActivity) mContext).callFlutterReloadAppWithJSWidgetData(widgetData);
    }
}
