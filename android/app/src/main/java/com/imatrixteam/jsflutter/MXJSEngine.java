package com.imatrixteam.jsflutter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MXJSEngine {
    public MXJSExecutor jsExecutor;

    private ArrayList<String> searchDirArray;

    private Set<String> runnedScriptFile;

    public MXJSEngine init() {
        this.searchDirArray = new ArrayList<>();
        this.runnedScriptFile = new HashSet<>();
        setup();
        return this;
    }

    private void setup() {
        this.jsExecutor = new MXJSExecutor();

        //todo setupBasicJSRuntime
        jsExecutor.executeMXJSBlockOnThread()
    }

    static void setupBasicJSRuntime(MXJSEngine mxjsEngine, JSContext context) {

    }

    public void addSearchDir(String dir) {
        if (TextUtils.isEmpty(dir) || searchDirArray.indexOf(dir) != -1) {
            return;
        }
        searchDirArray.add(dir);
    }
}
