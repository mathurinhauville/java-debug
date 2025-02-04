package com.ubo.debug.commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.ubo.debug.ScriptableDebugger;

public class ReceiverVariablesCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        debugger.printReceiverVariables();
    }
}