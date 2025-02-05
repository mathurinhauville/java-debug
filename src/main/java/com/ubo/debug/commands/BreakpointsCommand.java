package com.ubo.debug.commands;

import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

/**
 * Liste les points d’arrêts actifs et leurs locations dans le code.
 */
public class BreakpointsCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();
        breakpointManager.listBreakpoints();
    }
}