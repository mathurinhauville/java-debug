package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

public class BreakpointsCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        debugger.listBreakpoints();
    }
}