package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

public class SenderCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().get(0);

        if (thread.isSuspended()) {
            try {
                // Récupérer la frame actuelle et la frame appelante
                StackFrame currentFrame = thread.frame(0);
                StackFrame callingFrame = thread.frame(1);

                // Récupérer l'objet 'this' de la frame appelante
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