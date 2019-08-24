package com.imatrixteam.jsflutter;

import android.content.Context;
import android.os.Build;

public class MXJSFlutterEngine {

    private String rootPath;
    private MXJSFlutterApp currentApp;

    private Context mContext;

    private MXJSFlutterEngine INSTANCE;

    private MXJSFlutterEngine(Context context) {
        this.mContext = context;
    }

    private MXJSFlutterEngine getINSTANCE(Context context) {
        if (INSTANCE != null) {
            synchronized (this) {
                INSTANCE = new MXJSFlutterEngine(context);
            }
        }
        return INSTANCE;
    }

    public MXJSFlutterEngine initRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public void setup() {

    }

    public void unsetup() {
        if (currentApp != null)
        {
            currentApp.exitApp();
            currentApp = null;
        }
    }

    public void runApp(String appName, String pageName) {
        setup();

        String appRootPath = rootPath + "/" + appName;
        currentApp.initWithAppName(mContext, appName, this, appRootPath);
        currentApp.runAppWithPageName(pageName);
    }

    public boolean showPage(String pageName) {
        return true;
    }

    public void callFlutterReloadAppWithJSWidgetData(String widgetData) {
        ((MXFlutterActivity)mContext).callFlutterReloadAppWithJSWidgetData(widgetData);
    }
}
