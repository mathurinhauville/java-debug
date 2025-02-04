package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.ubo.debug.ScriptableDebugger;

public class TemporariesCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        debugger.printTemporaries();
    }
}