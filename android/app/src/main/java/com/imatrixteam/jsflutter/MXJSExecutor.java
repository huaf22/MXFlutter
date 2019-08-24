package com.imatrixteam.jsflutter;

import com.eclipsesource.v8.V8Object;

import java.util.List;

public class MXJSExecutor {
    private JsContext jsContext;
    private JavaScriptThread javaScriptThread;

    public MXJSExecutor initWithJSContext(JsContext context) {
        this.jsContext = context;
        return this;
    }

    public MXJSExecutor init() {
        setup();
        return this;
    }

    private void setup() {

    }

    public void executeScriptAsync(String script, String sourceURl, ExecuteScriptCallback callback) {

    }

    public void executeScriptPatj(String path, ExecuteScriptCallback callback) {

    }

    private boolean isValid() {
        return jsContext != null;
    }

    public void invalidate() {
        if (isValid()) {
            jsContext = null;
            javaScriptThread = null;
        }
    }

    interface ExecuteScriptCallback{
        void onComplete();
    }


    public void invokeJSValue(V8Object jsValue, String method, Object args, MXJSValueCallback callback){

    }
}


