package com.ubo.debug.commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.VirtualMachine;
import com.ubo.debug.ScriptableDebugger;

public class MethodCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        //debugger.printMethod();
        VirtualMachine vm = debugger.getVm();
        System.out.println(vm.allThreads().getFirst().frame(0).location().method());
    }
}
