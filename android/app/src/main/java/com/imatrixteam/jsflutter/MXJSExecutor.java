package com.imatrixteam.jsflutter;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import android.app.Activity;
import android.content.Context;

import com.eclipsesource.v8.V8;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MXJSExecutor {
    public V8 runtime;

    private Executor executor;

    public Context context;

    private static MXJSExecutor instance;

    private MXJSExecutor(Context context) {
        this.context = context;
        init(context);
    }

    public static MXJSExecutor getInstance(Context context) {
        if (instance == null) {
            synchronized (MXJSExecutor.class) {
                instance = new MXJSExecutor(context);
            }
        }
        return instance;
    }

    public MXJSExecutor init(Context context) {
        executor = Executors.newSingleThreadExecutor();
        setup();
        return this;
    }

    private void setup() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runtime = V8.createV8Runtime();
            }
        });
    }

    public void registerJavaMethod(JavaVoidCallback callback, String name) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runtime.registerJavaMethod(callback, name);
            }
        });
    }

    public void registerJavaMethod(JavaCallback callback, String name) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runtime.registerJavaMethod(callback, name);
            }
        });
    }

    public void registerJavaMethod(Object object, String methodName, String jsFunctionName, Class<?>[] parameterTypes) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runtime.registerJavaMethod(object,  methodName,  jsFunctionName, parameterTypes);
            }
        });
    }

    public void executeScriptAsync(String script, ExecuteScriptCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Object result = runtime.executeScript(script);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(result);
                    }
                });
            }
        });

    }

    public void executeScriptPath(String path, ExecuteScriptCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String script = FileUtils.getFromAssets(context, path);
                Object result = runtime.executeScript(script);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(result);
                    }
                });
            }
        });
    }

    public void executeScript(String script, ExecuteScriptCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Object result = runtime.executeScript(script);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(result);
                    }
                });
            }
        });
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
        void onComplete(Object value);
    }

    public void invokeJSValue(V8Object jsAppObj, String method, Object args, MXJSValueCallback callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //获取执行结果
                if (jsAppObj != null) {
                    System.out.println(args.toString());
                    jsAppObj.executeFunction(method, new V8Array(runtime).push(args.toString()));
                }
            }
        });
    }

    interface MXJSExecutorBlock {
        void execute(MXJSExecutor executor);
    }
}


