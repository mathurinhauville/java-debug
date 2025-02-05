package com.ubo.debug.commands;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.*;
import com.ubo.debug.ScriptableDebugger;

public class ContinueCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        VirtualMachine vm = debugger.getVm();

        try {
            vm.resume(); // Relancer l'exécution après un arrêt

            while (true) {
                EventSet eventSet = vm.eventQueue().remove();
                for (Event event : eventSet) {
                    if (event instanceof BreakpointEvent) {
                        return; // On s'arrête uniquement quand un breakpoint est atteint
                    } else if (event instanceof VMDisconnectEvent) {
                        System.out.println("Virtual Machine disconnected.");
                        return;
                    }
                    // Ignorer les StepEvent et autres événements non pertinents
                }
                vm.resume(); // Ne reprendre l'exécution que si aucun BreakpointEvent n'a été capté
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e.toString());
        }
    }
}
