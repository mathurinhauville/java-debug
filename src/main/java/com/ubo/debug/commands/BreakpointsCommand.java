package com.ubo.debug.commands;

import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

public class BreakpointsCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();
        breakpointManager.listBreakpoints();
    }
}