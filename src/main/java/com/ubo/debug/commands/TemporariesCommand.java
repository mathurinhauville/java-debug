package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

public class TemporariesCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();
        StackFrame frame = thread.frame(0);

        for (LocalVariable var : frame.visibleVariables()) {
            System.out.println(var.name() + " -> " + frame.getValue(var));
        }
    }
}