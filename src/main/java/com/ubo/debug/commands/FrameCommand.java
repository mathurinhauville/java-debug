package com.ubo.debug.commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.ubo.debug.ScriptableDebugger;

/**
 * Renvoie et affiche la frame courante.
 */
public class FrameCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();

        System.out.println(thread.frame(0));
    }
}