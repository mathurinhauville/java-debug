package com.ubo.debug;

import com.sun.jdi.VirtualMachine;

public class JDISimpleDebugger {
    public static void main(String[] args) throws Exception {
        VirtualMachine vm = DebuggerLauncher.launchVM("com.ubo.debug.JDISimpleDebuggee");
        ScriptableDebugger debuggerInstance = new ScriptableDebugger(vm);
        debuggerInstance.attachTo(JDISimpleDebuggee.class);

    }
}

