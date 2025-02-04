package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

public class BreakBeforeMethodCallCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        debugger.setMethodCallBreakpoint();
    }
}