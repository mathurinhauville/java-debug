package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

import java.util.List;

/**
 * Renvoie et imprime la liste des variables d’instance du
 * receveur courant, sous la forme d’un couple nom → valeur.
 */
public class ReceiverVariablesCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();

        if (thread.isSuspended()) {
            try {
                // Récupère la frame actuelle
                StackFrame frame = thread.frame(0);
                ObjectReference thisObject = frame.thisObject();

                if (thisObject != null) {
                    List<Field> fields = thisObject.referenceType().allFields();

                    // Parcourt les variables d'instance du receveur
                    for (Field field : fields) {
                        Value value = thisObject.getValue(field);
                        System.out.println(field.name() + " = " + value);
                    }
                } else {
                    System.out.println("Pas d'objet 'this' dans la frame actuelle (possiblement une méthode statique).");
                }
            } catch (IncompatibleThreadStateException e) {
                System.out.println("Le thread n'est pas dans un état approprié pour récupérer les frames: " + e.toString());
            }
        } else {
            System.out.println("Le thread n'est pas suspendu.");
        }
    }
}