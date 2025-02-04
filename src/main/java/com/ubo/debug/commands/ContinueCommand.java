package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

public class ContinueCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        debugger.continueExecution();
    }
}
