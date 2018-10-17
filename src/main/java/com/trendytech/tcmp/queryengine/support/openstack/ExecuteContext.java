package com.trendytech.tcmp.queryengine.support.openstack;

public class ExecuteContext {
    private static ThreadLocal<String> tl = new ThreadLocal<>();

    private static ExecuteContext context = new ExecuteContext();

    private ExecuteContext() {}

    public static ExecuteContext getContext() {
        return context;
    }

    public String getToken() {
        return tl.get();
    }

    public void setToken(String token) {
        tl.set(token);
    }
}
