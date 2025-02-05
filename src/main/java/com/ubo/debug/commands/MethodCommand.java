package com.ubo.debug.commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.VirtualMachine;
import com.ubo.debug.ScriptableDebugger;

/**
 * Renvoie et imprime la méthode en cours d’exécution.
 */
public class MethodCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();

        System.out.println(vm.allThreads().getFirst().frame(0).location().method());
    }
}
