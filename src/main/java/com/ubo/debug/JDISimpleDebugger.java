package com.ubo.debug;

public class JDISimpleDebugger {
    public static void main(String[] args) throws Exception {
        ScriptableDebugger debuggerInstance = new ScriptableDebugger();
        debuggerInstance.attachTo(JDISimpleDebuggee.class);
        debuggerInstance.startCommandInterpreter();
    }
}

