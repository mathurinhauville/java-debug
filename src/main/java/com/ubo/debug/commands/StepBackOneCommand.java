package com.ubo.debug.commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;
import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

/**
 * Recule le programme d'une ligne.
 */
public class StepBackOneCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        int pc = debugger.getPC();

        if (pc == 0) {
            System.out.println("Impossible de reculer plus loin");
            return;
        }

        System.out.println("Re-démarrage du programme pour revenir à PC = " + (pc - 1));

        debugger.getVm().dispose();
        debugger.setPC(pc - 1);
        debugger.attachTo(debugger.getDebugClass());
    }
}
