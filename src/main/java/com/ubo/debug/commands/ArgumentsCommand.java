package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

/**
 * Renvoie et imprime la liste des arguments de la méthode en cours d’exécution, sous la forme d’un couple nom → valeur.
 */
public class ArgumentsCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        StackFrame frame = vm.allThreads().getFirst().frame(0);

        // On parcourt les variables visibles du frame
        for (LocalVariable var : frame.visibleVariables()) {
            if (var.isArgument()) {
                System.out.println(var.name() + " -> " + frame.getValue(var));
            }
        }
    }

}
