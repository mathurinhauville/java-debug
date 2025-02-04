package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.ubo.debug.ScriptableDebugger;

public class StepOverCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        debugger.stepOver();
    }
}
