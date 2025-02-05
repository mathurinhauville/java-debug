package com.ubo.debug;

public class JDISimpleDebugger {
    public static void main(String[] args) {
        ScriptableDebugger debuggerInstance = new ScriptableDebugger();
        debuggerInstance.attachTo(JDISimpleDebuggee.class);
    }
}

