package com.ubo.debug.commands;

import com.sun.jdi.Location;
import com.sun.jdi.request.BreakpointRequest;
import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

public class BreakpointsCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();

        for (BreakpointRequest bpReq : breakpointManager.getBreakpoints()) {
            Location location = bpReq.location();
            System.out.println("Breakpoint at " + location.declaringType().name() + ":" + location.lineNumber());
        }
    }
}