package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

/**
 * Renvoie et affiche la pile d’exécution.
 */
public class StackCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();

        for (StackFrame frame : thread.frames()) {
            System.out.println(frame);
        }
    }
}