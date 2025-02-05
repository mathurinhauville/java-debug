package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

public class BreakOnceCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();

        breakpointManager.setBreakpoint("com.ubo.debug.JDISimpleDebuggee", 10);
    }
}