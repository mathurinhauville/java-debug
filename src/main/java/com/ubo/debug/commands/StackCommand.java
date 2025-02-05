package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

public class StackCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        //debugger.printStack();
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();
        for (StackFrame frame : thread.frames()) {
            System.out.println(frame);
        }
    }
}