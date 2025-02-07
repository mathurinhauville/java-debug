package com.ubo.debug;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

public class JDISimpleDebugger {
    public static void main(String[] args) throws AbsentInformationException, IncompatibleThreadStateException, InterruptedException {
        ScriptableDebugger debuggerInstance = new ScriptableDebugger();
        debuggerInstance.attachTo(JDISimpleDebuggee.class);
    }
}

