package com.imatrixteam.jsflutter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MXJSEngine {
    static private String TAG = "MXJSEngine";

    public MXJSExecutor jsExecutor;

    private ArrayList<String> searchDirArray;

    private Set<String> runnedScriptFile;

    private Context mContext;

    public MXJSEngine(Context context) {
        mContext = context;
    }

    public MXJSEngine init() {
        this.searchDirArray = new ArrayList<>();
        this.runnedScriptFile = new HashSet<>();
        setup();
        return this;
    }

    private void setup() {
        this.jsExecutor = new MXJSExecutor();

        //todo setupBasicJSRuntime

    }

    void setupBasicJSRuntime(MXJSEngine mxjsEngine) {
        JavaVoidCallback JSAPI_log = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object v8Object, V8Array args) {
                if (args.length() > 0) {
                    Log.i(TAG, args.get(0).toString());
                }
            }
        };
        mxjsEngine.jsExecutor.runtime.registerJavaMethod(JSAPI_log, "JSAPI_log");

        JavaCallback JSAPI_require = new JavaCallback() {
            @Override
            public Object invoke(V8Object v8Object, V8Array args) {
                if (args.length() > 0) {
                    String filePath = args.get(0).toString();

                    String prefix = "./";
                    if (filePath.contains(prefix)) {
                        filePath = filePath.substring(prefix.length());
                    }

                    String absolutePath = "";
                    String jsScript = "";

                    for (String dir : mxjsEngine.searchDirArray
                    ) {
                        String absolutePathTemp = dir + "/" + filePath;
                        jsScript = FileUtils.getFromAssets(mContext, absolutePathTemp);
                        if (TextUtils.isEmpty(jsScript)) {
                            absolutePath = absolutePathTemp;
                            break;
                        }
                    }

                    String injectScript = String.format("(function (){let module = {exports:{}};(function (){\\n%s\\n})(); return module.exports;})();", jsScript);
                    Object value = jsExecutor.runtime.executeScript(injectScript);
                    if (value != null) {
                        Map<String, Object> module = new HashMap<>();
                        module.put("exports", value);
                        module.put("absolutePath", absolutePath);
                        return module;
                    }
                }
                return null;
            }
        };
        mxjsEngine.jsExecutor.runtime.registerJavaMethod(JSAPI_require, "JSAPI_require");
    }

    public void addSearchDir(String dir) {
        if (TextUtils.isEmpty(dir) || searchDirArray.indexOf(dir) != -1) {
            return;
        }
        searchDirArray.add(dir);
    }
}
