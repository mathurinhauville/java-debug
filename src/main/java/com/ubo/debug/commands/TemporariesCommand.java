package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

/**
 * Renvoie et imprime la liste des variables temporaires de la frame courante, sous la forme de couples nom → valeur.
 */
public class TemporariesCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();
        StackFrame frame = thread.frame(0);

        for (LocalVariable var : frame.visibleVariables()) {
            System.out.println(var.name() + " -> " + frame.getValue(var));
        }
    }
}