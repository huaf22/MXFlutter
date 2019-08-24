package com.imatrixteam.jsflutter;

import android.os.Build;

public class MXJSFlutterEngine {

    private String rootPath;
    private MXJSFlutterApp currentApp;

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
        currentApp.initWithAppName(appName, this, appRootPath);
        currentApp.runAppWithPageName(pageName);
    }

    public boolean showPage(String pageName) {
        return true;
    }
}
