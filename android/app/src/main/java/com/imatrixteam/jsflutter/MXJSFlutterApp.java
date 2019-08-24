package com.imatrixteam.jsflutter;

public class MXJSFlutterApp {

    static MXJSFlutterEngine jsFlutterEngineStatic;

    private String appName;
    private MXJSFlutterEngine jsFlutterEngine;

    private MXJSEngine jsEngine;
    private MXJSExecutor jsExecutor;

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

    private void setUpChannel() {

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
