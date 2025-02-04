package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.ubo.debug.ScriptableDebugger;

public interface DebuggerCommand {
    void execute(ScriptableDebugger debugger) throws AbsentInformationException, IncompatibleThreadStateException;
}
