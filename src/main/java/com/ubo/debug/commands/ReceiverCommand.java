package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;

public class ReceiverCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().get(0);

        if (thread.isSuspended()) {
            try {
                StackFrame frame = thread.frame(0);
                ObjectReference thisObject = frame.thisObject();
                if (thisObject != null) {
                    System.out.println("Objet 'this' actuel: " + thisObject);
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