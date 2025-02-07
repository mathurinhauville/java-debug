package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.ubo.debug.ScriptableDebugger;
import com.ubo.debug.BreakpointManager;

import java.util.Scanner;

/**
 * Recule le programme d'un certain nombre de lignes.
 */
public class StepBackCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException, AbsentInformationException {
        int pc = debugger.getPC();
        BreakpointManager breakpointManager = debugger.getBreakpointManager();

        if (pc == 0) {
            System.out.println("Impossible de reculer plus loin");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Combien de lignes en arrière ? ");
        int stepBackCount = scanner.nextInt();

        if (stepBackCount <= 0 || pc - stepBackCount < 0) {
            System.out.println("Nombre invalide");
            return;
        }

        System.out.println("Re-démarrage du programme pour revenir à PC = " + (pc - stepBackCount));

        debugger.getVm().dispose();
        debugger.setPC(pc - stepBackCount);
        debugger.attachTo(debugger.getDebugClass());
    }
}