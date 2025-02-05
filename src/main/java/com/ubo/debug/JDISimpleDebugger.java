package com.ubo.debug;

import com.sun.jdi.VirtualMachine;

public class JDISimpleDebugger {
    public static void main(String[] args) throws Exception {
        ScriptableDebugger debuggerInstance = new ScriptableDebugger();
        debuggerInstance.attachTo(JDISimpleDebuggee.class);
        debuggerInstance.startCommandInterpreter();
    }
}

