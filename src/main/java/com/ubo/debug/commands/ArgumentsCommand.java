package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

public class ArgumentsCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        StackFrame frame = vm.allThreads().getFirst().frame(0);

        for (LocalVariable var : frame.visibleVariables()) {
            if (var.isArgument()) {
                System.out.println(var.name() + " -> " + frame.getValue(var));
            }
        }
    }
}