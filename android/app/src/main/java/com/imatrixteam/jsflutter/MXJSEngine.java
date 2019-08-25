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

    private static MXJSEngine instance;

    public MXJSExecutor jsExecutor;

    private ArrayList<String> searchDirArray;

    private Set<String> runnedScriptFile;

    private Context mContext;

    private MXJSEngine(Context context) {
        mContext = context;
        init();
    }

    static MXJSEngine getInstance(Context context) {
        if (instance == null) {
            synchronized (MXJSEngine.class) {
                instance = new MXJSEngine(context);
            }
        }
        return instance;
    }

    private MXJSEngine init() {
        this.searchDirArray = new ArrayList<>();
        this.runnedScriptFile = new HashSet<>();
        setup();
        return this;
    }

    private void setup() {
        this.jsExecutor = MXJSExecutor.getInstance(mContext);

        setupBasicJSRuntime();
    }

    private void setupBasicJSRuntime() {
        JavaVoidCallback JSAPI_log = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object v8Object, V8Array args) {
                if (args.length() > 0) {
                    Log.i(TAG, args.get(0).toString());
                }
            }
        };
        jsExecutor.registerJavaMethod(JSAPI_log, "JSAPI_log");

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

                    for (String dir : searchDirArray
                    ) {
                        try {
                            String[] files = mContext.getAssets().list(dir);
                            for (String fileName: files
                                 ) {
                                if (fileName.equals(filePath)){
                                    String absolutePathTemp = dir + "/" + filePath;
                                    jsScript = FileUtils.getFromAssets(mContext, absolutePathTemp);
                                    if (TextUtils.isEmpty(jsScript)) {
                                        absolutePath = absolutePathTemp;
                                        break;
                                    }
                                }
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    String injectScript = String.format("(function (){let module = {exports:{}};(function (){%s})(); return module.exports;})();", jsScript);
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
        jsExecutor.registerJavaMethod(JSAPI_require, "JSAPI_require");
    }

    void addSearchDir(String dir) {
        if (TextUtils.isEmpty(dir) || searchDirArray.indexOf(dir) != -1) {
            return;
        }
        searchDirArray.add(dir);
    }
}
