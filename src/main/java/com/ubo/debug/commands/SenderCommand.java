package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

/**
 * Renvoie l'objet qui a appelé la méthode actuelle.
 */
public class SenderCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();

        if (thread.isSuspended()) {
            try {
                // Récupère la frame appelante
                StackFrame callingFrame = thread.frame(1);

                // Récupère l'objet appelant
                ObjectReference senderObject = callingFrame.thisObject();
                if (senderObject != null) {
                    System.out.println("Objet appelant: " + senderObject);
                } else {
                    System.out.println("Pas d'objet 'this' dans la frame appelante (possiblement une méthode statique).");
                }
            } catch (IncompatibleThreadStateException e) {
                System.out.println("Le thread n'est pas dans un état approprié pour récupérer les frames: " + e.toString());
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Pas de frame appelante disponible: " + e.toString());
            }
        } else {
            System.out.println("Le thread n'est pas suspendu.");
        }
    }
}