package com.imatrixteam.jsflutter;

import com.eclipsesource.v8.V8Object;
import android.content.Context;

import com.eclipsesource.v8.V8;


public class MXJSExecutor {
    public V8 runtime;

    public Context context;

    public MXJSExecutor init(Context context) {
        setup();
        return this;
    }

    private void setup() {
        runtime = V8.createV8Runtime();
    }

    public void executeScriptAsync(String script, ExecuteScriptCallback callback) {
        runtime.executeScript(script);
        callback.onComplete();
    }

    public void executeScriptPath(String path, ExecuteScriptCallback callback) {
        String script = FileUtils.getFromAssets(context, path);
        runtime.executeScript(script);
        callback.onComplete();
    }

    public void executeMXJSBlockOnJSThread() {

    }

    private boolean isValid() {
        return runtime != null;
    }

    public void invalidate() {
        if (isValid()) {
            runtime.release();

        }
    }

    interface ExecuteScriptCallback{
        void onComplete();
    }

    public void invokeJSValue(V8Object jsValue, String method, Object args, MXJSValueCallback callback){

    }

    interface MXJSExecutorBlock {
        void execute(MXJSExecutor executor);
    }
}


